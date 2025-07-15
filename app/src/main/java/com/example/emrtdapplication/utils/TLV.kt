package com.example.emrtdapplication.utils

import kotlin.experimental.and
import kotlin.experimental.or
/**
 * Class representing a TLV structure
 * @param tag: The tag of the TLV
 * @param length: The length of the value param
 * @param value: The byte array containing the value. In case of a construct this is empty
 */
class TLV {
    private val tag: ByteArray
    private var length: Int
    private var value: ByteArray? = null
    private var list: TLVSequence? = null
    private var isValid: Boolean = true

    constructor(ba: ByteArray) {
        this.tag = getTag(ba)
        this.length = getLength(ba, tag.size)
        decode(ba)
    }

    constructor(tag: Byte, value: ByteArray) {
        this.tag = byteArrayOf(tag)
        this.length = value.size
        decode(value, 0)
    }

    constructor(tag: ByteArray, value: ByteArray) {
        this.tag = tag
        this.length = value.size
        decode(value,0)
    }

    constructor(tag:Byte, sequence: TLVSequence) {
        this.tag = byteArrayOf(tag)
        this.length = sequence.toByteArray().size
        this.list = sequence
        this.value = null
    }

    constructor(tag:ByteArray, sequence: TLVSequence) {
        this.tag = tag
        this.length = sequence.toByteArray().size
        this.list = sequence
        this.value = null
    }

    private fun decode(ba: ByteArray) {
        val l = if (tag.size < ba.size && ba[tag.size] < 0) {
            (ba[tag.size] and 0x7F) + 1
        } else {
            1
        } + tag.size
        decode(ba, l)
    }

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

    fun getTag() : ByteArray {
        return tag
    }

    fun getLength() : Int {
        return length
    }

    fun getValue() : ByteArray? {
        return value
    }

    fun getTLVSequence() : TLVSequence? {
        return list
    }

    fun isConstruct(): Boolean {
        return tag.isEmpty() || (tag[0] and TLV_TAGS.CONSTRUCT_BIT) == TLV_TAGS.CONSTRUCT_BIT
    }

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

    fun getIsValid(): Boolean {
        return isValid
    }

    private fun getTag(ba: ByteArray): ByteArray {
        if (ba.isEmpty()) {
            isValid = false
            return ByteArray(1)
        }
        var tag = byteArrayOf(ba[0])
        if ((ba[0] and 0x1F).toInt() == 0x1F) {
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

    private fun getLength(ba: ByteArray, offset: Int): Int {
        if (offset >= ba.size) {
            isValid = false
            return ba.size
        }
        if (ba[offset] < 0) {
            val i = ba[offset]+128
            if (offset+i >= ba.size) {
                isValid = false
                return ba.size
            }
            var l = 0
            for (j in 1..i) {
                l = ba[offset+j].toUByte().toInt() + l*256
            }
            return l
        } else {
            return ba[offset].toInt()
        }
    }

    private fun getLengthByteArray(): ByteArray {
        if (!isValid) {
            return ByteArray(1)
        }
        if (length in 0..127) {
            return byteArrayOf(length.toByte())
        } else {
            var ba = byteArrayOf(Byte.MIN_VALUE)
            var l = length
            var i = 0
            while (l > 0) {
                l /= 256
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