package com.rbs.iso8583corelibrary

import android.app.Application
import com.rbs.iso8583lib.expose.Iso8583Core

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Iso8583Core.init(
            app = this,
            connectionIp = "192.168.1.254",// change this to your server IP
            connectionPort = "55555" // change this to your server port
        )
    }

    companion object {
        fun getIsoCore() = Iso8583Core
    }
}
