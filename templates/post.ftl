<#include "header.ftl">

<#include "menu.ftl">

        <!-- Page Header -->
        <!-- Set your background image for this header on the line below. -->
         <header class="intro-header" style="background-image: url('${content.rootpath}${(content.banner)!"img/post-cover.jpg"}') ; background-color:#101010|">
           <div class="container">
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                        <div class="post-heading">
                            <h1>${content.title}</h1>
                            <#if (content.description??)>
                            <h2 class="subheading">
                                ${content.description}
                            </h2>
                            </#if>
                            <span class="meta">by <b>${(content.author)!config.site_author}</b> on 
                                ${content.date?string("MMMM dd, yyyy")}
                                </br>
                                <#if (config.render_tags?? && content.tags??)> 
                                    <#list content.tags as tag>
                                        <a href="${content.rootpath}${config.tag_path}/${tag}.html" class="label">${tag}</a>
                                    </#list>
                                </#if>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </header>

        <!-- Post Content -->
        <article>
            <div class="container">
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                        ${content.body}
                    </div>
                </div>
            </div>
        </article>
        <hr/>
        <div class="container"><div class="row"><div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
        
        <#include "disqus.ftl"/>

<#include "footer.ftl">