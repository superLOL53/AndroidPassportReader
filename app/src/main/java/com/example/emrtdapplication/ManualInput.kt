package com.example.emrtdapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

/**
 * Constants for the ManualInput class
 */
const val TAG = "ManualInput"
const val ENABLE_LOGGING = true
const val UPPER_CASE_DIGIT = 55
const val LOWER_CASE_DIGIT = 87
const val NAME_LENGTH = 8
const val DATE_LENGTH = 6


/** Class for manual input from the user. The manual input consists of:
 * Passport number, birthday and expiration date
 * OR
 * CAN number
 * One of these is needed to derive keys to establish secure messaging between the reader and the document.
 * Forwards the MRZ information or CAN to the next activity (EMRTD)
 */
//TODO: By invalid input provide a popup view and explain in detail how the input should be filled
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

    /**
     * Parsing the manual input and checks for validity of the input.
     * @return The MRZ information inclusive the check digits or null if any validity check fails
     */
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
            } else if (birthday!!.length != DATE_LENGTH) {
                log("Invalid date for birthday: " + birthday!!)
                return null
            }
            if (expirationDate == null) {
                log("No expiration date given")
                return null
            } else if (expirationDate!!.length != DATE_LENGTH) {
                log("Invalid expiration date")
                return null
            }
            return computeCheckDigit()
        }
    }

    /**
     * Builds the MRZ information based on the validated input inclusive the check digit
     * @return The MRZ information inclusive the check digits
     */
    private fun computeCheckDigit() : String {
        val sb = StringBuilder()
        val nameCD = name?.slice(0..NAME_LENGTH) ?: ""
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

    /**
     * Computes the check digit for a string
     * @param s: The string for which the check digit is computed
     * @return The check digit as a char
     */
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

    /**
     * Converts a char value to its corresponding value according to the ICAO specification
     * @param ch: The char to convert
     * @return The corresponding numeric value of the char
     */
    private fun computeValueForCheckDigit(ch: Char) : Int {
        return if (ch.isDigit()) {
            ch.digitToInt()
        } else if (ch.isUpperCase()) {
            ch.code-UPPER_CASE_DIGIT
        } else if (ch.isLowerCase()){
            ch.code-LOWER_CASE_DIGIT
        } else {
            0
        }
    }

    /**
     * Logs messages in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg : String) {
        Logger.log(TAG, ENABLE_LOGGING, msg)
    }
}