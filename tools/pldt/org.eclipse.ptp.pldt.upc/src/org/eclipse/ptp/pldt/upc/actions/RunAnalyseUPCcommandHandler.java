/**********************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.pldt.upc.actions;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.upc.UPCLanguage;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ptp.pldt.common.ScanReturn;
import org.eclipse.ptp.pldt.common.actions.RunAnalyseHandlerBase;
import org.eclipse.ptp.pldt.common.util.ViewActivater;
import org.eclipse.ptp.pldt.upc.UPCArtifactMarkingVisitor;
import org.eclipse.ptp.pldt.upc.UPCIDs;
import org.eclipse.ptp.pldt.upc.UPCPlugin;
import org.eclipse.ptp.pldt.upc.analysis.UPCCASTVisitor;

/**
 * @author tibbitts
 *
 */
public class RunAnalyseUPCcommandHandler extends RunAnalyseHandlerBase
{
	/**
	 * Constructor for the "Run Analysis" action
	 */
	public RunAnalyseUPCcommandHandler() {
		super("UPC", new UPCArtifactMarkingVisitor(UPCIDs.MARKER_ID), UPCIDs.MARKER_ID); //$NON-NLS-1$
	}

	/**
	 * Returns UPC analysis artifacts for file
	 * 
	 * @param file
	 * @param includes
	 *            UPC include paths
	 * @return
	 */

	public ScanReturn doArtifactAnalysis(final ITranslationUnit tu,	final List includes) {
		final ScanReturn msr = new ScanReturn();
		final String fileName = tu.getElementName();
		ILanguage lang;
		try {
			lang = tu.getLanguage();
            
			IASTTranslationUnit atu = tu.getAST();
			if (lang.getId().equals(UPCLanguage.ID)) {// cdt40
				atu.accept(new UPCCASTVisitor(includes, fileName, msr));
			} 

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msr;
	}

	/**
	 * Determination of if a given filename is valid for our artifact analysis
	 * @param filename
	 * @param isCPP  is the project a C++ project or not
	 * @return
	 */
	@Override
	protected boolean validForAnalysis(String filename, boolean isCPP) {
		int loc = filename.lastIndexOf(".");
		if (loc <= 0) // if no dot, or filename is ".foo", not valid for
						// analysis.
			return false;
		String ext = filename.substring(loc + 1);
		ext = ext.toLowerCase();
		boolean result = true;
		if (ext.equals("upc")) 
			result = true;
		else
			result = false;
		return result;
	}
	protected List getIncludePath() {
		return UPCPlugin.getDefault().getUPCIncludeDirs();
	}

	protected void activateArtifactView() {
		ViewActivater.activateView(UPCIDs.UPC_VIEW_ID);
	}
}
