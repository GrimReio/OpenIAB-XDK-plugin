function log(msg)
{
	console.log("OpenIAB-js: " + msg);
}

PLUGIN = "OpenIabCordovaPlugin";

openiab.init = function(callback)
{
    cordova.exec(
    	callback, 
    	function(error) 
    	{ 
    		log(error);
    		callback(error); 
    	}, 
    	PLUGIN, "init", ["INIT-PARAM"]);
}