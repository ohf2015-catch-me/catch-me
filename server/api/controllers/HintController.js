/**
 * HintController
 *
 * @description :: Server-side logic for managing hints
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */

module.exports = {
  create: function(req, res){
    var data = req.body;
    data.uuid = uuid.v4();
    Hint.create(data).done(function(err, hint) {
      res.json(hint);
    });
  },

  answerQuestion: function(req, res) {
    res.json(req);
  }
};

