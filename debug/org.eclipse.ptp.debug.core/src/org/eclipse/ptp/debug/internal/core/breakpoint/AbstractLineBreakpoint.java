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
package org.eclipse.ptp.debug.internal.core.breakpoint;

import java.util.Map;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ptp.debug.core.model.IPLineBreakpoint;

/**
 * @author Clement chu
 *
 */
public abstract class AbstractLineBreakpoint extends PBreakpoint implements IPLineBreakpoint {
	public AbstractLineBreakpoint() {
		super();
	}

	public AbstractLineBreakpoint(IResource resource, String markerType, Map attributes, boolean add) throws CoreException {
		super(resource, markerType, attributes, add);
	}

	public int getLineNumber() throws CoreException {
		return ensureMarker().getAttribute(IMarker.LINE_NUMBER, -1);
	}

	public int getCharStart() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_START, -1);
	}

	public int getCharEnd() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_END, -1);
	}
	
	public String getAddress() throws CoreException {
		return ensureMarker().getAttribute(IPLineBreakpoint.ADDRESS, "");
	}
	
	public String getFunction() throws CoreException {
		return ensureMarker().getAttribute(IPLineBreakpoint.FUNCTION, "");
	}
	
	public void setAddress(String address) throws CoreException {
		setAttribute(IPLineBreakpoint.ADDRESS, address);
	}
	
	public void setFunction(String function) throws CoreException {
		setAttribute(IPLineBreakpoint.FUNCTION, function);
	}
	
	public String getFileName() throws CoreException {
		String fileName = getSourceHandle();
		IPath path = new Path(fileName);
		return (path.isValidPath(fileName))?path.lastSegment():null;
	}
}
