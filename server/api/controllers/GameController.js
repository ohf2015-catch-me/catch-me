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
    Game.create(data).exec(function(err, game){
      console.log('created game: ' + data.uuid);
      res.json(game);
    });
  },

  getDetails: function(req, res) {
    Game.find(req.param('gameId')).exec(function(err, game){
      if (err) {
        res.notFound();
      } else {
        res.json(game);
      }
    });

  },

  getFound: function(req, res) {

  }


};

