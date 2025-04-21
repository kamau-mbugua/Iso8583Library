package com.rbs.iso8583lib.communication

import com.rbs.iso8583lib.encryption.TripleDESEncryption
import org.jpos.iso.*
import org.jpos.iso.channel.NACChannel
import timber.log.Timber
import java.io.EOFException
import java.io.IOException

/**
 * Example Kotlin channel that:
 *   1) Sends the first 0800 (ProcCode=92) unencrypted.
 *   2) Reads the key from 0810 field 62.
 *   3) Encrypts subsequent messages.
 */
class TranzWareChannel(
    host: String,
    port: Int,
    packager: ISOPackager,
    customHeader: ByteArray
) : NACChannel(host, port, packager, customHeader) {

    private var sessionKey: ByteArray? = null

    /**
     * Override send() in order to encrypt subsequent messages.
     */
    @Throws(IOException::class, ISOException::class)
    override fun send(m: ISOMsg) {
        try{
            Timber.e(
                "TranzWareChannel ----> Packed Field 52: %s",
                ISOUtil.hexString(m.getBytes(52))
            );
            Timber.e(
                "TranzWareChannel ----> Packed Field 55: %s",
                ISOUtil.hexString(m.getBytes(55))
            );
            Timber.e("TranzWareChannel ----> Full Packed Message: %s", ISOUtil.hexString(m.pack()));
        }catch (e: Exception){
            Timber.e("TranzWareChannel ---> Error logging message: ${e.message}")
        }


        if (!isConnected) {
            throw IOException("unconnected ISOChannel")
        }

        try {
            super.send(m)
        }catch (e: Exception){
            Timber.e("TranzWareChannel ---> Error sending message: ${e.message}")
            throw IOException("Error sending message: ${e.message}")
        }

    }

    override fun getMessage(b: ByteArray?, offset: Int, len: Int) {
        super.getMessage(b, offset, len)

    }

    /**
     * Override receive() in order to decrypt if we already have a session key.
     */
    @Throws(IOException::class, ISOException::class)
    override fun receive(): ISOMsg? {
        val m = super.receive() ?: throw EOFException("No data received")

        if (m != null) {
            // If we just got the 0810 that returns the new key
            if (isKeyExchangeResponse(m)) {
                parseAndStoreSessionKey(m)
            }

            if (sessionKey != null) {
                val dataToUnpack = decrypt(m.pack(), sessionKey!!)
                m.unpack(dataToUnpack)
            }

            Timber.d("TranzWareChannel ---> Message received: ${m.pack().contentToString()}")
            return m
        }

        return null
    }

    /**
     * Decide if we need to encrypt. Example logic: if it's 0800/Proc=92 => no encryption, else encrypt if sessionKey != null.
     */
    private fun shouldEncrypt(m: ISOMsg): Boolean {
        val mti = m.mti
        val proc = m.getString(3)
        return when {
            (mti == "0800" && proc == "920000") -> false
            sessionKey == null -> false
            else -> true
        }
    }

    /**
     * Check if this is the 0810 response with proc=920000 that contains the working key.
     */
    private fun isKeyExchangeResponse(resp: ISOMsg): Boolean {
        val mti = resp.mti
        val proc = resp.getString(3)
        return (mti == "0810" && proc == "920000")
    }

    /**
     * Parse the newly returned key from Field 62. Real code must handle subfields properly.
     */
    private fun parseAndStoreSessionKey(resp: ISOMsg) {
        val raw62 = resp.getBytes(62) ?: return
        // e.g. parse the subfield structure for working keys
        sessionKey = decodeWorkingKey(raw62)
        TripleDESEncryption.setFinalPinKey(sessionKey!!)
    }

    private fun bcdToInt(high: Byte, low: Byte): Int {
        val hi = ((high.toInt() and 0xF0) shr 4) * 10 + (high.toInt() and 0x0F)
        val lo = ((low.toInt() and 0xF0) shr 4) * 10 + (low.toInt() and 0x0F)
        return hi * 100 + lo
    }

    private fun decodeWorkingKey(raw62: ByteArray): ByteArray {
        // The first 2 bytes are the extra length in BCD (for example, "00 32" means 32 total bytes for both keys)
        val extraLength = bcdToInt(raw62[0], raw62[1])
        val keyLen = extraLength / 2  // length for one key
        return raw62.copyOfRange(2, 2 + keyLen)
    }



    /**
     * Example encryption (dummy).
     */
    private fun encrypt(plain: ByteArray, key: ByteArray): ByteArray {
        // Insert T-DES/AES logic here
        return plain
    }

    /**
     * Example decryption (dummy).
     */
    private fun decrypt(cipher: ByteArray, key: ByteArray): ByteArray {
        // Insert T-DES/AES logic here
        return cipher
    }
}
