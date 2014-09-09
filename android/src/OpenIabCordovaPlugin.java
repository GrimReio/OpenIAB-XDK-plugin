package org.onepf.openiab.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.onepf.oms.OpenIabHelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import org.onepf.oms.SkuManager;
import org.onepf.oms.appstore.googleUtils.*;

public class OpenIabCordovaPlugin extends CordovaPlugin
{
    public static final String TAG = "OpenIAB-CordovaPlugin";

    public static final int RC_REQUEST = 10001; /**< (arbitrary) request code for the purchase flow */

    private OpenIabHelper _helper;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
    {
        if ("init".equals(action))
        {
            JSONObject j = args.getJSONObject(0);
            boolean checkInventory = j.getBoolean("checkInventory");
            int checkInventoryTimeout = j.getInt("checkInventoryTimeout");
            int discoveryTimeout = j.getInt("discoveryTimeout");
            int verifyMode = j.getInt("verifyMode");

            OpenIabHelper.Options.Builder builder = new OpenIabHelper.Options.Builder()
                    .setCheckInventory(checkInventory)
                    .setCheckInventoryTimeout(checkInventoryTimeout)
                    .setDiscoveryTimeout(discoveryTimeout)
                    .setVerifyMode(verifyMode);

            JSONArray availableStores = j.getJSONArray("availableStores");
            for (int i = 0; i < availableStores.length(); ++i) {
                JSONArray pair = availableStores.getJSONArray(i);
                builder.addStoreKey(pair.get(0).toString(), pair.get(1).toString());
            }

            JSONArray prefferedStoreNames = j.getJSONArray("preferredStoreNames");
            for (int i = 0; i < prefferedStoreNames.length(); ++i) {
                builder.addPreferredStoreName(prefferedStoreNames.get(i).toString());
            }

            this.init(builder.build(), callbackContext);
            return true;
        }
        else if ("purchaseProduct".equals(action))
        {
            JSONObject j = args.getJSONObject(0);
            purchaseProduct(j.getString("sku"), j.getString("payload"), callbackContext);
        }
        else if ("purchaseSubscription".equals(action))
        {
            JSONObject j = args.getJSONObject(0);
            purchaseProduct(j.getString("sku"), j.getString("payload"), callbackContext);
        }
        else if ("consume".equals(action))
        {
            JSONObject j = args.getJSONObject(0);
            consume(j.getString("sku"), callbackContext);
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    private void init(final OpenIabHelper.Options options, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                _helper = new OpenIabHelper(cordova.getActivity(), options);
                createBroadcasts();

                // Start setup. This is asynchronous and the specified listener
                // will be called once setup completes.
                Log.d(TAG, "Starting setup.");
                _helper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    public void onIabSetupFinished(IabResult result) {
                        Log.d(TAG, "Setup finished.");

                        if (result.isFailure()) {
                            // Oh noes, there was a problem.
                            Log.e(TAG, "Problem setting up in-app billing: " + result);
                            callbackContext.error(result.getMessage());
                            return;
                        }

                        // Hooray, IAB is fully set up
                        Log.d(TAG, "Setup successful.");
                        callbackContext.success();
                    }
                });
            }
        });

    }

    public void purchaseProduct(final String sku, final String developerPayload, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _helper.launchPurchaseFlow(cordova.getActivity(), sku, RC_REQUEST, new BillingCallback(callbackContext), developerPayload);
            }
        });
    }

    public void purchaseSubscription(final String sku, final String developerPayload, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _helper.launchSubscriptionPurchaseFlow(cordova.getActivity(), sku, RC_REQUEST, new BillingCallback(callbackContext), developerPayload);
            }
        });
    }

    private void consume(final String sku, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    String appstoreName = jsonObject.getString("appstoreName");
                    String jsonPurchaseInfo = jsonObject.getString("originalJson");
                    String packageName = jsonObject.getString("packageName");
                    String token = jsonObject.getString("token");
                    Purchase p;
                    if (jsonPurchaseInfo == null || jsonPurchaseInfo.equals("")) {
                        p = new Purchase(appstoreName);
                        p.setSku(jsonObject.getString("sku"));
                    } else {
                        String itemType = jsonObject.getString("itemType");
                        String signature = jsonObject.getString("signature");
                        p = new Purchase(itemType, jsonPurchaseInfo, signature, appstoreName);
                    }
                    p.setPackageName(packageName);
                    p.setToken(token);
                    _helper.consumeAsync(p, new BillingCallback(callbackContext));
                } catch (org.json.JSONException e) {
                    callbackContext.error(Serialization.billingResultToJson(-1, "Invalid json: " + e));
                }
            }
        });
    }

    /**
     * Callback class for when a purchase or consumption process is finished
     */
    private static class BillingCallback implements IabHelper.OnIabPurchaseFinishedListener, IabHelper.OnConsumeFinishedListener {

        final CallbackContext _callbackContext;

        public BillingCallback(final CallbackContext callbackContext) {
            _callbackContext = callbackContext;
        }

        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase process finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                Log.e(TAG, "Error purchasing: " + result);
                _callbackContext.error(Serialization.billingResultToJson(result));
                return;
            }
            Log.d(TAG, "Purchase successful.");
            JSONObject jsonPurchase;
            try {
                jsonPurchase = Serialization.purchaseToJson(purchase);
            } catch (JSONException e) {
                _callbackContext.error(Serialization.billingResultToJson(-1, "Couldn't serialize the purchase"));
                return;
            }
            _callbackContext.success(jsonPurchase);
        }

        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption process finished. Purchase: " + purchase + ", result: " + result);

            purchase.setSku(SkuManager.getInstance().getSku(purchase.getAppstoreName(), purchase.getSku()));

            if (result.isFailure()) {
                Log.e(TAG, "Error while consuming: " + result);
                _callbackContext.error(Serialization.billingResultToJson(result));
                return;
            }
            Log.d(TAG, "Consumption successful. Provisioning.");
            JSONObject jsonPurchase;
            try {
                jsonPurchase = Serialization.purchaseToJson(purchase);
            } catch (JSONException e) {
                _callbackContext.error(Serialization.billingResultToJson(-1, "Couldn't serialize the purchase"));
                return;
            }
            _callbackContext.success(jsonPurchase);
        }
    }

    private void createBroadcasts() {
        Log.d(TAG, "createBroadcasts");
        IntentFilter filter = new IntentFilter(YANDEX_STORE_ACTION_PURCHASE_STATE_CHANGED);
        cordova.getActivity().registerReceiver(_billingReceiver, filter);
    }

    private void destroyBroadcasts() {
        Log.d(TAG, "destroyBroadcasts");
        try {
            cordova.getActivity().unregisterReceiver(_billingReceiver);
        } catch (Exception ex) {
            Log.d(TAG, "destroyBroadcasts exception:\n" + ex.getMessage());
        }
    }

    // Yandex specific
    public static final String YANDEX_STORE_SERVICE = "com.yandex.store.service";
    public static final String YANDEX_STORE_ACTION_PURCHASE_STATE_CHANGED = YANDEX_STORE_SERVICE + ".PURCHASE_STATE_CHANGED";

    private BroadcastReceiver _billingReceiver = new BroadcastReceiver() {
        private static final String TAG = "YandexBillingReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive intent: " + intent);

            if (YANDEX_STORE_ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
                purchaseStateChanged(intent);
            }
        }

        private void purchaseStateChanged(Intent data) {
            Log.d(TAG, "purchaseStateChanged intent: " + data);
            _helper.handleActivityResult(RC_REQUEST, Activity.RESULT_OK, data);
        }
    };
}