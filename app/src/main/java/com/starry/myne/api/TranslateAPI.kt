import okhttp3.*
import com.google.gson.Gson
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

// 定义翻译结果的数据模型
data class TranslateResult(
    val from: String,
    val to: String,
    val trans_result: List<TransResult>
)

data class TransResult(
    val src: String, // 原始文本
    val dst: String  // 翻译后的文本
)

fun translateWithDelay(
    text: String,
    targetLanguage: String,
    callback: (String?) -> Unit
) {
    // 调用实际的翻译函数
    translate(text, targetLanguage, callback)
}

// 翻译方法
fun translate(
    text: String,
    targetLanguage: String,
    callback: (String?) -> Unit
) {
    val appId = "20241128002213598"
    val key = "oNiOkKiqlTvBRv1vP9vf"
    val fromLanguage = "en" // 自动检测源语言
    val salt = System.currentTimeMillis().toString() // 随机数
    val sign = toMD5("$appId$text$salt$key") // 生成签名

    // 请求 URL
    val url = "https://fanyi-api.baidu.com/api/trans/vip/translate" +
            "?appid=$appId" +
            "&q=${text.urlEncode()}" +
            "&from=$fromLanguage" +
            "&to=$targetLanguage" +
            "&salt=$salt" +
            "&sign=$sign"

    println("请求的 URL: $url") // 打印请求的 URL，确认其正确性
    val client = OkHttpClient()
    val request = Request.Builder().url(url).get().build()

    // 异步请求翻译
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("翻译请求失败: ${e.message}")
            callback(null) // 请求失败
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                try {
                    val responseBody = response.body?.string()
                    val result = Gson().fromJson(responseBody, TranslateResult::class.java)

                    // 检查返回的翻译结果是否为 null 或空
                    val translation = result.trans_result?.firstOrNull()?.dst
                    if (translation != null) {
                        println("翻译结果为: $translation")
                        callback(translation) // 返回翻译结果
                    } else {
                        // 没有翻译结果时打印日志
                        println("翻译结果为空或无效: $responseBody")
                        callback(null)
                    }
                } catch (e: Exception) {
                    // 捕获并打印解析错误
                    println("解析错误: ${e.message}")
                    callback(null)
                }
            } else {
                // 输出 API 错误响应信息
                println("API 响应失败: ${response.code} - ${response.message}")
                callback(null) // 非成功响应
            }
        }
    })
}

fun toMD5(string: String):String {
    try {
        //获取md5加密对象
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        //对字符串加密，返回字节数组
        val digest: ByteArray = instance.digest(string.toByteArray())
        var sb = StringBuffer()
        for (b in digest) {
            //获取低八位有效值
            var i: Int = b.toInt() and 0xff
            //将整数转化为16进制
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                //如果是一位的话，补0
                hexString = "0$hexString"
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return ""
}

// 扩展函数：URL 编码
fun String.urlEncode(): String = java.net.URLEncoder.encode(this, "UTF-8")

