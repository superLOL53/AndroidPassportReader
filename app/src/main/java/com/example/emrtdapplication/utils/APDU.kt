package com.example.emrtdapplication.utils

/**
 * Constants for the class APDU
 */
//const val APDU_TAG = "APDU"
//const val APDU_ENABLE_LOGGING = true
const val MIN_APDU_LENGTH = 4
const val LC_MIN = 1
const val LC_MAX = 255
const val LC_EXT_MAX = 65535
const val LE_MIN = 1
const val LE_MAX = 256
const val LE_EXT_MAX = 65536

/**
 * Class representing an APDU
 * @property classByte The CLASS byte of the APDU
 * @property insByte The INS byte of the APDU
 * @property p1Byte The P1 byte of the APDU
 * @property p2Byte The P2 byte of the APDU
 * @property data The data to be send with the APDU.
 * @property lc The length of the data of the APDU
 * @property useLc Indicates if the Lc field and in extent the [data] is sent with the APDU
 * @property useLcExt Indicates if the APDU uses the extended length for the [lc]
 * @property le The expected length of the received APDU
 * @property useLe Indicates if the received APDU contains any data
 * @property useLeExt Indicates if the APDU uses extended length for [le]
 */
class APDU(private val classByte: Byte, private val insByte: Byte, private val p1Byte: Byte, private val p2Byte: Byte) {
    var lc : Int = 0
        private set
    var useLc = false
        private set
    var useLcExt = false
        private set
    var data : ByteArray = ByteArray(0)
        private set
    var le : Int = 0
        private set
    var useLe = false
        private set
    var useLeExt = false
        private set

    /**
     * Class representing an APDU
     * @param classByte: The CLASS byte of the APDU
     * @param insByte: The INS byte of the APDU
     * @param p1Byte: The P1 byte of the APDU
     * @param p2Byte: The P2 byte of the APDU
     * @param le: The expected length of the reply APDU
     */
    constructor(classByte: Byte, insByte: Byte, p1Byte: Byte, p2Byte: Byte, le : Int) : this(classByte, insByte, p1Byte, p2Byte) {
        calculateLe(le)
    }

    /**
     * Class representing an APDU
     * @param classByte: The CLASS byte of the APDU
     * @param insByte: The INS byte of the APDU
     * @param p1Byte: The P1 byte of the APDU
     * @param p2Byte: The P2 byte of the APDU
     * @param data: The byte array containing the data
     */
    constructor(classByte: Byte, insByte: Byte, p1Byte: Byte, p2Byte: Byte, data : ByteArray) : this(classByte, insByte, p1Byte, p2Byte) {
        if (data.isEmpty() || data.size > LC_EXT_MAX) {
            return
        }
        useLc = true
        lc = data.size
        this.data = data
        if (data.size in LC_MAX+1..LC_EXT_MAX){
            useLcExt = true
        }
    }

    /**
     * Class representing an APDU
     * @param classByte: The CLASS byte of the APDU
     * @param insByte: The INS byte of the APDU
     * @param p1Byte: The P1 byte of the APDU
     * @param p2Byte: The P2 byte of the APDU
     * @param data: The byte array containing the data
     * @param le: The expected length of the reply APDU
     */
    constructor(classByte: Byte, insByte: Byte, p1Byte: Byte, p2Byte: Byte, data : ByteArray, le : Int) : this(classByte, insByte, p1Byte, p2Byte, data) {
        calculateLe(le)
    }

    /**
     *
     */
    private fun calculateLe(le: Int) {
        if (le < LE_MIN || LE_EXT_MAX < le) {
            return
        }
        useLe = true
        this.le = le
        if (le in LE_MAX+1..LE_EXT_MAX) {
            useLeExt = true
        }
    }

    /**
     * Returns the header of the APDU
     * @return Byte array consisting of the CLASS byte, INS byte, P1 byte and P2 byte
     */
    fun getHeader() : ByteArray {
        return byteArrayOf(classByte, insByte, p1Byte, p2Byte)
    }

    /**
     * Returns the whole APDU in a single byte array
     * @return The APDU as a byte array
     */
    fun getByteArray() : ByteArray {
        var apduLength = MIN_APDU_LENGTH
        if (useLe) {
            apduLength += if (useLeExt) {
                3
            } else {
                1
            }
        }
        if (useLc) {
            apduLength += if (useLcExt) {
                3
            } else {
                1
            }
            apduLength += data.size
        }
        //log("APDU Length is $apduLength")
        val ba = ByteArray(apduLength)
        var pos = 0
        ba[pos++] = classByte
        ba[pos++] = insByte
        ba[pos++] = p1Byte
        ba[pos++] = p2Byte
        if (!useLc && !useLe) {
            //log("APDU Array: ", ba)
            return ba
        } else if (!useLc) {
            if (useLeExt) {
                ba[pos++] = 0
                ba[pos++] = le.ushr(8).toByte()
                ba[pos] = (le and 0xFF).toByte()
            } else {
                ba[pos] = le.toByte()
            }
        } else {
            if (useLcExt) {
                ba[pos++] = 0
                ba[pos++] = lc.ushr(8).toByte()
                ba[pos++] = (lc and 0xFF).toByte()
            } else {
                ba[pos++] = lc.toByte()
            }
            for (b : Byte in data) {
                ba[pos++] = b
            }
            if (useLe) {
                if (useLeExt) {
                    ba[pos++] = 0
                    ba[pos++] = le.ushr(8).toByte()
                    ba[pos] = (le and 0xFF).toByte()
                } else {
                    ba[pos] = le.toByte()
                }
            }
        }
        //log("APDU Array: ", ba)
        return ba
    }

    /*/**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(APDU_TAG, APDU_ENABLE_LOGGING, msg)
    }*/

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b : ByteArray) {
        return Logger.log("apdu", true, msg, b)
    }
}