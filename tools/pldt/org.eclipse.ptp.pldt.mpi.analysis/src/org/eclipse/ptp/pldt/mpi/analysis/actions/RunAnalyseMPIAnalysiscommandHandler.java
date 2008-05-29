/**********************************************************************
 * Copyright (c) 2007,2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ptp.pldt.mpi.analysis.actions;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ptp.pldt.common.actions.AnalysisDropdownHandler;
import org.eclipse.ptp.pldt.common.actions.RunAnalyseHandler;
import org.eclipse.ptp.pldt.common.util.ViewActivater;
import org.eclipse.ptp.pldt.mpi.analysis.IDs;
import org.eclipse.ptp.pldt.mpi.analysis.analysis.MPICallGraph;
import org.eclipse.ptp.pldt.mpi.analysis.analysis.MPIResourceCollector;


/**
 * Do MPI barrier analysis from dropdown toolbar menu
 * @author Beth Tibbitts
 *
 */
public class RunAnalyseMPIAnalysiscommandHandler extends RunAnalyseHandler  {
	protected MPICallGraph callGraph_;
	boolean traceOn=false;
	
	public RunAnalyseMPIAnalysiscommandHandler(){ 
		callGraph_ = null;
	}

	/** 
	 * Execute the action for MPI barrier analysis
	 */
	@SuppressWarnings("unchecked")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean foundError=false;
		getSelection(event);
		AnalysisDropdownHandler.setLastHandledAnalysis(this,selection);
		if ((selection == null) || selection.isEmpty()) {
			MessageDialog
					.openWarning(null, "No files selected for analysis.",
							"Please select a source file  or container (folder or project) to analyze.");
			return null;
		} else {
			callGraph_ = new MPICallGraph();
			//int numFiles=this.countFilesSelected();
			// BRT use numfiles for a progress monitor?

			for(Iterator iter = selection.iterator(); iter.hasNext();){
				Object obj =  iter.next();
				// It can be a Project, Folder, File, etc...
				if (obj instanceof IAdaptable) {
					final IResource res = (IResource) ((IAdaptable) obj)
							.getAdapter(IResource.class);
					if(traceOn)System.out.println("resourceCollector on "+res.getName());
					// FIXME put this in a runnable to batch resource changes?
					if (res != null) {
						resourceCollector(res);
					}
				}
			} // end for
			MPIAnalysisManager manager = new MPIAnalysisManager(callGraph_);
			foundError=manager.run();
		}
		ViewActivater.activateView(IDs.matchingSetViewID);
		ViewActivater.activateView(IDs.barrierViewID);
		// if error found, assure its view has focus
		if(foundError) {
			ViewActivater.activateView(IDs.errorViewID);
		}
		return null;
	}
	
/*	//ProgressMonitorDialog example?
public Object execute2(ExecutionEvent event) throws ExecutionException {
		int numFiles=this.countFilesSelected();
		// batch ws modifications *and* report progress
		WorkspaceModifyOperation wmo = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				err = runResources(monitor, indent, includes);
			}
		};
		ProgressMonitorDialog pmdialog = new ProgressMonitorDialog(
				shell);
		try {
			pmdialog.run(true, true, wmo); // fork=true; if false, not
											// cancelable

		} catch (InvocationTargetException e) {
			err = true;
			System.out.println("Error running analysis: ITE: "
					+ e.getMessage());
			System.out.println("  cause: " + e.getCause() + " - "
					+ e.getCause().getMessage());
			Throwable th = e.getCause();
			th.printStackTrace();
		} catch (InterruptedException e) {
			cancelledByUser = true;
		}
	}
	*/
	/**
	 * Run analysis (collect resource info in the call graph) on a resource (e.g. File or Folder) 
	 * <br>Will descend to members of folder
	 * 
	 * @param resource
	 *            the resource upon which barrier analysis was initiated: file, folder, or project
	 * @return
	 */
	protected boolean resourceCollector(IResource resource) {

		boolean foundError = false;

		if (resource instanceof IFile) {
			try{
				// BRT barrierMarker change to non-problem marker here?
				resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				//resource.deleteMarkers(IDs.errorMarkerID,true,IResource.DEPTH_INFINITE);
			} catch(CoreException e){
				//System.out.println("RM: exception deleting markers.");
				//e.printStackTrace();
			}
			IFile file = (IFile) resource;
			String filename = file.getName();
			if(filename.endsWith(".c")){
			  if(traceOn)System.out.println("resourceCollector on c file: "+file.getName());
				MPIResourceCollector rc = new MPIResourceCollector(callGraph_, file); // BRT why 'new' each time?
				rc.run();
			}
			return true;
		}
		else if (resource instanceof IContainer) {
			IContainer container = (IContainer) resource;
			try {
				IResource[] mems = container.members();
				for (int i = 0; i < mems.length; i++) {
				  if(traceOn)System.out.println("descend to "+mems[i].getName());
					boolean err = resourceCollector(mems[i]);
					foundError = foundError || err;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		else {
			String name = "";
			if (resource instanceof IResource) {
				IResource res = (IResource) resource;
				// name=res.getName(); // simple filename only, no path info
				IPath path = res.getProjectRelativePath();
				name = path.toString();
			}
			System.out.println("Cancelled by User, aborting analysis on subsequent files... " + name);
		}

		return foundError;
	}

}
