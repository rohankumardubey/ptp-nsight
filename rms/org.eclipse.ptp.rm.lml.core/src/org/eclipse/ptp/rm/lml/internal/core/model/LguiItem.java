/**
 * Copyright (c) 2011 Forschungszentrum Juelich GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Claudia Knobloch, Carsten Karbach, FZ Juelich
 */
package org.eclipse.ptp.rm.lml.internal.core.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.ptp.rm.lml.core.JobStatusData;
import org.eclipse.ptp.rm.lml.core.LMLCorePlugin;
import org.eclipse.ptp.rm.lml.core.events.ILguiUpdatedEvent;
import org.eclipse.ptp.rm.lml.core.listeners.ILguiListener;
import org.eclipse.ptp.rm.lml.core.model.ILguiHandler;
import org.eclipse.ptp.rm.lml.core.model.ILguiItem;
import org.eclipse.ptp.rm.lml.internal.core.elements.CellType;
import org.eclipse.ptp.rm.lml.internal.core.elements.ColumnType;
import org.eclipse.ptp.rm.lml.internal.core.elements.LguiType;
import org.eclipse.ptp.rm.lml.internal.core.elements.ObjectFactory;
import org.eclipse.ptp.rm.lml.internal.core.elements.RequestType;
import org.eclipse.ptp.rm.lml.internal.core.elements.RowType;
import org.eclipse.ptp.rm.lml.internal.core.elements.TableType;
import org.eclipse.ptp.rm.lml.internal.core.events.LguiUpdatedEvent;

/**
 * Class of the interface ILguiItem
 */
public class LguiItem implements ILguiItem {

	/*
	 * Source of the XML-file from which the LguiType was generated.
	 */
	private String name;

	/*
	 * The generated LguiType
	 */
	private LguiType lgui;

	/**
	 * collects listeners, which listen for changes in model
	 */
	private final List<ILguiListener> listeners = new LinkedList<ILguiListener>();

	/**
	 * List of encapsulated classes, which handle parts of the lml-hierarchy
	 */
	private final Map<Class<? extends ILguiHandler>, ILguiHandler> lguiHandlers = Collections
			.synchronizedMap(new HashMap<Class<? extends ILguiHandler>, ILguiHandler>());

	/*
	 * Map containing jobs under our control
	 */
	private final Map<String, JobStatusData> fJobMap = Collections.synchronizedMap(new TreeMap<String, JobStatusData>());

	private static String lmlNamespace = "http://www.llview.de"; //$NON-NLS-1$

	/**
	 * Constructor with LML-model as argument
	 * 
	 * @param lgui
	 *            LML-model
	 */
	public LguiItem(LguiType lgui) {
		this.lgui = lgui;
		createLguiHandlers();
	}

	/**
	 * Empty Constructor.
	 */
	public LguiItem(String name) {
		this.name = name;
	}

	/**
	 * Constructor with one argument, an URI. Within the constructor the method
	 * for parsing an XML-file into LguiItem is called.
	 * 
	 * @param xmlFile
	 *            the source of the XML file.
	 */
	public LguiItem(URI xmlFile) {
		name = xmlFile.getPath();
		try {
			lgui = parseLML(xmlFile);
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		createLguiHandlers();
		setCid();
	}

	/**
	 * Add a lml-data-listener. It listens for data-changes.
	 * 
	 * @param listener
	 *            new listening instance
	 */
	public void addListener(ILguiListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rm.lml.core.model.ILguiItem#addUserJob(java.lang.String,
	 * org.eclipse.ptp.rm.lml.core.model.jobs.JobStatusData)
	 */
	public synchronized void addUserJob(String jobId, JobStatusData status, boolean force) {
		JobStatusData jobStatus = fJobMap.get(jobId);
		/*
		 * If the job already exists, do nothing
		 */
		if (jobStatus == null) {
			if (force) {
				String oid = getOverviewAccess().getOIDByJobId(jobId);
				if (oid == null) {
					TableType table = getTableHandler().getTable(getGidFromJobStatus(status));
					if (table != null) {
						oid = generateOid();
						status.setOid(oid);
						addJobToTable(table, oid, status);
					}
				}
			}
			fJobMap.put(jobId, status);
		}
	}

	public synchronized void getCurrentLayout(OutputStream output) {
		LguiType layoutLgui = null;
		if (lgui == null) {
			layoutLgui = firstRequest();
		} else {
			layoutLgui = getLayoutAccess().getLayoutFromModel();
		}
		final Marshaller marshaller = LMLCorePlugin.getDefault().getMarshaller();
		try {
			marshaller.setProperty("jaxb.schemaLocation", lmlNamespace + " lgui.xsd"); //$NON-NLS-1$ //$NON-NLS-2$
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			final QName tagname = new QName(lmlNamespace, "lgui", "lml"); //$NON-NLS-1$ //$NON-NLS-2$

			final JAXBElement<LguiType> rootElement = new JAXBElement<LguiType>(tagname, LguiType.class, layoutLgui);
			marshaller.marshal(rootElement, output);
			output.close(); // Must close to flush stream
		} catch (final PropertyException e) {
			e.printStackTrace();
		} catch (final JAXBException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return object to map component-ids to corresponding layout definitions
	 */
	public LayoutAccess getLayoutAccess() {
		if (lguiHandlers.get(LayoutAccess.class) == null) {
			return null;
		}
		return (LayoutAccess) lguiHandlers.get(LayoutAccess.class);
	}

	public synchronized LguiType getLguiType() {
		return lgui;
	}

	/**
	 * @return NodedisplayAccess-instance for accessing layouts of nodedisplays
	 */
	public NodedisplayAccess getNodedisplayAccess() {
		if (lguiHandlers.get(NodedisplayAccess.class) == null) {
			return null;
		}
		return (NodedisplayAccess) lguiHandlers.get(NodedisplayAccess.class);
	}

	/**
	 * @return a object, which saves which object has to be highlighted. All
	 *         user interactions are saved globally for all components in this
	 *         object.
	 */
	public ObjectStatus getObjectStatus() {
		if (lguiHandlers.get(ObjectStatus.class) == null) {
			return null;
		}
		return (ObjectStatus) lguiHandlers.get(ObjectStatus.class);
	}

	/**
	 * @return object for getting infos for objects
	 */
	public OIDToInformation getOIDToInformation() {
		if (lguiHandlers.get(OIDToInformation.class) == null) {
			return null;
		}
		return (OIDToInformation) lguiHandlers.get(OIDToInformation.class);
	}

	/**
	 * @return a class, which provides an index for fast access to objects
	 *         within the objects tag of LML. You can pass the id of the objects
	 *         to the returned object. It then returns the corresponding
	 *         objects.
	 */
	public OIDToObject getOIDToObject() {
		if (lguiHandlers.get(OIDToObject.class) == null) {
			return null;
		}
		return (OIDToObject) lguiHandlers.get(OIDToObject.class);
	}

	public OverviewAccess getOverviewAccess() {
		if (lguiHandlers.get(OverviewAccess.class) == null) {
			return null;
		}
		return (OverviewAccess) lguiHandlers.get(OverviewAccess.class);
	}

	public void getRequestXml(FileOutputStream output) {
		LguiType layoutLgui = null;
		if (lgui == null) {
			layoutLgui = firstRequest();
		} else {
			layoutLgui = getLayoutAccess().getLayoutFromModel();
		}
		final Marshaller marshaller = LMLCorePlugin.getDefault().getMarshaller();
		try {
			marshaller.setProperty("jaxb.schemaLocation", lmlNamespace + " lgui.xsd"); //$NON-NLS-1$//$NON-NLS-2$
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			final QName tagname = new QName(lmlNamespace, "lgui", "lml"); //$NON-NLS-1$ //$NON-NLS-2$

			final JAXBElement<LguiType> rootElement = new JAXBElement<LguiType>(tagname, LguiType.class, layoutLgui);
			marshaller.marshal(rootElement, output);
			output.close();
		} catch (final PropertyException e) {
			e.printStackTrace();
		} catch (final JAXBException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	public TableHandler getTableHandler() {
		if (lguiHandlers.get(TableHandler.class) == null) {
			return null;
		}
		return (TableHandler) lguiHandlers.get(TableHandler.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rm.lml.core.model.ILguiItem#getUserJob(java.lang.String)
	 */
	public synchronized JobStatusData getUserJob(String jobId) {
		JobStatusData status = fJobMap.get(jobId);
		if (status != null && !status.isRemoved()) {
			return status;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.lml.core.model.ILguiItem#getUserJobs()
	 */
	public synchronized JobStatusData[] getUserJobs() {
		return fJobMap.values().toArray(new JobStatusData[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.lml.core.elements.ILguiItem#getVersion()
	 */
	public synchronized String getVersion() {
		return lgui.getVersion();
	}

	public synchronized boolean isEmpty() {
		return lgui == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.lml.core.elements.ILguiItem#isLayout()
	 */
	public synchronized boolean isLayout() {
		return !isEmpty() && lgui.isLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.lml.core.model.ILguiItem#notifyListeners()
	 */
	public void notifyListeners() {
		final LguiUpdatedEvent event = new LguiUpdatedEvent(this);
		for (final ILguiListener l : listeners) {
			l.handleEvent(event);
		}
	}

	/**
	 * Remove a lml-data-listener.
	 * 
	 * @param listener
	 *            listening instance
	 */
	public void removeListener(ILguiListener listener) {
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rm.lml.core.model.ILguiItem#removeUserJob(java.lang.String
	 * )
	 */
	public synchronized void removeUserJob(String jobId) {
		JobStatusData status = fJobMap.get(jobId);
		if (status != null) {
			TableType table = getTableHandler().getTable(getGidFromJobStatus(status));
			if (table != null) {
				int index = -1;
				for (int i = 0; i < table.getRow().size(); i++) {
					RowType row = table.getRow().get(i);
					if (row != null && row.getOid().equals(status.getOid())) {
						index = i;
						break;
					}
				}
				if (index >= 0) {
					table.getRow().remove(index);
				}
			}
			status.setRemoved();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.lml.core.elements.ILguiItem#toString
	 */
	@Override
	public String toString() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rm.lml.core.model.ILguiItem#update(java.io.InputStream)
	 */
	public void update(InputStream stream) throws JAXBException {
		synchronized (this) {
			lgui = parseLML(stream);
		}
		if (listeners.isEmpty()) {
			createLguiHandlers();
		}
		synchronized (this) {
			setCid();
		}
		fireUpdatedEvent();
		synchronized (this) {
			setCid();
			updateJobData();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rm.lml.core.model.ILguiItem#updateUserJob(java.lang.String
	 * , java.lang.String, java.lang.String)
	 */
	public synchronized void updateUserJob(String jobId, String status, String detail) {
		JobStatusData jobStatus = fJobMap.get(jobId);
		if (jobStatus != null) {
			TableType table = getTableHandler().getTable(getGidFromJobStatus(jobStatus));
			if (table != null) {
				for (RowType row : table.getRow()) {
					if (row.getOid().equals(jobStatus.getOid())) {
						getTableHandler().setCellValue(table, row, JOB_STATUS, status);
					}
				}
			}
		}
	}

	private void addCellToRow(RowType row, ColumnType column, String value) {
		final CellType cell = new CellType();
		cell.setCid(column.getId());
		cell.setValue(value);
		row.getCell().add(cell);
	}

	private void addJobToTable(TableType table, String oid, JobStatusData status) {
		RowType row = new RowType();
		row.setOid(oid);

		for (final ColumnType column : table.getColumn()) {
			if (column.getName().equals(JOB_ID)) {
				addCellToRow(row, column, status.getJobId());
			} else if (column.getName().equals(JOB_STATUS)) {
				addCellToRow(row, column, status.getState());
			} else if (column.getName().equals(JOB_OWNER)) {
				addCellToRow(row, column, status.getOwner());
			} else if (column.getName().equals(JOB_QUEUE_NAME)) {
				addCellToRow(row, column, status.getQueueName());
			}
		}

		table.getRow().add(row);
	}

	/**
	 * The instance lgui is filled with a new data-model. This method creates
	 * all modules, which handle the data. These modules can then be accessed by
	 * corresponding getter-functions.
	 */
	private void createLguiHandlers() {
		lguiHandlers.put(OverviewAccess.class, new OverviewAccess(this, lgui));
		lguiHandlers.put(LayoutAccess.class, new LayoutAccess(this, lgui));
		lguiHandlers.put(OIDToObject.class, new OIDToObject(this, lgui));
		lguiHandlers.put(ObjectStatus.class, new ObjectStatus(this, lgui));
		lguiHandlers.put(OIDToInformation.class, new OIDToInformation(this, lgui));
		lguiHandlers.put(TableHandler.class, new TableHandler(this, lgui));
		lguiHandlers.put(NodedisplayAccess.class, new NodedisplayAccess(this, lgui));
	}

	private void fireUpdatedEvent() {
		final ILguiUpdatedEvent e = new LguiUpdatedEvent(this);
		for (final ILguiListener listener : listeners) {
			listener.handleEvent(e);
		}
	}

	private LguiType firstRequest() {
		final ObjectFactory objectFactory = new ObjectFactory();

		final LguiType layoutLgui = objectFactory.createLguiType();
		layoutLgui.setVersion("1"); //$NON-NLS-1$
		layoutLgui.setLayout(true);

		final RequestType request = objectFactory.createRequestType();
		request.setGetDefaultData(true);
		layoutLgui.setRequest(request);

		return layoutLgui;
	}

	private String generateOid() {
		return UUID.randomUUID().toString();
	}

	private String getGidFromJobStatus(JobStatusData status) {
		if (status.getState().equals(JobStatusData.RUNNING)) {
			return ACTIVE_JOB_TABLE;
		}
		return INACTIVE_JOB_TABLE;
	}

	/**
	 * Parsing an XML file. The method generates from an XML file an instance of
	 * LguiType.
	 * 
	 * @param stream
	 *            the input stream of the XML file
	 * @return the generated LguiType
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	private LguiType parseLML(InputStream stream) throws JAXBException {
		final Unmarshaller unmarshaller = LMLCorePlugin.getDefault().getUnmarshaller();
		final JAXBElement<LguiType> doc = (JAXBElement<LguiType>) unmarshaller.unmarshal(stream);
		return doc.getValue();
	}

	private void setCid() {
		for (final TableType table : getTableHandler().getTables()) {
			for (final RowType row : table.getRow()) {
				int cid = 1;
				for (final CellType cell : row.getCell()) {
					if (cell.getCid() == null) {
						cell.setCid(BigInteger.valueOf(cid));
					} else {
						cid = cell.getCid().intValue();
					}
					cid++;
				}
			}
		}
	}

	/**
	 * Update the job map with the new job data. On this refresh, the new job
	 * that was added to the table should have been "discovered" by the
	 * scheduler, so it should appear in one of the job tables. We need to find
	 * these jobs and update the OID and status information.
	 */
	private void updateJobData() {
		Set<JobStatusData> jobsInTable = new HashSet<JobStatusData>();
		List<RowType> rowsToRemove = new ArrayList<RowType>();

		/*
		 * First check for jobs that are in the table and update them
		 */
		for (TableType table : getTableHandler().getTables()) {
			for (RowType row : table.getRow()) {
				String jobId = getTableHandler().getCellValue(table, row, JOB_ID);
				if (jobId != null) {
					JobStatusData status = fJobMap.get(jobId);
					if (status != null) {
						jobsInTable.add(status);
						if (!status.isRemoved()) {
							/*
							 * job exists in both map and LML, so update the map
							 * with the oid and latest status
							 */
							status.setOid(row.getOid());
							status.setState(getTableHandler().getCellValue(table, row, JOB_STATUS));
						} else {
							/*
							 * job has been removed by the user. remove it from
							 * the table
							 */
							rowsToRemove.add(row);
						}
					}
				}
			}

			/*
			 * Remove any rows for removed jobs
			 */
			for (RowType row : rowsToRemove) {
				table.getRow().remove(row);
			}
			rowsToRemove.clear();
		}

		/*
		 * Next find any jobs that are no longer in the table. We need to create
		 * a "fake" entry in the jobslistwait table for these. Note that these
		 * jobs are now considered "COMPLETED".
		 */
		TableType table = getTableHandler().getTable(INACTIVE_JOB_TABLE);
		for (JobStatusData status : fJobMap.values()) {
			if (!status.isRemoved() && !jobsInTable.contains(status)) {
				status.setState(JobStatusData.COMPLETED);
				addJobToTable(table, status.getOid(), status);
			}
		}
	}

	/**
	 * Parsing an XML file. The method generates from an XML file an instance of
	 * LguiType.
	 * 
	 * @param xml
	 *            the URL source of the XML file
	 * @return the generated LguiType
	 * @throws MalformedURLException
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	private static LguiType parseLML(URI xml) throws MalformedURLException {
		LguiType lml = null;
		try {
			final Unmarshaller unmarshaller = LMLCorePlugin.getDefault().getUnmarshaller();

			final JAXBElement<LguiType> doc = (JAXBElement<LguiType>) unmarshaller.unmarshal(xml.toURL());

			lml = doc.getValue();
		} catch (final JAXBException e) {
			e.printStackTrace();
		}

		return lml;

	}

}