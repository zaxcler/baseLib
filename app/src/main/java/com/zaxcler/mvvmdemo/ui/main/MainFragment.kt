package com.zaxcler.mvvmdemo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.zaxcler.baselib.ext.ToastMsg
import com.zaxcler.baselib.http.ZXNetManager
import com.zaxcler.baselib.utils.ZXActivityManager
import com.zaxcler.baselib.utils.ZXLog
import com.zaxcler.baselib.weidget.ZXToast
import com.zaxcler.mvvmdemo.MainActivity
import com.zaxcler.mvvmdemo.R
import com.zaxcler.mvvmdemo.R.id.button
import com.zaxcler.mvvmdemo.R.id.button2
import com.zaxcler.mvvmdemo.SecondActivity
import com.zaxcler.mvvmdemo.data.TestApi
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.function.Consumer

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    var a = 1
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        button.setOnClickListener {
            val intent = Intent(context,SecondActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            ToastMsg(ZXActivityManager.get().findActivity(MainActivity::class.java.name)?.localClassName?:"")

        }
        val testApi = ZXNetManager.get().createService(TestApi::class.java)
        testApi?.let {
            testApi.sendSmsCode("18723580580","1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object  : Observer<Any>{
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: Any) {
                        ZXLog.d("")
                    }

                    override fun onError(e: Throwable) {
                        ZXLog.d(e.toString())
                    }
                })

        }

    }

}
