require "capistrano/node-deploy"
require 'codebase/recipes'

set :application, "lovepotion-websocket"
set :repository,  "git@codebasehq.com:love/lp/lovepotion-websocket.git"

set :scm, :git 

set :user, "jeroen"
set :use_sudo, true

set :deploy_to, "/opt/lovepotion"

ssh_options[:forward_agent] = true

default_run_options[:pty] = true

role :app, "176.58.116.106"

namespace :deploy do
  task :start do 
  	sudo 'start nodejs-app'
  end
  task :stop do 
  	sudo 'stop nodejs-app'
  end
end

namespace :node do
	task :restart do
		deploy.stop
		deploy.start
	end
end

