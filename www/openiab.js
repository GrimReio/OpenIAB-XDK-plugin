var exec = require('cordova/exec');

PLUGIN = "OpenIabCordovaPlugin";

function log(msg)
{
	console.log("OpenIAB-js: " + msg);
}

function OpenIAB()
{
}

var VERIFY_MODE =
{
	EVERYTHING:0,
	SKIP:1,
	ONLY_KNOWN:2
}

var STORE_NAME =
{
    GOOGLE:"com.google.play",
    AMAZON:"com.amazon.apps",
    SAMSUNG:"com.samsung.apps",
    YANDEX:"com.yandex.store",
    NOKIA:"com.nokia.nstore",
    APPLAND:"Appland",
    SLIDEME:"SlideME",
    APTOIDE:"cm.aptoide.pt"
}

var options = 
{
	checkInventory:false,
	discoveryTimeout:5 * 1000,
	checkInventoryTimeout:10 * 1000,
	verifyMode:VERIFY_MODE.SKIP,
	availableStores:
	[
		[STORE.NAME_GOOGLE, "public_key"],
	],
	prefferedStoreNames: [ STORE_NAME.GOOGLE, STORE_NAME.YANDEX ]
}

OpenIAB.prototype.init = function(success, error)
{
	exec(success, error, PLUGIN, "init", [JSON.stringify(this)]);
}

module.exports = new OpenIAB();