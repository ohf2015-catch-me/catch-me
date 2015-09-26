/**
 * UserController
 *
 * @description :: Server-side logic for managing users
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */

module.exports = {

  create: function(req, res) {
    User.create(req.body).exec(function(err, user) {
      res.json(user);
    })
  }

};

