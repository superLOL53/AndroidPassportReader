package com.example.emrtdapplication.utils

import com.example.emrtdapplication.ElementaryFileTemplate

abstract class ElementaryFilesToBeDefined<T>(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    var tlvS : Array<T>? = null
        protected set

    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != efTag ||
            tlv.list == null || tlv.list!!.tlvSequence.isEmpty()
        ) {
            return FAILURE
        }
        if (tlv.list!!.tlvSequence.size > 1) {
            val list = ArrayList<T>()
            for (i in 1..<tlv.list!!.tlvSequence.size) {
                try {
                    val el = tlv.list!!.tlvSequence[i]
                    add(el, list)
                } catch (e : Exception) {
                    println(e.message)
                }
            }
            toTypedArray(list)
        }
        return SUCCESS
    }

    protected abstract fun add(tlv : TLV, list: ArrayList<T>)
    protected abstract fun toTypedArray(list: ArrayList<T>)
}