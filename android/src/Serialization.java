package org.onepf.openiab.cordova;

import android.util.Log;

import org.onepf.openiab.cordova.OpenIabCordovaPlugin;
import com.amazon.inapp.purchasing.PurchaseResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.onepf.oms.appstore.googleUtils.IabResult;
import org.onepf.oms.appstore.googleUtils.Purchase;
import org.onepf.oms.appstore.googleUtils.SkuDetails;

public class Serialization {

    /**
     * Serialize purchase data to json
     * @param purchase purchase data
     * @return json string
     * @throws org.json.JSONException
     */
    public static JSONObject purchaseToJson(Purchase purchase) throws JSONException {
        JSONObject j = new JSONObject();
        j.put("itemType", purchase.getItemType());
        j.put("orderId", purchase.getOrderId());
        j.put("packageName", purchase.getPackageName());
        j.put("sku", purchase.getSku());
        j.put("purchaseTime", purchase.getPurchaseTime());
        j.put("purchaseState", purchase.getPurchaseState());
        j.put("developerPayload", purchase.getDeveloperPayload());
        j.put("token", purchase.getToken());
        j.put("originalJson", purchase.getOriginalJson());
        j.put("signature", purchase.getSignature());
        j.put("appstoreName", purchase.getAppstoreName());
        return j;
    }

    public static JSONObject billingResultToJson(IabResult result) {
        return billingResultToJson(result.getResponse(), result.getMessage());
    }

    public static JSONObject billingResultToJson(int responseCode, String errorMessage) {
        JSONObject j = new JSONObject();
        try {
            j.put("responseCode", responseCode);
            j.put("errorMessage", errorMessage);
        } catch (JSONException e) {
            Log.e(OpenIabCordovaPlugin.TAG, e.getMessage());
        }
        return j;
    }

    /**
     * Serialize sku details data to json
     * @param skuDetails sku details data
     * @return json string
     * @throws JSONException
     */
    // TODO: serialize to JSONObject
    public static String skuDetailsToJson(SkuDetails skuDetails) throws JSONException {
        return new JSONStringer().object()
                .key("itemType").value(skuDetails.getItemType())
                .key("sku").value(skuDetails.getSku())
                .key("type").value(skuDetails.getType())
                .key("price").value(skuDetails.getPrice())
                .key("title").value(skuDetails.getTitle())
                .key("description").value(skuDetails.getDescription())
                .key("json").value(skuDetails.getJson())
                .endObject().toString();
    }
}
