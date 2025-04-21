package com.rbs.iso8583lib.utils.packager


import android.content.Context
import org.jpos.iso.ISOException
import org.jpos.iso.packager.GenericPackager
import java.io.IOException

object PackagerProvider {

    private var packager: GenericPackager? = null

    fun init(context: Context) {
        if (packager == null) {
            try {
                packager = GenericPackager(context.assets.open("packager.xml"))
            } catch (e: Exception) {
                throw RuntimeException("Failed to load packager.xml", e)
            }
        }
    }

    fun get(): GenericPackager {
        return packager ?: throw IllegalStateException("Packager not initialized. Call PackagerProvider.init(context) first.")
    }
}

