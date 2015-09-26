/**
 * GameController
 *
 * @description :: Server-side logic for managing games
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */
var uuid = require('node-uuid');


var findActiveGameForUser = function (user, callback) {
  Game.find().populate('owner', {where: { uuid: user.uuid }}).exec(function (err, games) {
    console.log("Games for user: ", games)
    var activeGames = games.filter(function (game) {
      return game.isActive();
    });
    if (activeGames.length == 0) {
      callback(null);
    }
    else {
      callback(activeGames[0]);
    }
  });
}

module.exports = {

  create: function (req, res) {
    UserService.findUserForRequest(req, function (err, user) {
      findActiveGameForUser(user, function (game) {
        if (game) {
          res.badRequest();
        }
        else {
          var data = req.body;
          data.uuid = uuid.v4();
          data.owner = user.uuid;
          Game.create(data).exec(function (err, game) {
            console.log('created game: ' + data.uuid);
            res.json(game);
          });
        }
      });
    });
  },

  getDetails: function(req, res) {
    var gameId = req.param('gameId');
    Game.find(gameId).populate('questions').exec(function(err, games){
      if (err || games.length == 0) {
        res.notFound();
      } else {
        res.json(games[0]);
      }
    });

  },

  getMyGame: function (req, res) {
    UserService.findUserForRequest(req, function (err, user) {
      findActiveGameForUser(user, function (game) {
        if (game) {
          res.json(game);
        }
        else {
          res.notFound();
        }
      })
    });
  },

  getFound: function (req, res) {

  }


};

