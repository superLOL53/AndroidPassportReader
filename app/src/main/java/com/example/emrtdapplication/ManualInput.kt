package com.example.emrtdapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class ManualInput : Activity() {
    private var name : String? = null
    private var expirationDate : String? = null
    private var birthday : String? = null
    private var can : String? = null
    private var checkDigitSequence = byteArrayOf(7, 3, 1)

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_input)
        findViewById<Button>(R.id.next).setOnClickListener{
            name = findViewById<EditText>(R.id.passportNr).text.toString()
            birthday = findViewById<EditText>(R.id.birthday).text.toString()
            expirationDate = findViewById<EditText>(R.id.expirationDate).text.toString()
            can = findViewById<EditText>(R.id.can).text.toString()
            val mrzInfo = parse()
            if (mrzInfo == null) {
                log("Invalid input")
            } else {
                log("Using following value for BAC/PACE: $mrzInfo")
                val emrtd = Intent(this, EMRTD().javaClass)
                if (can != null && can!!.isNotEmpty()) {
                    emrtd.putExtra("UseCAN", true)
                } else {
                    emrtd.putExtra("UseCAN", false)
                }
                emrtd.putExtra("MRZ", mrzInfo)
                startActivity(emrtd)
            }
        }
    }

    private fun parse() : String? {
        if (can != null && can!!.isNotEmpty()) {
            log("CAN is given. Not using MRZ. CAN: ${can!!.length} $can")
            return can
        } else {
            if (name == null) {
                log("No name given")
                return null
            }
            if (birthday == null) {
                log("No birthday given")
                return null
            } else if (birthday!!.length != 6) {
                log("Invalid date for birthday: " + birthday!!)
                return null
            }
            if (expirationDate == null) {
                log("No expiration date given")
                return null
            } else if (expirationDate!!.length != 6) {
                log("Invalid expiration date")
                return null
            }
            return computeCheckDigit()
        }
    }

    private fun computeCheckDigit() : String {
        val sb = StringBuilder()
        val nameCD = name?.slice(0..8) ?: ""
        sb.append(nameCD)
        log("Name is $nameCD")
        sb.append(checkDigit(nameCD))
        log("Birthday is $birthday")
        sb.append(birthday)
        sb.append(checkDigit(birthday!!))
        log("Expiration date is $expirationDate")
        sb.append(expirationDate)
        sb.append(checkDigit(expirationDate!!))
        return sb.toString()
    }

    private fun checkDigit(s : String) : Char {
        log("String is $s")
        var checkDigit = 0
        for (i in s.indices) {
            log("Char is ${s[i]}")
            log("Sequence multiplier is ${checkDigitSequence[i%checkDigitSequence.size]}")
            checkDigit += computeValueForCheckDigit(s[i])*checkDigitSequence[i%checkDigitSequence.size]
            log("CheckDigit is $checkDigit")
        }
        log("CheckDigit is $checkDigit")
        return (checkDigit % 10).toString()[0]
    }

    private fun computeValueForCheckDigit(ch: Char) : Int {
        if (ch.isDigit()) {
            return ch.digitToInt()
        } else if (ch.isUpperCase()) {
            return ch.code-55
        } else if (ch.isLowerCase()){
            return ch.code-87
        } else {
            return 0
        }
    }

    private fun log(msg : String) {
        Logger.log(ManualInputConstants.TAG, ManualInputConstants.ENABLE_LOGGING, msg)
    }
}

object ManualInputConstants {
    const val TAG = "ManualInput"
    const val ENABLE_LOGGING = true
}