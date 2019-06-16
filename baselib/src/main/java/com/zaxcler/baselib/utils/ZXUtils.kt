package com.zaxcler.baselib.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.webkit.WebSettings

/**
 * 类描述：基础的一些工具
 * 作者: Created by zaxcler on 2019/6/14.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */



/**
 * 转换像素工具
 */
fun dip2px(dpValue: Float): Float {
    val scale = Resources.getSystem().displayMetrics.density
    return dpValue * scale + 0.5f
}

/**
 * 转换像素工具
 */
fun px2dip(pxValue: Float): Float {
    var scale = Resources.getSystem().displayMetrics.density
    return (pxValue / scale + 0.5f)
}

/**
 * 获取屏幕宽高
 */
fun getScreenSize(): DisplayMetrics {
    return Resources.getSystem().displayMetrics
}

/**
 * 计算合适的大小
 * @w 用来计算宽高比
 * @h 用来计算宽高比
 * @exceptW 按期望的宽来算高 两者都存在则直接返回
 * @exceptH 按期望的高来算宽 两者都存在则直接返回
 * */
fun calculatePerfectSize(w: Float, h: Float, exceptW: Float = 0f, exceptH: Float = 0f): FloatArray {
    var resultH = 0f
    var resultW = 0f
    val result = FloatArray(2)
    if (exceptH == 0f && exceptH != 0f) {
        resultW = exceptW
        val scale = h / w
        resultH = exceptW * scale
        result[0] = resultW
        result[1] = resultH
    } else if (exceptW == 0f && exceptH != 0f) {
        resultH = exceptH
        val scale = w / h
        resultW = exceptH * scale
        result[0] = resultW
        result[1] = resultH
    } else if (exceptH != 0f && exceptW != 0f) {
        result[0] = exceptW
        result[1] = exceptH
    }
    return result
}

/**
 * 判断是否是http链接
 * */
fun isHttp(url: String?): Boolean {
    if (url == null)
        return false
    if (url.startsWith("http", true)) return true
    if (url.startsWith("https", true)) return true
    return false
}

/**
 * 获取设备唯一标识
 * 先获取imei  如果有效则返回
 * 否则 获取androidid 返回
 * 如果androidid 为null 则自己拼凑唯一标示
 * */
fun getUdId(mContext: Context?): String {

    if (mContext != null) {
        val permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //唯一标识
            //1 compute IMEI
            val msg = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val Imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                msg.imei
            } else {
                msg.deviceId
            } // Requires READ_PHONE_STATE

            if (Imei != null && "000000000000000" != Imei) {
                return Imei
            }

            //3 android ID - unreliable
            val m_szAndroidID = Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)

            return m_szAndroidID ?: "35"+ //we make this look like a valid IMEI
            Build.BOARD.length % 10+ Build.BRAND.length % 10+
            Build.CPU_ABI.length % 10+ Build.DEVICE.length % 10+
            Build.DISPLAY.length % 10+ Build.HOST.length % 10+
            Build.ID.length % 10+ Build.MANUFACTURER.length % 10+
            Build.MODEL.length % 10+ Build.PRODUCT.length % 10+
            Build.TAGS.length % 10+ Build.TYPE.length % 10+
            Build.USER.length % 10

            //2 compute DEVICE ID
        }
    }
    return  "35"+ //we make this look like a valid IMEI
            Build.BOARD.length % 10+ Build.BRAND.length % 10+
            Build.CPU_ABI.length % 10+ Build.DEVICE.length % 10+
            Build.DISPLAY.length % 10+ Build.HOST.length % 10+
            Build.ID.length % 10+ Build.MANUFACTURER.length % 10+
            Build.MODEL.length % 10+ Build.PRODUCT.length % 10+
            Build.TAGS.length % 10+ Build.TYPE.length % 10+
            Build.USER.length % 10
}

//获取webview的useragent
fun getUserAgent(context: Context): String {
    val userAgent : String =
        try {
            WebSettings.getDefaultUserAgent(context)
        } catch (e: Exception) {
            System.getProperty("http.agent")?:"unknown"
        }

    val sb = StringBuffer()
    var i = 0
    val length = userAgent.length
    while (i < length) {
        val c = userAgent[i]
        if (c <= '\u001f' || c >= '\u007f') {
            sb.append(String.format("\\u%04x", c.toInt()))
        } else {
            sb.append(c)
        }
        i++
    }
    return sb.toString()
}


//判断字符串是否是null 或者0 或者空字符串 ""
fun isNotNullAndNotEmptyAndZero(data : String?): Boolean{
    return data!=null && data.isNotEmpty() && "0"!=data
}