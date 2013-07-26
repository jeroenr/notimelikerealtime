var express = require('express')
  , app = express()
  , http = require('http')
  , util = require('util')
  , redis = require('redis')
  , RedisStore = require('connect-redis')(express);


var redisClient = exports.redisClient = redis.createClient();
var deletepermitClient = exports.deletepermitClient = redis.createClient();
var updatepermitClient = exports.updatepermitClient = redis.createClient();
var newpermitClient = exports.newpermitClient = redis.createClient();
var redisPub = exports.redisPub = redis.createClient();
var redisSub = exports.redisSub = redis.createClient();
var sessionStore = exports.sessionStore = new RedisStore({client: redisClient});

/*
 * Create and config server
 */

var app = exports.app = express();

app.configure(function() {
	app.set('port', 4000);
	app.use(app.router);
});

exports.server = http.createServer(app).listen(app.get('port'), function() {
  console.log('Realtime permit socket handler started on port %d', app.get('port'));
});


/*
 * Socket.io
 */

require('./sockets');

/*
 * Catch uncaught exceptions
 */

process.on('uncaughtException', function(err){
  console.log('Uncaught exception: ' + util.inspect(err));
});



