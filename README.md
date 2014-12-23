# CSS and JS Compression via the YUI Compressor

## Description

Compresses and merges a set of CSS/JS files into one single CSS/JS file. Files will be sorted by names before merging.

Since enabled via a context parameter, you can easily distinguish between your development environment and your test or production one. 

If enabled, compression is launched at server startup.

## How-to

Create the file `WEB-INF/yuiCompressor.properties`, for example:

	yui.js.inputDir=/static/js
	yui.js.outputFile=/static/js/myapp-all.min.js
	yui.js.excludes=doNotMergeMe.js;mergeMeNeither.js
	
	yui.css.inputDir=/static/css
	yui.css.outputFile=/static/css/myapp-all.min.css
	yui.css.excludes=doNotMergeMe.css;mergeMeNeither.css

In your `web.xml`, add a context parameter and a listener:

	<context-param>
		<param-name>yuiCompressorEnabled</param-name>
		<!-- you may set this value depending on your Maven profile for example -->
		<param-value>true</param-value>
	</context-param>
	
	<listener>
		<listener-class>com.boogiedev.yui.YuiCompressorListener</listener-class>
	</listener>

In your views:

	<!-- CSS -->
	
	<c:choose>
		<c:when test="${initParam['yuiCompressorEnabled']}">
			<link href="<c:url value='/static/lib/jquery/jquery-ui.min.css' />" rel="stylesheet" />
			<link href="<c:url value='/static/css/myapp-all.min.css' />" rel="stylesheet" />
			<link href="<c:url value='/static/css/anotherSubdir/doNotMergeMe.css' />" rel="stylesheet" />
			<link href="<c:url value='/static/css/mergeMeNeither.css' />" rel="stylesheet" />
		</c:when>
		<c:otherwise>
			<link href="<c:url value='/static/lib/jquery/jquery-ui.css' />" rel="stylesheet" />
			<link href="<c:url value='/static/css/anySubdir/reset.css' />" rel="stylesheet" />
			<link href="<c:url value='/static/css/homepage.css' />" rel="stylesheet" />
			<link href="<c:url value='/static/css/account.css' />" rel="stylesheet" />
			<link href="<c:url value='/static/css/terms.css' />" rel="stylesheet" />
			<link href="<c:url value='/static/css/anotherSubdir/doNotMergeMe.css' />" rel="stylesheet" />
			<link href="<c:url value='/static/css/mergeMeNeither.css' />" rel="stylesheet" />
		</c:otherwise>
	</c:choose>
	
	<!-- JS -->
	
	<c:choose>
		<c:when test="${initParam['yuiCompressorEnabled']}">
			<script src="<c:url value='/static/lib/jquery/jquery.min.js' />"></script>
			<script src="<c:url value='/static/lib/jquery/jquery-ui.min.js' />"></script>
			<script src="<c:url value='/static/js/myapp-all.min.js' />"></script>
			<script src="<c:url value='/static/js/anotherSubdir/doNotMergeMe.js' />"></script>
			<script src="<c:url value='/static/js/mergeMeNeither.js' />"></script>
		</c:when>
		<c:otherwise>
			<script src="<c:url value='/static/lib/jquery/jquery.js' />"></script>
			<script src="<c:url value='/static/lib/jquery/jquery-ui.js' />"></script>
			<script src="<c:url value='/static/js/anySubdir/0-bootstrap.js' />"></script>
			<script src="<c:url value='/static/js/anySubdir/1-utils.js' />"></script>
			<script src="<c:url value='/static/js/homepage.js' />"></script>
			<script src="<c:url value='/static/js/account.js' />"></script>
			<script src="<c:url value='/static/js/anotherSubdir/doNotMergeMe.js' />"></script>
			<script src="<c:url value='/static/js/mergeMeNeither.js' />"></script>
		</c:otherwise>
	</c:choose>

Start your server.