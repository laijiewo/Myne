package com.starry.myne.api.result;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: FinalResult</p>
 * <p>Description: Class representing the final result of the evaluation</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 14, 2015, 11:12:58 AM
 */
public class FinalResult extends Result {

    public int ret;

    public float total_score;

    @Override
    public String toString() {
        return "Return value: " + ret + ", Total score: " + total_score;
    }
}