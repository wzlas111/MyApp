package com.eastelsoft.util.settinghelper;

import com.eastelsoft.util.GlobalVar;

import android.content.Context;

public class SettingUtility {

    public static final String DEALER_UPDATECODE = "dealer_updatecode";

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
