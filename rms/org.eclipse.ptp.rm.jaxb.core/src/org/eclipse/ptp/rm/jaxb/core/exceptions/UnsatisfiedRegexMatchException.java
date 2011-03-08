/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.core.exceptions;

public class UnsatisfiedRegexMatchException extends Exception {

	private static final long serialVersionUID = 4521238998263940220L;

	public UnsatisfiedRegexMatchException() {
	}

	public UnsatisfiedRegexMatchException(String message) {
		super(message);
	}

	public UnsatisfiedRegexMatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsatisfiedRegexMatchException(Throwable cause) {
		super(cause);
	}
}
