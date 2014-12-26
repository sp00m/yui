/*
 * Copyright (c) boogiedev.com, all rights reserved.
 * This code is licensed under the LGPL 3.0 license,
 * available at the root application directory.
 */

package com.boogiedev.yui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * YUI compressor.
 */
public class YuiCompressor {

	/** Logger. */
	private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(YuiCompressor.class);

	/**
	 * Custom error reporter.
	 */
	private class YuiErrorReporter implements ErrorReporter {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
			logger.error(line < 0 ? message : line + ":" + lineOffset + ":" + message);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
			error(message, sourceName, line, lineSource, lineOffset);
			return new EvaluatorException(message);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
			logger.warn(line < 0 ? message : line + ":" + lineOffset + ":" + message);
		}

	}

	/** Position of the line break. */
	private static final int LINE_BREAK = 200;

	/** The CSS files names (or directories names) that must be excluded from the process. */
	private final List<String> cssExcludes;

	/** The CSS input directory, may be null. */
	private final File cssInputDir;

	/** The CSS input files directories. */
	private final List<File> cssInputDirs;

	/** The CSS input files to compress and merge. */
	private final List<File> cssInputFiles;

	/** The CSS final file. */
	private final File cssOutputFile;

	/** The JS files names (or directories names) that must be excluded from the process. */
	private final List<String> jsExcludes;

	/** The JS input directory, may be null. */
	private final File jsInputDir;

	/** The JS input files directories. */
	private final List<File> jsInputDirs;

	/** The JS input files to compress and merge. */
	private final List<File> jsInputFiles;

	/** The JS final file. */
	private final File jsOutputFile;

	/** Logger. */
	private Logger logger;

	/**
	 * Constructor.
	 *
	 * @param jsInputDir
	 *          The directory containing the JS files to compress and merge.
	 * @param jsOutputFile
	 *          The file that will contain the final JS file.
	 * @param jsExcludes
	 *          The JS files names (or directories names) that must be excluded from the process, separated by ";".
	 * @param cssInputDir
	 *          The directory containing the CSS files to compress and merge.
	 * @param cssOutputFile
	 *          The file that will contain the final CSS file.
	 * @param cssExcludes
	 *          The CSS files names (or directories names) that must be excluded from the process, separated by ";".
	 */
	public YuiCompressor(File jsInputDir, File jsOutputFile, String jsExcludes, File cssInputDir, File cssOutputFile, String cssExcludes) {
		this.jsInputDir = jsInputDir;
		this.jsOutputFile = jsOutputFile;
		jsInputFiles = new ArrayList<>();
		jsInputDirs = new ArrayList<>();
		this.jsExcludes = new ArrayList<>();
		if (!Strings.isNullOrEmpty(jsExcludes)) {
			this.jsExcludes.addAll(Arrays.asList(jsExcludes.split(";", -1)));
		}
		this.cssInputDir = cssInputDir;
		this.cssOutputFile = cssOutputFile;
		cssInputFiles = new ArrayList<>();
		cssInputDirs = new ArrayList<>();
		this.cssExcludes = new ArrayList<>();
		if (!Strings.isNullOrEmpty(cssExcludes)) {
			this.cssExcludes.addAll(Arrays.asList(cssExcludes.split(";", -1)));
		}
		logger = DEFAULT_LOGGER;
	}

	/**
	 * Launches JS and CSS compression.
	 *
	 * @throws YuiCompressorException
	 *           If an error occurred while compressing or merging.
	 */
	public void compressAll() throws YuiCompressorException {
		compressJs();
		compressCss();
	}

	/**
	 * Launches JS compression.
	 *
	 * @throws YuiCompressorException
	 *           If an error occurred while compressing or merging.
	 */
	public void compressJs() throws YuiCompressorException {
		if (jsInputDir != null && jsInputDir.exists()) {
			readDirectory(jsInputDir, jsInputFiles, jsInputDirs, jsExcludes, ".js");
		}
		if (shouldBeProcessed(jsInputFiles, jsOutputFile)) {
			List<File> compressedFiles = new ArrayList<>();
			for (File jsInputFile : jsInputFiles) {
				String compressedFilePath = jsInputFile.getAbsolutePath().replaceAll("(?i)\\.js$", ".min.js");
				File compressedFile = new File(compressedFilePath);
				try (Reader reader = new FileReader(jsInputFile); Writer writer = new FileWriter(compressedFile)) {
					JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new YuiErrorReporter());
					compressor.compress(writer, LINE_BREAK, true, true, true, false);
					compressedFiles.add(compressedFile);
				} catch (IOException e) {
					throw new YuiCompressorException("An error occurred while compressing " + jsInputFile, e);
				}
				logger.info("Compressed: " + compressedFile);
			}
			merge(compressedFiles, jsOutputFile);
			delete(jsInputFiles);
			clean(jsInputDirs);
		}
	}

	/**
	 * Launches CSS compression.
	 *
	 * @throws YuiCompressorException
	 *           If an error occurred while compressing or merging.
	 */
	public void compressCss() throws YuiCompressorException {
		if (cssInputDir != null && cssInputDir.exists()) {
			readDirectory(cssInputDir, cssInputFiles, cssInputDirs, cssExcludes, ".css");
		}
		if (shouldBeProcessed(cssInputFiles, cssOutputFile)) {
			List<File> compressedFiles = new ArrayList<>();
			for (File cssInputFile : cssInputFiles) {
				String compressedFilePath = cssInputFile.getAbsolutePath().replaceAll("(?i)\\.css$", ".min.css");
				File compressedFile = new File(compressedFilePath);
				try (Reader reader = new FileReader(cssInputFile); Writer writer = new FileWriter(compressedFile)) {
					CssCompressor compressor = new CssCompressor(reader);
					compressor.compress(writer, LINE_BREAK);
					compressedFiles.add(compressedFile);
				} catch (IOException e) {
					throw new YuiCompressorException("An error occurred while compressing " + cssInputFile, e);
				}
				logger.info("Compressed: " + compressedFile);
			}
			merge(compressedFiles, cssOutputFile);
			delete(cssInputFiles);
			clean(cssInputDirs);
		}
	}

	/**
	 * Recursively scans a directory:
	 * <ul>
	 * <li>Does not take into account the files whose names are contained in the excludes list.</li>
	 * <li>Deletes the files whose names match "*.min{suffix}".</li>
	 * <li>Stores the scanned files whose names match "*{suffix}" into the files list.</li>
	 * <li>Stores any scanned directory into the filesDirs list.</li>
	 * </ul>
	 *
	 * @param dir
	 *          The directory to scan.
	 * @param files
	 *          The scanned files whose names match "*{suffix}" will be added into this list.
	 * @param filesDirs
	 *          Any scanned directory will be added into this list.
	 * @param excludes
	 *          The files whose names are contained in this list won't be taken into account.
	 * @param suffix
	 *          The suffix of the files to process.
	 * @throws YuiCompressorException
	 *           If an error occurred while deleting a previously minified file.
	 */
	private void readDirectory(File dir, List<File> files, List<File> filesDirs, List<String> excludes, String suffix) throws YuiCompressorException {
		filesDirs.add(dir);
		for (File file : dir.listFiles()) {
			if (excludes.contains(file.getName())) {
				continue;
			}
			if (file.isDirectory()) {
				readDirectory(file, files, filesDirs, excludes, suffix);
			} else if (file.getName().endsWith(".min" + suffix)) {
				if (!file.delete()) {
					throw new YuiCompressorException("Unable to delete file " + file);
				}
			} else if (file.getName().matches(".*(?i)" + Pattern.quote(suffix))) {
				files.add(file);
			}
		}
	}

	/**
	 * Check whether it is necessary to process the input files or not, based on their last modified dates.
	 *
	 * @param inputFiles
	 *          The input files that may need to be processed.
	 * @param outputFile
	 *          The output file.
	 * @return false if the output file is more recent than all the input files, true otherwise.
	 */
	private boolean shouldBeProcessed(List<File> inputFiles, File outputFile) {
		return !inputFiles.isEmpty() && (outputFile == null || !outputFile.exists() || outputFile.lastModified() < findLatestLastModified(inputFiles));
	}

	/**
	 * Returns the latest last modified date of the given files.
	 *
	 * @param files
	 *          The files, cannot be empty ({@link IndexOutOfBoundsException} otherwise).
	 * @return The lasted last modified date.
	 */
	private long findLatestLastModified(List<File> files) {
		long latestLastModified = files.get(0).lastModified();
		for (int i = 1, n = files.size(); i < n; i++) {
			long lastModified = files.get(i).lastModified();
			if (lastModified > latestLastModified) {
				latestLastModified = lastModified;
			}
		}
		return latestLastModified;
	}

	/**
	 * Merges the input files into the output file:
	 * <ul>
	 * <li>Deletes the output file if it already exists.</li>
	 * <li>Sorts the input files by their names.</li>
	 * <li>Appends all the input files contents to the output file.</li>
	 * <li>Deletes the input files.</li>
	 * </ul>
	 *
	 * @param inputFiles
	 *          The input files to merge into the output file.
	 * @param outputFile
	 *          The output file.
	 * @throws YuiCompressorException
	 *           If an error occurred while merging compressed files.
	 */
	private void merge(List<File> inputFiles, File outputFile) throws YuiCompressorException {
		if (outputFile != null) {
			if (outputFile.exists() && !outputFile.delete()) {
				throw new YuiCompressorException("Unable to delete file " + outputFile);
			}
			try (OutputStream outputStream = new FileOutputStream(outputFile, true)) {
				Collections.sort(inputFiles, new Comparator<File>() {

					@Override
					public int compare(File o1, File o2) {
						return o1.getName().compareTo(o2.getName());
					}

				});
				for (File inputFile : inputFiles) {
					try (InputStream inputStream = new FileInputStream(inputFile)) {
						ByteStreams.copy(inputStream, outputStream);
					}
				}
				logger.info("Merged: " + outputFile);
			} catch (IOException e) {
				throw new YuiCompressorException("An error occurred while merging files", e);
			}
			delete(inputFiles);
		}
	}

	/**
	 * Deletes files.
	 *
	 * @param files
	 *          The files to delete.
	 * @throws YuiCompressorException
	 *           If an error occurred while deleting a file.
	 */
	private void delete(List<File> files) throws YuiCompressorException {
		for (File file : files) {
			if (!file.delete()) {
				throw new YuiCompressorException("Unable to delete file " + file);
			}
		}
	}

	/**
	 * Cleans directories:
	 * <ul>
	 * <li>Deletes a directory only if it is empty.</li>
	 * <li>Sorts them so that the nested ones will be deleted before their parents.</li>
	 * </ul>
	 *
	 * @param dirs
	 *          The directories to delete.
	 * @throws YuiCompressorException
	 *           If an error occurred while deleting a directory.
	 */
	private void clean(List<File> dirs) throws YuiCompressorException {
		if (!dirs.isEmpty()) {
			Collections.sort(dirs, new Comparator<File>() {

				@Override
				public int compare(File o1, File o2) {
					return o2.getAbsolutePath().compareTo(o1.getAbsolutePath());
				}

			});
			for (File dir : dirs) {
				if (dir.list().length == 0 && !dir.delete()) {
					throw new YuiCompressorException("Unable to delete dir " + dir);
				}
			}
		}
	}

	/**
	 * Sets the logger to use.
	 *
	 * @param logger
	 *          The logger to use.
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

}
