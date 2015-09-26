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
    Game.create(data).exec(function(err, game) {
      res.json(game);
    });
  },

  getDetails: function(req, res) {
    Game.find(req.param('gameId')).exec(function(err, games){
      if (err || games.length == 0) {
        res.notFound();
      } else {
        res.json(game[0]);
      }
    });

  },

  getFound: function(req, res) {

  }


};

