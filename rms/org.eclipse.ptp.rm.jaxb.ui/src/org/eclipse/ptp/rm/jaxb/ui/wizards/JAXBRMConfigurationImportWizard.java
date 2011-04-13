/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.ui.wizards;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ptp.rm.jaxb.ui.IJAXBUINonNLSConstants;
import org.eclipse.ptp.rm.jaxb.ui.JAXBUIPlugin;
import org.eclipse.ptp.rm.jaxb.ui.messages.Messages;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * For importing XML configurations from the installed plugins to the workspace
 * for possible editing and export.
 * 
 * @author arossi
 * 
 */
public class JAXBRMConfigurationImportWizard extends Wizard implements IImportWizard, IJAXBUINonNLSConstants {

	private JAXBRMConfigurationImportWizardPage mainPage;

	public JAXBRMConfigurationImportWizard() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(mainPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.ConfigurationImportWizardTitle);
		setNeedsProgressMonitor(true);
		mainPage = new JAXBRMConfigurationImportWizardPage(Messages.ConfigurationImportWizardPageTitle);
		mainPage.loadConfigurations();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		URL selection = mainPage.getSelectedConfiguration();
		if (selection != null) {
			String name = mainPage.getSelectedName();
			File newConfig = new File(resourceManagersDir(), name + DOT_XML);
			BufferedReader br = null;
			FileWriter fw = null;
			try {
				fw = new FileWriter(newConfig, false);
				br = new BufferedReader(new InputStreamReader(selection.openStream()));
				while (true) {
					try {
						String line = br.readLine();
						if (null == line) {
							break;
						}
						fw.write(line);
						fw.write(LINE_SEP);
					} catch (EOFException eof) {
						break;
					} finally {
						fw.flush();
					}
				}
			} catch (IOException io) {
				JAXBUIPlugin.log(io);
			} finally {
				try {
					if (fw != null) {
						fw.close();
					}
					if (br != null) {
						br.close();
					}
				} catch (IOException io) {
				}
			}
		}
		return true;
	}

	/*
	 * By convention, "resourceManagers" project in the user's workspace.
	 */
	private static File resourceManagersDir() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(RESOURCE_MANAGERS);
		if (project != null) {
			IPath path = project.getLocation();
			if (path != null) {
				File dir = path.toFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}
				return dir;
			}
		}
		return null;
	}
}