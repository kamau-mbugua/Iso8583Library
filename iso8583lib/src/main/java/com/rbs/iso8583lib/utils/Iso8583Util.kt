package com.rbs.iso8583lib.utils

import com.rbs.iso8583lib.model.Iso8583Message
import org.jpos.iso.ISOMsg
import org.jpos.iso.packager.GenericPackager

class Iso8583Util(private val packager: GenericPackager) {

    // Method to create an ISOMsg object with only required fields
    fun createISOMsg(message: Iso8583Message): ISOMsg {
        return ISOMsg().apply {
            packager = this@Iso8583Util.packager
            mti = message.messageType
            setIfNotNull(2, message.p2)
            setIfNotNull(3, message.p3)
            setIfNotNull(4, message.p4)
            setIfNotNull(11, message.p11)
            setIfNotNull(12, message.p12)
            setIfNotNull(13, message.p13)
            setIfNotNull(14, message.p14)
            setIfNotNull(22, message.p22)
            setIfNotNull(23, message.p23)
            setIfNotNull(25, message.p25)
            setIfNotNull(35, message.p35)
            setIfNotNull(37, message.p37)
            setIfNotNull(38, message.p38)
            setIfNotNull(39, message.p39)
            setIfNotNull(41, message.p41)
            setIfNotNull(42, message.p42)
            setIfNotNull(45, message.p45)
            setIfNotNull(48, message.p48)
            setIfNotNull(49, message.p49)
            setIfNotNull(52, message.p52)
            setIfNotNull(54, message.p54)
            setIfNotNull(55, message.p55)
            setIfNotNull(57, message.p57)
            setIfNotNull(58, message.p58)
            setIfNotNull(59, message.p59)
            setIfNotNull(60, message.p60)
            setIfNotNull(62, message.p62)
            setIfNotNull(63, message.p63)
            setIfNotNull(64, message.p64)
        }
    }

    // Packs the ISOMsg into a byte array
    fun pack(message: Iso8583Message): ByteArray {
        val isoMsg = createISOMsg(message)
        return isoMsg.pack()
    }

    // Unpacks a byte array into an Iso8583Message object with only required fields
    fun unpack(messageBytes: ByteArray): Iso8583Message {
        val isoMsg = ISOMsg().apply {
            packager = this@Iso8583Util.packager
            unpack(messageBytes)
        }

        return Iso8583Message(
            messageType = isoMsg.mti,
            p2 = isoMsg.getString(2),
            p3 = isoMsg.getString(3),
            p4 = isoMsg.getString(4),
            p11 = isoMsg.getString(11),
            p12 = isoMsg.getString(12),
            p13 = isoMsg.getString(13),
            p14 = isoMsg.getString(14),
            p22 = isoMsg.getString(22),
            p23 = isoMsg.getString(23),
            p25 = isoMsg.getString(25),
            p35 = isoMsg.getString(35),
            p37 = isoMsg.getString(37),
            p38 = isoMsg.getString(38),
            p39 = isoMsg.getString(39),
            p41 = isoMsg.getString(41),
            p42 = isoMsg.getString(42),
            p45 = isoMsg.getString(45),
            p48 = isoMsg.getString(48),
            p49 = isoMsg.getString(49),
            p52 = isoMsg.getString(52),
            p54 = isoMsg.getString(54),
            p55 = isoMsg.getString(55),
            p57 = isoMsg.getString(57),
            p58 = isoMsg.getString(58),
            p59 = isoMsg.getString(59),
            p60 = isoMsg.getString(60),
            p62 = isoMsg.getString(62),
            p63 = isoMsg.getString(63),
            p64 = isoMsg.getString(64)
        )
    }

    // Helper function to set field values only if they are not null
    private fun ISOMsg.setIfNotNull(fieldNumber: Int, value: String?) {
        value?.let { set(fieldNumber, it) }
    }
}
