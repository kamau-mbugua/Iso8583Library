package com.rbs.iso8583lib.viewmodel

import androidx.lifecycle.ViewModel
import com.rbs.iso8583lib.model.Iso8583Message
import com.rbs.iso8583lib.utils.Iso8583Util
import org.jpos.iso.ISOMsg
import org.jpos.iso.packager.GenericPackager


class TciViewModel(packager: GenericPackager) : ViewModel() {

    private val isoUtil = Iso8583Util(packager)

    // Return ISOMsg instead of ByteArray
    fun createISOMessage(message: Iso8583Message): ISOMsg {
        return isoUtil.createISOMsg(message)
    }

    // Optional: Keep the existing methods if needed
    fun packMessage(message: Iso8583Message): ByteArray {
        return isoUtil.pack(message)
    }

    fun unpackMessage(messageBytes: ByteArray): Iso8583Message {
        return isoUtil.unpack(messageBytes)
    }
}

