package com.zaxcler.mvvmdemo.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zaxcler.baselib.ext.ToastMsg
import com.zaxcler.baselib.utils.ZXActivityManager
import com.zaxcler.baselib.weidget.ZXToast
import com.zaxcler.mvvmdemo.MainActivity
import com.zaxcler.mvvmdemo.R
import com.zaxcler.mvvmdemo.SecondActivity
import kotlinx.android.synthetic.main.main_fragment.*

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
            ZXActivityManager.get().findActivity(MainActivity::class.java.name)
        }
    }

}
