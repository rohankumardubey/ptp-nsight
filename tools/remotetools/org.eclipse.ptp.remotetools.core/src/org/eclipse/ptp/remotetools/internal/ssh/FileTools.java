/******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial Implementation
 *     Roland Schulz, University of Tennessee
 *
 *****************************************************************************/
package org.eclipse.ptp.remotetools.internal.ssh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ptp.remotetools.core.IRemoteCopyTools;
import org.eclipse.ptp.remotetools.core.IRemoteFileEnumeration;
import org.eclipse.ptp.remotetools.core.IRemoteFileTools;
import org.eclipse.ptp.remotetools.core.IRemoteItem;
import org.eclipse.ptp.remotetools.core.IRemotePathTools;
import org.eclipse.ptp.remotetools.exception.CancelException;
import org.eclipse.ptp.remotetools.exception.RemoteConnectionException;
import org.eclipse.ptp.remotetools.exception.RemoteException;
import org.eclipse.ptp.remotetools.exception.RemoteExecutionException;
import org.eclipse.ptp.remotetools.exception.RemoteOperationException;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

/**
 * @author Richard Maciel, Daniel Ferber
 * 
 */
public class FileTools implements IRemoteFileTools {
	public static class FileToolsProgressMonitor implements SftpProgressMonitor {
		private IProgressMonitor fMonitor;
		private long fWorkToDate;

		public FileToolsProgressMonitor(IProgressMonitor monitor) {
			fMonitor = monitor;
		}
		public boolean count(long count){
			fWorkToDate += count;
			return !(fMonitor.isCanceled());
		}
		public void end(){
			// Nothing
		}
		public void init(int op, String src, String dest, long max){
			fWorkToDate = 0;
			String srcFile = new Path(src).lastSegment();
			String desc = srcFile;
			//TODO avoid cast from long to int
			fMonitor.beginTask(desc, (int)max);

		}
	}

	private abstract class SftpCallable<T> implements Callable<T> {
		private ChannelSftp fSftpChannel = null;
		
		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		public abstract T call() throws SftpException, IOException;
		
		public ChannelSftp getChannel() {
			return fSftpChannel;
		}
		
		public void setChannel(ChannelSftp channel) {
			fSftpChannel = channel;
		}
		
		private Future<T> asyncCmdInThread(String jobName, IProgressMonitor monitor) throws SftpException, IOException, RemoteConnectionException {
			if (monitor!=null) {
				monitor.beginTask(jobName, 1);
			}
			setChannel(getSFTPChannel());
			return fPool.submit(this);
		}
		
		private void finalizeCmdInThread(IProgressMonitor monitor) {
			releaseSFTPChannel(getChannel());
			setChannel(null);
			if (monitor != null) {
				monitor.done();		
			}
		}
		
		/**
		 * Function opens sftp channel and then executes the sftp operation. If run on the main thread it executes it on 
		 * a separate thread
		 */
		private T syncCmdInThread(String jobName, IProgressMonitor monitor) throws RemoteConnectionException, SftpException, IOException, RemoteOperationException, CancelException {
			Future<T> future = null;
			try {
				future = asyncCmdInThread(jobName, monitor);
				return waitCmdInThread(future, monitor);
			} finally {
				finalizeCmdInThread(monitor);
			}
		}

		private T waitCmdInThread(Future<T> future,
				IProgressMonitor monitor) throws IOException, CancelException, SftpException, RemoteOperationException {
			T ret = null;
			boolean bInterrupted = Thread.interrupted();
			while (ret == null) {
				if (monitor != null && monitor.isCanceled()) {
					future.cancel(true);
					getChannel().quit();
					throw new CancelException();
				}
				try {
					ret = future.get(100,TimeUnit.MILLISECONDS); //throws InterruptedException if Thread.interrupted() is true
				} catch (InterruptedException e) {
					bInterrupted = true;
				} catch (TimeoutException e) { 
					//ignore
				} catch (ExecutionException e) {
					getChannel().quit();  //close sftp channel (gets automatically reopened) to make sure the channel is not in undefined state because of exception
					if (e.getCause() instanceof IOException) {
						throw (IOException)e.getCause();
					}
					if (e.getCause() instanceof SftpException) {
						throw (SftpException)e.getCause();
					}
					throw new RemoteOperationException(e);
				}
			}
			if (bInterrupted) {
				Thread.currentThread().interrupt();  //set current thread flag
			}
			if (monitor!=null) {
				monitor.worked(1);
			}
			return ret;
		}
	} 
	
	private static ExecutorService fPool = Executors.newSingleThreadExecutor();	
	protected ExecutionManager manager;
	private int cachedUserID;
	private Set<Integer> cachedGroupIDSet;
	private String fOSName = null;

	protected FileTools(ExecutionManager manager) {
		this.manager = manager;
	}

	public String addTrailingSlash(String path) {
		if (path.endsWith("/")) { //$NON-NLS-1$
			return path;
		} else {
			return path + "/"; //$NON-NLS-1$
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#assureDirectory(java.lang.String)
	 */
	public void assureDirectory(String directory, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(directory);
		if (! hasDirectory(directory, monitor)) {
			createDirectory(directory, monitor);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#canExecute(java.lang.String)
	 */
	public boolean canExecute(String remotePath) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(remotePath);
		RemoteItem item = (RemoteItem) getItem(remotePath);
		return item.isExecutable();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#canRead(java.lang.String)
	 */
	public boolean canRead(String remotePath) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(remotePath);
		RemoteItem item = (RemoteItem) getItem(remotePath);
		return item.isReadable();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#canWrite(java.lang.String)
	 */
	public boolean canWrite(String remotePath) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(remotePath);
		RemoteItem item = (RemoteItem) getItem(remotePath);
		return item.isWritable();
	}
	
	public void chmod(final int permissions, final String path, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		try {
			test();
			validateRemotePath(path);
			SftpCallable<Integer> c = new SftpCallable<Integer>(){
				public Integer call() throws SftpException {
					getChannel().chmod(permissions, path);
					return 0;
				}
			};
			c.syncCmdInThread(Messages.FileTools_2, monitor);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
		
	}
	
	public String concatenateRemotePath(String p1, String p2) {
		if (p1.endsWith("/")) { //$NON-NLS-1$
			return p1 + p2;
		} else {
			return p1 + "/" + p2; //$NON-NLS-1$
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#copyFile(java.lang.String, java.lang.String)
	 */
	public void copyFile(String from, String to, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(from);
		validateRemotePath(to);
		
		IRemotePathTools pathTool = manager.getRemotePathTools();
		try {
			executeCommand("cp -f " + pathTool.quote(from, true) + " " + pathTool.quote(to, true));//$NON-NLS-1$ //$NON-NLS-2$
		} catch (RemoteExecutionException e) {
			throw new RemoteOperationException(e);
		} 
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#createDirectory(java.lang.String)
	 */
	public void createDirectory(String directory, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		validateRemotePath(directory);
		IRemotePathTools pathTool = manager.getRemotePathTools();
		
		final String path = pathTool.quote(directory, true);
		String parent = pathTool.parent(path);
		
		RemoteFileAttributes attrs = fetchRemoteAttr(parent,monitor);
		if (attrs == null) {
			createDirectory(parent, monitor);
		}
		
		try {
			SftpCallable<Integer> c = new SftpCallable<Integer>(){
				public Integer call() throws SftpException {
					getChannel().mkdir(path);
					return 0;
				}
			};
			c.syncCmdInThread(Messages.FileTools_3, monitor);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#createFile(java.lang.String)
	 */
	public void createFile(String file, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(file);
		IRemotePathTools pathTool = manager.getRemotePathTools();
		final String path = pathTool.quote(file, true);
		
		try {
			SftpCallable<Integer> c = new SftpCallable<Integer>(){
				public Integer call() throws SftpException, IOException {
					OutputStream os = getChannel().put(path);
					os.close();
					return 0;
				}
			};
			c.syncCmdInThread(Messages.FileTools_4, monitor);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#createFileEnumeration(java.lang.String)
	 */
	public IRemoteFileEnumeration createFileEnumeration(String path) throws RemoteOperationException, RemoteConnectionException, CancelException {
		return new RemoteFileEnumeration(this, path);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#createRecursiveFileEnumeration(java.lang.String)
	 */
	public IRemoteFileEnumeration createRecursiveFileEnumeration(String path) throws RemoteOperationException, RemoteConnectionException, CancelException {
		return new RemoteFileRecursiveEnumeration(this, path);
	}
	
	public void downloadIntoOutputStream(final String remotePath, final OutputStream sink, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		try {
			test();
			validateRemotePath(remotePath);
			SftpCallable<Integer> c = new SftpCallable<Integer>(){
				public Integer call() throws SftpException {
					getChannel().get(remotePath, sink);
					return 0;
				}
			};
			c.syncCmdInThread(Messages.FileTools_5, monitor);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
	}

	public Set<Integer> getCachedGroupIDSet() {
		return cachedGroupIDSet;
	}
	
	public int getCachedUserID() {
		return cachedUserID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#getDirectory(java.lang.String)
	 */
	public IRemoteItem getDirectory(String directoryPath, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException, RemoteOperationException {
		test();
		validateRemotePath(directoryPath);
		directoryPath = removeTrailingSlash(directoryPath);
		cacheUserData();
				
		IRemoteItem remfile = new RemoteItem(this, directoryPath);
		remfile.refreshAttributes(monitor);
		if (!remfile.isDirectory()) {
			throw new RemoteOperationException(Messages.FileTools_6);
		}
		return remfile;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#getFile(java.lang.String)
	 */
	public IRemoteItem getFile(String filePath, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException, RemoteOperationException {
		test();
		validateRemotePath(filePath);
		filePath = removeTrailingSlash(filePath);
		cacheUserData();
				
		IRemoteItem remfile = new RemoteItem(this, filePath);
		remfile.refreshAttributes(monitor);
		if (remfile.isDirectory()) {
			throw new RemoteOperationException(Messages.FileTools_7);
		}
		return remfile;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#getInputStream(java.lang.String, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public InputStream getInputStream(String file, IProgressMonitor m) throws RemoteOperationException,
			RemoteConnectionException, CancelException {
		test();
		validateRemotePath(file);
		IRemotePathTools pathTool = manager.getRemotePathTools();
		final String path = pathTool.quote(file, true);

		final IProgressMonitor monitor = (m == null) ? new NullProgressMonitor() : m;
		
		final SftpProgressMonitor sftpMonitor = new FileToolsProgressMonitor(monitor);
		
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			SftpCallable<Integer> c = new SftpCallable<Integer>(){
				public Integer call() throws SftpException, IOException {
					getChannel().get(path, out, sftpMonitor);
					out.close();
					return 0;
				}
			};
			c.syncCmdInThread(Messages.FileTools_8, monitor);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#getItem(java.lang.String)
	 */
	public IRemoteItem getItem(String path) throws RemoteConnectionException, RemoteOperationException, CancelException, RemoteOperationException {
		test();
		validateRemotePath(path);
		path = removeTrailingSlash(path);
		cacheUserData();

		return new RemoteItem(this, path);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#getOutputStream(java.lang.String, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public OutputStream getOutputStream(String file, int options, IProgressMonitor m) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(file);
		IRemotePathTools pathTool = manager.getRemotePathTools();
		final String path = pathTool.quote(file, true);

		final IProgressMonitor monitor = (m == null) ? new NullProgressMonitor() : m;

		final SftpProgressMonitor sftpMonitor = new FileToolsProgressMonitor(monitor);
		final int mode;
		if ((options & IRemoteFileTools.APPEND) == 0) {
			mode = ChannelSftp.OVERWRITE;
		} else {
			mode = ChannelSftp.APPEND;
		}

		return new ByteArrayOutputStream() {
			public void close() throws IOException {
				super.close();
				final InputStream is = new ByteArrayInputStream(this.toByteArray());
				try {
					SftpCallable<Integer> c = new SftpCallable<Integer>(){
						public Integer call() throws SftpException, IOException {
							getChannel().put(is, path, sftpMonitor, mode);
							is.close();
							return 0;
						}
					};
					c.syncCmdInThread(Messages.FileTools_9, monitor);
				} catch (SftpException e) {
					throw new IOException(e.getMessage());
				} catch (RemoteException e) {
					throw new IOException(e.getMessage());
				} catch (CancelException e) {
					throw new IOException(e.getMessage());
				}
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#getRemoteCopyTools()
	 */
	public IRemoteCopyTools getRemoteCopyTools() throws RemoteConnectionException {
		return manager.getRemoteCopyTools();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.internal.ssh.teste#hasDirectory(java.lang.String)
	 */
	public boolean hasDirectory(String directory, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(directory);
		RemoteItem item = (RemoteItem) getItem(directory);
		item.refreshAttributes(monitor);
		if (!item.exists()) {
			return false;
		}
		if (!item.isDirectory()) {
			return false;
		}		
		return true;
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.internal.ssh.teste#hasFile(java.lang.String)
	 */
	public boolean hasFile(String file, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(file);
		IRemoteItem item = getItem(file);
		item.refreshAttributes(monitor);
		if (!item.exists()) {
			return false;
		}
		if (item.isDirectory()) {
			return false;
		}		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#listItems(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public IRemoteItem[] listItems(final String root, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		validateRemotePath(root);
		Vector<LsEntry> files;

		try {
			SftpCallable<Vector<LsEntry>> c = new SftpCallable<Vector<LsEntry>>(){
				public Vector<LsEntry> call() throws SftpException {
					return getChannel().ls(root);
				}
			};
			files = c.syncCmdInThread(Messages.FileTools_10, monitor);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
		
		cacheUserData();
		
		List<RemoteItem> result = new ArrayList<RemoteItem>();
		Enumeration<LsEntry> enumeration = files.elements();
		while (enumeration.hasMoreElements()) {
			LsEntry entry = enumeration.nextElement();
			String fileName = entry.getFilename();
			String pathName = concatenateRemotePath(root, fileName);
			if (fileName.equals(".") || fileName.equals("..")) { //$NON-NLS-1$ //$NON-NLS-2$
				// Ignore parent and current dir entry.
				continue;
			}
			result.add(new RemoteItem(this, pathName, entry.getAttrs()));
		}
		
		IRemoteItem [] resultArray = new IRemoteItem[result.size()];
		result.toArray(resultArray);
		return resultArray;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#moveFile(java.lang.String, java.lang.String)
	 */
	public void moveFile(String from, String to, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(from);
		validateRemotePath(to);
		IRemotePathTools pathTool = manager.getRemotePathTools();
		try {
			executeCommand("mv -f " + pathTool.quote(from, true) + " " + pathTool.quote(to, true)); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RemoteExecutionException e) {
			throw new RemoteOperationException(e);
		}
	}
	public String parentOfRemotePath(String path) {
		path = removeTrailingSlash(path);
		int index = path.lastIndexOf('/');
		if (index == -1) return null;
		return removeTrailingSlash(path.substring(0, index));
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#removeDirectory(java.lang.String)
	 */
	public void removeDirectory(String dir,IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(dir);
		IRemotePathTools pathTool = manager.getRemotePathTools();
		final String path = pathTool.quote(dir, true);
		
		
		try {
			SftpCallable<Integer> c = new SftpCallable<Integer>(){
				public Integer call() throws SftpException {
					getChannel().rmdir(path);
					return 0;
				}
			};
			c.syncCmdInThread(Messages.FileTools_11, monitor);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.remotetools.core.IRemoteFileTools#removeFile(java.lang.String)
	 */
	public void removeFile(String file, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		test();
		validateRemotePath(file);
		IRemotePathTools pathTool = manager.getRemotePathTools();
		final String path = pathTool.quote(file, true);
		
		try {
			SftpCallable<Integer> c = new SftpCallable<Integer>(){
				public Integer call() throws SftpException {
					getChannel().rm(path);
					return 0;
				}
			};
			c.syncCmdInThread(Messages.FileTools_12, monitor);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
		
	}

	public String removeTrailingSlash(String path) {
		if (!path.equals("/") && path.endsWith("/")) { //$NON-NLS-1$ //$NON-NLS-2$
			return path.substring(0, path.length() - 1);
		} else {
			return path;
		}
	}
	
	public void setMtime(final String path, final int mtime, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		try {
			test();
			validateRemotePath(path);
			SftpCallable<Integer> c = new SftpCallable<Integer>(){
				public Integer call() throws SftpException {
					getChannel().setMtime(path, mtime);
					return 0;
				}
			};
			c.syncCmdInThread(Messages.FileTools_13, monitor);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
	}
	
	public String suffixOfRemotePath(String path) {
		path = removeTrailingSlash(path);
		int index = path.lastIndexOf('/');
		if (index == -1) return null;
		return removeTrailingSlash(path.substring(index+1));
	}

	public void uploadFromInputStream(final InputStream source, final String remotePath, IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, CancelException {
		try {
			test();
			validateRemotePath(remotePath);
			SftpCallable<Integer> c = new SftpCallable<Integer>(){
				public Integer call() throws SftpException {
					getChannel().put(source, remotePath);
					return 0;
				}
			};
			c.syncCmdInThread(Messages.FileTools_14, monitor);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		} catch (SftpException e) {
			throw new RemoteOperationException(e);
		}
	}

	/**
	 * @throws CancelException 
	 * @throws RemoteOperationException 
	 * @throws RemoteConnectionException 
	 * @deprecated
	 */
	public void uploadPermissions(File file, String remoteFilePath) throws RemoteConnectionException, RemoteOperationException, CancelException {
		IRemoteItem item = getItem(remoteFilePath);
		item.setReadable(file.canRead());
		item.setWriteable(file.canWrite());
		item.commitAttributes(null);
	}

	public void validateRemotePath(String path) throws RemoteOperationException {
		if (!path.startsWith("/")) { //$NON-NLS-1$
			throw new RemoteOperationException(path + Messages.RemoteFileTools_ValidateRemotePath_NotValid);
		}
	}

	private void cacheUserData() throws RemoteConnectionException, RemoteOperationException, CancelException {
		if (cachedGroupIDSet == null) {
			cachedGroupIDSet = manager.getRemoteStatusTools().getGroupIDSet();
			cachedUserID = manager.getRemoteStatusTools().getUserID();
		}
	}

	private boolean checkOSName(String name) {
		if (fOSName == null) {
			try {
				fOSName = manager.getExecutionTools().executeWithOutput("uname").trim(); //$NON-NLS-1$
			} catch (RemoteExecutionException e) {
				return false;
			} catch (RemoteConnectionException e) {
				return false;
			} catch (CancelException e) {
				return false;
			}
		}
		return fOSName.equals(name);
	}
	
	private ChannelSftp getSFTPChannel() throws RemoteConnectionException {
		return manager.connection.getSFTPChannel();
	}

	private void releaseSFTPChannel(ChannelSftp sftp) {
		manager.connection.releaseSFTPChannel(sftp);
	}

	protected void executeCommand(String command) throws RemoteConnectionException, RemoteExecutionException, CancelException {
		manager.executionTools.executeBashCommand(command);
	}
	
	/**
	 * Read attributes of the remote file.
	 * @param path
	 * @return A Jsch data structure with attributes or null if path does not exist.
	 * @throws RemoteConnectionException 
	 * @throws RemoteExecutionException
	 */
	protected RemoteFileAttributes fetchRemoteAttr(final String path, IProgressMonitor monitor) throws RemoteOperationException, CancelException, RemoteConnectionException {
		validateRemotePath(path);
			
		try {
			test();
			SftpCallable<SftpATTRS> c = new SftpCallable<SftpATTRS>(){
				public SftpATTRS call() throws SftpException {
					return getChannel().stat(path);
				}
			};
			SftpATTRS attrs = c.syncCmdInThread(Messages.FileTools_15, monitor);
			return RemoteFileAttributes.getAttributes(attrs);	
		} catch (SftpException e) {
			if (((SftpException)e).id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				return null;
			}
			throw new RemoteOperationException(e);
		} catch (IOException e) {
			throw new RemoteOperationException(e);
		}
	}

	protected void test() throws RemoteConnectionException, CancelException {
		manager.test();
		manager.testCancel();
	}

}
