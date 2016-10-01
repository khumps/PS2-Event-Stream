var WebSocket = require('ws');
var config = require('config');
var serviceID = config.get('serviceID');
var debug = true;
var ws = new WebSocket("wss://push.planetside2.com/streaming?environment=ps2&service-id=s:" + serviceID);

// Opens Websocket
ws.on('open', function open() {
	console.log("Connected");
	send("test");
});

// Executed whenever ws recieves a message
  ws.on('message', function incoming(message) {
  	if(debug)
    console.log('received: %s', message);
  });


function send(msg)
{
	if(debug)
		console.log("[" + parseCurrentTime(Date.now()) + "] Sent: " + msg);
	ws.send(msg);
}

/*function parseTime(date)
{
	var hours = date / 
	var min = date.prototype.getMinutes();
	var sec = date.prototype.getSeconds();
	return hours + ":" + min + ":" + sec;
}*/