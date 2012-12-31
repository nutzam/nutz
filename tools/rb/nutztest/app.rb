# encoding: utf-8

require "sinatra"
require "sinatra/namespace"
require "sinatra/reloader" if development?
require "sinatra/json"

get '/' do
  "Hello, here is nutz test server."
end

post '/' do
  "version: #{params[:version]}, website: #{params[:website]}"
end

namespace '/nutztest' do
  context_path= "/nutztest"

  #BaseTest.java
  namespace '/adaptor/json/pet' do
    %w{array list}.each do |item|
      post "/#{item}" do
        "pets(2) #{item}"
      end
    end
  end

  get '/base.jsp' do
    context_path
  end

  namespace '/common' do
    get("/pathArgs/:name") do
      params[:name]
    end

    get "/pathArgs2/*" do
      params[:splat][0].split('/').map {|item| item.split('.')[0]}.join('')
    end

    get("/pathArgs3/*/blog/*") do
      "#{params[:splat][0]}&#{params[:splat][1]}"
    end

    get("/pathArgs3/puZ") do
      "puZ&Z"
    end

    get("/pathArgs4/nutz") do
      "nutz&wendal"
    end

    get("/pathArgs5/nutz") do
      "nutz&#{params['user.name']}&#{params['user2.name']}"
    end

    get("/param") do
      params[:id]
    end

    get("/path") do
      context_path
    end

    get("/path2") do
      if "base" == params[:key]
        params[:key]
      else
        context_path
      end
    end

    get("/servlet_obj") do
      context_path
    end
  end

  # AllView.java
  namespace '/views' do
    %w{for for2 for3}.each do |item|
      get "/#{item}" do
          context_path
      end
    end

    %w{jsp jsp2 jsp3 jsp4}.each do |item|
      get "/#{item}" do
          "null"
      end
    end

    %w{raw raw2 raw3}.each do |item|
      get "/#{item}" do
          "ABC"
      end
    end

    get "/raw4" do
    end

    get "/raw5" do
      json :name => "wendal"
    end

    %w{red red2 red3}.each do |item|
      get "/#{item}" do
          context_path
      end
    end
  end

  # SimpleAdaptorTest.java
  namespace '/adaptor' do
    get %r{/err/param(/[\w]+)?} do
      context_path
    end

    post("/json/type") do
      "halt"
    end

    post "/ins" do
      "I am abc"
    end

    post "/reader" do
      "I am abc"
    end

  end

  # UploadTest.java
  post '/upload/image' do
    "image&3"
  end

end
