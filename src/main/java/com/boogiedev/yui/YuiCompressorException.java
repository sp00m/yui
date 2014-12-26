/*
 * Copyright (c) boogiedev.com, all rights reserved.
 * This code is licensed under the LGPL 3.0 license,
 * available at the root application directory.
 */

package com.boogiedev.yui;

/**
 * YUI compressor exception.
 */
public class YuiCompressorException extends Exception {

	/** UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message
	 *          The message.
	 */
	public YuiCompressorException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param cause
	 *          The cause.
	 */
	public YuiCompressorException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 *
	 * @param message
	 *          The message.
	 * @param cause
	 *          The cause.
	 */
	public YuiCompressorException(String message, Throwable cause) {
		super(message, cause);
	}

}
