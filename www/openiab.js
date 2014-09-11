var exec = require('cordova/exec');

PLUGIN = "OpenIabCordovaPlugin";

function OpenIAB()
{
	this.VERIFY_MODE =
	{
		EVERYTHING:0,
		SKIP:1,
		ONLY_KNOWN:2
	}

	this.STORE_NAME =
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

	this.options = 
	{
		checkInventory:false,
		discoveryTimeout:5 * 1000,
		checkInventoryTimeout:10 * 1000,
		verifyMode:this.VERIFY_MODE.SKIP,
		preferredStoreNames: [ this.STORE_NAME.GOOGLE, this.STORE_NAME.YANDEX ],
		availableStores:
		[
			[this.STORE_NAME.GOOGLE, 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkxePPiDjF2+Ejs9zZUjp+CFJWt/Ia7ubLy/HtJ4JX6RSrdRN9c9tr2UzzQSG2CkHwhDdovxHY4xD2F71B6Buuhb4/qAfqVx3h96n9VyNivFx45xiDzBX22Zlhz6c/QOCsEh0cvKGGhTmK0zFhvFj7eKqDT+eavKFDyuBboR8k+sPgtDthbcCpZNDu0jVIH032+cdX0IBN+LstNl6+AUA7JKY58hvcOWUYV/Yk4+oddYuhvvnnXIwAIWtuacCc3oFyR4+slCQ4WmSw3Xu7ag93NlRmbofV0+mHZ4lqsqf6xJqxfpw5y8Jcm8cBt9+LESMeur+ZdSnNR54stA/6rXuwwIDAQAB']
		]
	}

	this.error =
	{
		code:-1,
		message:""
	}

	this.purchase =
	{
        itemType:null,
        orderId:null,
        packageName:null,
        sku:null,
        purchaseTime:null,
        purchaseState:null,
        developerPayload:null,
        token:null,
        originalJson:null,
        signature:null,
        appstoreName:null
	}

	this.skuDetails =
	{
		itemType:null,
        sku:null,
        type:null,
        price:null,
        title:null,
        description:null,
        json:null
	}
}

OpenIAB.prototype.getSkuDetails = function(success, error, sku)
{
	exec(success, error, PLUGIN, "getSkuDetails", [sku]);
}

OpenIAB.prototype.init = function(success, error, skuList)
{
	exec(success, error, PLUGIN, "init", [this.options, skuList]);
}

OpenIAB.prototype.purchaseProduct = function(success, error, sku, payload)
{
	exec(success, error, PLUGIN, "purchaseProduct", [sku, payload]);
}

OpenIAB.prototype.purchaseSubscription = function(success, error, sku, payload)
{
	exec(success, error, PLUGIN, "purchaseSubscription", [sku, payload]);
}

OpenIAB.prototype.consume = function(success, error, sku)
{
	exec(success, error, PLUGIN, "consume", [sku]);
}

module.exports = new OpenIAB();