package com.rbs.iso8583corelibrary

import android.app.Application
import com.rbs.iso8583lib.expose.Iso8583Core

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Iso8583Core.init(
            app = this,
            connectionIp = BuildConfig.ISO8583_HOST,// change this to your server IP
            connectionPort = BuildConfig.ISO8583_PORT // change this to your server port
        )
    }

    companion object {
        fun getIsoCore() = Iso8583Core
    }
}
