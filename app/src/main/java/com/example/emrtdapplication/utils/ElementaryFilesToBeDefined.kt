package com.example.emrtdapplication.utils

import com.example.emrtdapplication.ElementaryFileTemplate

abstract class ElementaryFilesToBeDefined<T>(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    var tlvs : Array<T>? = null
        protected set

    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.getTag().size != 1 || tlv.getTag()[0] != EFTag ||
            tlv.getTLVSequence() == null || tlv.getTLVSequence()!!.getTLVSequence().size < 1) {
            return FAILURE
        }
        if (tlv.getTLVSequence()!!.getTLVSequence().size > 1) {
            val list = ArrayList<T>()
            for (i in 1..<tlv.getTLVSequence()!!.getTLVSequence().size) {
                try {
                    val el = tlv.getTLVSequence()!!.getTLVSequence()[i]
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