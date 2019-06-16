package com.zaxcler.baselib.ext

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ScrollView
import com.zaxcler.baselib.weidget.ZXToast

/**
 * 类描述：
 * 作者: Created by zaxcler on 2019/6/14.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */

//控制view是否显示
fun View.setVisible(visible: Boolean){
    visibility = when (visible) {
        true -> View.VISIBLE
        false -> View.GONE
    }
}

/**
 * fragment显示Toast
 */
fun Fragment.ToastMsg(msg:String,duration:Int = 0){
    ZXToast.show(this.context?.applicationContext!!,msg,duration)
}

/**
 * Activity显示Toast
 */
fun Activity.ToastMsg(msg:String,duration:Int = 0){
    ZXToast.show(this.applicationContext,msg,duration)
}

/**
 * Context显示Toast
 */
fun Context.ToastMsg(msg:String,duration:Int = 0){
    ZXToast.show(this.applicationContext,msg,duration)
}

/** 普通view 截图*/
fun View.getBitmap(): Bitmap? {
    //开启绘图缓存
    isDrawingCacheEnabled = true
    buildDrawingCache(true)
    val backache = drawingCache
    return if (backache != null) {
        val bitmap = Bitmap.createBitmap(backache)
        isDrawingCacheEnabled = false
        bitmap
    } else {
        null
    }
}

/** scrollview 截取全部的内容*/
fun ScrollView.getBitmap(): Bitmap? {
    var h = 0
    for (i in 0..childCount) {
        h += getChildAt(i)?.height ?: 0
    }
    val bitmap: Bitmap?
    bitmap = Bitmap.createBitmap(width, h, Bitmap.Config.ARGB_8888)
    bitmap?.setHasAlpha(true)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.TRANSPARENT)
    val bg = background
    bg.draw(canvas)
    bg.setBounds(0, 0, width, h)
    draw(canvas)
    return bitmap
}

/** RecyclerView获取截图*/
fun RecyclerView.getBitmap(): Bitmap? {
    if (adapter == null) {
        return null
    }
    adapter?.let {
        var h = 0
        val cacheSize = java.lang.Runtime.getRuntime().maxMemory() / 1024 / 8  //取1/8的最大内存作为缓冲区
        val cache = android.util.LruCache<String, Bitmap>(cacheSize.toInt())
        for (i in 0..childCount) {
            //手动调用创建和绑定ViewHolder方法，
            val holder = it.createViewHolder(this, it.getItemViewType(i))
            it.onBindViewHolder(holder, i)
            //测量
            holder.itemView.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(width, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED))
            //布局
            holder.itemView.layout(0, 0, holder.itemView.measuredWidth,
                holder.itemView.measuredHeight)
            //开启绘图缓存
            holder.itemView.isDrawingCacheEnabled = true
            holder.itemView.buildDrawingCache()
            val drawingCache = holder.itemView?.drawingCache
            if (drawingCache != null) {
                cache.put(i.toString(), drawingCache)
            }
            h += holder.itemView.measuredHeight
        }
        val bitmap = android.graphics.Bitmap.createBitmap(width, h, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        val bg = background
        bg.draw(canvas)
        bg.setBounds(0, 0, width, h)

        var currentTop = 0f
        val bitmapPaint = android.graphics.Paint()
        for (i in 0..cache.size()) {
            val b = cache[i.toString()]
            canvas.drawBitmap(b, 0f, currentTop, bitmapPaint)
            currentTop += b.height
        }
        return bitmap
    }
    return null
}