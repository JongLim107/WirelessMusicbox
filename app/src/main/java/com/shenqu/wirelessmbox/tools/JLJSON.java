package com.shenqu.wirelessmbox.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JongLim on 2016/11/24.
 */

public class JLJSON {
    private static final String TAG = "JLJSON";
    public static final int ERROR_CODE = -100;

    public static boolean getBoolean(JSONObject jsonObj, String key) {
        if (jsonObj == null || jsonObj.isNull(key))
            return false;

        try {
            return jsonObj.getBoolean(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double getDouble(JSONObject jsonObj, String key) {
        return getDouble(jsonObj, key, 0.0D);
    }

    public static double getDouble(JSONObject jsonObj, String key, double ret) {
        if (jsonObj == null || jsonObj.isNull(key))
            return ret;

        try {
            return Double.parseDouble(jsonObj.getString(key));
        } catch (Exception e) {
            e.printStackTrace();
            return ret;
        }
    }

    public static int getInt(JSONObject jsonObj, String key) {
        if (jsonObj == null || jsonObj.isNull(key))
            return ERROR_CODE;

        try {
            return Integer.parseInt(jsonObj.getString(key));
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_CODE;
        }
    }

    public static long getLong(JSONObject jsonObj, String key) {
        if (jsonObj == null || jsonObj.isNull(key))
            return ERROR_CODE;

        try {
            return Long.parseLong(jsonObj.getString(key));
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_CODE;
        }
    }

    public static String getString(JSONObject jobj, String key) {
        if (jobj == null || jobj.isNull(key))
            return "";

        try {
            return jobj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static JSONObject getJSONObject(JSONObject jobj, String key) {
        if (jobj == null || jobj.isNull(key))
            return new JSONObject();

        try {
            return jobj.getJSONObject(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONObject getJSONObject(JSONArray jarray, int index) {
        if (jarray == null || jarray.isNull(index))
            return new JSONObject();
        try {
            return jarray.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONArray getJSONArray(JSONObject jobj, String key) {
        if (jobj == null || jobj.isNull(key))
            return new JSONArray();

        try {
            return jobj.getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static String replace(String key) {
        String str = key;
        if (key != null) {
            str = key.replace("\"", "'");
        }
        return str;
    }
}
