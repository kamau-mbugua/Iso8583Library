package com.rbs.iso8583corelibrary

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rbs.iso8583lib.expose.Iso8583Core
import com.rbs.iso8583lib.expose.Iso8583Core.generateSignOnRequests
import com.rbs.iso8583lib.iso.generateSignOnRequest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        signOn()
    }

    fun signOn() {
        val signOnMessage = generateSignOnRequests()
        Iso8583Core.sendTransaction(signOnMessage)
    }
}