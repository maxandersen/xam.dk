<#include "header.ftl">

<#include "menu.ftl">

        <!-- Page Header -->
        <!-- Set your background image for this header on the line below. -->
        <header class="intro-header" style="background-image: url('${content.rootpath}img/index-cover.jpg') ; background-color:#101010|">
            <div class="container">
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                        <div class="site-heading">
                            <h1>${tag}</h1>
                            <hr class="small" />
                            <span class="subheading">Tagged Posts</span>
                        </div>
                    </div>
                </div>
            </div>
        </header>

        <!-- Main Content -->
        <div class="container">
            <div class="row">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">

                    <#list tag_posts as post>
                        <div class="post-preview"  >
                            <a href='${content.rootpath}${post.uri}'>
                                <h2 class="post-title">
                                    ${post.title}
                                </h2>
                                <#if (post.summary?? || post.description??)>
                                <h3 class="post-subtitle">
                                    ${(post.summary)!post.description}
                                </h3>    
                                </#if>
                            </a>
                            <p class="post-meta">
                                by <b>${(post.author)!config.site_author}</b>
                                on <span>${post.date?string('dd.MM.yyyy')}</span>
                                <#if (config.render_tags?? && post.tags??)> 
                                    <#list post.tags as tag>
                                    <a href="${content.rootpath}${config.tag_path}/${tag}.html" class="label">${tag}</a>
                                    </#list>
                                </#if>
                            </p>
                            
                        </div>
                        <hr/>
                    </#list>

                    
                </div>
            </div>
        </div>

<#include "footer.ftl">