require 'sinatra'

get '/' do
	timestamp = params[:timestamp] || Time.now.to_f
	erb :index, :locals => { :server => 'localhost', :id => params[:id] }
end
