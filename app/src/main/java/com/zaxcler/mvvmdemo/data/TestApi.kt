package com.zaxcler.mvvmdemo.data

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 类描述：
 * 作者: Created by zaxcler on 2019/6/21.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */
interface TestApi{

    @FormUrlEncoded
    @POST("api/sendSmsCode")
    fun sendSmsCode(@Field("phone") phone:String,@Field("type") type:String):Observable<Any>

    @POST("api/sendSmsCode")
    fun sendSmsCode(@Body requestBody: RequestBody):Observable<Any>
}