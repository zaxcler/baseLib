package com.zaxcler.baselib.http

import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 类描述：单例网络管理类
 * 作者: Created by zaxcler on 2019/6/14.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */
class ZXNetManager {
    private var mOkHttpClient: OkHttpClient? = null
    private var mRetrofit: Retrofit? = null
    private var mBaseUrl: String = "https://www.baidu.com"
    private var mConnectTimeout = 10L //链接超时 默认10秒
    private var mReadTimeout = 10L //读取超时 默认10秒
    private var mWriteTimeout = 10L //写入超时超时 默认10秒
    private var mDispatcher: Dispatcher? = null // 网络请求分发策略
    private var mMaxRequests = 64 // 网络请求最大并发数量 默认64
    private var mMaxRequestsPerHost = 10 // 单个域名最大并发数 默认10个
    private var mInterceptors = arrayListOf<Interceptor>() // 添加拦截器
    private val mCallAdapterFactorys = arrayListOf<CallAdapter.Factory>() //转换器

    companion object {
        fun get(): ZXNetManager {
            return Holder.instance
        }
    }

    object Holder {
        val instance = ZXNetManager()
    }

    //设置自定义okhttp
    fun okhttpClient(okHttpClient: OkHttpClient): ZXNetManager {
        mOkHttpClient = okHttpClient
        return this
    }

    //设置基础域名
    fun baseUrl(baseUrl: String): ZXNetManager {
        mBaseUrl = baseUrl
        return this
    }

    //链接超时时间
    fun connecntTimeOut(timeOut: Long): ZXNetManager {
        mConnectTimeout = timeOut
        return this
    }

    //读取超时时间
    fun readTimeOut(timeOut: Long): ZXNetManager {
        mReadTimeout = timeOut
        return this
    }

    //写入超时时间
    fun writeTimeOut(timeOut: Long): ZXNetManager {
        mWriteTimeout = timeOut
        return this
    }

    //网络请求最大并发数
    fun maxRequests(maxRequests: Int): ZXNetManager {
        mMaxRequests = maxRequests
        return this
    }

    //单个域名请求最大并发数
    fun maxRequestsPerHost(maxRequestsPerHost: Int): ZXNetManager {
        mMaxRequestsPerHost = maxRequestsPerHost
        return this
    }

    //添加拦截器
    fun addInterceptor(interceptor: Interceptor): ZXNetManager {
        mInterceptors.add(interceptor)
        return this
    }

    //添加转换器
    fun addCallAdapterFactory(factory: CallAdapter.Factory): ZXNetManager {
        mCallAdapterFactorys.add(factory)
        return this
    }

    //构建网络请求retrofit
    fun build() {
        //若没有设置mOkHttpClient 则自己创建一个
        if (mOkHttpClient == null) {
            //若没有设置分发策略则自己创建一个
            if (mDispatcher == null) {
                mDispatcher = Dispatcher()
                mDispatcher?.maxRequests = mMaxRequests
                mDispatcher?.maxRequestsPerHost = mMaxRequestsPerHost
            }

            //初始化网络基础设置
            val httpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(mReadTimeout, TimeUnit.SECONDS)
                .writeTimeout(mWriteTimeout, TimeUnit.SECONDS)
                .dispatcher(mDispatcher!!)
            //添加拦截器
            for (interceptor in mInterceptors) {
                httpClientBuilder.addInterceptor(interceptor)
            }
            val netWorkInterceptor = HttpLoggingInterceptor(HttpLogger())
            netWorkInterceptor.level = HttpLoggingInterceptor.Level.BODY
            //添加网络日志打印
            httpClientBuilder.addInterceptor(netWorkInterceptor)

            mOkHttpClient = httpClientBuilder.build()
        }
        mOkHttpClient?.let {
            val rfBuilder =
                Retrofit.Builder()
                    .client(it)
                    .baseUrl(mBaseUrl)
            for (f in mCallAdapterFactorys) {
                rfBuilder.addCallAdapterFactory(f)
            }
            rfBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            mRetrofit = rfBuilder.addConverterFactory(GsonConverterFactory.create())
                .build()

        }

    }

    //创建服务
    fun <T> createService(clazz: Class<T>): T? {
        return mRetrofit?.create(clazz)
    }

}