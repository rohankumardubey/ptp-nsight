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
package org.eclipse.ptp.core;

public interface IPNode extends IPElement {
	/**
	 * Returns an array of all the Processes residing on this Node. May return <code>null</code> if there are none.
	 * 
	 * @return All Processes residing on this Node or <code>null</code> if there are none.
	 */
	public IPProcess[] getProcesses();
	/**
	 * Returns a sorted array of all the Processes residing on this Node. May return <code>null</code> if there are none. How the Processes are sorted is left open to the implementer of this interface.
	 * 
	 * @return All Processes residing on this Node, sorted or <code>null</code> if there are none.
	 */
	public IPProcess[] getSortedProcesses();
	/**
	 * Returns the node number of this Node as a String.
	 * 
	 * @return The node number of this Node as a String
	 */
	public String getNodeNumber();
	/**
	 * Finds a Process given a <code>processNumber</code> which resides on this Node. If found, the Process object is returned, else <code>null</code> is returned.
	 * 
	 * @param processNumber
	 *            The Process number to find
	 * @return The Process if found, else <code>null</code>
	 */
	public IPProcess findProcess(String processNumber);
	/**
	 * Returns the parent Machine that this Node is contained within.
	 * 
	 * @return The Machine this Node is part of
	 */
	public IPMachine getMachine();
	/**
	 * Returns an array of Jobs that have Processes running on this Node. May return <code>null</code> if there are none.
	 * 
	 * @return Array of Jobs that have Processes running on this Node.
	 */
	public IPJob[] getJobs();
	/**
	 * Sets an attribute on a Node with a key-value pair. If the key already exists the new value overwrites the old one.
	 * 
	 * @param key
	 *            The attribute key String
	 * @param val
	 *            The value to associate with <code>key</code>
	 */
	public void setAttrib(String key, Object val);
	/**
	 * Attempts to find an attribute on this Node by the given <code>key</code> and returns the associated value. If no key is found, <code>null</code> is returned.
	 * 
	 * @param key
	 *            The attribute key to search for
	 * @return The value assocaited with <code>key</code> or <code>null</code> if not found
	 */
	public Object getAttrib(String key);
	public void fireEvent(INodeEvent event);
	public void addNodeListener(INodeListener listener);
	public void removerNodeListener(INodeListener listener);
}
