package com.zaxcler.baselib.http

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 类描述：添加header的拦截器
 * 作者: Created by zaxcler on 2019/6/15.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */

class HeaderInterceptor : Interceptor {

    var mHeaders = HashMap<String,String>()
    var mHeaderInterface : HeaderInterface?  = null
    constructor()
    constructor(headers : HashMap<String,String>){
        mHeaders.putAll(headers)
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder =  request.newBuilder()
        mHeaderInterface?.addHeader(mHeaders)
        for (k in mHeaders.keys){
            val v = mHeaders[k]
            v?.let {
                builder.addHeader(k,it)
            }
        }
        return chain.proceed(builder.build())
    }

    interface HeaderInterface{
        fun addHeader(headers:HashMap<String,String>)
    }

}
