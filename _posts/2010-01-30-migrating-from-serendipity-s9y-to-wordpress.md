---
title: 'Migrating from Serendipity (s9y) to WordPress '
author: 'Max Rydahl Andersen'

tags: [ Java ]
orignallink: 'http://blog.xam.dk/?p=174'
---
<div>
<p>I migrated this blog from from <a href="http://www.s9y.org/">Serendipity</a> (s9y) to <a href="http://wordpress.org">WordPress</a>.
<br><br>
It turned out to be pretty simple to import s9y posts to WordPress with the <a href="http://technosailor.com/2007/01/02/new-version-of-serendipity-s9y-to-wordpress-importer-available/">s9y importer</a>, but making sure the old links to posts and feeds turned out to be a bit trickier.
<br><br>
I found a <a href="http://blog.solutionperspectivemedia.co.uk/?p=63">blog post</a> that outlined how to use .htaccess to solve some of these problems, but it included manually having to figure out which id's that had become skewed during the import process. Luckily the SQL schema of both s9y and WordPress is pretty similar and easy to do queries against to generate matching .htaccess lines.
<br><br>
The following SQL queries is what I used to create a working .htaccess file for my migration, use at your own risk ;)
<br><br><strong>SQL for posts:</strong>
</p>
<pre lang="sql" escaped="true">SELECT CONCAT( "RewriteRule ^archives/", s9.id,</pre>
<pre lang="sql" escaped="true">"\-.*.html$ /index.php?p=", wp.id, " [L,R=301]" )</pre>
<pre lang="sql" escaped="true">FROM `serendipity_entries` s9, wp_posts wp
WHERE wp.id != s9.id
AND wp.post_parent=0</pre>
<pre lang="sql" escaped="true">AND s9.title = wp.post_title
ORDER BY s9.id</pre>
<strong>SQL for category feeds:</strong>
<pre lang="sql" escaped="true">SELECT concat("RewriteRule ^feeds/categories/", s9y.categoryid,</pre>
<pre lang="sql" escaped="true"> "\-.*.rss$ /index.php?cat=", wp.term_id, "&amp;feed=rss2 [L,R=301]")
FROM `wp_terms` as wp, serendipity_category as s9y
where name = category_name</pre>
<strong>SQL for category:</strong>
<pre lang="sql" escaped="true">SELECT CONCAT( "RewriteRule ^categories/", s9y.categoryid,</pre>
<pre lang="sql" escaped="true"> "\-.*$ /index.php?cat=", wp.term_id, "[L,R=301]" )
FROM `wp_terms` AS wp, serendipity_category AS s9y
WHERE name = category_name</pre>
Once you have the output from these insert them into the following .htaccess file:
<pre lang="bash" line="1" escaped="true">RewriteEngine On
RewriteBase /
<br><br>
## skewed articles
[Insert output for Posts]
<br><br>
## categories
[Insert output for Categories]
<br><br>
## category feeds
[Insert output for Categories feeds]
<br><br>
## handle defaults
RewriteRule ^archives/([0-9]+)\-.*.html$ /index.php?p=$1 [L,R=301]
RewriteRule ^archives/([0-9]{4,4})/([0-9]{2,2}).*.html$ /index.php?m=$1$2 [L,R=301]
RewriteRule ^archive$ / [L,R=301]
RewriteRule ^feeds/index.rss2$ /index.php?feed=rss2 [L,R=301]
RewriteRule ^feeds/index.rss1$ /index.php?feed=rss [L,R=301]
RewriteRule ^feeds/index.rss$ /index.php?feed=rss [L,R=301]
RewriteRule ^feeds/index.atom$ /index.php?feed=atom [L,R=301]
RewriteRule ^plugin/tag/(.*)$ /index.php?tag=$1 [L,R=301]
RewriteRule ^categories/(2)\-.*$ /index.php?cat=3 [L,R=301]</pre>
With that in place in the root of your WordPress installation those using your old s9y links should now be redirected automatically to the matching WordPress posts/feeds/categories.
<br><br>
Another useful post I found were about <a href="http://www.mydigitallife.info/2007/10/01/how-to-move-wordpress-blog-to-new-domain-or-location/">how to move WordPress between domains</a>, which I needed because I intially installed it on wordpress.xam.dk but needed it to end up on blog.xam.dk and apparently WordPress thinks it is a good idea to stored absolute url's in its settings.
<br><br>
Now if I could just find a WordPress theme that were both beautiful, simply and functional...still searching..</div>
