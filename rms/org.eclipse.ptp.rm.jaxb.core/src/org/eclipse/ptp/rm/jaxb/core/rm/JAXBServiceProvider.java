/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.core.rm;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.rm.jaxb.core.IJAXBNonNLSConstants;
import org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerConfiguration;
import org.eclipse.ptp.rm.jaxb.core.data.ResourceManagerData;
import org.eclipse.ptp.rm.jaxb.core.data.Site;
import org.eclipse.ptp.rm.jaxb.core.messages.Messages;
import org.eclipse.ptp.rm.jaxb.core.rmsystem.AbstractControlMonitorRMServiceProvider;
import org.eclipse.ptp.rm.jaxb.core.utils.JAXBInitializationUtils;
import org.eclipse.ptp.rm.jaxb.core.variables.RMVariableMap;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ptp.services.core.IServiceProviderWorkingCopy;

public class JAXBServiceProvider extends AbstractControlMonitorRMServiceProvider implements IJAXBResourceManagerConfiguration,
		IJAXBNonNLSConstants {

	private ResourceManagerData rmdata;
	private RMVariableMap map;
	private IRemoteServices service;

	public JAXBServiceProvider() {
		super();
		setDescription(Messages.JAXBServiceProvider_defaultDescription);
	}

	/**
	 * Constructor for creating a working copy of the service provider Don't
	 * register listeners as this copy will just be discarded at some point.
	 * 
	 * @param provider
	 *            provider we are making a copy from
	 */
	public JAXBServiceProvider(IServiceProvider provider) {
		super(provider);
	}

	public void clearReferences() {
		map.clear();
		map = null;
		rmdata = null;
		service = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.services.core.IServiceProvider#copy()
	 */
	@Override
	public IServiceProviderWorkingCopy copy() {
		return new JAXBServiceProvider(this);
	}

	public String getDefaultControlHost() {
		if (rmdata != null) {
			Site site = rmdata.getSite();
			URI defaultURI = null;
			if (site != null) {
				try {
					defaultURI = new URI(site.getControlConnection());
					if (defaultURI != null) {
						return defaultURI.getHost();
					}
				} catch (URISyntaxException t) {
					t.printStackTrace();
				}
			}
		}
		return ZEROSTR;
	}

	public String getDefaultControlPath() {
		if (rmdata != null) {
			Site site = rmdata.getSite();
			URI defaultURI = null;
			if (site != null) {
				try {
					defaultURI = new URI(site.getControlConnection());
					if (defaultURI != null) {
						return defaultURI.getPath();
					}
				} catch (URISyntaxException t) {
					t.printStackTrace();
				}
			}
		}
		return ZEROSTR;
	}

	public String getDefaultControlPort() {
		if (rmdata != null) {
			Site site = rmdata.getSite();
			URI defaultURI = null;
			if (site != null) {
				try {
					defaultURI = new URI(site.getControlConnection());
					if (defaultURI != null) {
						int p = defaultURI.getPort();
						if (p != -1) {
							return ZEROSTR + p;
						}
					}
				} catch (URISyntaxException t) {
					t.printStackTrace();
				}
			}
		}
		return ZEROSTR;
	}

	public String getDefaultMonitorHost() {
		if (rmdata != null) {
			Site site = rmdata.getSite();
			URI defaultURI = null;
			if (site != null) {
				try {
					defaultURI = new URI(site.getMonitorServerInstall());
					if (defaultURI != null) {
						return defaultURI.getHost();
					}
				} catch (URISyntaxException t) {
					t.printStackTrace();
				}
			}
		}
		return ZEROSTR;
	}

	public String getDefaultMonitorPath() {
		if (rmdata != null) {
			Site site = rmdata.getSite();
			URI defaultURI = null;
			if (site != null) {
				try {
					String uri = site.getMonitorServerInstall();
					if (uri != null && uri.length() > 0) {
						defaultURI = new URI(site.getMonitorServerInstall());
						return defaultURI.getPath();
					}
				} catch (URISyntaxException t) {
					t.printStackTrace();
				}
			}
		}
		return ZEROSTR;
	}

	public String getDefaultMonitorPort() {
		if (rmdata != null) {
			Site site = rmdata.getSite();
			URI defaultURI = null;
			if (site != null) {
				try {
					defaultURI = new URI(site.getMonitorServerInstall());
					int p = defaultURI.getPort();
					if (p != -1) {
						return ZEROSTR + p;
					}
				} catch (URISyntaxException t) {
					t.printStackTrace();
				}
			}
		}
		return ZEROSTR;
	}

	public String[] getExternalRMInstanceXMLLocations() {
		String list = getString(EXTERNAL_RM_XSD_PATHS, ZEROSTR);
		return list.split(CM);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rmsystem.AbstractResourceManagerServiceProvider#
	 * getResourceManagerId()
	 */
	@Override
	public String getResourceManagerId() {
		return getId();
	}

	public String getRMInstanceXMLLocation() {
		return getString(RM_XSD_PATH, ZEROSTR);
	}

	public String getSelectedAttributeSet() {
		return getString(SELECTED_ATTRIBUTES, null);
	}

	public IRemoteServices getService() {
		return service;
	}

	public String getValidAttributeSet() {
		return getString(VALID_ATTRIBUTES, null);
	}

	public void realizeRMDataFromXML() throws Throwable {
		String location = getRMInstanceXMLLocation();
		if (ZEROSTR.equals(location)) {
			rmdata = null;
		} else {
			rmdata = JAXBInitializationUtils.initializeRMData(location);
		}
	}

	public void removeSelectedAttributeSet() {
		keySet().remove(SELECTED_ATTRIBUTES);
	}

	public void removeValidAttributeSet() {
		keySet().remove(VALID_ATTRIBUTES);
	}

	public ResourceManagerData resourceManagerData() {
		return rmdata;
	}

	public void setActive() {
		map = RMVariableMap.setActiveInstance(map);
		if (!map.isInitialized()) {
			JAXBInitializationUtils.initializeMap(rmdata, map);
		}
	}

	public void setDefaultNameAndDesc() {
		String name = JAXB;
		String conn = getConnectionName();
		if (conn != null && !conn.equals(ZEROSTR)) {
			name += AMP + conn;
		}
		setName(name);
		setDescription(Messages.JAXBServiceProvider_defaultDescription);
	}

	public void setExternalRMInstanceXMLLocations(String[] locations) {
		if (locations == null || locations.length == 0) {
			putString(EXTERNAL_RM_XSD_PATHS, ZEROSTR);
		} else {
			StringBuffer list = new StringBuffer(locations[0]);
			for (int i = 1; i < locations.length; i++) {
				list.append(CM).append(locations[i]);
			}
			putString(EXTERNAL_RM_XSD_PATHS, list.toString());
		}
	}

	public void setRMInstanceXMLLocation(String location) {
		String current = getRMInstanceXMLLocation();
		if (!current.equals(location)) {
			putString(RM_XSD_PATH, location);
			clearRMData();
		}
	}

	public void setSelectedAttributeSet(String serialized) {
		putString(SELECTED_ATTRIBUTES, serialized);
	}

	public void setService(IRemoteServices service) {
		this.service = service;
	}

	public void setValidAttributeSet(String serialized) {
		putString(VALID_ATTRIBUTES, serialized);
	}

	@Override
	protected void clearRMData() {
		rmdata = null;
		setRemoteServicesId(null);
		setConnectionName(null);
		setConnectionName(CONTROL_CONNECTION_NAME, null);
		setConnectionName(MONITOR_CONNECTION_NAME, null);
		super.clearRMData();
	}
}
