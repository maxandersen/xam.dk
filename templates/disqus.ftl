
<#if (config.disqus_shortname??)>
    <div id="disqus_thread"></div>
    <script th:inline="javascript">
        var disqus_shortname = '${config.disqus_shortname}';
        var disqus_config = function () {
            this.page.url = '${config.site_host}/${content.uri};
            this.page.identifier = ${content.file} 'snippets.thyme';
        };
        (function () { // DON'T EDIT BELOW THIS LINE
            var d = document, s = d.createElement('script');
            s.src = 'https://' + disqus_shortname + '.disqus.com/embed.js';
            s.setAttribute('data-timestamp', +new Date());
            (d.head || d.body).appendChild(s);
        })();
    </script>
</#if>