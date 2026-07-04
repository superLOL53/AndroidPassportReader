package com.example.emrtdapplication

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Constant for converting an upper case letter to a numerical value according to ICAO Doc9303-3
 */
const val UPPER_CASE_DIGIT = 55

/**
 * Constant for converting a lower case letter to a numerical value according to ICAO Doc9303-3
 */
const val LOWER_CASE_DIGIT = 87

/**
 * Length of the passport number field in the MRZ
 */
const val PASSPORT_NUMBER_LENGTH = 8

/**
 * Length of the expiration/birthday date field in the MRZ
 */
const val DATE_LENGTH = 6

/**
 * First byte in the Check Digit sequence
 */
const val CHECK_DIGIT_SEQUENCE_1: Byte = 7

/**
 * Second byte in the Check Digit sequence
 */
const val CHECK_DIGIT_SEQUENCE_2: Byte = 3

/**
 * Third byte in the Check Digit sequence
 */
const val CHECK_DIGIT_SEQUENCE_3: Byte = 1

const val CHECK_DIGIT_MODULO = 10
const val PASSPORT_NUMBER_STRING = "passportNumber"
const val BIRTHDAY_STRING = "birthday"
const val EXPIRATION_DATE_STRING = "expirationDate"
const val INVALID_INPUT_STRING = "Unable to decode given information. Please make sure you entered everything correctly."

/** Class for manual input from the user. The manual input consists of:
 *
 * Passport number, birthday and expiration date
 *
 * This is needed to derive keys to establish secure messaging between the reader and the eMRTD.
 * Forwards the MRZ information to [ReadPassport]
 *
 * @property passportNr The passport number
 * @property expirationDate The expiration date of the passport
 * @property birthday The birthday of the passport holder
 * @property checkDigitSequence The byte sequence used to compute the check digit number
 *
 */
class ManualInput: AppCompatActivity() {
    private var passportNr: String? = null
    private var expirationDate: String? = null
    private var birthday: String? = null
    private var checkDigitSequence = byteArrayOf(
        CHECK_DIGIT_SEQUENCE_1,
        CHECK_DIGIT_SEQUENCE_2,
        CHECK_DIGIT_SEQUENCE_3
    )


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_input)
        val passportNumberText = findViewById<EditText>(R.id.passportNr)
        val birthdayText = findViewById<EditText>(R.id.birthday)
        val expirationDateText = findViewById<EditText>(R.id.expirationDate)
        if (savedInstanceState != null) {
            val number = savedInstanceState.getString(PASSPORT_NUMBER_STRING)
            val expirationDate = savedInstanceState.getString(EXPIRATION_DATE_STRING)
            val birthday = savedInstanceState.getString(BIRTHDAY_STRING)
            if (number != null) {
                passportNumberText.text = SpannableStringBuilder(number)
            }
            if (expirationDate != null) {
                expirationDateText.text = SpannableStringBuilder(expirationDate)
            }
            if (birthday != null) {
                birthdayText.text = SpannableStringBuilder(birthday)
            }
        }
        findViewById<Button>(R.id.next).setOnClickListener{
            passportNr = passportNumberText.text.toString()
            birthday = birthdayText.text.toString()
            expirationDate = expirationDateText.text.toString()
            val mrzInfo = parse()
            if (mrzInfo != null) {
                val intent = Intent(this, ReadPassport::class.java)
                intent.putExtra(MRZ_STRING, mrzInfo)
                startActivity(intent)
            } else {
                val info = Toast(this)
                info.setText(INVALID_INPUT_STRING)
                info.duration = Toast.LENGTH_LONG
                info.show()
            }
        }
    }

    @Override
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(
            PASSPORT_NUMBER_STRING,
            findViewById<EditText>(R.id.passportNr).text.toString()
        )
        outState.putString(
            BIRTHDAY_STRING,
            findViewById<EditText>(R.id.birthday).text.toString()
        )
        outState.putString(
            EXPIRATION_DATE_STRING,
            findViewById<EditText>(R.id.expirationDate).text.toString()
        )
        super.onSaveInstanceState(outState)
    }



    /**
     * Parsing the manual input and checks for validity of the input.
     * @return The MRZ information inclusive the check digits or null if any validity check fails
     */
    private fun parse(): String? {
        if (passportNr == null) {
            return null
        }
        if (birthday == null) {
            return null
        } else if (birthday!!.length != DATE_LENGTH) {
            return null
        }
        if (expirationDate == null) {
            return null
        } else if (expirationDate!!.length != DATE_LENGTH) {
            return null
        }
        return computeCheckDigit()
    }

    /**
     * Builds the MRZ information based on the validated input inclusive the check digit
     * @return The MRZ information inclusive the check digits
     */
    private fun computeCheckDigit(): String {
        val sb = StringBuilder()
        val nameCD = if (passportNr == null) {
            ""
        } else if (passportNr!!.length <= PASSPORT_NUMBER_LENGTH) {
            while (passportNr!!.length <= PASSPORT_NUMBER_LENGTH) {
                passportNr = "$passportNr<"
            }
            passportNr!!
        } else {
            passportNr!!.slice(0..PASSPORT_NUMBER_LENGTH)
        }
        sb.append(nameCD)
        sb.append(checkDigit(nameCD))
        sb.append(birthday)
        sb.append(checkDigit(birthday!!))
        sb.append(expirationDate)
        sb.append(checkDigit(expirationDate!!))
        return sb.toString()
    }

    /**
     * Computes the check digit for a string
     * @param s: The string for which the check digit is computed
     * @return The check digit as a char
     */
    private fun checkDigit(s: String): Char {
        var checkDigit = 0
        for (i in s.indices) {
            checkDigit += computeValueForCheckDigit(s[i]) *
                    checkDigitSequence[i%checkDigitSequence.size]
        }
        return (checkDigit % CHECK_DIGIT_MODULO).toString()[0]
    }

    /**
     * Converts a char value to its corresponding value according to the ICAO specification
     * @param ch: The char to convert
     * @return The corresponding numeric value of the char
     */
    private fun computeValueForCheckDigit(ch: Char): Int {
        return if (ch.isDigit()) {
            ch.digitToInt()
        } else if (ch.isUpperCase()) {
            ch.code - UPPER_CASE_DIGIT
        } else if (ch.isLowerCase()){
            ch.code - LOWER_CASE_DIGIT
        } else {
            0
        }
    }
}