package com.zaxcler.baselib.http

import com.zaxcler.baselib.utils.ZXLog
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.nio.charset.Charset

/**
 * 类描述：统一的请求解密拦截器（若自己服务器所有接口需要统一的加解密可添加）
 * 作者: Created by zaxcler on 2019/6/15.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */

abstract class ResponseDecryptInterceptor :Interceptor{
    var mNeedDecryptBaseUrl: String? = null //是否需要解密的baseurl

    constructor(needDecryptBaseUrl:String){
        mNeedDecryptBaseUrl = needDecryptBaseUrl
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url()
        val apiPath = "${url.scheme()}://${url.host()}:${url.port()}${url.encodedPath()}".trim()
        var response = chain.proceed(request)
        //请求是否成功
        if (response.isSuccessful){
            //是否是需要解密的url的返回
            if (apiPath.startsWith(mNeedDecryptBaseUrl?:"")){
                val responseBody = response.body()
                try {
                    responseBody?.let {
                        val sources = it.source()
                        sources.request(Long.MAX_VALUE)
                        val buffer = sources.buffer()
                        var charset = Charset.forName("UTF-8")
                        val contentType = responseBody.contentType()
                        if (contentType != null) {
                            charset = contentType.charset(charset)
                        }
                        val bodyString = buffer.clone().readString(charset)
                        val responseData = decryptBodyParameter(bodyString)
                        /*将解密后的明文返回*/
                        val newResponseBody = ResponseBody.create(contentType, responseData.trim())
                        response = response.newBuilder().body(newResponseBody).build()


                        return response
                    }
                }catch (e:Exception){
                    ZXLog.d("数据解密错误 --->$e")
                    return response
                }

            }
        }
        return response
    }

    abstract fun decryptBodyParameter(bodyString: String?): String

}