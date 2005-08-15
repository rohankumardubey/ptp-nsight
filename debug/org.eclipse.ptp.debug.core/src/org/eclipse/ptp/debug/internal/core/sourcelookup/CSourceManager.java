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
 * Copyright (c) 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.debug.internal.core.sourcelookup;

import org.eclipse.cdt.debug.core.model.ICStackFrame;
import org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocation;
import org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.ptp.debug.internal.core.model.PDebugTarget;

/**
 * Locates sources for a C/C++ debug session.
 */
public class CSourceManager implements ICSourceLocator, IPersistableSourceLocator, IAdaptable {

	private ISourceLocator fSourceLocator = null;

	private ILaunch fLaunch = null;

	private PDebugTarget fDebugTarget = null;

	/**
	 * Constructor for CSourceManager.
	 */
	public CSourceManager( ISourceLocator sourceLocator ) {
		setSourceLocator( sourceLocator );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocator#getLineNumber(IStackFrameInfo)
	 */
	public int getLineNumber( IStackFrame frame ) {
		if ( getCSourceLocator() != null ) {
			return getCSourceLocator().getLineNumber( frame );
		}
		if ( frame instanceof ICStackFrame ) {
			return ((ICStackFrame)frame).getFrameLineNumber();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocator#getSourceLocations()
	 */
	public ICSourceLocation[] getSourceLocations() {
		return (getCSourceLocator() != null) ? getCSourceLocator().getSourceLocations() : new ICSourceLocation[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocator#setSourceLocations(ICSourceLocation[])
	 */
	public void setSourceLocations( ICSourceLocation[] locations ) {
		if ( getCSourceLocator() != null ) {
			getCSourceLocator().setSourceLocations( locations );
			PDebugTarget target = getDebugTarget();
			if ( target != null ) {
				// FIXME Donny
/*				Disassembly d = null;
				try {
					d = (Disassembly)target.getDisassembly();
				}
				catch( DebugException e ) {
				}
				if ( d != null ) {
					d.reset();
				}
*/			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocator#contains(IResource)
	 */
	public boolean contains( IResource resource ) {
		return (getCSourceLocator() != null) ? getCSourceLocator().contains( resource ) : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter( Class adapter ) {
		if ( adapter.equals( CSourceManager.class ) )
			return this;
		if ( adapter.equals( ICSourceLocator.class ) )
			return this;
		if ( adapter.equals( IPersistableSourceLocator.class ) )
			return this;
		if ( adapter.equals( IResourceChangeListener.class ) && fSourceLocator instanceof IResourceChangeListener )
			return fSourceLocator;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISourceLocator#getSourceElement(IStackFrame)
	 */
	public Object getSourceElement( IStackFrame stackFrame ) {
		Object result = null;
		if ( getSourceLocator() != null )
			result = getSourceLocator().getSourceElement( stackFrame );
		return result;
	}

	protected ICSourceLocator getCSourceLocator() {
		if ( getSourceLocator() instanceof ICSourceLocator )
			return (ICSourceLocator)getSourceLocator();
		return null;
	}

	protected ISourceLocator getSourceLocator() {
		if ( fSourceLocator != null )
			return fSourceLocator;
		else if ( fLaunch != null )
			return fLaunch.getSourceLocator();
		return null;
	}

	private void setSourceLocator( ISourceLocator sl ) {
		fSourceLocator = sl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocator#findSourceElement(String)
	 */
	public Object findSourceElement( String fileName ) {
		if ( getCSourceLocator() != null ) {
			return getCSourceLocator().findSourceElement( fileName );
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#getMemento()
	 */
	public String getMemento() throws CoreException {
		if ( getPersistableSourceLocator() != null )
			return getPersistableSourceLocator().getMemento();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#initializeDefaults(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeDefaults( ILaunchConfiguration configuration ) throws CoreException {
		if ( getPersistableSourceLocator() != null )
			getPersistableSourceLocator().initializeDefaults( configuration );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#initializeFromMemento(java.lang.String)
	 */
	public void initializeFromMemento( String memento ) throws CoreException {
		if ( getPersistableSourceLocator() != null )
			getPersistableSourceLocator().initializeFromMemento( memento );
	}

	private IPersistableSourceLocator getPersistableSourceLocator() {
		if ( fSourceLocator instanceof IPersistableSourceLocator )
			return (IPersistableSourceLocator)fSourceLocator;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocator#getProject()
	 */
	public IProject getProject() {
		return (getCSourceLocator() != null) ? getCSourceLocator().getProject() : null;
	}

	public void setDebugTarget( PDebugTarget target ) {
		fDebugTarget = target;
	}

	protected PDebugTarget getDebugTarget() {
		return fDebugTarget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocator#setSearchForDuplicateFiles(boolean)
	 */
	public void setSearchForDuplicateFiles( boolean search ) {
		if ( getCSourceLocator() != null )
			getCSourceLocator().setSearchForDuplicateFiles( search );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.debug.core.sourcelookup.ICSourceLocator#searchForDuplicateFiles()
	 */
	public boolean searchForDuplicateFiles() {
		return (getCSourceLocator() != null) ? getCSourceLocator().searchForDuplicateFiles() : false;
	}
}
