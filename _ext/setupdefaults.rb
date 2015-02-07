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
               page.banner = '/img/home-bg.jpg'
            end
        end
      end
    end
  end
end
