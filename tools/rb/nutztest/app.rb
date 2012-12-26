require 'sinatra'

get '/' do
  "Hello, here is nutz test server."
end

post '/' do
  "version: #{params[:version]}, website: #{params[:website]}"
end
