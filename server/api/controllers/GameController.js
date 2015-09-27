/**
 * GameController
 *
 * @description :: Server-side logic for managing games
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */
var uuid = require('node-uuid');


var findActiveGameForUser = function (user, callback) {
  User.findOne(user.uuid).populate('games').exec(function(err, u) {
    var games = u.games;
    console.log("Games for user: ", games);
    var activeGames = games.filter(function (game) {
      return game.isActive();
    });
    if (activeGames.length == 0) {
      console.log("no active games for user");
      callback(null);
    }
    else {
      console.log("found active game for user");
      callback(activeGames[0]);
    }
  });
};

module.exports = {

  create: function (req, res) {
    UserService.findUserForRequest(req, function (err, user) {
      findActiveGameForUser(user, function (game) {
        if (game) {
          console.log('could not create game, already exists. ', game);
          res.badRequest();
        }
        else {
          var data = req.body;
          data.uuid = uuid.v4();
          data.owner = user.uuid;
          data.secret = Math.floor(Math.random() * (9999 - 1000)) + 1000;
          data.endTime = '2015-09-29T00:00:00Z';
          Game.create(data).exec(function (err, game) {
            if(err) {
              console.log("error creating game: ",err)
            }
            else {
              console.log('created game: ' + game);
              res.json(game);
            }
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
        var game = games[0].toObject();
        delete game.secret;
        res.json(game);
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
    var secretCode = req.body.secret;
    Game.find(req.param('gameId')).exec(function(err, game) {
      if (err || secretCode !== game[0].secret) {
        res.notFound();
      } else {
        res.json()
      }
    });
  }


};

