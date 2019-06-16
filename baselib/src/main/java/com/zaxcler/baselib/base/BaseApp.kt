package com.zaxcler.baselib.base

import android.app.Application
import com.zaxcler.baselib.utils.ZXActivityManager
import com.zaxcler.baselib.utils.ZXLog

/**
 * 类描述：基础application 可以继承这个 或者 自己继承Application将onCreate()方法中的代码 复制过去
 * 作者: Created by zaxcler on 2019/6/15.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ZXActivityManager.get().registerManager(this)
        ZXLog.initLog()
    }
}
