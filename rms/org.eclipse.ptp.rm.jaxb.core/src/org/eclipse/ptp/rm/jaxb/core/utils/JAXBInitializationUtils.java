/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.ptp.rm.jaxb.core.IJAXBNonNLSConstants;
import org.eclipse.ptp.rm.jaxb.core.JAXBCorePlugin;
import org.eclipse.ptp.rm.jaxb.core.data.AttributeDefinitions;
import org.eclipse.ptp.rm.jaxb.core.data.Command;
import org.eclipse.ptp.rm.jaxb.core.data.Commands;
import org.eclipse.ptp.rm.jaxb.core.data.Control;
import org.eclipse.ptp.rm.jaxb.core.data.JobAttribute;
import org.eclipse.ptp.rm.jaxb.core.data.Parsers;
import org.eclipse.ptp.rm.jaxb.core.data.Property;
import org.eclipse.ptp.rm.jaxb.core.data.ResourceManagerData;
import org.eclipse.ptp.rm.jaxb.core.data.StreamParser;
import org.eclipse.ptp.rm.jaxb.core.variables.RMVariableMap;
import org.xml.sax.SAXException;

public class JAXBInitializationUtils implements IJAXBNonNLSConstants {

	private JAXBInitializationUtils() {
	}

	public static URL getURL(String name) throws IOException {
		URL instance = JAXBCorePlugin.getResource(name);
		if (instance == null) {
			File f = new File(name);
			if (f.exists() && f.isFile()) {
				instance = f.toURL();
			} else {
				throw new FileNotFoundException(name);
			}
		}
		return instance;
	}

	public static void initializeMap(ResourceManagerData rmData, RMVariableMap instance) {
		Control control = rmData.getControl();
		Map<String, Object> env = instance.getVariables();
		env.clear();
		addProperties(env, control);
		addAttributes(env, control);
		addCommands(env, control);
		addParsers(env, control);
		instance.getDiscovered().clear();
		instance.setInitialized(true);
	}

	public static ResourceManagerData initializeRMData(String xml) throws IOException, SAXException, URISyntaxException,
			JAXBException {
		URL instance = getURL(xml);
		return unmarshalResourceManagerData(instance);
	}

	public static void validate(String xml) throws SAXException, IOException, URISyntaxException {
		URL instance = getURL(xml);
		URL xsd = JAXBCorePlugin.getResource(RM_XSD);
		SchemaFactory factory = SchemaFactory.newInstance(XMLSchema);
		Schema schema = factory.newSchema(xsd);
		Validator validator = schema.newValidator();
		Source source = new StreamSource(instance.openStream());
		validator.validate(source);
	}

	private static void addAttributes(Map<String, Object> env, Control control) {
		AttributeDefinitions adefs = control.getAttributeDefinitions();
		if (adefs == null) {
			return;
		}
		List<JobAttribute> jobAttributes = adefs.getJobAttribute();
		for (JobAttribute jobAttribute : jobAttributes) {
			String name = jobAttribute.getName();
			env.put(name, jobAttribute);
		}
	}

	private static void addCommands(Map<String, Object> env, Control control) {
		Commands comms = control.getCommands();
		if (comms == null) {
			return;
		}
		List<Command> commands = comms.getCommand();
		for (Command command : commands) {
			env.put(command.getName(), command);
		}
	}

	private static void addParsers(Map<String, Object> env, Control control) {
		Parsers prsrs = control.getParsers();
		if (prsrs == null) {
			return;
		}
		List<StreamParser> parsers = prsrs.getStreamParser();
		for (StreamParser parser : parsers) {
			env.put(parser.getName(), parser);
		}
	}

	private static void addProperties(Map<String, Object> env, Control control) {
		List<Property> properties = control.getProperty();
		for (Property property : properties) {
			env.put(property.getName(), null);
		}
	}

	private static ResourceManagerData unmarshalResourceManagerData(URL xml) throws JAXBException, IOException {
		JAXBContext jc = JAXBContext.newInstance(JAXB_CONTEXT, JAXBInitializationUtils.class.getClassLoader());
		Unmarshaller u = jc.createUnmarshaller();
		ResourceManagerData rmdata = (ResourceManagerData) u.unmarshal(xml.openStream());
		return rmdata;
	}
}
