<!DOCTYPE html>
<html lang="en">
        <meta charset="utf-8">
        <title>${(content.title)!config.site_title}</title>
 
        <meta name="viewport"    content="width=device-width, initial-scale=1.0">
        <meta name="description" content="${(content.description)!config.site_description}">
        <meta name="author"      content="${(content.author)!config.site_author}">
        <meta name="generator"   content="JBake">
        
        <!--Facebook Card-->
		<meta property="og:type"        content="article" />
		<meta property="og:title"       content="${(content.title)!config.site_title}"/>
		<meta property="og:description" content="${(content.description)!config.site_description}"/>
		<meta property="og:image"       content="${config.site_host}/${(content.shareimage)!(content.coverimage)!'img/yosemite.jpg'}"/>
        
        <!--Twitter card -->
        <#if (config.twitter_username??)>
        <meta name="twitter:card"        content="summary_large_image">
		<meta name="twitter:title"       content="${(content.title)!config.site_title}">
		<meta name="twitter:creator"     content="@${config.twitter_username}">
		<meta name="twitter:description" content="${(content.description)!config.site_description}"/>
		<meta name="twitter:image"       content="${config.site_host}/${(content.shareimage)!(content.coverimage)!'img/yosemite.jpg'}"/>    
        </#if>

        <!-- Bootstrap Core CSS -->
        <link href="${content.rootpath}vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

         <!-- Le styles -->
        <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/coderay.css" rel="stylesheet">
        <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/asciidoctor.css" rel="stylesheet">
        <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/clean-blog.css" rel="stylesheet">


        <!-- Custom Fonts -->
        <link href="${content.rootpath}vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <link href='https://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic' rel='stylesheet' type='text/css'>
        <link href='https://fonts.googleapis.com/css(family=Lato:300&amp;text=${(tag)!config.site_title}' rel='stylesheet' type='text/css'>
        <link href='https://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->

       <link rel="shortcut icon" href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>favicon.ico">
    </head>
 
<body>
    <div id="wrap">
   