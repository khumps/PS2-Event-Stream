var WebSocket = require('ws');
var config = require('config');
var queryString = require("querystring");
var useSQL = config.get('useSQL');
if (useSQL)
    var mysql = require('mysql');
var serviceID = config.get('serviceID');
var debug = false;
var showMessages = false;
var ws = new WebSocket("wss://push.planetside2.com/streaming?environment=ps2&service-id=s:" + serviceID);

// Opens Websocket
ws.on('open', function open() {
    console.log("Connected");
    subscribe(["all"], ["all"], ["all"]);
   //unsubscribe(["all"], ["all"], ["all"]);
});

// Executed whenever ws recieves a message
		ws.on('message', (data) => {
			let json = JSON.parse(data)
			if(showMessages)
			console.log('got message', json)
		pushToSQL(json);
		})


function send(msg) {
    if (debug)
        console.log("[" + getDateTime() + "] Sent: " + msg);
    ws.send(msg);
}

function getDateTime() {
    var now = new Date();
    var year = now.getFullYear();
    var month = now.getMonth() + 1;
    var day = now.getDate();
    var hour = now.getHours();
    var minute = now.getMinutes();
    var second = now.getSeconds();
    if (month.toString().length == 1) {
        var month = '0' + month;
    }
    if (day.toString().length == 1) {
        var day = '0' + day;
    }
    if (hour.toString().length == 1) {
        var hour = '0' + hour;
    }
    if (minute.toString().length == 1) {
        var minute = '0' + minute;
    }
    if (second.toString().length == 1) {
        var second = '0' + second;
    }
    var dateTime = year + '/' + month + '/' + day + ' ' + hour + ':' + minute + ':' + second;
    return dateTime;
}

// Writes an entry to the SQL server
function pushToSQL(json) {
    if (useSQL) {
        var table = getTable(json);
        if (debugSQL) {
            console.log(json);
            console.log(getKeys(json).toString());
            console.log('INSERT INTO ' + table + ' VALUES ' + "(" + getValues(json) + ", " + rTime + ");");
        }
        /*    if (table == "GainExperience")
                connection.query('INSERT INTO ' + table + ' VALUES ' + "(" + getValues(json) + ", " + rTime + ");", function(err, result) {
                    if (debugSQL)
                        console.log("RESULT: " + err);

                });
            else*/
        connection.query('INSERT INTO ' + table + ' VALUES ' + "(" + getValues(json) + ", " + getDateTime() + ");", function(err, result) {
            if (debugSQL)
                console.log("RESULT: " + err);

        });
    }
}

function getTable(json) {
    return json["payload"["event_name"]];
}

function getVal(obj, key) {
    if (debug)
        console.log(obj[key]);
    return obj[key];

}

function getKeys(obj) {
    var str = "";
    var keys = Object.keys(obj);
    keys.pop();
    keys.pop();
    str += ("'" + keys[0] + "'");
    for (i = 1; i < keys.length - 1; i++) {
        str += ', ';
        str += ("'" + keys[i] + "'");
    }
    str += (", '" + keys[keys.length - 1] + "'");
    return str;

}
function getValues(obj) {
    var str = "";
    var keys = Object.keys(obj);
    keys.pop();
    keys.pop();
    str += ("'" + obj[keys[0]] + "'");
    for (i = 1; i < keys.length - 1; i++) {
        str += ', ';
        str += ("'" + obj[keys[i]] + "'");
    }
    str += (", '" + obj[keys[keys.length - 1]] + "'");
    return str;
}
// All paramaters are arrays
function subscribe(characters, worlds, eventNames) {
    var json = {
        "service": "event",
        "action": "subscribe",
        "characters": characters,
        "worlds": worlds,
        "eventNames": eventNames};
    send(JSON.stringify(json));
}
// All paramaters are arrays
function unsubscribe(characters, worlds, eventNames) {
    var json = {
        "service": "event",
        "action": "clearSubscribe",
        "characters": characters,
        "worlds": worlds,
        "eventNames": eventNames};
    send(JSON.stringify(json));
}

// Database to push to
if (useSQL) {
    var connection = mysql.createConnection({
        host: config.get('host'),
        user: config.get('user'),
        password: config.get('password'),
        database: config.get('database')
    });
    connection.connect();
}