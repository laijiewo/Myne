package com.starry.myne.api.result;

import com.starry.myne.api.result.util.ResultFormatUtil;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: ReadSyllableResult</p>
 * <p>Description: Evaluation result for a single Chinese character</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 12, 2015, 5:03:14 PM
 */
public class ReadSyllableResult extends Result {

    public ReadSyllableResult() {
        language = "cn";
        category = "read_syllable";
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[Overall Result]\n")
                .append("Evaluation Content: " + content + "\n")
                .append("Reading Duration: " + time_len + "\n")
                .append("Total Score: " + total_score + "\n\n")
                .append("[Reading Details]").append(ResultFormatUtil.formatDetails_CN(sentences));

        return buffer.toString();
    }
}