package com.zaxcler.mvvmdemo

import android.app.Application
import com.zaxcler.baselib.utils.ZXActivityManager

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
    }
}