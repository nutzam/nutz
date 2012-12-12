# encoding: utf-8

=begin

=zDoc 文档生成脚本

==用法

    ruby zdoc.rb [zdoc_type] [input_path] [output_path]

--- zdoc_type

    支持参数为`html`、`site`、`pdf`、`Github Pages`、`Google Code Wiki`

--- input_path

    zDoc文档的路径，拿nutz来说就是`path/to/nutz/doc/manual`

--- output_path

    生成文档的路径

==注意事项

在本脚本中`Github Pages`的上传目录为`nutz`的`gh-pages`分支，
如果打算使用本脚本自动生成你自己的`Github Pages`的话，
请自行修改`pages_before_work`方法

=end

require 'fileutils'
require 'find'

class Zdoc

  def initialize
    validate_params

    FileUtils.rm_rf ARGV[2] if File.directory?(ARGV[2])
    FileUtils.mkpath ARGV[2] unless ARGV[0] == 'pdf'

    set_classpath

    send ARGV[0]
  end

  def pages
    pages_before_work

    send "html"

    work_path = File.expand_path(ARGV[2])
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

    system("git add .")
    system('git commit -am "update doc"')
    puts "-"*80
    puts "start push to Github Pages ......"
    system("git push #{remote_name} gh-pages")
    puts "Github Pages has pushed."
    puts "-"*80
  end

  %w{html site pdf}.each do |type|
    define_method "#{type}" do
      puts "start to #{type}......"
      puts "-"*80
      result = system("#{@set} #{@class_path} && java org.nutz.doc.Doc #{type} #{ARGV[1]} #{ARGV[2]}")
      puts "-"*80
      if result
        puts "#{type} has created."
      else
        puts "#{type} *HASN'T* created."
      end
    end
  end

  def gwiki
    puts "start to Google Wiki......"
    puts "-"*80
    wiki_index_page = ARGV[3]? ARGV[3] : "null"
    imgs_path = ARGV[4]? ARGV[4] : "null"
    result = system("#{@set} #{@class_path} && java org.nutz.doc.Doc gwiki #{ARGV[1]} #{ARGV[2]} #{wiki_index_page} #{imgs_path}")
    puts "-"*80
    if result
      puts "Google Wiki has created."
    else
      puts "Google Wiki *HASN'T* created."
    end
  end

  private
  def validate_params
    if ARGV.length == 0
      puts "Please enter want to got document's type."
      exit
    end

    unless %w{html gwiki pdf pages site}.include? ARGV[0]
      puts "Enter the document type is wrong.\nThe document type must be #{%w[html gwiki pdf pages].join(', ')} or site."
      exit
    end

    case ARGV.length
    when 1
      puts "Please enter input folder path."
      exit
    when 2
      unless File.directory?(ARGV[1])
        puts "Input folder path doesn't exist, please check it."
        exit
      end
      if ARGV[0] == 'pdf'
        puts "Please enter output pdf file name."
        exit
      else
        puts "Please enter output folder path."
        exit
      end
    when 3
      if ARGV[0] == 'pdf'
        puts "Pdf file will uesd font.\nPlease enter font folder path."
        exit
      end
    else
      if ARGV[0] == 'pdf'
        unless File.directory?(ARGV[3])
          puts "Font folder path doesn't exist, please check it."
          exit
        else
          unless File.exist?("#{ARGV[3]}/pdf_font.ttf") || File.exist?("#{ARGV[3]}/pdf_font.ttc")
            puts "Font folder path hasn't font file, please check it, and must be font file name is pdf_font.ttf or pdf_font.ttc."
          end
        end
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

    if ARGV[0] == 'pdf'
      ['iText.jar', 'iTextAsian.jar', ARGV[3]].each do |jar|
        @class_path += "#{symbil}#{this_file_path}/#{jar}"
      end
    end
  end

  def pages_before_work
    work_path = File.expand_path(ARGV[2])
    FileUtils.cp_r("#{File.expand_path(ARGV[1])}/../../.git", "#{work_path}/.git")
    Dir.chdir(work_path)

    remote_name = "origin"
    can_push = false
    `git remote -v`.split("\n").each do |line|
      if m = line.match(/(.+)\s+git@github\.com:nutzam\/nutz\.git \(push\)/)
        can_push = true
        remote_name = m[1]
      end
    end

    unless can_push
      puts "Oops, you not have push remote. So, just Check it."
      exit
    end

    system("git checkout gh-pages")
    system("git pull --rebase #{remote_name} gh-pages")
    Dir['**'].each do |dir|
      FileUtils.rm_rf File.expand_path(dir)
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

Zdoc.new
