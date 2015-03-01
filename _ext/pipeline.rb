require 'bootstrap-sass'

require 'myposts'
require 'mypaginator'

require 'setupdefaults'

Awestruct::Extensions::Pipeline.new do
  # extension Awestruct::Extensions::Posts.new '/news'
  # extension Awestruct::Extensions::Indexifier.new
  # Indexifier *must* come before Atomizer
  # extension Awestruct::Extensions::Atomizer.new :posts, '/feed.atom'

  helper Awestruct::Extensions::Relative

  extension Awestruct::Extensions::MyPosts.new('/blog', :posts)
  extension Awestruct::Extensions::MyPaginator.new( :posts, '/blog/index', :per_page=>2 )
  extension Awestruct::Extensions::Tagger.new( :posts, '/blog/index', '/blog/tags', :per_page=>10, :sanitize=>true )
  extension Awestruct::Extensions::TagCloud.new(:posts, '/blog/tags/index.html', :layout=>'tab', :title=>'Tags')

  extension Awestruct::Extensions::SetupDefaults.new

  extension Awestruct::Extensions::Indexifier.new
  extension Awestruct::Extensions::Atomizer.new( 
    :posts, 
    '/blog/feed-private.atom', 
    :num_entries=>10000,
    :content_url=> "http://blog.xam.dk",
    :feed_title=> 'What was I thinking ?' )
  extension Awestruct::Extensions::Disqus.new
  helper Awestruct::Extensions::GoogleAnalytics

end
