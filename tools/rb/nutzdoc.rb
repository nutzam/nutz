# encoding: utf-8

=begin

=Nutzdoc 文档生成脚本

==用法

    ruby nutzdoc.rb [options]

--- options
  请使用`-h`查看使用文档

==注意事项
默认情况下把所需要用到的nutz.jar, nutzdoc.jar, iText.jar, iTextAsian.jar需要放在该文件同一目录下

在本脚本中`Github Pages`的上传目录为`nutz`的`gh-pages`分支，
如果打算使用本脚本自动生成你自己的`Github Pages`的话，
请自行修改`pages_before_work`方法中所指定的remote地址（需要有push权限），
以及`pages_after_work`方法（里面的处理可能是不需要的）。

=end

require 'optparse'
require 'ostruct'

class Nutzdoc

  TYPE = %w{html gwiki pdf pages site}

  class << self
    def parse(args)
      options = OpenStruct.new
      options.pdf_font_path = File.dirname(File.expand_path(__FILE__))
      opts = OptionParser.new do |opts|
        opts.banner = "Usage: nutzdoc.rb [options]"
        opts.separator ""
        opts.separator "Specific options:"

        opts.on('-t', '--type [TYPE]', "Set doc type (#{TYPE.join(' ')})") do |type|
          options.type = type
        end

        opts.on('-i', '--input-path [INPUT-PATH]', 'Set doc input path') do |input_path|
          options.input_path = input_path
        end

        opts.on('-o', '--output-path [OUTPUT_PATH]', 'Set doc output path.') do |output_path|
          options.output_path = output_path
        end

        opts.on('--wip', '--wiki-index-page [GOOGLE_WIKI_INDEX_PAGE]', "Set Google wiki doc's index page") do |wiki_index_page|
          options.wiki_index_page = wiki_index_page
        end

        opts.on('--wiu' ,'--wiki-img-url [GOOGLE_WIKI_IMAGE_URL]', "Set Google wiki doc's image url") do |wiki_img_url|
          options.wiki_img_url = wiki_img_url
        end

        opts.on('--pdf-font-path [PDF_FONT_PATH]', 'Set pdf font path', 'Default path is THIS file path') do |pdf_font_path|
          options.pdf_font_path = pdf_font_path
        end

        opts.on('--pdf-needs-jar-path [PDF_NEEDS_JAR_PATH]', 'Set pdf iText and iTextAsian jar path', 'Default path is THIS file path') do |pdf_needs_jar_path|
          options.pdf_needs_jar_path = pdf_needs_jar_path
        end

        opts.separator ""
        opts.on_tail("-h", "--help", "Show this message") do
          puts opts
          exit
        end
      end

      opts.parse!(args)
      Nutzdoc::Zdoc.new(options)
      options
    end
  end

  require 'fileutils'
  require 'find'
  class Nutzdoc::Zdoc

    def initialize(options)
      @options = options

      before_work

      send @options.type
    end

    private
    def before_work
      validate_options

      FileUtils.rm_rf @options.output_path if File.directory?(@options.output_path)
      FileUtils.mkpath @options.output_path unless @options.type == 'pdf'

      set_classpath

      @message = @options.type
      type = @options.type == 'pages' ? 'html' : @options.type
      @command = "#{@set} #{@class_path} && java org.nutz.doc.Doc #{type} #{@options.input_path} #{@options.output_path}"
      if 'gwiki' == @options.type
        @message = "Google Wiki"
        @command += " #{@options.wiki_index_page} #{@options.wiki_img_url}"
      end
      if is_windows?
        @command += "> nul"
      else
        @command += ">/dev/null 2>&1"
      end
    end

    def pages
      pages_before_work

      send "html"

      pages_after_work

      system("git add .")
      system('git commit -am "update doc"')
      puts "-"*80
      puts "start push to Github Pages ......"
      system("git push gh-pages master:gh-pages")
      puts "Github Pages has pushed."
      puts "-"*80
    end

    def pages_before_work
      work_path = File.expand_path(@options.output_path)
      Dir.chdir(work_path)

      remote_url = 'git@github.com:nutzam/nutz.git'
      system('git init .')
      system("git remote add gh-pages #{remote_url}")
      system("git pull --rebase gh-pages gh-pages")

      Dir['**'].each do |dir|
        FileUtils.rm_rf File.expand_path(dir)
      end
    end

    def pages_after_work
      work_path = File.expand_path(@options.output_path)
      File.rename("#{work_path}/main.html", "#{work_path}/main.bak.html")
      File.rename("#{work_path}/index.html", "#{work_path}/main.html")
      File.rename("#{work_path}/main.bak.html", "#{work_path}/index.html")

      lines = File.readlines("#{work_path}/index.html")
      File.open("#{work_path}/index.html", 'w') do |file|
        lines.each do |line|
          unless m = line.match(/(.+")(index.html)(".+)/)
            file.puts line
          else
            file.puts m[1] + "main.html" + m[3]
          end
        end
      end
    end

    %w{html gwiki pdf site}.each do |type|
      define_method "#{type}" do
        puts "start to #{@message}......"
        puts "-"*80
        result = system(@command)
        puts "-"*80
        if result
          puts "#{@message} has created."
        else
          puts "#{@message} *HASN'T* created."
        end
      end
    end

    def validate_options
      unless @options.type
        puts "Please enter want to got document's type."
        exit
      end

      unless TYPE.include? @options.type
        puts "Enter the document type is wrong.\nThe document type must be #{TYPE.join(', ')}."
        exit
      end

      if @options.input_path
        unless File.directory?(@options.input_path)
          puts "Input folder path doesn't exist, please check it."
          exit
        end
      else
          puts "Please enter input folder path."
          exit
      end

      if @options.type == 'pdf'
        unless @options.output_path
          puts "Please enter output pdf file name."
          exit
        end

        unless File.directory?(@options.pdf_font_path)
          puts "Font folder path doesn't exist, please check it."
          exit
        end

        if File.exist?("#{@options.pdf_font_path}/pdf_font.ttf")
          @options.pdf_font = "#{@options.pdf_font_path}/pdf_font.ttf"
        elsif File.exist?("#{@options.pdf_font_path}/pdf_font.ttc")
          @options.pdf_font = "#{@options.pdf_font_path}/pdf_font.ttc"
        else
          puts "Font folder path hasn't font file, please check it, and must be font file name is pdf_font.ttf or pdf_font.ttc."
          exit
        end

        unless File.exist?("#{@options.pdf_needs_jar_path}iText.jar") && File.exist?("#{@options.pdf_needs_jar_path}iTextAsian.jar")
          puts "Pdf needs jar hasn't jar file, please check it, and must be jar file name is iText.jar, iTextAsian.jar."
          exit
        end

      else
        unless @options.output_path
          puts "Please enter output folder path."
          exit
        end
      end
    end

    def set_classpath
      @set, symbil = "export", ":"
      if is_windows?
        @set, symbil = "SET", ";"
      end

      this_file_path = File.dirname(File.expand_path(__FILE__))
      @class_path = "CLASSPATH=."
      %w{nutzdoc.jar nutz.jar}.each do |jar|
        @class_path += "#{symbil}#{this_file_path}/#{jar}"
      end

      if @options.type == 'pdf'
        ['iText.jar', 'iTextAsian.jar', @options.pdf_font_path].each do |jar|
          @class_path += "#{symbil}#{this_file_path}/#{jar}"
        end
        @class_path += "#{symbil}#{@options.pdf_font}"
      end
    end

    %w{windows macosx linux unix}.each do |os_name|
      define_method "is_#{os_name}?" do
        return os_name == os.to_s
      end
    end

    def os
      host_os = RbConfig::CONFIG['host_os']
      case host_os
      when /mswin|msys|mingw|cygwin|bccwin|wince|emc/
        :windows
      when /darwin|mac os/
        :macosx
      when /linux/
        :linux
      when /solaris|bsd/
        :unix
      else
        raise Error::WebDriverError, "unknown os: #{host_os.inspect}"
      end
    end
  end
end

Nutzdoc.parse(ARGV)
