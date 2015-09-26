/**
 * QuestionController
 *
 * @description :: Server-side logic for managing questions
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */

module.exports = {
  create: function(req, res){
    var data = req.body;
    data.uuid = uuid.v4();
    Question.create(data).done(function(err, question) {
      res.json(question);
    });
  },

  answerQuestion: function(req, res) {
    //uuid?
    var data = req.body;
    res.json(data);
  }
};

