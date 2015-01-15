# CSS and JS Compression via the YUI Compressor

## Description

Compresses and merges a set of CSS/JS files into one single CSS/JS file. Files will be sorted by names before merging.

## How-to

### From Java

	File jsInputDir = new File("/path/to/js/input/dir");
	File jsOutputFile = new File("/path/to/js/output/file.js");
	String jsExcludes = "jsFileToKeepAsIs.js;anotherFileToExclude.js";
	
	File cssInputDir = new File("/path/to/css/input/dir");
	File cssOutputFile = new File("/path/to/css/output/file.js");
	String cssExcludes = "cssFileToKeepAsIs.css;anotherFileToExclude.css";
	
	YuiCompressor yuiCompressor = new YuiCompressor(jsInputDir, jsOutputFile, jsExcludes, cssInputDir, cssOutputFile, cssExcludes);
	yuiCompressor.compressAll();

### From Ant

Build a JAR from the sources, and create the following target:

	<target name="yui-compress">
		<taskdef name="yuiCompress" classname="com.boogiedev.yui.YuiCompressorTask" classpath="boogiedev-yui-1.2.jar" />
		<yuiCompress
			jsInputDir="${js.inputDir}" jsOutputFile="${js.outputFile}" jsExcludes="${js.excludes}"
			cssInputDir="${css.inputDir}" cssOutputFile="${css.outputFile}" cssExcludes="${css.excludes}" />
	</target>