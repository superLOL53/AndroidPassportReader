package com.example.emrtdapplication.utils

/**
 * Minimum length of a command APDU
 */
const val MIN_APDU_LENGTH = 4

/**
 * Maximum value for the Lc field of a command APDU, excluding 256 (0x00)
 */
const val LC_MAX = 255

/**
 * Maximum value for the extended Lc field of a command APDU
 */
const val LC_EXT_MAX = 65535

/**
 * Minimum value for the Le field to be present in a command APDU
 */
const val LE_MIN = 0

/**
 * Maximum value for the Le field of a command APDU
 */
const val LE_MAX = 256

/**
 * Maximum value for the extended Le field of a command APDU
 */
const val LE_EXT_MAX = 65536

/**
 * Byte mask for converting command APDU length field(s) to a byte
 */
const val BYTE_MASK = 0xFF

/**
 * Number of bits to be shifted to convert the high byte of a command APDU length field
 * (Le or Lc) to a byte
 */
const val EXTENDED_LENGTH_SHIFT_COUNT = 8

const val EXTENDED_LENGTH_SIZE_IN_BYTES = 3

const val NORMAL_LENGTH_SIZE_IN_BYTES = 1

/**
 * Class representing an APDU
 * @property classByte The CLASS byte of the APDU
 * @property insByte The INS byte of the APDU
 * @property p1Byte The P1 byte of the APDU
 * @property p2Byte The P2 byte of the APDU
 * @property data The data to be sent with the APDU.
 * @property lc The length of the data of the APDU
 * @property useLc Indicates if the Lc field and in extent the [data] is sent with the APDU
 * @property useLcExt Indicates if the APDU uses the extended length for the [lc]
 * @property le The expected length of the received APDU
 * @property useLe Indicates if the received APDU contains any data
 * @property useLeExt Indicates if the APDU uses extended length for [le]
 */
class APDU(
    private val classByte: Byte,
    private val insByte: Byte,
    private val p1Byte: Byte,
    private val p2Byte: Byte
) {
    var lc: Int = 0
        private set
    var useLc = false
        private set
    var useLcExt = false
        private set
    var data: ByteArray = ByteArray(0)
        private set
    var le: Int = 0
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
    constructor(
        classByte: Byte,
        insByte: Byte,
        p1Byte: Byte,
        p2Byte: Byte,
        le: Int
    ): this(classByte, insByte, p1Byte, p2Byte) {
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
    constructor(
        classByte: Byte,
        insByte: Byte,
        p1Byte: Byte,
        p2Byte: Byte,
        data: ByteArray
    ): this(classByte, insByte, p1Byte, p2Byte) {
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
     * @param le: The expected length of the response APDU
     */
    constructor(
        classByte: Byte,
        insByte: Byte,
        p1Byte: Byte,
        p2Byte: Byte,
        data: ByteArray,
        le: Int
    ): this(classByte, insByte, p1Byte, p2Byte, data) {
        calculateLe(le)
    }

    /**
     * Determines the usage of the Le field in the APDU.
     *
     * @param le The expected length of the response APDU
     */
    private fun calculateLe(le: Int) {
        if (le !in LE_MIN..LE_EXT_MAX) {
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
    fun getHeader(): ByteArray {
        return byteArrayOf(classByte, insByte, p1Byte, p2Byte)
    }

    /**
     * Returns the whole APDU in a single byte array
     * @return The APDU as a byte array
     */
    fun getByteArray(): ByteArray {
        var apduLength = MIN_APDU_LENGTH
        if (useLe) {
            apduLength += if (useLeExt) {
                EXTENDED_LENGTH_SIZE_IN_BYTES
            } else {
                NORMAL_LENGTH_SIZE_IN_BYTES
            }
        }
        if (useLc) {
            apduLength += if (useLcExt) {
                EXTENDED_LENGTH_SIZE_IN_BYTES
            } else {
                NORMAL_LENGTH_SIZE_IN_BYTES
            }
            apduLength += data.size
        }
        val ba = ByteArray(apduLength)
        var pos = 0
        ba[pos++] = classByte
        ba[pos++] = insByte
        ba[pos++] = p1Byte
        ba[pos++] = p2Byte
        if (!useLc && !useLe) {
            return ba
        } else if (!useLc) {
            if (useLeExt) {
                ba[pos++] = 0
                ba[pos++] = le.ushr(EXTENDED_LENGTH_SHIFT_COUNT).toByte()
                ba[pos] = (le and BYTE_MASK).toByte()
            } else {
                ba[pos] = le.toByte()
            }
        } else {
            if (useLcExt) {
                ba[pos++] = 0
                ba[pos++] = lc.ushr(EXTENDED_LENGTH_SHIFT_COUNT).toByte()
                ba[pos++] = (lc and BYTE_MASK).toByte()
            } else {
                ba[pos++] = lc.toByte()
            }
            for (b: Byte in data) {
                ba[pos++] = b
            }
            if (useLe) {
                if (useLeExt) {
                    ba[pos++] = 0
                    ba[pos++] = le.ushr(EXTENDED_LENGTH_SHIFT_COUNT).toByte()
                    ba[pos] = (le and BYTE_MASK).toByte()
                } else {
                    ba[pos] = le.toByte()
                }
            }
        }
        return ba
    }
}