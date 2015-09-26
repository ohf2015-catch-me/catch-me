// check if userId already exists

module.exports = {
  checkIfUserExists: function(user) {
    return !!User.find(user);
  }
};
