var parent = module.parent.exports 
  , app = parent.app
  , server = parent.server
  , express = require('express')
  , redisClient = parent.redisClient
  , deletepermitClient = parent.deletepermitClient
  , updatepermitClient = parent.updatepermitClient
  , newpermitClient = parent.newpermitClient
  , redisPub = parent.redisPub
  , redisSub = parent.redisSub
  , sessionStore = parent.sessionStore
  , _ = require('underscore')
  , sio = require('socket.io')
  , util = require('util');

var io = exports.io = sio.listen(server);

var logger = io.log;

io.configure('production', function() {
  io.set('log level', 2);
  io.set('close timeout', 60*60*24);
  io.set('store', new sio.RedisStore({
  	redisPub: redisPub,
  	redisSub: redisSub,
  	redisClient: redisClient
  }));
  // io.set('transports', ['xhr-polling','jsonp-polling']);
  _.each(['browser client minification','browser client gzip','browser client etag'], function(setting) {
  	io.enable(setting);
  });

  io.set('authorization', function(hsData, accept){
  	hsData.id = hsData.query.id;
	accept(null,true);
  });
});

io.configure('development', function() {
	io.set('close timeout', 60*60*24);
	io.set('transports', ['websocket']);
	io.set('authorization', function(hsData, accept){
		hsData.id = hsData.query.id;
		accept(null,true);
  	});
});

newpermitClient.subscribe("newpermit");
newpermitClient.on("message", function(channel, message) {
	io.sockets.emit('permit_created', JSON.parse(message));
});


deletepermitClient.subscribe("deletepermit");
deletepermitClient.on("message", function(channel, message) {
	io.sockets.emit('permit_deleted', JSON.parse(message));
});


updatepermitClient.subscribe("updatepermit");
updatepermitClient.on("message", function(channel, message) {
	io.sockets.emit('permit_updated', JSON.parse(message));
});

io.sockets.on('connection', function (socket) {
	logger.info("user " + socket.id + " has connected");

	io.sockets.emit('viewer_connected', {viewer: socket.handshake.id, total: Object.keys(io.sockets.sockets).length});

	// when the user disconnects.. perform this
	socket.on('disconnect', function(){
		delete io.sockets.sockets[socket.id];
		io.sockets.emit('viewer_connected', {viewer: socket.handshake.id, total: Object.keys(io.sockets.sockets).length});
		// echo globally that this client has left
		socket.broadcast.emit('server_message', socket.id + ' has disconnected');
	});
});
