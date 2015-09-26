
module.exports = {
  findUserForRequest: function(req, callback) {
    var uuid = req.headers['catch-me-uuid'];
    if(!uuid) {
      callback("uuid not set", null);
      return;
    }
    User.find(uuid).exec(function(err, users){
      if(err) {
        callback(err, null);
      }
      else {
        if(users.length == 0) {
          User.create({uuid: uuid}).exec(function(err, user) {
            if(err) {
              callback(err);
            }
            else {
              console.log("user " + user.uuid + " created.");
              callback(null, user);
            }
          });
        }
        console.log("user " + users[0].uuid + " authenticated.");
        callback(null, users[0]);
      }
    });
  }
};
