define ["app", "jquery", "underscore", "backbone", "../../templates/permititem"], (app, $, _, Backbone) ->

  class app.Models.PermitModel extends Backbone.Model
    urlRoot: "/permits"

  class app.Collections.PermitCollection extends Backbone.Collection
    model: app.Models.PermitModel
    url: "/permits"
    comparator: (permit) ->
      permit.get("id")

  class app.Views.PermitItemView extends Backbone.View
    tagName: "li"
    initialize: ->
      _.bindAll @
      @model.bind "sync", @render
      @model.bind "remove", @unrender

    render: ->
      that = @
      dust.render "templates/permititem", @model.toJSON(), (err, out) ->
        that.$el.html(if err then err else out).data "item-id", that.model.toJSON().id
        $(".edit", that.$el).editInPlace context: that, onChange: that.editPermit
        $("a.delete", that.$el).click that.deletePermit
      @

    remove: -> @model.destroy()

    unrender: -> @$el.remove()

    editPermit: (attribute, newValue) -> @model.set(attribute, newValue).save @model.toJSON()

    deletePermit: (event) ->
          event.preventDefault()
          @model.destroy()

  class app.Views.PermitListView extends Backbone.View
    el: ".permits"
    events:
      "submit .permitform" : "addPermit"
    initialize: ->
      _.bindAll @
      @collection = new app.Collections.PermitCollection
      @collection.fetch success: @render

    render: ->
      $(".permitlist").html "<ul><li class='heading'><span>Project</span><span>Owner</span><span>Assignee</span></li></ul>"
      for model in @collection.models
        do (model) =>
          $(".permitlist ul").append new app.Views.PermitItemView(model: model).render().el

    addPermit: (event) ->
      event.preventDefault()
      return if _.any($("input", "#form-inputs"), (input) -> $.trim($(input).val()) is "")
      that = @
      permit = new app.Models.PermitModel project: $("#project", event.target).val(), assignee: $("#assignee", event.target).val(), owner: $("#owner", event.target).val()
      permit.save permit.toJSON(),
        success: (model, response, options) ->
          $("input", "#form-inputs").val("")

