var exec = require('cordova/exec');

PLUGIN = "OpenIabCordovaPlugin";

function log(msg)
{
	console.log("OpenIAB-js: " + msg);
}

function OpenIAB()
{
	this.init = function(success, error)
	{
	    exec(success, error, PLUGIN, "init", ["INIT-PARAM"]);
	} 
}

exports.init = new OpenIAB();