package com.starry.myne.api.result.entity;

import java.util.HashMap;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: Phone</p>
 * <p>Description: Phoneme, corresponding to the Phone tag in the XML result</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 12, 2015, 3:55:56 PM
 */
public class Phone {
    /**
     * Mapping table between iFlytek phonetic symbols and standard phonetic symbols (en)
     */
    public static HashMap<String, String> phone_map = new HashMap<String, String>();

    static {
        phone_map.put("aa", "ɑ:");
        phone_map.put("oo", "ɔ");
        phone_map.put("ae", "æ");
        phone_map.put("ah", "ʌ");
        phone_map.put("ao", "ɔ:");
        phone_map.put("aw", "aʊ");
        phone_map.put("ax", "ə");
        phone_map.put("ay", "aɪ");
        phone_map.put("eh", "e");
        phone_map.put("er", "ə:");
        phone_map.put("ey", "eɪ");
        phone_map.put("ih", "ɪ");
        phone_map.put("iy", "i:");
        phone_map.put("ow", "əʊ");
        phone_map.put("oy", "ɔɪ");
        phone_map.put("uh", "ʊ");
        phone_map.put("uw", "ʊ:");
        phone_map.put("ch", "tʃ");
        phone_map.put("dh", "ð");
        phone_map.put("hh", "h");
        phone_map.put("jh", "dʒ");
        phone_map.put("ng", "ŋ");
        phone_map.put("sh", "ʃ");
        phone_map.put("th", "θ");
        phone_map.put("zh", "ʒ");
        phone_map.put("y", "j");
        phone_map.put("d", "d");
        phone_map.put("k", "k");
        phone_map.put("l", "l");
        phone_map.put("m", "m");
        phone_map.put("n", "n");
        phone_map.put("b", "b");
        phone_map.put("f", "f");
        phone_map.put("g", "g");
        phone_map.put("p", "p");
        phone_map.put("r", "r");
        phone_map.put("s", "s");
        phone_map.put("t", "t");
        phone_map.put("v", "v");
        phone_map.put("w", "w");
        phone_map.put("z", "z");
        phone_map.put("ar", "eə");
        phone_map.put("ir", "iə");
        phone_map.put("ur", "ʊə");
        phone_map.put("tr", "tr");
        phone_map.put("dr", "dr");
        phone_map.put("ts", "ts");
        phone_map.put("dz", "dz");
    }

    /**
     * Starting frame position, each frame corresponds to 10ms
     */
    public int beg_pos;
    /**
     * Ending frame position
     */
    public int end_pos;
    /**
     * Phoneme content
     */
    public String content;
    /**
     * Deletion and insertion message: 0 (correct), 16 (omission), 32 (insertion), 64 (repeat), 128 (replacement)
     */
    public int dp_message;
    /**
     * Duration (unit: frame, each frame corresponds to 10ms) (cn)
     */
    public int time_len;

    /**
     * Get the standard phonetic symbol corresponding to the content (en)
     */
    public String getStdSymbol() {
        return getStdSymbol(content);
    }

    public static String getStdSymbol(String content) {
        String std = phone_map.get(content);
        return (null == std) ? content : std;
    }

}
