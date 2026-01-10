package com.example.emrtdapplication

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emrtdapplication.constants.ManualInputConstants.CHECK_DIGIT_SEQUENCE_1
import com.example.emrtdapplication.constants.ManualInputConstants.CHECK_DIGIT_SEQUENCE_2
import com.example.emrtdapplication.constants.ManualInputConstants.CHECK_DIGIT_SEQUENCE_3
import com.example.emrtdapplication.constants.ManualInputConstants.DATE_LENGTH
import com.example.emrtdapplication.constants.ManualInputConstants.LOWER_CASE_DIGIT
import com.example.emrtdapplication.constants.ManualInputConstants.NAME_LENGTH
import com.example.emrtdapplication.constants.ManualInputConstants.UPPER_CASE_DIGIT


/** Class for manual input from the user. The manual input consists of:
 *
 * Passport number, birthday and expiration date
 *
 * OR
 *
 * CAN number
 *
 * One of these is needed to derive keys to establish secure messaging between the reader and the document.
 * Forwards the MRZ information or CAN to the next activity (eMRTD)
 *
 * @property passportNr The passport number
 * @property expirationDate The expiration date of the passport
 * @property birthday The birthday of the passport holder
 * @property checkDigitSequence The byte sequence used to compute the check digit number
 *
 */
class ManualInput : AppCompatActivity() {
    private var passportNr : String? = null
    private var expirationDate : String? = null
    private var birthday : String? = null
    private var checkDigitSequence = byteArrayOf(CHECK_DIGIT_SEQUENCE_1, CHECK_DIGIT_SEQUENCE_2, CHECK_DIGIT_SEQUENCE_3)


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_input)
        val passportNumberText = findViewById<EditText>(R.id.passportNr)
        val birthdayText = findViewById<EditText>(R.id.birthday)
        val expirationDateText = findViewById<EditText>(R.id.expirationDate)
        if (savedInstanceState != null) {
            val number = savedInstanceState.getString("passportNumber")
            val expirationDate = savedInstanceState.getString("expirationDate")
            val birthday = savedInstanceState.getString("birthday")
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
                intent.putExtra("MRZ", mrzInfo)
                startActivity(intent)
            } else {
                val info = Toast(this)
                info.setText("Unable to decode given information. Please make sure you entered everything correctly.")
                info.duration = Toast.LENGTH_LONG
                info.show()
            }
        }
    }

    @Override
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("passportNumber", findViewById<EditText>(R.id.passportNr).text.toString())
        outState.putString("birthday", findViewById<EditText>(R.id.birthday).text.toString())
        outState.putString("expirationDate", findViewById<EditText>(R.id.expirationDate).text.toString())
        super.onSaveInstanceState(outState)
    }



    /**
     * Parsing the manual input and checks for validity of the input.
     * @return The MRZ information inclusive the check digits or null if any validity check fails
     */
    private fun parse() : String? {
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
    private fun computeCheckDigit() : String {
        val sb = StringBuilder()
        val nameCD = if (passportNr == null) {
            ""
        } else if (passportNr!!.length <= NAME_LENGTH) {
            while (passportNr!!.length <= NAME_LENGTH) {
                passportNr = "$passportNr<"
            }
            passportNr!!
        } else {
            passportNr!!.slice(0..NAME_LENGTH)
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
    private fun checkDigit(s : String) : Char {
        var checkDigit = 0
        for (i in s.indices) {
            checkDigit += computeValueForCheckDigit(s[i])*checkDigitSequence[i%checkDigitSequence.size]
        }
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
            ch.code - UPPER_CASE_DIGIT
        } else if (ch.isLowerCase()){
            ch.code - LOWER_CASE_DIGIT
        } else {
            0
        }
    }
}