# CSS and JS Compression via the YUI Compressor

## Description

Compresses and merges a set of CSS/JS files into one single CSS/JS file. Files will be sorted by names before merging.

## How-to

Build a JAR from these sources.

Create a target:

	<target name="yui-compress">
		<taskdef name="yuiCompress" classname="com.boogiedev.yui.YuiCompressorTask" classpath="ant/yui-1.1.jar" />
		<yuiCompress
			jsInputDir="${js.inputDir}" jsOutputFile="${js.outputFile}" jsExcludes="${js.excludes}"
			cssInputDir="${css.inputDir}" cssOutputFile="${css.outputFile}" cssExcludes="${css.excludes}" />
	</target>