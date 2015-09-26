/**
 * QuestionController
 *
 * @description :: Server-side logic for managing questions
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */

var uuid = require('node-uuid');

module.exports = {

  create: function(req, res){
    var data = req.body;
    data.uuid = uuid.v4();
    data.game = req.param('gameId');
    Question.create(data).exec(function(err, question){
      res.json(question);
      Game.find({'uuid': data.game}).populate('questions').exec(function (err, data) {
      });
    });
  },

  answerQuestion: function(req, res) {
    var questionId = req.param('questionId');
    Question.update({'uuid': questionId}, {'answer': req.body.answer}).exec(function(err, answer){
      if (err) {
        res.badRequest();
      } else {
        res.json(answer);
      }
    });
  }
};

