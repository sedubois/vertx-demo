require('sockjs-client');
var EventBus = require('vertx3-eventbus-client');
var eb = new EventBus('http://localhost:8080/eventbus');

eb.onopen = function() {

  eb.registerHandler('notif', function(error, message) {
    console.log("Task state changed: " + message.body);
  });
};
