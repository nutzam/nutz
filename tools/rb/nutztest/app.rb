require 'sinatra'

get '/' do
  "Hello, here is nutz test server."
end

get '/get' do
  "hello nutz"
end

post '/post' do
  "version: #{params[:version]}, website: #{params[:website]}"
end
