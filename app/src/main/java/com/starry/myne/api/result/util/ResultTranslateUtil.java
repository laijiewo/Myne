package com.starry.myne.api.result.util;

import java.util.HashMap;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: ResultTranslateUtil</p>
 * <p>Description: Utility class to translate result details</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 13, 2015, 6:05:03 PM
 */
public class ResultTranslateUtil {

    private static HashMap<Integer, String> dp_message_map = new HashMap<Integer, String>();
    private static HashMap<String, String> special_content_map = new HashMap<String, String>();

    static {
        dp_message_map.put(0, "Normal");
        dp_message_map.put(16, "Omitted");
        dp_message_map.put(32, "Inserted");
        dp_message_map.put(64, "Repeated");
        dp_message_map.put(128, "Replaced");

        special_content_map.put("sil", "Silence");
        special_content_map.put("silv", "Silence");
        special_content_map.put("fil", "Noise");
    }

    public static String getDpMessageInfo(int dp_message) {
        return dp_message_map.get(dp_message);
    }

    public static String getContent(String content) {
        String val = special_content_map.get(content);
        return (null == val) ? content : val;
    }
}