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
/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.debug.external.cdi.model;

import java.math.BigInteger;

import org.eclipse.cdt.debug.core.cdi.CDIException;
import org.eclipse.cdt.debug.core.cdi.ICDILocator;
import org.eclipse.cdt.debug.core.cdi.model.ICDIArgumentDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDILocalVariableDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDIStackFrame;
import org.eclipse.cdt.debug.core.cdi.model.ICDIThread;
import org.eclipse.cdt.debug.core.cdi.model.ICDIValue;
import org.eclipse.ptp.debug.external.cdi.Locator;
import org.eclipse.ptp.debug.external.cdi.Session;
import org.eclipse.ptp.debug.external.cdi.VariableManager;
import org.eclipse.ptp.debug.external.simulator.SimStackFrame;

public class StackFrame extends PTPObject implements ICDIStackFrame {
	SimStackFrame frame;
	Thread cthread;
	int level;
	ICDIArgumentDescriptor[] argDescs;
	ICDILocalVariableDescriptor[] localDescs;
	Locator fLocator;

	public StackFrame(Thread thread, SimStackFrame f, int l) {
		super((Target)thread.getTarget());
		cthread = thread;
		frame = f;
		level = l;
	}

	public SimStackFrame getSimStackFrame() {
		return frame;
	}
	
	public ICDILocator getLocator() {
		// Auto-generated method stub
		System.out.println("StackFrame.getLocator()");
		BigInteger addr = BigInteger.ZERO;
		if (frame != null) {
			if (fLocator == null) {
				String address = frame.getAddress();
				if (address != null) {
					addr = new BigInteger(address);
				}
				fLocator = new Locator(frame.getFile(), 
					            frame.getFunction(),
					            frame.getLine(),  
								addr);
			}
			return fLocator;
		}
		return new Locator("", "", 0, addr); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public ICDIThread getThread() {
		return cthread;
	}

	public int getLevel() {
		return level;
	}

	public boolean equals(ICDIStackFrame stackframe) {
		// Auto-generated method stub
		System.out.println("StackFrame.equals()");
		if (stackframe instanceof StackFrame) {
			StackFrame stack = (StackFrame)stackframe;
			boolean equal =  cthread != null &&
				cthread.equals(stack.getThread()) &&
				getLevel() == stack.getLevel();
			if (equal) {
				ICDILocator otherLocator = stack.getLocator();
				ICDILocator myLocator = getLocator();
				if (Locator.equalString(myLocator.getFile(), otherLocator.getFile())) {
					if (Locator.equalString(myLocator.getFunction(), otherLocator.getFunction())) {
						return true;
					}
				}
			}
		}
		return super.equals(stackframe);
	}

	public void stepReturn() throws CDIException {
		// Auto-generated method stub
		System.out.println("StackFrame.stepReturn()");
	}

	public void stepReturn(ICDIValue value) throws CDIException {
		// Auto-generated method stub
		System.out.println("StackFrame.stepReturn()");
	}

	public ICDILocalVariableDescriptor[] getLocalVariableDescriptors() throws CDIException {
		// Auto-generated method stub
		System.out.println("StackFrame.getLocalVariableDescriptors");
        if (localDescs == null) {
                Session session = (Session) getTarget().getSession();
                VariableManager mgr = session.getVariableManager();
                localDescs = mgr.getLocalVariableDescriptors(this);
        }
        return localDescs;
	}

	public ICDIArgumentDescriptor[] getArgumentDescriptors() throws CDIException {
		// Auto-generated method stub
		System.out.println("StackFrame.getArgumentDescriptors");
        if (argDescs == null) {
                Session session = (Session)getTarget().getSession();
                VariableManager mgr = session.getVariableManager();
                argDescs = mgr.getArgumentDescriptors(this);
        }
        return argDescs;
	}
}
