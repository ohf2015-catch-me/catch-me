/**
 * HintController
 *
 * @description :: Server-side logic for managing hints
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */
var uuid = require('node-uuid');

module.exports = {
  create: function(req, res){
    var data = req.body;
    data.uuid = uuid.v4();
    data.game = req.param('gameId');
    Hint.create(data).exec(function(err, hint) {
      res.json(hint);
    });
  }


};

