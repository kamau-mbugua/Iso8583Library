package com.rbs.iso8583corelibrary

import android.app.Application
import com.rbs.iso8583lib.expose.Iso8583Core

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Iso8583Core.init(
            app = this,
            connectionIp = "196.13.200.254",
            connectionPort = "3010"
        )
    }

    companion object {
        fun getIsoCore() = Iso8583Core
    }
}
