		</div>
		<div id="push"></div>
    </div>
    
     <div class="container">
                <div class="row">
                    <footer class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                        <ul class="list-inline text-center"> 
                            <#if (config.render_feed??)>
                                
                            </#if>
                            <li th:if="">
                                <a href="${content.rootpath}${config.feed_file}" title="Subscribe to RSS feed">
                                    <span class="fa-stack fa-lg">
                                        <i class="fa fa-circle fa-stack-2x"></i>
                                        <i class="fa fa-rss fa-stack-1x fa-inverse"></i>
                                    </span>
                                </a>
                            </li>

                            <#if (config.twitter_username??)>
                            <li> 
                                <a href="https://twitter.com/${config.twitter_username}">
                                    <span class="fa-stack fa-lg">
                                        <i class="fa fa-circle fa-stack-2x"></i>
                                        <i class="fa fa-twitter fa-stack-1x fa-inverse"></i>
                                    </span>
                                </a>
                            </li>
                            </#if>
                            
                            <#if (config.github_username??)>
                            <li>
                                <a href="https://github.com/${config.github_username}">
                                    <span class="fa-stack fa-lg">
                                        <i class="fa fa-circle fa-stack-2x"></i>
                                        <i class="fa fa-github fa-stack-1x fa-inverse"></i>
                                    </span>
                                </a>
                            </li>    
                            </#if>

                            <#if (config.linkedin_username??)>
                            <li>
                                <a href="https://linkedin.com/in/${config.linkedin_username}/">
                                    <span class="fa-stack fa-lg">
                                        <i class="fa fa-circle fa-stack-2x"></i>
                                        <i class="fa fa-linkedin fa-stack-1x fa-inverse"></i>
                                    </span>
                                </a>
                            </li>    
                            </#if>
                            
                        </ul>
                        <div class="copyright text-muted">&copy; ${config.site_author} ${.now?string('YYYY')}</div>
                    </footer>
                </div>
        </div>
    

     <script th:replace="snippets.thyme::googleAnalytics" />
       
       <#if (config.google_trackingID??)>
       <script>
            (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
            })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
            ga('create', '${config.google_trackingID}', 'auto');
            ga('send', 'pageview', {
            'page': '${content.noExtensionUri!(content.uri!config.site_host)}',
            'title': '${content.title!config.site_title}'
            });
        </script>
       </#if>
       
    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>vendor/jquery/jquery.min.js"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>vendor/bootstrap/js/bootstrap.min.js"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>/js/prettify.js"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>/js/clean-blog.min.js"></script>
    
  </body>
</html>