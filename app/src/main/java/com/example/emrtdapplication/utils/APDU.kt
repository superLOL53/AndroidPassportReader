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
 * @param classByte: The CLASS byte of the APDU
 * @param insByte: The INS byte of the APDU
 * @param p1Byte: The P1 byte of the APDU
 * @param p2Byte: The P2 byte of the APDU
 */
class APDU(private val classByte: Byte, private val insByte: Byte, private val p1Byte: Byte, private val p2Byte: Byte) {
    private var lc : Int = 0
    private var useLc = false
    private var useLcExt = false
    private var data : ByteArray = ByteArray(0)
    private var le : Int = 0
    private var useLe = false
    private var useLeExt = false

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
     * Returns the data of the APDU
     * @return The byte array containing the data of the APDU
     */
    fun getData() : ByteArray {
        return data
    }

    /**
     * Returns the expected length of the reply APDU
     * @return The expected length of the reply APDU
     */
    fun getLe() : Int {
        return le
    }

    /**
     * Returns if the Le field is used in the APDU
     * @return True if Le is used in the APDU otherwise False
     */
    fun getUseLe() : Boolean {
        return useLe
    }

    fun getUseLeExt(): Boolean {
        return useLeExt
    }

    fun getLc() : Int {
        return lc
    }

    /**
     * Returns if the Lc field is used in the APDU
     * @return True if Lc is used in the APDU otherwise False
     */
    fun getUseLc() : Boolean {
        return useLc
    }

    fun getUseLcExt() : Boolean {
        return useLcExt
    }

    /**
     * Returns the whole APDU in a single byte array
     * @return The APDU as a byte array
     */
    fun getByteArray() : ByteArray {
        //log("Class Byte: ${classByte.toString(16)}")
        //log("INS Byte: " + insByte.toString(16))
        //log("P1 Byte: " + p1Byte.toString(16))
        //log("P2 Byte: " + p2Byte.toString(16))
        //log("Lc Byte: " + lc.toString(16))
        //log("LcExt Short: " + lcExt.toString(16))
        //log("Le Byte: "+ le.toString(16))
        //log("LeExt Byte: " + leExt.toString(16))
        //log("Data Array: ", data)
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