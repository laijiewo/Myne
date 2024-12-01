/**
 * This is provided by the iFlytek speech evaluation API.
 */
package com.starry.myne.api.result.util;

import com.starry.myne.api.result.entity.Phone;
import com.starry.myne.api.result.entity.Sentence;
import com.starry.myne.api.result.entity.Syll;
import com.starry.myne.api.result.entity.Word;

import java.util.ArrayList;

/**
 * This is provided by the iFlytek speech evaluation API.
 *
 * <p>Title: ResultFormatUtil</p>
 * <p>Description: Utility class to format the evaluation result details</p>
 * <p>Company: www.iflytek.com</p>
 * Author: iflytek
 * Date: January 19, 2015, 10:01:14 AM
 */
public class ResultFormatUtil {

    /**
     * Formats English evaluation details
     *
     * @param sentences Array of sentences to format
     * @return Formatted English evaluation details
     */
    public static String formatDetails_EN(ArrayList<Sentence> sentences) {
        StringBuffer buffer = new StringBuffer();
        if (null == sentences) {
            return buffer.toString();
        }

        for (Sentence sentence : sentences) {
            if ("Noise".equals(ResultTranslateUtil.getContent(sentence.content))
                    || "Silence".equals(ResultTranslateUtil.getContent(sentence.content))) {
                continue;
            }

            if (null == sentence.words) {
                continue;
            }
            for (Word word : sentence.words) {
                if ("Noise".equals(ResultTranslateUtil.getContent(word.content))
                        || "Silence".equals(ResultTranslateUtil.getContent(word.content))) {
                    continue;
                }

                buffer.append("\nWord [" + ResultTranslateUtil.getContent(word.content) + "] ")
                        .append("Pronunciation: " + ResultTranslateUtil.getDpMessageInfo(word.dp_message))
                        .append(" Score: " + word.total_score);
                if (null == word.sylls) {
                    buffer.append("\n");
                    continue;
                }

                for (Syll syll : word.sylls) {
                    buffer.append("\n└Syllable [" + ResultTranslateUtil.getContent(syll.getStdSymbol()) + "] ");
                    if (null == syll.phones) {
                        continue;
                    }

                    for (Phone phone : syll.phones) {
                        buffer.append("\n\t└Phone [" + ResultTranslateUtil.getContent(phone.getStdSymbol()) + "] ")
                                .append(" Pronunciation: " + ResultTranslateUtil.getDpMessageInfo(phone.dp_message));
                    }

                }
                buffer.append("\n");
            }
        }

        return buffer.toString();
    }

    /**
     * Formats Chinese evaluation details
     *
     * @param sentences Array of sentences to format
     * @return Formatted Chinese evaluation details
     */
    public static String formatDetails_CN(ArrayList<Sentence> sentences) {
        StringBuffer buffer = new StringBuffer();
        if (null == sentences) {
            return buffer.toString();
        }

        for (Sentence sentence : sentences) {
            if (null == sentence.words) {
                continue;
            }

            for (Word word : sentence.words) {
                buffer.append("\nWord [" + ResultTranslateUtil.getContent(word.content) + "] " + word.symbol + " Duration: " + word.time_len);
                if (null == word.sylls) {
                    continue;
                }

                for (Syll syll : word.sylls) {
                    if ("Noise".equals(ResultTranslateUtil.getContent(syll.content))
                            || "Silence".equals(ResultTranslateUtil.getContent(syll.content))) {
                        continue;
                    }

                    buffer.append("\n└Syllable [" + ResultTranslateUtil.getContent(syll.content) + "] " + syll.symbol + " Duration: " + syll.time_len);
                    if (null == syll.phones) {
                        continue;
                    }

                    for (Phone phone : syll.phones) {
                        buffer.append("\n\t└Phone [" + ResultTranslateUtil.getContent(phone.content) + "] " + "Duration: " + phone.time_len)
                                .append(" Pronunciation: " + ResultTranslateUtil.getDpMessageInfo(phone.dp_message));
                    }

                }
                buffer.append("\n");
            }
        }

        return buffer.toString();
    }
}
