notimelikerealtime
==================

Cuz there ain't no time like realtime!

This repo contains two applications that should be connected through Redis. In the crud_app directory is a small Play/Akka + Backbone.js CRUD application that pushes CRUD events over redis channels. In the sockethandler is a tiny Node.js + Socket.io application that listens for CRUD events from redis and broadcast over a socket connection (depending on the supported transport of the client)
