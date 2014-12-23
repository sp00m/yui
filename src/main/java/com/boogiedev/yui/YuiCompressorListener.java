/*
 * Copyright (c) boogiedev.com, all rights reserved.
 * This code is licensed under the LGPL 3.0 license,
 * available at the root application directory.
 */

package com.boogiedev.yui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * YUI context listener.
 */
public class YuiCompressorListener implements ServletContextListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// no-op
	}

	/**
	 * If the context has an init parameter set to true, launches the compression process.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		if (Boolean.valueOf(context.getInitParameter("yuiCompressorEnabled"))) {
			Properties properties = toProperties(context, "/WEB-INF/yuiCompressor.properties");
			File jsInputDir = toFile(context, properties.getProperty("yui.js.inputDir"));
			File jsOutputFile = toFile(context, properties.getProperty("yui.js.outputFile"));
			String jsExcludes = properties.getProperty("yui.js.excludes");
			File cssInputDir = toFile(context, properties.getProperty("yui.css.inputDir"));
			File cssOutputFile = toFile(context, properties.getProperty("yui.css.outputFile"));
			String cssExcludes = properties.getProperty("yui.css.excludes");
			YuiCompressor yuiCompressor = new YuiCompressor(jsInputDir, jsOutputFile, jsExcludes, cssInputDir, cssOutputFile, cssExcludes);
			yuiCompressor.compressAll();
		}
	}

	/**
	 * Returns a file instance, or null if path is null.
	 *
	 * @param context
	 *          The servlet context.
	 * @param path
	 *          The relative path.
	 * @return The file instance.
	 */
	private static File toFile(ServletContext context, String path) {
		if (path == null) {
			return null;
		}
		return new File(context.getRealPath(path));
	}

	/**
	 * Builds properties from path.
	 *
	 * @param context
	 *          The servlet context.
	 * @param path
	 *          The relative path.
	 * @return The properties.
	 */
	private static Properties toProperties(ServletContext context, String path) {
		Properties properties = new Properties();
		try (InputStream inputStream = context.getResourceAsStream(path)) {
			properties.load(inputStream);
		} catch (IOException e) {
			throw new YuiCompressorException(e);
		}
		return properties;
	}

}
