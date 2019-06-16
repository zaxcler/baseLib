package com.zaxcler.baselib.utils

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * 类描述：日志打印套壳 添加开关
 * 作者: Created by zaxcler on 2019/6/15.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */

class ZXLog {

    companion object {
        private const val TAG = "ZXLog"
        var printLog = true // 打印日志
        fun initLog() {
            val fs = PrettyFormatStrategy.newBuilder()
                .tag(TAG)
                .build()
            Logger.addLogAdapter(AndroidLogAdapter(fs))
        }


        fun d(any: Any?) {
            if (printLog) {
                Logger.d(any)
            }
        }

        fun d(msg: String, vararg args: String) {
            if (printLog) {
                Logger.d(msg, args)
            }
        }

        fun e(throwable: Throwable?, msg: String, vararg args: String) {
            if (printLog) {
                Logger.e(throwable, msg, args)
            }
        }

        fun e(msg: String, vararg args: String) {
            if (printLog) {
                Logger.e(msg, args)
            }
        }

        fun i(msg: String, vararg args: String) {
            if (printLog) {
                Logger.i(msg, args)
            }
        }

        fun v(msg: String, vararg args: String) {
            if (printLog) {
                Logger.v(msg, args)
            }
        }

        fun w(msg: String, vararg args: String) {
            if (printLog) {
                Logger.w(msg, args)
            }
        }

        fun wtf(msg: String, vararg args: String) {
            if (printLog) {
                Logger.wtf(msg, args)
            }
        }

        fun json(json: String) {
            if (printLog) {
                Logger.json(json)
            }
        }

        fun xml(xml: String?) {
            if (printLog) {
                Logger.xml(xml)
            }
        }
    }

}