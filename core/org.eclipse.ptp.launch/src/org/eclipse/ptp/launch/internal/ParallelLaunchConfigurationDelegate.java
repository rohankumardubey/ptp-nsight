/*******************************************************************************
 * Copyright (c) 2005 The Regents of the University of California.
 * This material was produced under U.S. Government contract W-7405-ENG-36
 * for Los Alamos National Laboratory, which is operated by the University
 * of California for the U.S. Department of Energy. The U.S. Government has
 * rights to use, reproduce, and distribute this software. NEITHER THE
 * GOVERNMENT NOR THE UNIVERSITY MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR
 * ASSUMES ANY LIABILITY FOR THE USE OF THIS SOFTWARE. If software is modified
 * to produce derivative works, such modified software should be clearly marked,
 * so as not to confuse it with the version available from LANL.
 *
 * Additionally, this program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * LA-CC 04-115
 *******************************************************************************/
package org.eclipse.ptp.launch.internal;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ElementAttributes;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;
import org.eclipse.ptp.debug.core.IPDebugConfiguration;
import org.eclipse.ptp.debug.core.IPDebugger;
import org.eclipse.ptp.debug.core.IPSession;
import org.eclipse.ptp.debug.core.PTPDebugCorePlugin;
import org.eclipse.ptp.debug.core.launch.IPLaunch;
import org.eclipse.ptp.debug.ui.IPTPDebugUIConstants;
import org.eclipse.ptp.launch.PTPLaunchPlugin;
import org.eclipse.ptp.launch.internal.ui.LaunchMessages;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
/**
 *
 */
public class ParallelLaunchConfigurationDelegate
	extends AbstractParallelLaunchConfigurationDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.launch.internal.AbstractParallelLaunchConfigurationDelegate#doCompleteJobLaunch(org.eclipse.ptp.core.elements.IPJob)
	 */
	@Override
	protected void doCompleteJobLaunch(ILaunchConfiguration configuration, String mode, final IPLaunch launch,
			AttributeManager mgr, final IPDebugger debugger, final IPJob job) {
		launch.setAttribute(ElementAttributes.getIdAttributeDefinition().getId(), job.getID());
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			launch.setPJob(job);
			try {
				setDefaultSourceLocator(launch, configuration);
				final IProject project = verifyProject(configuration);
				final IPath execPath = verifyExecutablePath(configuration);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						IRunnableWithProgress runnable = new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								if (monitor.isCanceled()) {
									throw new InterruptedException("The job is cancelled."); //$NON-NLS-1$
								}
								monitor.beginTask("Debugger has started, waiting for connection...", 1); //$NON-NLS-1$
								try {
									IPSession session = PTPDebugCorePlugin.getDebugModel().createDebugSession(debugger, launch, project, execPath);

									String app = job.getAttribute(JobAttributes.getExecutableNameAttributeDefinition()).getValueAsString();
									String path = job.getAttribute(JobAttributes.getExecutablePathAttributeDefinition()).getValueAsString();
									String cwd = job.getAttribute(JobAttributes.getWorkingDirectoryAttributeDefinition()).getValueAsString();
									List<String> args = job.getAttribute(JobAttributes.getProgramArgumentsAttributeDefinition()).getValue();

									session.connectToDebugger(monitor, app, path, cwd, args.toArray(new String[args.size()]));
								} catch (CoreException e) {
									throw new InvocationTargetException(e);
								} finally {
									monitor.done();
								}
							}
						};
						try {
							new ProgressMonitorDialog(PTPLaunchPlugin.getActiveWorkbenchShell()).run(true, true, runnable);
						} catch (InterruptedException e) {
							System.out.println("Debug job launch interrupted: " + e.getMessage()); //$NON-NLS-1$
							terminateJob(job);
						} catch (InvocationTargetException e) {
							String msg = e.getMessage();
							Throwable t = e.getCause();
							if (t != null) {
								msg = t.getMessage();
							}
							System.out.println("Error completing debug job launch: " + msg); //$NON-NLS-1$
							PTPLaunchPlugin.errorDialog("Error completing debug job launch", t);
							PTPLaunchPlugin.log(t);
							terminateJob(job);
						}
					}
				});
			} catch (final CoreException e) {
				/*
				 * Completion of launch fails, then terminate the job and display error message.
				 */
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						PTPLaunchPlugin.errorDialog("Error completing launch", e.getStatus());
					}
				});
				PTPLaunchPlugin.log(e);
				terminateJob(job);
			}
		} else {
			new RuntimeProcess(launch, job, null);
		}
	}

	@Override
	protected void doCleanuoLaunch(ILaunchConfiguration configuration,
			String mode, IPLaunch launch, AttributeManager attrMgr,
			IPDebugger debugger, IPJob job) {
		if (debugger != null) {
			debugger.cleanup(configuration, attrMgr, launch);
		}
	}

	private void terminateJob(final IPJob job) {
		try {
			IResourceManager rm = job.getQueue().getResourceManager();
			rm.terminateJob(job);
		} catch (CoreException e1) {
			// Ignore, but log
			PTPLaunchPlugin.log(e1);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (!(launch instanceof IPLaunch)) {
			abort(LaunchMessages.getResourceString("ParallelLaunchConfigurationDelegate.Invalid_launch_object"), null, 0); //$NON-NLS-1$
		}
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("", 250);
		monitor.setTaskName(MessageFormat.format("{0} . . .", new Object[] { "Launching " + configuration.getName() })); //$NON-NLS-1$ $NON-NLS-2$
		if (monitor.isCanceled()) {
			return;
		}
		IPDebugger debugger = null;
		IPJob job = null;

		monitor.worked(10);
		monitor.subTask("Copying data . . .");

		// All copy pre-"job submission" occurs here
		copyExecutable(configuration, monitor);
		doPreLaunchSynchronization(configuration, monitor);

		//switch perspective
		switchPerspective(DebugUITools.getLaunchPerspective(configuration.getType(), mode));
		AttributeManager attrManager = getAttributeManager(configuration, mode);
		try {
			if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				// show ptp debug view
				showPTPDebugView(IPTPDebugUIConstants.ID_VIEW_PARALLELDEBUG);
				monitor.subTask("Configuring debug setting . . ."); //$NON-NLS-1$

				/*
				 * Create the debugger extension, then the connection point for the debug server.
				 * The debug server is launched via the submitJob() command.
				 */

				IPDebugConfiguration debugConfig = getDebugConfig(configuration);
				debugger = debugConfig.getDebugger();
				debugger.initialize(configuration, attrManager, monitor);
				if (monitor.isCanceled()) {
					return;
				}
				debugger.getLaunchAttributes(configuration, attrManager);
			}

			monitor.worked(10);
			monitor.subTask("Submitting the job . . ."); //$NON-NLS-1$

			submitJob(configuration, mode, (IPLaunch)launch, attrManager, debugger, monitor);

			monitor.worked(10);
		} catch (CoreException e) {
			if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				PTPDebugCorePlugin.getDebugModel().shutdownSession(job);
			}
			if (e.getStatus().getCode() != IStatus.CANCEL) {
				throw e;
			}
		} finally {
			monitor.done();
		}
	}

	/**
	 * Show the PTP Debug view
	 *
	 * @param viewID
	 */
	protected void showPTPDebugView(final String viewID) {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		if (display != null && !display.isDisposed()) {
			display.syncExec(new Runnable() {
				public void run() {
					IWorkbenchWindow window = PTPLaunchPlugin.getActiveWorkbenchWindow();
					if (window != null) {
						IWorkbenchPage page = window.getActivePage();
						if (page != null) {
							try {
								page.showView(viewID, null, IWorkbenchPage.VIEW_CREATE);
							} catch (PartInitException e) {}
						}
					}
				}
			});
		}
	}


	/**
	 * Used to force switching to the PTP Debug perspective
	 *
	 * @param perspectiveID
	 */
	protected void switchPerspective(final String perspectiveID) {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		if (display != null && !display.isDisposed()) {
			display.syncExec(new Runnable() {
	            public void run() {
					IWorkbenchWindow window = PTPLaunchPlugin.getActiveWorkbenchWindow();
					if (window != null) {
						IWorkbenchPage page = window.getActivePage();
						if (page != null) {
			                if (page.getPerspective().getId().equals(perspectiveID))
			                    return;

							try {
				                window.getWorkbench().showPerspective(perspectiveID, window);
			                } catch (WorkbenchException e) { }
						}
					}
	            }
	        });
		}
	}
}
