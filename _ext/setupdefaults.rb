require 'uri_helper'

require 'auto_excerpt'

module Awestruct
  module Extensions
    class SetupDefaults

      def initialize()
      end
      
      def execute(site)
        site.pages.each do |page|
            if page.banner.nil? 
              if page.output_path =~ /blog/i 
                page.banner = '/img/post-bg.jpg'
              else
                page.banner = '/img/home-bg.jpg'
              end
            end
        end
      end
    end
  end
end
