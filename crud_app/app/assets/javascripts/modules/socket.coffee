define ["app", "jquery", 'noty/jqueryNoty', "noty/layouts/top", "noty/themes/default", "socket.io-client"], (app, jQuery) ->

  class SocketHandler

    # RFC1422-compliant Javascript UUID function. Generates a UUID from a random
    # number (which means it might not be entirely unique, though it should be
    # good enough for many uses). See http://stackoverflow.com/questions/105034
    uuid = ->
      'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) ->
        r = Math.random() * 16 | 0
        v = if c is 'x' then r else (r & 0x3|0x8)
        v.toString(16)
      )

    updateView = ->
      window.view.collection.fetch reset:true, success: window.view.render

    start: ->
        refresh: (col)->
                $(".permitlist").html "<ul></ul>"
                for model in col.models
                  do (model) =>
                    $(".permitlist ul").append new app.Views.PermitItemView(model: model).render().el

        socket = io.connect """http://#{window.sockethandlerEndpoint || 'localhost'}:4000""", query: """id=#{uuid()}"""
        socket.on 'connect', () ->
          console.log("""session id: #{socket.socket.sessionid} """)
          socket.on 'permit_created', (permitJson) ->
            updateView()
            noty(text: """Project #{permitJson.project} was assigned to #{permitJson.assignee}""", type: 'warning', timeout: 1000)

          socket.on 'permit_deleted', (permitJson) ->
              updateView()
              noty(text: """Permit #{permitJson.permit} was deleted""", type: 'warning', timeout: 1000)

          socket.on 'permit_updated', (permitJson) ->
              updateView()
              noty(text: """Project #{permitJson.project} was assigned to #{permitJson.assignee}""", type: 'information', timeout: 1000)

          socket.on 'viewer_connected', (viewerUpdateJson) ->
              $numViewers = $('#numViewers')
              $numViewers.animate fontSize:'3em', "fast"
              $numViewers.html(viewerUpdateJson.total)
              $numViewers.animate fontSize:'1em', "fast"

          socket.on 'disconnect', () ->
            console.log(""" #{socket.id} has disconnected""")