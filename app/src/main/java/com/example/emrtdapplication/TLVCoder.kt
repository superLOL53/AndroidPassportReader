package com.example.emrtdapplication

import kotlin.experimental.and

/**
 * Constants for the TLVCoder class
 */
const val TC_TAG = "TLVCoder"
const val TC_ENABLE_LOGGING = true
/**
 * Class for decoding a byte array into a TLV structure. An array list contains the decoded TLV values.
 */
class TLVCoder {

    private var contents = ArrayList<TLV>()

    /**
     * Decodes a byte array into a TLV structure
     * @param tlv: The byte array to decode into a TLV structure
     * @return An array list containing the TLV structure
     */
    fun decode(tlv : ByteArray) : ArrayList<TLV> {
        log("Byte Array is", tlv)
        var i = 0
        var tag : ByteArray
        var length : Byte
        while (i < tlv.size-1 && tlv[i] != ZERO_BYTE) {
            tag = tagDecoding(tlv, i)
            log("Tag is", tag)
            if (i+tag.size >= tlv.size) {
                log("Invalid TLV detected. TLV length exceeds the byte array")
            }
            length = tlv[i+tag.size]
            log("Length is $length")
            if (!isConstruct(tag)) {
                if (i+tag.size+length >= tlv.size) {
                    log("Invalid TLV detected. TLV length exceeds the byte array")
                }
                val value = tlv.slice(i+tag.size+1..i+tag.size+length).toByteArray()
                log("Value is", value)
                contents.add(TLV(tag, length, value))
                i += length + tag.size + 1
            } else {
                log("Value is empty")
                contents.add(TLV(tag, length, null))
                i += tag.size + 1
            }
        }
        return contents
    }

    /**
     * Checks if the tag indicates a construct
     * @param tag: The tag of the TLV structure
     * @return True if the TLV structure is a construct, otherwise False
     */
    private fun isConstruct(tag : ByteArray) : Boolean {
        return (tag[0] and TLV_TAGS.CONSTRUCT_BIT) == TLV_TAGS.CONSTRUCT_BIT
    }

    /**
     * Decodes the tag in the byte array at the index
     * @param byteArray: the byte array containing the tag to decode
     * @param index: The index at which the tag starts
     * @return The byte array containing the tag
     */
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

    /**
     * Checks if the tag is one byte
     * @param b: The first byte of the tag
     * @return True if the tag has length 1, otherwise False
     */
    private fun isOneByteLength(b : Byte) : Boolean {
        return (b and 0x1F) in 0..30
    }

    /**
     * Checks if the tag has a length of 2 bytes
     * @param b1: The first byte of the tag
     * @return True if the tag has a length of 2 otherwise False
     */
    private fun isTwoByteLength(b1: Byte) : Boolean {
        return b1 and 0x1F == 0x1F.toByte()
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(TC_TAG, TC_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b : ByteArray) {
        return Logger.log(TC_TAG, TC_ENABLE_LOGGING, msg, b)
    }
}