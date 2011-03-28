/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/

package org.eclipse.ptp.rm.jaxb.ui.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ptp.rm.jaxb.ui.messages.messages"; //$NON-NLS-1$

	public static String HideResourceManagerEditorAction_error;
	public static String HideResourceManagerEditorAction_title;
	public static String OpenResourceManagerEditorAction_error;
	public static String OpenResourceManagerEditorAction_title;

	public static String ConfigUtils_exportResourceTitle;
	public static String ConfigUtils_exportResourceError_0;
	public static String ConfigUtils_exportResourceError_1;

	public static String JAXBUIPlugin_Exception_InternalError;
	public static String JAXBConnectionWizardPage_Description;
	public static String JAXBConfigurationWizardPage_Description;
	public static String JAXBRMConfigurationSelectionWizardPage_Title;
	public static String JAXBRMControlConfigurationWizardPage_Title;
	public static String JAXBRMMonitoringConfigurationWizardPage_Title;
	public static String JAXBRMConfigurationSelectionComboTitle_0;
	public static String JAXBRMConfigurationSelectionComboTitle_1;

	public static String JAXBRMConfigurationSelectionWizardPage_0;
	public static String JAXBRMConfigurationSelectionWizardPage_1;
	public static String JAXBRMConfigurationSelectionWizardPage_2;
	public static String JAXBRMConfigurationSelectionWizardPage_4;
	public static String JAXBRMConfigurationSelectionWizardPage_Project_Selection_Title;
	public static String JAXBRMConfigurationSelectionWizardPage_Project_Selection_Message;

	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_0;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_1;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_2;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_3;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_3b;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_4;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_5;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_6;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_7;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_8;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_9;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_10;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_11;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_12;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_13;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_14;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_15;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_16;
	public static String AbstractRemoteProxyResourceManagerConfigurationWizardPage_17;

	public static String ResourceManagerEditor_title;
	public static String CustomBatchScriptTab_title;
	public static String MXGroupTitle;
	public static String ManualLaunch;

	public static String SaveToFileButton;
	public static String ClearScript;
	public static String BatchScriptPath;
	public static String DefaultDynamicTab_title;
	public static String SelectAttributesForDisplay;
	public static String ViewScript;
	public static String ConfigureLaunchSettings;
	public static String ViewValuesReplaced;
	public static String AttributeName;
	public static String AttributeDescription;
	public static String ErrorOnLoadTitle;
	public static String ErrorOnLoadFromStore;
	public static String WidgetSelectedError;
	public static String WidgetSelectedErrorTitle;
	public static String MissingLaunchConfigurationError;
	public static String JAXBRMLaunchConfigurationFactory_doCreateError;

	public static String FileContentsDirty;
	public static String ErrorOnCopyToStorageTitle;
	public static String ErrorOnCopyToStorage;
	public static String RevertScript;
	public static String InvalidOptionIndex;
	public static String InvalidOption;
	public static String UnsupportedColumnDescriptor;
	public static String InvalidColumnName;
	public static String UnsupportedColumnType;
	public static String TreeDataLabelProviderColumnError;
	public static String TableDataLabelProviderColumnError;
	public static String RemoteConnectionSelection;
	public static String JAXBRMConnectionChoiceTitle;
	public static String ErrorOnCopyFromFields;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Prevent instances.
	}
}