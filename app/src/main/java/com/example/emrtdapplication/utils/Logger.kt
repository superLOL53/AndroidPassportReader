package com.example.emrtdapplication.utils

import android.util.Log

object Logger {
    private const val ENABLE_LOGGING = true
    private const val APPLICATION_TAG = "AppLog"
    private const val RETURN_CODE = "\nReturn Code: "

    fun log(tag : String, enableClassLog : Boolean, message : String) {
        if (ENABLE_LOGGING) {
            if (enableClassLog) {
                Log.d(tag, message)
            } else {
                Log.d(APPLICATION_TAG, message)
            }
        }
    }

    /*fun log(tag : String, enableClassLog : Boolean, returnCode : Int, message : String) : Int {
        if (ENABLE_LOGGING) {
            if (enableClassLog) {
                Log.d(tag, message + RETURN_CODE + returnCode)
            } else {
                Log.d(APPLICATION_TAG, message + RETURN_CODE + returnCode)
            }
        }
        return returnCode
    }*/

    @OptIn(ExperimentalStdlibApi::class)
    fun log(tag : String, enableClassLog : Boolean, message : String, info : ByteArray) {
        if (ENABLE_LOGGING) {
            if (enableClassLog) {
                Log.d(tag, message + info.toHexString())
            } else {
                Log.d(APPLICATION_TAG, message)
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun log(tag: String, enableClassLog : Boolean, returnCode : Int, message : String, info: ByteArray): Int {
        if (ENABLE_LOGGING) {
            if (enableClassLog) {
                Log.d(tag, message + info.toHexString() + RETURN_CODE + returnCode)
            } else {
                Log.d(APPLICATION_TAG, message + info.toHexString() + RETURN_CODE + returnCode)
            }
        }
        return returnCode
    }
}