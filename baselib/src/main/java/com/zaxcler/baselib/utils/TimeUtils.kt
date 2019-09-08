package com.zaxcler.baselib.utils

import java.lang.Exception
import java.text.SimpleDateFormat


//格式化时间为对应的格式
fun formartTime(time:Long,format:String):String{
    return try {
        val format = SimpleDateFormat(format)
         format.format(time)
    }catch (e:Exception){
         ""
    }
}