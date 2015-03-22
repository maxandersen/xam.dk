require 'uri_helper'

require 'auto_excerpt'

module Awestruct
  module Extensions
    class MyPosts < Posts

      def initialize(path_prefix='/posts', assign_to=:posts, archive_template=nil, archive_path=nil, opts={})
        super(path_prefix, assign_to, archive_template, archive_path, opts)
        @path_prefix = path_prefix
        #@images_dir=opts[:imagesdir]

        $LOG.debug "*** Initialized MyPosts extension" if $LOG.debug?
      end
      
      def execute(site)
        $LOG.debug "*** Executing MyPosts extension..." if $LOG.debug?
        super(site)
        site.blog_dir = site.base_url + @path_prefix
        # changing the output path to avoid the /yyyy/mm/dd subfolders and problems with relative path to images
        #puts " blog posts: #{site.posts}"
        draft_posts = Array.new
        site.posts.each do |post|
          #puts " processing blog with title: '#{post.title}'..." 
          if ( post.relative_source_path =~ /^#{@path_prefix}\/([^.]+)\..*$/ ) then
            basename=$1
            # strip out date if entry start with such
            if (basename =~ /^\d{4}-[01]\d-[0-3]\d-(.*)$/ ) then
              basename=$1
            end
            
            post.output_path="#{@path_prefix}/#{basename}/index.html"
            #puts "  post date: #{post.date} >  #{Date.today.next_day} ? #{post.date > Date.today.next_day}"
            if post.date > Date.today.next_day
              draft_posts << post
              post.draft_article = true
            end
            ## TODO: why are there some posts with empty url ?

            url = "#{site.base_url}#{@path_prefix}/#{basename}"
            
            post.excerpt = AutoExcerpt.new(post.content, { :sentences => 1, :ending => "...<a href=\"#{url}\"><i class=\"fa fa-arrow-circle-right
\"></i></a>"})
            
          end
        end
        unless site.profile == "development"
          draft_posts.each do |post|
            site.posts.delete(post) 
          end
        end
        $LOG.debug "*** Done executing posts extension." if $LOG.debug?
      end
    end
  end
end
