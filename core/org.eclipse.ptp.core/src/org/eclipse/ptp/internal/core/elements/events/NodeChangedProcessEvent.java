/*******************************************************************************
 * Copyright (c) 2005, 2006, 2007 Los Alamos National Security, LLC.
 * This material was produced under U.S. Government contract DE-AC52-06NA25396
 * for Los Alamos National Laboratory (LANL), which is operated by the Los Alamos
 * National Security, LLC (LANS) for the U.S. Department of Energy.  The U.S. Government has
 * rights to use, reproduce, and distribute this software. NEITHER THE
 * GOVERNMENT NOR LANS MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR
 * ASSUMES ANY LIABILITY FOR THE USE OF THIS SOFTWARE. If software is modified
 * to produce derivative works, such modified software should be clearly marked,
 * so as not to confuse it with the version available from LANL.
 *
 * Additionally, this program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.ptp.internal.core.elements.events;

import java.util.Collection;

import org.eclipse.ptp.core.attributes.IAttribute;
import org.eclipse.ptp.core.elements.IPNode;
import org.eclipse.ptp.core.elements.IPProcess;
import org.eclipse.ptp.core.elements.events.INodeChangedProcessEvent;

/**
 * @author grw
 *
 */
public class NodeChangedProcessEvent implements INodeChangedProcessEvent {

	private final IPNode node;
	private final IPProcess process;
	private final Collection<IAttribute> attributes;
	
	public NodeChangedProcessEvent(IPNode node, IPProcess process, Collection<IAttribute> attrs) {
		this.node = node;
		this.process = process;
		this.attributes = attrs;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.events.INodeChangedProcessEvent#getAttributes()
	 */
	public Collection<IAttribute> getAttributes() {
		return attributes;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.events.INodeChangedProcessEvent#getProcess()
	 */
	public IPProcess getProcess() {
		return process;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.events.INodeChangedProcessEvent#getSource()
	 */
	public IPNode getSource() {
		return node;
	}

}
