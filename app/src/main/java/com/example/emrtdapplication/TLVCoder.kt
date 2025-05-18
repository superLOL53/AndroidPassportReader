package com.example.emrtdapplication

import kotlin.experimental.and

class TLVCoder {

    private var contents = ArrayList<TLV>()

    fun decode(tlv : ByteArray) : ArrayList<TLV> {
        Logger.log(TLVCoderConstants.TAG, TLVCoderConstants.ENABLE_LOGGING, "Byte Array is", tlv)
        var i = 0
        var tag : ByteArray
        var length : Byte
        while (i < tlv.size && tlv[i] != ZERO_BYTE) {
            tag = tagDecoding(tlv, i)
            Logger.log(TLVCoderConstants.TAG, TLVCoderConstants.ENABLE_LOGGING, "Tag is", tag)
            length = tlv[i+tag.size]
            Logger.log(TLVCoderConstants.TAG, TLVCoderConstants.ENABLE_LOGGING, "Length is $length")
            if (!isConstruct(tag)) {
                val value = tlv.slice(i+tag.size+1..i+tag.size+length).toByteArray()
                Logger.log(TLVCoderConstants.TAG, TLVCoderConstants.ENABLE_LOGGING, "Value is", value)
                contents.add(TLV(tag, length, value))
                i += length + tag.size + 1
            } else {
                Logger.log(TLVCoderConstants.TAG, TLVCoderConstants.ENABLE_LOGGING, "Value is empty")
                contents.add(TLV(tag, length, null))
                i += tag.size + 1
            }
        }
        return contents
    }

    private fun isConstruct(tag : ByteArray) : Boolean {
        return (tag[0] and TLV_TAGS.CONSTRUCT_BIT) == TLV_TAGS.CONSTRUCT_BIT
    }

    private fun tagDecoding(byteArray: ByteArray, index : Int) : ByteArray {
        if (isOneByteLength(byteArray[index])) {
            return byteArrayOf(byteArray[index])
        } else if (isTwoByteLength(byteArray[index])) {
            return byteArrayOf(byteArray[index], byteArray[index+1])
        } else {
            var tag : ByteArray = byteArrayOf(byteArray[index], byteArray[index+1])
            var i = index+2
            while (byteArray.size > i && byteArray[i] < 0) {
                tag += byteArray[i]
                i++
            }
            return tag
        }
    }

    private fun isOneByteLength(b : Byte) : Boolean {
        return (b and 0x1F) in 0..30
    }

    private fun isTwoByteLength(b1: Byte) : Boolean {
        return b1 and 0x1F == 0x1F.toByte()
    }
}