package com.zaxcler.baselib.weidget

import android.content.Context
import android.widget.Toast

/**
 * 类描述：toast单例
 * 作者: Created by zaxcler on 2019/6/14.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */

class ZXToast {

    companion object {
        private var mToast: Toast? = null
        /**
         * @msg 显示的信息
         * @duration 显示时间 0 短 1 长
         */
        fun show(context: Context,msg: String?, duration: Int =0) {
            msg?.let {
                if (mToast ==null){
                    mToast  = Toast.makeText(context.applicationContext,msg,duration)
                    mToast?.show()
                }else{
                    mToast?.cancel()
                    mToast = null
                    mToast  = Toast.makeText(context.applicationContext,msg,duration)
                    mToast?.show()
                }
            }
        }
    }

}