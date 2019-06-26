package com.zaxcler.baselib.http

import com.zaxcler.baselib.utils.ZXLog
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.lang.Exception
import java.net.URLDecoder
import java.nio.charset.Charset

/**
 * 类描述：统一的请求加密拦截器（若自己服务器所有接口需要统一的加解密可添加）
 * 作者: Created by zaxcler on 2019/6/15.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */
abstract class RequestEncryptInterceptor : Interceptor {

    var mNeedEncryptBaseUrl: String? = null

    constructor(needEncryptBaseUrl:String){
        mNeedEncryptBaseUrl = needEncryptBaseUrl
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val method = request.method().toUpperCase().trim()
        var charset = Charset.forName("UTF-8")
        val url = request.url()
        val apiPath = "${url.scheme()}://${url.host()}:${url.port()}${url.encodedPath()}".trim()
        //非需要加密的链接不加密
        if (!apiPath.startsWith(mNeedEncryptBaseUrl ?: "")) {
            return chain.proceed(request)
        }

        //get 和 delete 方法参数拼接在链接之后
        if (method == "GET" || method == "DELETE") {

            //参数非空 进行query参数加密
            if (!url.encodedQuery().isNullOrEmpty()) {
                return try {
                    val parameter = url.encodedQuery()
                    val newUrl = encryptQueryParameter(apiPath, parameter)
                    newUrl?.let {
                        request = request.newBuilder().url(it).build()
                    }
                    chain.proceed(request)
                } catch (e: Exception) {
                    e.printStackTrace()
                    chain.proceed(request)
                }
            }
        } else {
            //不是Get和Delete请求时，则请求数据在请求体中
            val body = request.body()
            body?.let { requestBody ->
                val contentType = requestBody.contentType()
                contentType?.let {
                    charset = contentType.charset(charset)
                    //二进制文件直接上传，不进行加密操作
                    if (contentType.type().toLowerCase() == "multipart") {
                        return chain.proceed(request)
                    }
                }
                /*获取请求的数据*/
                try {
                    val buffer = Buffer()
                    requestBody.writeTo(buffer)
                    val requestData = URLDecoder.decode(buffer.readString(charset).trim(), "utf-8")
                    val encryptData = encryptBodyParameter(requestData)
                    encryptData?.let {
                        /*构建新的请求体*/
                        val newRequestBody = RequestBody.create(contentType, encryptData)
                        /*构建新的requestBuilder*/
                        val newRequestBuilder = request.newBuilder()
                        //根据请求方式构建相应的请求
                        when (method) {
                            "POST" -> newRequestBuilder.post(newRequestBody)
                            "PUT" -> newRequestBuilder.put(newRequestBody)
                        }
                        request = newRequestBuilder.build()
                    }
                    return chain.proceed(request)
                } catch (e: Exception) {
                    return chain.proceed(request)
                }
            }
        }
        return chain.proceed(request)
    }

    //进行加密返回  返回null 表示不处理
    abstract fun encryptBodyParameter(requestData: String?): String?

    //进行加密返回  返回null 表示不处理
    abstract fun encryptQueryParameter(apiPath: String, parameter: String?): String?

}