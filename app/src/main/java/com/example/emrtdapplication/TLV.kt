package com.example.emrtdapplication

class TLV(private val tag: ByteArray, private val length : Byte, private val value : ByteArray?) {

    fun getTag() : ByteArray {
        return tag
    }

    fun getLength() : Byte {
        return length
    }

    fun getValue() : ByteArray? {
        return value
    }
}