package com.example.emrtdapplication

/**
 * Class representing a single TLV structure
 * @param tag: The tag of the TLV
 * @param length: The length of the value param
 * @param value: The byte array containing the value. In case of a construct this is empty
 */
class TLV(private val tag: ByteArray, private val length : Byte, private val value : ByteArray?) {

    /**
     * Returns the tag of the TLV
     * @return The tag of the TLV
     */
    fun getTag() : ByteArray {
        return tag
    }

    /**
     * Returns the length of the TLV
     * @return The length of the TLV
     */
    fun getLength() : Byte {
        return length
    }

    /**
     * Returns the value of the TLV
     * @return The value of the TLV
     */
    fun getValue() : ByteArray? {
        return value
    }
}