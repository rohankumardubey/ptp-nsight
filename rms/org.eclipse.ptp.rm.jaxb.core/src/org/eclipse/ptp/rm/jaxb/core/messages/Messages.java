/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.core.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ptp.rm.jaxb.core.messages.messages"; //$NON-NLS-1$
	public static String JAXBCorePlugin_Exception_InternalError;
	public static String JAXBServiceProvider_defaultDescription;
	public static String JAXBServiceProvider_defaultName;
	public static String JAXBResourceManager_initError;
	public static String PropertyResolutionException0;
	public static String PropertyResolutionException1;
	public static String PropertyResolutionException2;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Prevent instances.
	}
}