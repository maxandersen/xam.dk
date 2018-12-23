require 'pathname'

module Jekyll
  module UrlRelativizer
    ## this overrides relative_url default filter to actually generate relative urls
    ## based on github.com/jekyll/jekyll/pull/6362/ (and with some bugfixes from comments)
    def relative_url(input)
      return if input.nil?
      input    = ensure_leading_slash(input)
      page_url = @context.registers[:page]["url"]
      page_dir = Pathname(page_url).parent

      target   = page_url.end_with?("/") ? Pathname(page_url) : page_dir
      Pathname(input).relative_path_from(target).to_s
    end
  end
end

Liquid::Template.register_filter(Jekyll::UrlRelativizer)
