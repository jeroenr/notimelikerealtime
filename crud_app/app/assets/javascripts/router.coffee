define ["app", "modules/permit"], (app) ->

  class Router extends Backbone.Router
    routes:
      "":           "index"
    index: ->
      window.view = new app.Views.PermitListView