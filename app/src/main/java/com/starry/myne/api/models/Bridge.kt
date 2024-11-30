package com.starry.myne.api.models

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.iflytek.cloud.EvaluatorListener
import com.iflytek.cloud.EvaluatorResult
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.SpeechEvaluator
import com.starry.myne.MainActivity
import com.starry.myne.api.result.xml.XmlResultParser
import com.starry.myne.helpers.toToast


class Bridge(
    private var context: Context,
    private var mIse: SpeechEvaluator?
) {
    private val TAG = "JsBride"
    private var onResultCallback: ((String) -> Unit)? = null
    // 创建语音评测对象，使用lateinit关键字标记为非空
//    private lateinit var mIse:SpeechEvaluator ;
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 1
    }

    init {
        setParams()
    }

    fun setOnResultCallback(callback: (String) -> Unit) {
        this.onResultCallback = callback
    }

    // 评测监听接口
    private val mEvaluatorListener: EvaluatorListener = object : EvaluatorListener {
        override fun onResult(result: EvaluatorResult, isLast: Boolean) {
            if (isLast) {
                val builder = StringBuilder()
                builder.append(result.resultString)
                val mLastResult = builder.toString()

                // 使用 XML 解析器解析结果
                val resultParser = XmlResultParser()
                val parsedResult = resultParser.parse(mLastResult)

                // 获取评分
                val score = parsedResult?.total_score ?: 0.0
                Log.d(TAG, "Parsed Score: $score")

                // 通过回调返回评分
                onResultCallback?.invoke("Score: ${score}")
            }
        }

        override fun onError(error: SpeechError) {
            Log.d(TAG, "evaluator over")
        }

        override fun onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.d(TAG, "evaluator begin")
        }

        override fun onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.d("WebView", "evaluator stoped")
        }

        override fun onVolumeChanged(volume: Int, data: ByteArray) {
            Log.d("WebView", "当前音量：$volume")
            Log.d(TAG, "返回音频数据：" + data.size)
        }

        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    }

    // 启动录音
    fun startRecord(content: String, timeout: String, vad_bos: String, vad_eos: String) {
        Log.e(TAG, "timeout::$timeout")
        Log.e(TAG, "vad_bos::$vad_bos")
        Log.e(TAG, "vad_eos::$vad_eos")
        // 已经有录音权限，执行录音操作
        mIse?.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, timeout)
        mIse?.setParameter(SpeechConstant.VAD_BOS, vad_bos)// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIse?.setParameter(
            SpeechConstant.VAD_EOS,
            vad_eos
        )// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        val int = mIse?.startEvaluating(content, null, mEvaluatorListener)
        if (mIse == null) {
            Log.e(TAG, "mIse is null")
        }
        Log.e(TAG, "startRecord")
        Log.e(TAG, "ErrorCode:$int")
    }

    // 停止录音
    fun stopRecord() {
        Log.e(TAG, "stoping...")
        mIse?.stopEvaluating()
    }

    // 评测语种
    private var language: String? = null

    // 评测题型
    private var category: String? = null

    // 结果等级
    private var result_level: String? = null
    private var mLastResult: String? = null
    private fun setParams() {
//        mIse = SpeechEvaluator.createEvaluator(context, null);
        // 设置评测语言
        language = "en_us"
        // 设置需要评测的类型
        category = "read_sentence"
        // 设置结果等级（中文仅支持complete）
        result_level = "complete"
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        val vad_bos = "5000"
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        val vad_eos = "1800"
        // 语音输入超时时间，即用户最多可以连续说多长时间；
        val speech_timeout = "5000"

        // 设置流式版本所需参数 : ent sub plev
        if ("zh_cn" == language) {
            mIse?.setParameter("ent", "cn_vip")
        }
        if ("en_us" == language) {
            mIse?.setParameter("ent", "en_vip")
        }
        mIse?.setParameter(SpeechConstant.SUBJECT, "ise")
        mIse?.setParameter("plev", "0")

        // 设置评分百分制 使用 ise_unite  rst  extra_ability 参数, 其中ise_unite==0是5分制，==1是百分制
        mIse?.setParameter("ise_unite", "1")
        mIse?.setParameter("rst", "entirety")
        mIse?.setParameter("extra_ability", "syll_phone_err_msg;pitch;multi_dimension")
        mIse?.setParameter(SpeechConstant.LANGUAGE, language)
        mIse?.setParameter(SpeechConstant.ISE_CATEGORY, category)
        mIse?.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8")
//        mIse?.setParameter(SpeechConstant.VAD_BOS, vad_bos)
//        mIse?.setParameter(SpeechConstant.VAD_EOS, vad_eos)
//        mIse?.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, speech_timeout)
        mIse?.setParameter(SpeechConstant.RESULT_LEVEL, result_level)
        mIse?.setParameter(SpeechConstant.AUDIO_FORMAT_AUE, "opus")
        // 设置音频保存路径，保存音频格式支持pcm、wav，
        mIse?.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
        mIse?.setParameter(
            SpeechConstant.ISE_AUDIO_PATH,
            context.getExternalFilesDir("msc")?.absolutePath + "/ise.wav"
        )
        //通过writeaudio方式直接写入音频时才需要此设置
        //mIse?.setParameter(SpeechConstant.AUDIO_SOURCE,"-1");
    }
}
