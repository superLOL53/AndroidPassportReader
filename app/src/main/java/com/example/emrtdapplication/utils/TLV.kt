package com.example.emrtdapplication.utils

import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.BYTE_MODULO
import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.UBYTE_MODULO
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.constants.TlvTags.LENGTH_MULTIPLE_BYTES
import com.example.emrtdapplication.constants.TlvTags.TAG_MULTIPLE_BYTES
import kotlin.experimental.and
import kotlin.experimental.or
/**
 * Class representing a Tag-Length-Value (TLV) structure
 * @property tag The tag of the TLV structure
 * @property length The length of the TLV value
 * @property value The byte array containing the value if the TLV structure is not a construct
 * @property list A list of TLV structures contained in the TLV structure if it is a construct
 * @property isValid Tells if the TLV is valid
 */
class TLV {
    val tag: ByteArray
    var length: Int
        private set
    var value: ByteArray? = null
        private set
    var list: TLVSequence? = null
        private set
    var isValid: Boolean = true
        private set

    /**
     * Converts the byte array into a TLV structure
     * @param ba The byte array containing a TLV structure
     */
    constructor(ba: ByteArray) {
        this.tag = getTag(ba)
        this.length = getLength(ba, tag.size)
        decode(ba)
    }

    /**
     * Constructs a TLV structure from the given [tag] and [value]
     * @param tag The tag of the TLV
     * @param value The value of the TLV
     */
    constructor(tag: Byte, value: ByteArray) {
        this.tag = byteArrayOf(tag)
        this.length = value.size
        decode(value, 0)
    }

    /**
     * Constructs a TLV structure from the given [tag] and [value]
     * @param tag The tag of the TLV
     * @param value The value of the TLV
     */
    constructor(tag: ByteArray, value: ByteArray) {
        this.tag = tag
        this.length = value.size
        decode(value,0)
    }

    /**
     * Constructs a TLV structure from the given [tag] and [sequence]
     * @param tag The tag of the TLV
     * @param sequence A list of TLV structures
     */
    constructor(tag:Byte, sequence: TLVSequence) {
        this.tag = byteArrayOf(tag)
        this.length = sequence.toByteArray().size
        this.list = sequence
        this.value = null
    }

    /**
     * Constructs a TLV structure from the given [tag] and [sequence]
     * @param tag The tag of the TLV
     * @param sequence A list of TLV structures
     */
    constructor(tag:ByteArray, sequence: TLVSequence) {
        this.tag = tag
        this.length = sequence.toByteArray().size
        this.list = sequence
        this.value = null
    }

    /**
     * Decodes the byte array into a TLV structure
     * @param ba The byte array containing a TLV structure
     */
    private fun decode(ba: ByteArray) {
        val l = if (tag.size < ba.size && ba[tag.size] < 0) {
            (ba[tag.size] and LENGTH_MULTIPLE_BYTES) + 1
        } else {
            1
        } + tag.size
        decode(ba, l)
    }

    /**
     * Decodes the byte array into a TLV structure
     * @param ba The byte array containing a TLV structure
     * @param l The index where the value of the TLV starts in the byte array
     */
    private fun decode(ba: ByteArray, l: Int) {
        if (l+length > ba.size) {
            isValid = false
            length = 0
            return
        }
        if (isConstruct()) {
            list = TLVSequence(ba.slice(l..<l+length).toByteArray())
            value = null
        } else {
            value = ba.slice(l..<l+length).toByteArray()
            list = null
        }
    }

    /**
     * Determines if the TLV contains another TLV or just a value
     * @return If the TLV structure is a construct
     */
    fun isConstruct(): Boolean {
        return tag.isEmpty() || (tag[0] and TlvTags.CONSTRUCT_BIT) == TlvTags.CONSTRUCT_BIT
    }

    /**
     * Converts the TLV structure into a byte array
     * @return TLV structure represented as a byte array
     */
    fun toByteArray(): ByteArray {
        val ba = tag+getLengthByteArray()
        return if (!isValid) {
            ba
        } else if (isConstruct()) {
            ba + list!!.toByteArray()
        } else {
            ba + value!!
        }
    }

    /**
     * Gets the tag of the TLV structure encoded as a byte array
     * @param ba The byte array containing a TLV structure
     * @return The tag of the TLV in the byte array
     */
    private fun getTag(ba: ByteArray): ByteArray {
        if (ba.isEmpty()) {
            isValid = false
            return ByteArray(1)
        }
        var tag = byteArrayOf(ba[0])
        if ((ba[0] and TAG_MULTIPLE_BYTES) == TAG_MULTIPLE_BYTES) {
            var i = 1
            while (ba.size > i && ba[i] < 0) {
                tag += ba[i]
                i++
            }
            return if (ba.size <= i) {
                isValid = false
                ByteArray(1)
            } else {
                tag + ba[i]
            }
        } else {
            return tag
        }
    }

    /**
     * Decodes the length of the TLV contained in the byte array
     * @param ba The TLV structure as a byte array
     * @param offset Index where the encoded length starts
     * @return The length of the value of the TLV
     */
    private fun getLength(ba: ByteArray, offset: Int): Int {
        if (offset >= ba.size) {
            isValid = false
            return ba.size
        }
        if (ba[offset] < 0) {
            val i = ba[offset]+BYTE_MODULO
            if (offset+i >= ba.size) {
                isValid = false
                return ba.size
            }
            var l = 0
            for (j in 1..i) {
                l = ba[offset+j].toUByte().toInt() + l*UBYTE_MODULO
            }
            return l
        } else {
            return ba[offset].toInt()
        }
    }

    /**
     * Converts the length of the value of the TLV into a
     * byte array containing the length according to TLV rules
     * @return The length of the TLV structure as byte array
     */
    private fun getLengthByteArray(): ByteArray {
        if (!isValid) {
            return ByteArray(1)
        }
        if (length in 0..Byte.MAX_VALUE) {
            return byteArrayOf(length.toByte())
        } else {
            var ba = byteArrayOf(Byte.MIN_VALUE)
            var l = length
            var i = 0
            while (l > 0) {
                l /= UBYTE_MODULO
                i += 1
            }
            ba[0] = ba[0] or i.toByte()
            for (j in i-1 downTo 0) {
                ba += length.ushr(j*8).toByte()
            }
            return ba
        }
    }
}