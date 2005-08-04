package org.eclipse.ptp.debug.core.cdi.model;

import org.eclipse.cdt.debug.core.cdi.CDIException;
import org.eclipse.cdt.debug.core.cdi.model.ICDITarget;
import org.eclipse.cdt.debug.core.cdi.model.ICDIThread;

public interface IPCDITarget extends ICDITarget {
	
	public Process[] getProcesses();
	public Process getProcess(int num);
	
	public ICDIThread[] getThreads(int procNumber) throws CDIException;
}
