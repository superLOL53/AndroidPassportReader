package com.example.emrtdapplication.utils

class TLVSequence(byteArray: ByteArray) {
    private val tlvSequence = ArrayList<TLV>()

    init {
        var l = 0
        var i = 0
        while (l < byteArray.size) {
            tlvSequence.add(TLV(byteArray.slice(l..<byteArray.size).toByteArray()))
            l += tlvSequence[i].toByteArray().size
            i++
        }
    }

    fun getTLVSequence(): ArrayList<TLV> {
        return tlvSequence
    }

    fun toByteArray(): ByteArray {
        var ba = ByteArray(0)
        for (tlv in tlvSequence) {
            ba += tlv.toByteArray()
        }
        return ba
    }
}