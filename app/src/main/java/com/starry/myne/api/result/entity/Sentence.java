package com.starry.myne.api.result.entity;

import java.util.ArrayList;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: Sentence</p>
 * <p>Description: Sentence, corresponding to the sentence tag in the XML result</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 12, 2015, 4:10:09 PM
 */
public class Sentence {
    /**
     * Starting frame position, each frame corresponds to 10ms
     */
    public int beg_pos;
    /**
     * Ending frame position
     */
    public int end_pos;
    /**
     * Sentence content
     */
    public String content;
    /**
     * Total score
     */
    public float total_score;
    /**
     * Duration (unit: frame, each frame corresponds to 10ms) (cn)
     */
    public int time_len;
    /**
     * Index of the sentence (en)
     */
    public int index;
    /**
     * Word count (en)
     */
    public int word_count;
    /**
     * Words included in the sentence
     */
    public ArrayList<Word> words;
}
