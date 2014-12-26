package com.eastelsoft.util.settinghelper;

import com.eastelsoft.util.GlobalVar;

import android.content.Context;

public class SettingUtility {

    public static final String DEALER_UPDATECODE = "dealer_updatecode";
    public static final String CLIENT_UPDATECODE = "client_updatecode";
    public static final String CLIENT_TYPE_UPDATECODE = "client_type_updatecode";
    public static final String CLIENT_REGION_UPDATECODE = "client_region_updatecode";
    public static final String VISIT_EVALUATE_UPDATECODE = "visit_evaluate_updatecode";
    public static final String PRODUCT_TYPE_UPDATECODE = "product_type_updatecode";
    public static final String ORDER_TYPE_UPDATECODE = "order_type_updatecode";
    public static final String ENTERPRISE_TYPE_UPDATECODE = "enterprise_type_updatecode";
    public static final String COMMODITY_UPDATECODE = "commodity_updatecode";
    public static final String COMMODITY_REASON_UPDATECODE = "commodity_reason_updatecode";
    
    
    private SettingUtility() {}

    public static void setValue(String key, String value) {
        SettingHelper.setEditor(getContext(), key, value);
    }
    
    public static String getUpdatecodeValue(String key) {
    	return SettingHelper.getSharedPreferences(getContext(), key, "0");
    }

    public static String getValue(String key) {
        return SettingHelper.getSharedPreferences(getContext(), key, "");
    }

    private static Context getContext() {
        return GlobalVar.getInstance();
    }

}
