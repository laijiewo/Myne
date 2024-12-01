package com.starry.myne.api.result.entity;

import java.util.ArrayList;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: Word</p>
 * <p>Description: Word, corresponding to the word tag in the XML result</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 12, 2015, 3:29:30 PM
 */
public class Word {
    /**
     * Starting frame position, each frame corresponds to 10ms
     */
    public int beg_pos;
    /**
     * Ending frame position
     */
    public int end_pos;
    /**
     * Word content
     */
    public String content;
    /**
     * Deletion and insertion message: 0 (correct), 16 (omission), 32 (insertion), 64 (repeat), 128 (replacement)
     */
    public int dp_message;
    /**
     * Global index of the word (en)
     */
    public int global_index;
    /**
     * Index of the word within the sentence (en)
     */
    public int index;
    /**
     * Pinyin (cn), number represents tone, 5 indicates neutral tone, e.g., fen1
     */
    public String symbol;
    /**
     * Duration (unit: frame, each frame corresponds to 10ms) (cn)
     */
    public int time_len;
    /**
     * Total score of the word (en)
     */
    public float total_score;
    /**
     * Syllables included in the word
     */
    public ArrayList<Syll> sylls;
}