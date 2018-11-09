<#include "header.ftl">

<#include "menu.ftl">

        <!-- Page Header -->
        <!-- Set your background image for this header on the line below. -->
        <header class="intro-header" style="background-image: url('${content.rootpath}img/index-cover.jpg') ; background-color:#101010|">
            <div class="container">
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                        <div class="site-heading">
                            <h1>${config.site_title}</h1>
                            <hr class="small" />
                            <span class="subheading">${config.site_description}</span>
                        </div>
                    </div>
                </div>
            </div>
        </header>

        <!-- Main Content -->
        <div class="container">
            <div class="row">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">

                    <#list published_posts as post>
                        <div class="post-preview"  >
                            <a href="${content.rootpath}${post.uri}">
                                <h2 class="post-title">
                                    ${post.title}
                                </h2>
                                <#if (post.description??)>
                                <h3 class="post-subtitle">
                                    ${post.description}
                                </h3>    
                                </#if>
                            </a>
                            <p class="post-meta">
                                <span>${post.date?string("EEEEE, MMMM dd, yyyy")}</span>
                                <#if (config.render_tags?? && post.tags??)>
                                tag path: ${config.tag_path}
                                <#list post.tags as tag>
                                  <a href="${content.rootpath}${config.tag_path}/${tag}.html" class="label">${tag}</a> 
                                </#list>
                                </#if>
                            </p>
                            <p>${post.body}</p>
                        </div>
                        <hr/>
                    </#list>

                    <!-- Pager -->
                    <#if (config.index_paginate??)>
                    <ul class="pager">
                        <li class="next">
                            <a href="#">Older Posts &rarr;</a>
                        </li>
                    </ul>    
                    </#if>
                    
                </div>
            </div>
        </div>

<#include "footer.ftl">