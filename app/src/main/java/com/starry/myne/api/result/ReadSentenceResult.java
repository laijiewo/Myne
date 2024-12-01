package com.starry.myne.api.result;

import com.starry.myne.api.result.util.ResultFormatUtil;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: ReadSentenceResult</p>
 * <p>Description: Class representing the result of reading a sentence</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 12, 2015, 5:04:14 PM
 */
public class ReadSentenceResult extends Result {

    public ReadSentenceResult() {
        category = "read_sentence";
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        if ("cn".equals(language)) {
            buffer.append("[Overall Result]\n")
                    .append("Evaluation Content: " + content + "\n")
                    .append("Reading Duration: " + time_len + "\n")
                    .append("Total Score: " + total_score + "\n\n")
                    .append("[Reading Details]").append(ResultFormatUtil.formatDetails_CN(sentences));
        } else {
            if (is_rejected) {
                buffer.append("Misreading detected, ")
                        .append("except_info: " + except_info + "\n\n");    // For except_info codes, refer to the "Speech Evaluation Parameters and Results Documentation"
            }

            buffer.append("[Overall Result]\n")
                    .append("Evaluation Content: " + content + "\n")
                    .append("Total Score: " + total_score + "\n\n")
                    .append("[Reading Details]").append(ResultFormatUtil.formatDetails_EN(sentences));
        }

        return buffer.toString();
    }
}