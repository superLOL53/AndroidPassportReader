package com.example.emrtdapplication

import kotlin.experimental.and

open class APDU(private val classByte: Byte, private val insByte: Byte, private val p1Byte: Byte, private val p2Byte: Byte) {
    private var lc : Byte = 0
    private var lcExt : Short = 0
    private var useLc : Boolean = false
    private var le : Byte = 0
    private var leExt : Short = 0
    private var useLe : Boolean = false
    private var data : ByteArray = ByteArray(0)
    private val minAPDULength = 4


    constructor(classByte: Byte, instrByte: Byte, p1Byte: Byte, p2Byte: Byte, useLe : Boolean, le : Byte, leExt : Short) : this(classByte, instrByte, p1Byte, p2Byte) {
        this.useLe = useLe
        this.le = le
        this.leExt = leExt
    }

    constructor(classByte: Byte, instrByte: Byte, p1Byte: Byte, p2Byte: Byte, useLc : Boolean, lc : Byte, lcExt : Short, data : ByteArray) : this(classByte, instrByte, p1Byte, p2Byte) {
        this.useLc = useLc
        this.lc = lc
        this.lcExt = lcExt
        this.data = data
    }

    constructor(classByte: Byte, instrByte: Byte, p1Byte: Byte, p2Byte: Byte, useLc : Boolean, lc : Byte, lcExt : Short, data : ByteArray, useLe : Boolean, le : Byte, leExt: Short) : this(classByte, instrByte, p1Byte, p2Byte, useLc, lc, lcExt, data) {
        this.useLe = useLe
        this.le = le
        this.leExt = leExt
    }

    fun getHeader() : ByteArray {
        return byteArrayOf(classByte, insByte, p1Byte, p2Byte)
    }

    fun getData() : ByteArray {
        return data
    }

    fun getLc() : Byte {
        return lc
    }

    fun getLe() : Byte {
        return le
    }

    fun getUseLe() : Boolean {
        return useLe
    }

    fun getUseLc() : Boolean {
        return useLc
    }

    fun getLeExt() : Short {
        return leExt
    }

    fun getLcExt() : Short {
        return lcExt
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getByteArray() : ByteArray {
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "Class Byte: ${classByte.toHexString()}")
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "INS Byte: " + insByte.toString(16))
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "P1 Byte: " + p1Byte.toString(16))
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "P2 Byte: " + p2Byte.toString(16))
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "Lc Byte: " + lc.toString(16))
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "LcExt Short: " + lcExt.toString(16))
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "Le Byte: "+ le.toString(16))
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "LeExt Byte: " + leExt.toString(16))
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "Data Array: " + data.toHexString())
        var apduLength = minAPDULength
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
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "APDU Length is $apduLength")
        val ba = ByteArray(apduLength)
        ba[0] = classByte
        ba[1] = insByte
        ba[2] = p1Byte
        ba[3] = p2Byte
        if (!useLc && !useLe) {
            Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "APDU Array: " + ba.toHexString())
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
        Logger.log(APDUConstants.TAG, APDUConstants.ENABLE_LOGGING, "APDU Array: " + ba.toHexString())
        return ba
    }
}