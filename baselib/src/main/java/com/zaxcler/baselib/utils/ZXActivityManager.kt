package com.zaxcler.baselib.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

/**
 * 类描述：统一的activity管理类 使用该类需要再application中 调用注册方法 #registerManager(application: Application)
 * 作者: Created by zaxcler on 2019/6/15.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */

class ZXActivityManager {
    private val mActivates = arrayListOf<WeakReference<Activity>>()//保存所有打开的activity
    private val mActionGroups = WeakHashMap<String, ArrayList<WeakReference<String>>>()//保存一组特定的动作的activity,方便一起关闭
    private var mForegroundActivityCounts = 0 //前台activity数量
    private var mIsAppInForeground = true // app 是否在前台
    private var mAppStateListener : AppStateChangeListener? =null //前后台切换监听

    companion object {
        fun get(): ZXActivityManager {
            return Holder.instance
        }
    }

    object Holder {
        val instance = ZXActivityManager()
    }

    /**
     * 注册管理者
     */
    fun registerManager(application: Application) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
                activity?.let {
                    mForegroundActivityCounts--
                    if (mForegroundActivityCounts ==0){
                        //app切换到后台
                        mIsAppInForeground = false
                        mAppStateListener?.appOnBackground()
                    }
                }
            }

            override fun onActivityResumed(activity: Activity?) {
                activity?.let {
                    if (mForegroundActivityCounts ==0){
                        //app切换到前台
                        mIsAppInForeground = true
                        mAppStateListener?.appOnForeground()
                    }
                    mForegroundActivityCounts++

                }

            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
                activity?.let {
                    removeActivity(it)
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                activity?.let {
                    addActivity(it)
                }
            }

        })
    }

    //设置前后台切换监听
    fun setAppStateChangeLisenter(appStateChangeListener: AppStateChangeListener){
        this.mAppStateListener = appStateChangeListener
    }

    //判断app是否在前台
    fun isAppInForeground():Boolean{
        return mIsAppInForeground
    }

    //获取栈顶activity
    fun getTopActivity(): Activity? {
        if (mActivates.size >= 1) {
            val i = mActivates.size - 1
            return mActivates[i].get()
        }
        return null
    }

    //删除activity
    fun removeActivity(activity: Activity) {
        val iterator = mActivates.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().get() == activity) {
                iterator.remove()
            }
        }
    }

    //添加activity
    fun addActivity(activity: Activity) {
        mActivates.add(WeakReference(activity))
    }

    //退出activity
    fun finishActivity(activity: Activity) {
        removeActivity(activity)
        activity.finish()
    }

    //退出所有activity
    fun finishAllActivity() {
        for (weak in mActivates) {
            val activity = weak.get()
            activity?.let {
                removeActivity(it)
            }
        }
    }

    //根据类名找activity
    fun findActivity(className: String): Activity? {
        val iterator = mActivates.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next().get()
            activity?.let {
                if (className == it::class.java.name) {
                    return it
                }
            }
        }
        return null
    }

    //添加到特定的动作组，以action为key
    fun addToActionGroup(action: String, activity: Activity) {
        var actionGroup = mActionGroups[action]
        if (actionGroup == null) {
            actionGroup = arrayListOf()
        }
        actionGroup.add(WeakReference(activity::class.java.name))
        mActionGroups[action] = actionGroup
    }

    //根据分组action关闭对应的所有activity
    fun finishGroupByAction(action: String) {
        val actionGroup = mActionGroups[action]
        actionGroup?.let {
            for (wks in it) {
                val name = wks.get()
                name?.let { key ->
                    val activity = findActivity(key)
                    activity?.let {
                        finishActivity(activity)
                    }
                }
            }
        }
    }


    interface AppStateChangeListener{
        //app在前台
        fun appOnForeground()
        //APP 在后台
        fun appOnBackground()
    }

}