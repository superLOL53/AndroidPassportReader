package com.example.emrtdapplication.utils

/**
 * Class representing a Tag-Length-Value (TLV) sequence
 * @param byteArray The byte array containing a TLV sequence
 * @property tlvSequence A list containing all TLVs that are contained in the byte array
 */
class TLVSequence(byteArray: ByteArray) {
    val tlvSequence = ArrayList<TLV>()

    init {
        var l = 0
        var i = 0
        while (l < byteArray.size) {
            tlvSequence.add(TLV(byteArray.slice(l..<byteArray.size).toByteArray()))
            l += tlvSequence[i].toByteArray().size
            i++
        }
    }

    /*fun getTLVSequence(): ArrayList<TLV> {
        return tlvSequence
    }*/

    /**
     * Converts the sequence into a byte array
     * @return The byte array representing the TLV sequence
     */
    fun toByteArray(): ByteArray {
        var ba = ByteArray(0)
        for (tlv in tlvSequence) {
            ba += tlv.toByteArray()
        }
        return ba
    }
}