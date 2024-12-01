package com.starry.myne.api.result.entity;

import java.util.ArrayList;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: Syll</p>
 * <p>Description: Syllable, corresponding to the Syll tag in the XML result</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 12, 2015, 3:49:51 PM
 */
public class Syll {
    /**
     * Starting frame position, each frame corresponds to 10ms
     */
    public int beg_pos;
    /**
     * Ending frame position
     */
    public int end_pos;
    /**
     * Syllable content
     */
    public String content;
    /**
     * Pinyin (cn), number represents tone, 5 indicates neutral tone, e.g., fen1
     */
    public String symbol;
    /**
     * Deletion and insertion message: 0 (correct), 16 (omission), 32 (insertion), 64 (repeat), 128 (replacement)
     */
    public int dp_message;
    /**
     * Duration (unit: frame, each frame corresponds to 10ms) (cn)
     */
    public int time_len;
    /**
     * Phones included in the syllable
     */
    public ArrayList<Phone> phones;

    /**
     * Get the standard phonetic symbol of the syllable (en)
     *
     * @return Standard phonetic symbol
     */
    public String getStdSymbol() {
        String stdSymbol = "";
        String[] symbols = content.split(" ");

        for (int i = 0; i < symbols.length; i++) {
            stdSymbol += Phone.getStdSymbol(symbols[i]);
        }

        return stdSymbol;
    }
}