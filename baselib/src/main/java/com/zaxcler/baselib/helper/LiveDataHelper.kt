package com.zaxcler.baselib.helper

import android.arch.lifecycle.MutableLiveData
import java.util.*

/**
 * 类描述：live事件通知类
 * 作者: Created by zaxcler on 2018/11/6.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */
class LiveDataHelper {

    private val mMap = WeakHashMap<Any, MutableLiveData<Any>?>()

        companion object {
            fun getHelper(): LiveDataHelper {
                return Holder.instance
            }
    }

    object Holder {
        val instance = LiveDataHelper()
    }

    fun get(key: Any): MutableLiveData<Any> {
        if (mMap[key]  == null)
            mMap[key] = MutableLiveData()
        return mMap[key]!!
    }

    fun contains(key: Any):Boolean{
        return mMap[key]!=null
    }


    fun remove(key:Any): LiveDataHelper {
        mMap[key] = null
        mMap.remove(key)
        return this
    }



}