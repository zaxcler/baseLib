package com.zaxcler.mvvmdemo

import android.app.Application
import com.zaxcler.baselib.http.RequestEncryptInterceptor
import com.zaxcler.baselib.http.ResponseDecryptInterceptor
import com.zaxcler.baselib.http.ZXNetManager
import com.zaxcler.baselib.utils.ZXActivityManager
import com.zaxcler.baselib.utils.ZXLog

/**
 * 类描述：
 * 作者: Created by zaxcler on 2019/6/15.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */
class App : Application() {


    override fun onCreate() {
        super.onCreate()
        ZXActivityManager.get().registerManager(this)
        val requestEncryptInterceptor = object : RequestEncryptInterceptor("http://www.zaxcler.top"){
            override fun encryptBodyParameter(requestData: String?): String? {
                return ""
            }

            override fun encryptQueryParameter(apiPath: String, parameter: String?): String? {
                return ""
            }

        }
        ZXNetManager
            .get()
            .baseUrl("http://www.zaxcler.top:8080")
            .addInterceptor(requestEncryptInterceptor)
            .build()
        ZXLog.initLog()
    }
}