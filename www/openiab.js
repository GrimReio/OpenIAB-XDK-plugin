var exec = require('cordova/exec');

PLUGIN = "OpenIabCordovaPlugin";

function log(msg)
{
	console.log("OpenIAB-js: " + msg);
}

exports.init = function(success, error)
{
    exec(success, error, PLUGIN, "init", ["INIT-PARAM"]);
}