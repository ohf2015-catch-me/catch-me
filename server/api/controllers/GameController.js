/**
 * GameController
 *
 * @description :: Server-side logic for managing games
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */
var uuid = require('node-uuid');

module.exports = {


	create: function(req, res){
    var data = req.body;
    data.uuid = uuid.v4();
    Game.create(data).done(function(err, game) {
      res.json(game);
    });
  },




};

