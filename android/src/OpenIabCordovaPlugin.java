package org.onepf.openiab.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class OpenIabCordovaPlugin extends CordovaPlugin
{
	private final String TAG = "OpenIAB-Cordova";

	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException 
    {
        if ("init".equals(action)) 
        {
            String message = args.getString(0);
            this.init(message, callbackContext);
            callbackContext.success();
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    private void init(String message, CallbackContext callbackContext)
    {
		if (message != null && message.length() > 0)
            callbackContext.success(message);
        else
            callbackContext.error("Expected one non-empty string argument.");
    }
}