require.config
  shim:
   'noty/jqueryNoty':
      deps: ["jquery"]
   'noty/layouts/top':
      deps: ["noty/jqueryNoty"]
   'noty/themes/default':
      deps: ["noty/jqueryNoty"]
    backbone:
      deps: ["underscore", "jquery"]
      exports: "Backbone"
  optimize: "uglify2"

require ["domReady", "app", "router"], (domReady, app, Router) ->

  domReady ->
    app.router = new Router()
    app.router.on "all", (eventName) ->
      app.loaded = true
    Backbone.history.start
      pushState: true
      root: "/"
    require ["modules/socket"], (SocketHandler) ->
        app.socketHandler = new SocketHandler()
        app.socketHandler.start()

