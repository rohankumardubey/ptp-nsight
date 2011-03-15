/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/

package org.eclipse.ptp.rm.jaxb.ui.launch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ptp.core.elements.IPQueue;
import org.eclipse.ptp.launch.ui.extensions.RMLaunchValidation;
import org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerControl;
import org.eclipse.ptp.rm.jaxb.core.data.TabController;
import org.eclipse.ptp.rm.jaxb.core.data.Widget;
import org.eclipse.ptp.rm.jaxb.core.variables.LTVariableMap;
import org.eclipse.ptp.rm.jaxb.core.variables.RMVariableMap;
import org.eclipse.ptp.rm.jaxb.ui.IJAXBUINonNLSConstants;
import org.eclipse.ptp.rm.jaxb.ui.JAXBUIPlugin;
import org.eclipse.ptp.rm.jaxb.ui.data.LaunchTabBuilder;
import org.eclipse.ptp.rm.jaxb.ui.dialogs.AttributeChoiceDialog;
import org.eclipse.ptp.rm.jaxb.ui.messages.Messages;
import org.eclipse.ptp.rm.jaxb.ui.util.WidgetActionUtils;
import org.eclipse.ptp.rm.jaxb.ui.util.WidgetBuilderUtils;
import org.eclipse.ptp.rm.ui.launch.BaseRMLaunchConfigurationDynamicTab;
import org.eclipse.ptp.rm.ui.launch.RMLaunchConfigurationDynamicTabDataSource;
import org.eclipse.ptp.rm.ui.launch.RMLaunchConfigurationDynamicTabWidgetListener;
import org.eclipse.ptp.rm.ui.utils.DataSource.ValidationException;
import org.eclipse.ptp.rmsystem.IResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * @author arossi
 * 
 */
public class JAXBRMConfigurableAttributesTab extends BaseRMLaunchConfigurationDynamicTab implements IJAXBUINonNLSConstants {

	private class JAXBUniversalDataSource extends RMLaunchConfigurationDynamicTabDataSource {

		protected JAXBUniversalDataSource(BaseRMLaunchConfigurationDynamicTab page) {
			super(page);
		}

		@Override
		protected void copyFromFields() throws ValidationException {
			// TODO Auto-generated method stub

		}

		@Override
		protected void copyToFields() {
			// TODO Auto-generated method stub

		}

		@Override
		protected void copyToStorage() {
			// TODO Auto-generated method stub

		}

		@Override
		protected void loadDefault() {
			// TODO Auto-generated method stub

		}

		/*
		 * The LTVariableMap is initialized from the active instance of the
		 * RMVariableMap once. Its values are updated from the most recent
		 * LaunchConfiguration here.
		 */
		@Override
		protected void loadFromStorage() {
			try {
				ILaunchConfiguration config = getConfiguration();
				if (config == null) {
					JAXBUIPlugin.log(Messages.MissingLaunchConfigurationError);
					return;
				}

				Map<?, ?> attrMap = config.getAttributes();
				LTVariableMap ltmap = LTVariableMap.getActiveInstance();
				Map<String, String> vars = ltmap.getVariables();
				Map<String, String> disc = ltmap.getDiscovered();
				Map<String, String> defaults = ltmap.getDefaults();
				for (Object k : attrMap.keySet()) {
					if (vars.containsKey(k)) {
						vars.put((String) k, (String) attrMap.get(k));
					} else if (disc.containsKey(k)) {
						disc.put((String) k, (String) attrMap.get(k));
					} else {

					}
				}

			} catch (Throwable t) {
				WidgetActionUtils.errorMessage(control.getShell(), t, Messages.ErrorOnLoadFromStore, Messages.ErrorOnLoadTitle,
						false);
			}
		}

		@Override
		protected void validateLocal() throws ValidationException {
		}
	}

	private class JAXBUniversalWidgetListener extends RMLaunchConfigurationDynamicTabWidgetListener {
		public JAXBUniversalWidgetListener(BaseRMLaunchConfigurationDynamicTab dynamicTab) {
			super(dynamicTab);
		}
	}

	private class SelectAttributesListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public synchronized void widgetSelected(SelectionEvent e) {
			try {
				Object source = e.getSource();
				if (source == selectAttributes) {
					buildMain(updateVisibleAttributes(true));
				} else if (source == viewScript) {

				}
			} catch (Throwable t) {
				WidgetActionUtils.errorMessage(control.getShell(), t, Messages.WidgetSelectedError,
						Messages.WidgetSelectedErrorTitle, false);
			}
		}
	}

	private final JAXBRMLaunchConfigurationDynamicTab pTab;
	private final TabController controller;
	private final Map<Control, Widget> valueWidgets;

	private AttributeChoiceDialog selectionDialog;

	private Composite dynamicControl;
	private Composite control;
	private final String title;
	private Button selectAttributes;
	private Button viewScript;

	private JAXBUniversalWidgetListener universalListener;
	private JAXBUniversalDataSource dataSource;

	private boolean loading;

	public JAXBRMConfigurableAttributesTab(IJAXBResourceManagerControl rm, ILaunchConfigurationDialog dialog,
			TabController controller, JAXBRMLaunchConfigurationDynamicTab pTab) {
		super(dialog);
		this.pTab = pTab;
		this.controller = controller;
		String t = controller.getTitle();
		if (t == null) {
			t = Messages.DefaultDynamicTab_title;
		}
		this.title = t;
		valueWidgets = new HashMap<Control, Widget>();
		createListener();
		createDataSource();
		try {
			pTab.getRmConfig().setActive();
			LTVariableMap.setActiveInstance(RMVariableMap.getActiveInstance());
		} catch (Throwable t1) {
			JAXBUIPlugin.log(t1);
		}
	}

	public void createControl(Composite parent, IResourceManager rm, IPQueue queue) throws CoreException {
		loading = true;
		control = WidgetBuilderUtils.createComposite(parent, 1);
		selectionDialog = new AttributeChoiceDialog(parent.getShell());

		if (controller.isDynamic()) {
			createDynamicSelectionGroup(control);
		} else if (pTab.hasScript()) {
			createViewScriptGroup(control);
		}
		try {
			buildMain(updateVisibleAttributes(false));
		} catch (Throwable t) {
			JAXBUIPlugin.log(t);
		}
		loading = false;
	}

	public Control getControl() {
		return control;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getText() {
		return title;
	}

	public RMLaunchValidation setDefaults(ILaunchConfigurationWorkingCopy configuration, IResourceManager rm, IPQueue queue) {
		/*
		 * Defaults are recorded in the LT map.
		 */
		return null;
	}

	@Override
	public void updateControls() {
		dataSource.loadFromStorage();
		dataSource.copyToFields();
	}

	@Override
	protected RMLaunchConfigurationDynamicTabDataSource createDataSource() {
		if (dataSource == null) {
			dataSource = new JAXBUniversalDataSource(this);
		}
		return dataSource;
	}

	@Override
	protected RMLaunchConfigurationDynamicTabWidgetListener createListener() {
		if (universalListener == null) {
			universalListener = new JAXBUniversalWidgetListener(this);
		}
		return universalListener;
	}

	private void buildMain(Map<String, Boolean> checked) {
		universalListener.disable();
		saveSettings();

		if (dynamicControl != null) {
			dynamicControl.dispose();
			valueWidgets.clear();
		}

		if (control.isDisposed()) {
			return;
		}

		dynamicControl = WidgetBuilderUtils.createComposite(control, 1);
		LaunchTabBuilder builder = new LaunchTabBuilder(controller, valueWidgets, checked);
		try {
			builder.build(dynamicControl);
		} catch (Throwable t) {
			JAXBUIPlugin.log(t);
		}

		/*
		 * We need to repeat this here (the ResourcesTab does it when it
		 * initially builds the control).
		 */
		pTab.resize(control);

		updateControls();
		universalListener.enable();
	}

	private void createDynamicSelectionGroup(Composite control) {
		GridLayout layout = WidgetBuilderUtils.createGridLayout(4, true);
		GridData gd = WidgetBuilderUtils.createGridDataFillH(4);
		Group grp = WidgetBuilderUtils.createGroup(control, SWT.NONE, layout, gd);
		WidgetBuilderUtils.createLabel(grp, Messages.ConfigureLaunchSettings, SWT.RIGHT, 1);
		selectAttributes = WidgetBuilderUtils.createPushButton(grp, Messages.SelectAttributesForDisplay,
				new SelectAttributesListener());
		if (pTab.hasScript()) {
			viewScript = WidgetBuilderUtils.createPushButton(grp, Messages.ViewScript, new SelectAttributesListener());
			WidgetBuilderUtils.createLabel(grp, Messages.ViewValuesReplaced, SWT.LEFT, 1);
		}
	}

	private void createViewScriptGroup(Composite control) {
		GridLayout layout = WidgetBuilderUtils.createGridLayout(2, true);
		GridData gd = WidgetBuilderUtils.createGridDataFillH(2);
		Group grp = WidgetBuilderUtils.createGroup(control, SWT.NONE, layout, gd);
		WidgetBuilderUtils.createLabel(grp, Messages.ViewValuesReplaced, SWT.RIGHT, 1);
		viewScript = WidgetBuilderUtils.createPushButton(grp, Messages.ViewScript, new SelectAttributesListener());
	}

	private void saveSettings() {
		if (loading) {
			return;
		}

		try {
			dataSource.copyFromFields();
			dataSource.copyToStorage();
		} catch (ValidationException t) {
			WidgetActionUtils.errorMessage(control.getShell(), t, Messages.ErrorOnSaveWidgets, Messages.ErrorOnSaveTitle, false);
		}
	}

	private Map<String, Boolean> updateVisibleAttributes(boolean showDialog) throws Throwable {
		Map<String, Boolean> checked = null;
		selectionDialog.clearChecked();
		Map<String, String> selected = pTab.getRmConfig().getSelectedAttributeSet();
		selectionDialog.setCurrentlyVisible(selected);
		if (!showDialog || Window.OK == selectionDialog.open()) {
			checked = selectionDialog.getChecked();
			selected.clear();
			Iterator<String> k = checked.keySet().iterator();
			if (k.hasNext()) {
				String key = k.next();
				if (checked.get(key)) {
					selected.put(key, key);
				} else {
					k.remove();
				}
			}
			while (k.hasNext()) {
				String key = k.next();
				if (checked.get(key)) {
					selected.put(key, key);
				} else {
					k.remove();
				}
			}
			pTab.getRmConfig().setSelectedAttributeSet(selected);
		}
		return checked;
	}
}
