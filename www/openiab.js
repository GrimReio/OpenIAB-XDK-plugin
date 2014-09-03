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

// openiab.init = function(callback)
// {
//     cordova.exec(
//     	callback, 
//     	function(error) 
//     	{ 
//     		log(error);
//     		callback(error); 
//     	}, 
//     	PLUGIN, "init", ["INIT-PARAM"]);
// }