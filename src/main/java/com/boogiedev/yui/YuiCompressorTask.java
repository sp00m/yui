/*
 * Copyright (c) boogiedev.com, all rights reserved.
 * This code is licensed under the LGPL 3.0 license,
 * available at the root application directory.
 */

package com.boogiedev.yui;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * YUI compressor Ant task.
 */
public class YuiCompressorTask extends Task {

	/** The directory containing the JS files to compress and merge. */
	private File jsInputDir;

	/** The file that will contain the final JS file. */
	private File jsOutputFile;

	/** The JS files names (or directories names) that must be excluded from the process, separated by ";". */
	private String jsExcludes;

	/** The directory containing the CSS files to compress and merge. */
	private File cssInputDir;

	/** The file that will contain the final CSS file. */
	private File cssOutputFile;

	/** The CSS files names (or directories names) that must be excluded from the process, separated by ";". */
	private String cssExcludes;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() throws BuildException {
		try {
			YuiCompressor yuiCompressor = new YuiCompressor(jsInputDir, jsOutputFile, jsExcludes, cssInputDir, cssOutputFile, cssExcludes);
			yuiCompressor.setLogger(new TaskLogger(this));
			yuiCompressor.compressAll();
		} catch (YuiCompressorException e) {
			throw new BuildException(e);
		}
	}

	/**
	 * Sets the directory containing the JS files to compress and merge.
	 *
	 * @param jsInputDir
	 *          The directory containing the JS files to compress and merge.
	 */
	public void setJsInputDir(String jsInputDir) {
		this.jsInputDir = new File(jsInputDir);
	}

	/**
	 * Sets the file that will contain the final JS file.
	 *
	 * @param jsOutputFile
	 *          The file that will contain the final JS file.
	 */
	public void setJsOutputFile(String jsOutputFile) {
		this.jsOutputFile = new File(jsOutputFile);
	}

	/**
	 * Sets the JS files names (or directories names) that must be excluded from the process, separated by ";".
	 *
	 * @param jsExcludes
	 *          The JS files names (or directories names) that must be excluded from the process, separated by ";".
	 */
	public void setJsExcludes(String jsExcludes) {
		this.jsExcludes = jsExcludes;
	}

	/**
	 * Sets the directory containing the CSS files to compress and merge.
	 *
	 * @param cssInputDir
	 *          The directory containing the CSS files to compress and merge.
	 */
	public void setCssInputDir(String cssInputDir) {
		this.cssInputDir = new File(cssInputDir);
	}

	/**
	 * Sets the file that will contain the final CSS file.
	 *
	 * @param cssOutputFile
	 *          The file that will contain the final CSS file.
	 */
	public void setCssOutputFile(String cssOutputFile) {
		this.cssOutputFile = new File(cssOutputFile);
	}

	/**
	 * Sets the CSS files names (or directories names) that must be excluded from the process, separated by ";".
	 *
	 * @param cssExcludes
	 *          The CSS files names (or directories names) that must be excluded from the process, separated by ";".
	 */
	public void setCssExcludes(String cssExcludes) {
		this.cssExcludes = cssExcludes;
	}

}
