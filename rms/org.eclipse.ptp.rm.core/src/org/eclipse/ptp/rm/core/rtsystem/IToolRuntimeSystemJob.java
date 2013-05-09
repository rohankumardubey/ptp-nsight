/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.core.rtsystem;

/**
 * 
 * @author Daniel Felix Ferber
 *
 */
public interface IToolRuntimeSystemJob {
	/**
	 * Get the job ID for this job.
	 * 
	 * @return job id
	 */
	public String getJobID();

	/**
	 * Get the runtime system that launched this job.
	 * 
	 * @return runtime system
	 */
	public AbstractToolRuntimeSystem getRtSystem();
}