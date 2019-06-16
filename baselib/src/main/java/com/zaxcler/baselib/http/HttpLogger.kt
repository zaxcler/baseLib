package com.zaxcler.baselib.http
import com.zaxcler.baselib.utils.ZXLog
import okhttp3.logging.HttpLoggingInterceptor
/**
 * 类描述：网络日志打印
 * 作者: Created by zaxcler on 2019/6/15.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */



/**
 * 类描述：
 * 作者: Created by zaxcler on 2018/1/17.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */
class HttpLogger : HttpLoggingInterceptor.Logger{
    var mMessage = StringBuilder()

    override fun log( message: String?) {
        if (message!!.startsWith("--> POST") || message!!.startsWith("--> GET"))
            mMessage.setLength(0)
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        var m = message
        if ((message.startsWith("{") && message.endsWith("}"))
            || (message.startsWith("[") && message.endsWith("]"))){
            m = formatJson(decodeUnicode(message))
        }
        mMessage.append(m.plus("\n"))
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            ZXLog.d(mMessage.toString())
            mMessage.setLength(0)
        }
    }
}
/**
 * 格式化json字符串
 *
 * @param jsonStr 需要格式化的json串
 * @return 格式化后的json串
 */
fun formatJson(jsonStr: String?): String {
    if (null == jsonStr || "" == jsonStr) return ""
    val sb = StringBuilder()
    var last = '\u0000'
    var current = '\u0000'
    var indent = 0
    for (i in 0 until jsonStr.length) {
        last = current
        current = jsonStr[i]
        //遇到{ [换行，且下一行缩进
        when (current) {
            '{', '[' -> {
                sb.append(current)
                sb.append('\n')
                indent++
                addIndentBlank(sb, indent)
            }
            //遇到} ]换行，当前行缩进
            '}', ']' -> {
                sb.append('\n')
                indent--
                addIndentBlank(sb, indent)
                sb.append(current)
            }
            //遇到,换行
            ',' -> {
                sb.append(current)
                if (last != '\\') {
                    sb.append('\n')
                    addIndentBlank(sb, indent)
                }
            }
            else -> sb.append(current)
        }
    }
    return sb.toString()
}

/**
 * 添加space
 *
 * @param sb
 * @param indent
 */
private fun addIndentBlank(sb: StringBuilder, indent: Int) {
    for (i in 0 until indent) {
        sb.append('\t')
    }
}

/**
 * http 请求数据返回 json 中中文字符为 unicode 编码转汉字转码
 *
 * @param theString
 * @return 转化后的结果.
 */
fun decodeUnicode(theString: String): String {
    var aChar: Char
    val len = theString.length
    val outBuffer = StringBuffer(len)
    var x = 0
    while (x < len) {
        aChar = theString[x++]
        if (aChar == '\\') {
            aChar = theString[x++]
            if (aChar == 'u') {
                var value = 0
                for (i in 0..3) {
                    aChar = theString[x++]
                    when (aChar) {
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> value = (value shl 4) + aChar.toInt() - '0'.toInt()
                        'a', 'b', 'c', 'd', 'e', 'f' -> value = (value shl 4) + 10 + aChar.toInt() - 'a'.toInt()
                        'A', 'B', 'C', 'D', 'E', 'F' -> value = (value shl 4) + 10 + aChar.toInt() - 'A'.toInt()
                        else -> throw IllegalArgumentException(
                            "Malformed   \\uxxxx   encoding.")
                    }

                }
                outBuffer.append(value.toChar())
            } else {
                when(aChar){
                    't' ->  aChar = '\t'
                    'r' ->  aChar ='\r'
                    'n' ->  aChar = '\n'
                    'f' ->  aChar = '\u000C'
                }
                outBuffer.append(aChar)
            }
        } else
            outBuffer.append(aChar)
    }
    return outBuffer.toString()
}
