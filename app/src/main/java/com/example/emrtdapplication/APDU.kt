package com.example.emrtdapplication

import kotlin.experimental.and

/**
 * Constants for the class APDU
 */
const val APDU_TAG = "APDU"
const val APDU_ENABLE_LOGGING = true
const val MIN_APDU_LENGTH = 4

/**
 * Class representing an APDU
 * @param classByte: The CLASS byte of the APDU
 * @param insByte: The INS byte of the APDU
 * @param p1Byte: The P1 byte of the APDU
 * @param p2Byte: The P2 byte of the APDU
 */
class APDU(private val classByte: Byte, private val insByte: Byte, private val p1Byte: Byte, private val p2Byte: Byte) {
    private var lc : Byte = 0
    private var lcExt : Short = 0
    private var useLc : Boolean = false
    private var le : Byte = 0
    private var leExt : Short = 0
    private var useLe : Boolean = false
    private var data : ByteArray = ByteArray(0)

    /**
     * Class representing an APDU
     * @param classByte: The CLASS byte of the APDU
     * @param insByte: The INS byte of the APDU
     * @param p1Byte: The P1 byte of the APDU
     * @param p2Byte: The P2 byte of the APDU
     * @param useLe: Indicates the use of the Le field
     * @param le: The expected length of the reply APDU
     * @param leExt: The expected extended length of the reply APDU
     */
    constructor(classByte: Byte, insByte: Byte, p1Byte: Byte, p2Byte: Byte, useLe : Boolean, le : Byte, leExt : Short) : this(classByte, insByte, p1Byte, p2Byte) {
        this.useLe = useLe
        this.le = le
        this.leExt = leExt
    }

    /**
     * Class representing an APDU
     * @param classByte: The CLASS byte of the APDU
     * @param insByte: The INS byte of the APDU
     * @param p1Byte: The P1 byte of the APDU
     * @param p2Byte: The P2 byte of the APDU
     * @param useLc: Indicates the use of the Le field
     * @param lc: The length of the data
     * @param lcExt: The extended length of the data if it exceeds 255
     * @param data: The byte array containing the data
     */
    constructor(classByte: Byte, insByte: Byte, p1Byte: Byte, p2Byte: Byte, useLc : Boolean, lc : Byte, lcExt : Short, data : ByteArray) : this(classByte, insByte, p1Byte, p2Byte) {
        this.useLc = useLc
        this.lc = lc
        this.lcExt = lcExt
        this.data = data
    }

    /**
     * Class representing an APDU
     * @param classByte: The CLASS byte of the APDU
     * @param insByte: The INS byte of the APDU
     * @param p1Byte: The P1 byte of the APDU
     * @param p2Byte: The P2 byte of the APDU
     * @param useLc: Indicates the use of the Le field
     * @param lc: The length of the data
     * @param lcExt: The extended length of the data if it exceeds 255
     * @param data: The byte array containing the data
     * @param useLe: Indicates the use of the Le field
     * @param le: The expected length of the reply APDU
     * @param leExt: The expected extended length of the reply APDU
     */
    constructor(classByte: Byte, insByte: Byte, p1Byte: Byte, p2Byte: Byte, useLc : Boolean, lc : Byte, lcExt : Short, data : ByteArray, useLe : Boolean, le : Byte, leExt: Short) : this(classByte, insByte, p1Byte, p2Byte, useLc, lc, lcExt, data) {
        this.useLe = useLe
        this.le = le
        this.leExt = leExt
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
    fun getLe() : Byte {
        return le
    }

    /**
     * Returns if the Le field is used in the APDU
     * @return True if Le is used in the APDU otherwise False
     */
    fun getUseLe() : Boolean {
        return useLe
    }

    /**
     * Returns if the Lc field is used in the APDU
     * @return True if Lc is used in the APDU otherwise False
     */
    fun getUseLc() : Boolean {
        return useLc
    }

    /**
     * Returns the extended Lc value used in the APDU
     * @return The extended Lc value
     */
    fun getLcExt() : Short {
        return lcExt
    }

    /**
     * Returns the whole APDU in a single byte array
     * @return The APDU as a byte array
     */
    fun getByteArray() : ByteArray {
        log("Class Byte: ${classByte.toString(16)}")
        log("INS Byte: " + insByte.toString(16))
        log("P1 Byte: " + p1Byte.toString(16))
        log("P2 Byte: " + p2Byte.toString(16))
        log("Lc Byte: " + lc.toString(16))
        log("LcExt Short: " + lcExt.toString(16))
        log("Le Byte: "+ le.toString(16))
        log("LeExt Byte: " + leExt.toString(16))
        log("Data Array: ", data)
        var apduLength = MIN_APDU_LENGTH
        if (useLe) {
            apduLength += if (le == ZERO_BYTE) {
                3
            } else {
                1
            }
        }
        if (useLc) {
            apduLength += if (lc == ZERO_BYTE) {
                3
            } else {
                1
            }
            apduLength += data.size
        }
        log("APDU Length is $apduLength")
        val ba = ByteArray(apduLength)
        ba[0] = classByte
        ba[1] = insByte
        ba[2] = p1Byte
        ba[3] = p2Byte
        if (!useLc && !useLe) {
            log("APDU Array: ", ba)
            return ba
        } else if (!useLc) {
            ba[4] = le
            if (le == ZERO_BYTE) {
                ba[5] = (leExt.rotateRight(8) and 0xFF).toByte()
                ba[6] = (leExt and 0xFF).toByte()
            }
        } else {
            ba[4] = lc
            var i = 5
            if (lc == ZERO_BYTE) {
                ba[5] = (lcExt.rotateRight(8) and 0xFF).toByte()
                ba[6] = (lcExt and 0xFF).toByte()
                i = 7
            }
            for (b : Byte in data) {
                ba[i] = b
                i += 1
            }
            if (useLe) {
                ba[i] = le
                if (le == ZERO_BYTE) {
                    ba[i+1] = (leExt.rotateRight(8) and 0xFF).toByte()
                    ba[i+2] = (leExt and 0xFF).toByte()
                }
            }
        }
        log("APDU Array: ", ba)
        return ba
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(APDU_TAG, APDU_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b : ByteArray) {
        return Logger.log(APDU_TAG, APDU_ENABLE_LOGGING, msg, b)
    }
}