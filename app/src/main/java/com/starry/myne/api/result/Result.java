package com.starry.myne.api.result;

import com.starry.myne.api.result.entity.Sentence;

import java.util.ArrayList;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: Result</p>
 * <p>Description: Class representing the evaluation result</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 12, 2015, 4:58:38 PM
 */
public class Result {
    /**
     * Evaluation language: en (English), cn (Chinese)
     */
    public String language;
    /**
     * Evaluation category: read_syllable (cn single character), read_word (word), read_sentence (sentence)
     */
    public String category;
    /**
     * Start frame position, each frame is equivalent to 10ms
     */
    public int beg_pos;
    /**
     * End frame position
     */
    public int end_pos;
    /**
     * Evaluation content
     */
    public String content;
    /**
     * Total score
     */
    public float total_score;
    /**
     * Duration (cn)
     */
    public int time_len;
    /**
     * Exception information (en)
     */
    public String except_info;
    /**
     * Whether misreading occurred (cn)
     */
    public boolean is_rejected;
    /**
     * List of sentences corresponding to the xml result's sentence tag
     */
    public ArrayList<Sentence> sentences;
}