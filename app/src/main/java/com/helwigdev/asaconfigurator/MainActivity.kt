package com.helwigdev.asaconfigurator

import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.backgroundColor

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var enabledView: View? = null
        b_dialup.setOnClickListener(View.OnClickListener { view ->
            b_dialup.background.setColorFilter(Color.CYAN,PorterDuff.Mode.MULTIPLY)
            b_dsl.background.clearColorFilter()
        })
        b_dsl.setOnClickListener(View.OnClickListener { view ->
            b_dsl.background.setColorFilter(Color.CYAN,PorterDuff.Mode.MULTIPLY)
            b_dialup.background.clearColorFilter()
        })


    }
}
