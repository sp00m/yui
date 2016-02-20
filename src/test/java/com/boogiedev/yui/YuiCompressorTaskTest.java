/*
 * Copyright (c) boogiedev.com, all rights reserved.
 * This code is licensed under the LGPL 3.0 license,
 * available at the root application directory.
 */

package com.boogiedev.yui;

import java.io.File;

import junitx.framework.FileAssert;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

/**
 * Tests the YUI compressor.
 */
public class YuiCompressorTaskTest {

	/** JS input files names to exclude. */
	private static final String JS_EXCLUDES = "1-exclude.js; 3-exclude.js";

	/** CSS input files names to exclude. */
	private static final String CSS_EXCLUDES = "1-exclude.css; 3-exclude.css";

	/** JS input files directory. */
	private File jsInputDir;

	/** CSS input files directory. */
	private File cssInputDir;

	/**
	 * Sets the JS and CSS input files directories.
	 */
	@Before
	public void setup() {
		jsInputDir = getFile("/js");
		cssInputDir = getFile("/css");
	}

	/**
	 * Compresses JS and CSS input files, taking into account the excludes.
	 *
	 * @throws Exception
	 *           If an error occurred while compressing.
	 */
	@Test
	public void compressWithExcludes() throws Exception {
		compress(true, "compressWithExcludes");
	}

	/**
	 * Compresses JS and CSS input files, taking no account of the excludes.
	 *
	 * @throws Exception
	 *           If an error occurred while compressing.
	 */
	@Test
	public void compressWithoutExcludes() throws Exception {
		compress(false, "compressWithoutExcludes");
	}

	/**
	 * Compresses JS and CSS input files, and compares the output files with the expected ones.
	 *
	 * @param withExcludes
	 *          If true, {@link #JS_EXCLUDES} and {@link #CSS_EXCLUDES} will be taken into account.
	 * @param expectedFileName
	 *          The expected file name (without extension).
	 * @throws Exception
	 *           If an error occurred while compressing.
	 */
	private void compress(boolean withExcludes, String expectedFileName) throws Exception {

		YuiCompressorTask compressor = new YuiCompressorTask();

		File jsInputDir = Files.createTempDir();
		copyDir(this.jsInputDir, jsInputDir);
		File jsOutputFile = File.createTempFile("yui", "tmp");
		jsOutputFile.delete();
		compressor.setJsInputDir(jsInputDir.getAbsolutePath());
		compressor.setJsOutputFile(jsOutputFile.getAbsolutePath());
		if (withExcludes) {
			compressor.setJsExcludes(JS_EXCLUDES);
		}

		File cssInputDir = Files.createTempDir();
		copyDir(this.cssInputDir, cssInputDir);
		File cssOutputFile = File.createTempFile("yui", "tmp");
		cssOutputFile.delete();
		compressor.setCssInputDir(cssInputDir.getAbsolutePath());
		compressor.setCssOutputFile(cssOutputFile.getAbsolutePath());
		if (withExcludes) {
			compressor.setCssExcludes(CSS_EXCLUDES);
		}

		compressor.execute();

		FileAssert.assertEquals(getFile("/" + expectedFileName + ".js"), jsOutputFile);
		FileAssert.assertEquals(getFile("/" + expectedFileName + ".css"), cssOutputFile);

		if (withExcludes) {
			Assert.assertTrue(jsInputDir.exists());
			Assert.assertTrue(cssInputDir.exists());
		} else {
			Assert.assertTrue(!jsInputDir.exists());
			Assert.assertTrue(!cssInputDir.exists());
		}

	}

	/**
	 * Returns the file denoted by a path (see {@link Class#getResource(String)}).
	 *
	 * @param path
	 *          The path.
	 * @return The file.
	 */
	private File getFile(String path) {
		return new File(getClass().getResource(path).getPath());
	}

	/**
	 * Copies the content of a directory to another directory.
	 *
	 * @param from
	 *          The source directory.
	 * @param to
	 *          The destination directory.
	 * @throws Exception
	 *           If an error occurred while copying.
	 */
	private static void copyDir(File from, File to) throws Exception {
		for (File fromChild : from.listFiles()) {
			File toChild = new File(to, fromChild.getName());
			if (fromChild.isDirectory()) {
				toChild.mkdirs();
				toChild.mkdir();
				copyDir(fromChild, toChild);
			} else {
				toChild.createNewFile();
				Files.copy(fromChild, toChild);
			}
		}
	}

}
