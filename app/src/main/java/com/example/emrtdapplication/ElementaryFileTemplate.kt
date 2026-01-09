package com.example.emrtdapplication

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FILE_UNABLE_TO_READ
import com.example.emrtdapplication.utils.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.FAILURE
import java.security.MessageDigest
import com.example.emrtdapplication.lds1.EfSod
import java.security.NoSuchAlgorithmException
import java.security.Provider

/**
 * Abstract class representing elementary files (EF)
 *
 * @property rawFileContent Content of the EF if it is present in the ePassport, otherwise null
 * @property shortEFIdentifier Short EF identifier for the EF
 * @property longEFIdentifier Long EF identifier for the EF
 * @property efTag The Tag associated with the EF
 * @property contentStart The position in the [rawFileContent] where the actual content of the EF starts
 * @property matchHash Tells if the hash in the [EfSod] matches.
 * @property isPresent Indicates if the ePassport contains the EF.
 * @property isRead Indicates if the whole EF was read from the ePassport.
 */
abstract class ElementaryFileTemplate(protected val apduControl: APDUControl) {
    protected abstract var rawFileContent: ByteArray?
    abstract val shortEFIdentifier: Byte
    protected open val longEFIdentifier: Byte = 0x01
    protected abstract val efTag: Byte
    protected var contentStart = -1
    var matchHash = false
    var isPresent = false
    var isRead = false

    /**
     * Reads the EF and stores the content in [rawFileContent]
     * @return One of the following:
     * - [FILE_UNABLE_TO_SELECT] if no file with the EF identifier was found in the ePassport
     * - [FILE_UNABLE_TO_READ] if the file could not be read from the ePassport
     * - [SUCCESS] if the whole file was successfully read from the ePassport
     */
    fun read() : Int {
        var info = apduControl.sendAPDU(
            APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(longEFIdentifier, shortEFIdentifier))
        )
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        isPresent = true
        //Extract the length of the EF file
        info = apduControl.sendAPDU(
            APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 6
        )
        )
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_READ
        }
        val le = if (info[1] < 0) {
            contentStart = 2 + info[1]+128
            var l = 0
            for (i in 0..<(info[1]+128)) {
                l = l*256 + info[i+2].toUByte().toInt()
            }
            l += 2 + (info[1] + 128)
            l
        } else {
            contentStart = 2
            2 + info[1]
        }
        //Read the whole EF file
        if (le >= apduControl.maxResponseLength) {
            val tmp = ByteArray(le)
            var p1 : Byte
            var p2 : Byte
            var readBytes : Int
            for (i in 0..le step apduControl.maxResponseLength) {
                p1 = (i/256).toByte()
                p2 = (i % 256).toByte()
                readBytes = if (le - i > apduControl.maxResponseLength) {
                    apduControl.maxResponseLength
                } else {
                    le - i
                }
                info = apduControl.sendAPDU(
                    APDU(
                        NfcClassByte.ZERO,
                        NfcInsByte.READ_BINARY,
                        p1,
                        p2, readBytes
                    )
                )
                if (!apduControl.checkResponse(info)) {
                    return FILE_UNABLE_TO_READ
                }
                apduControl.removeRespondCodes(info).copyInto(tmp, i, 0)
            }
            rawFileContent = tmp
        } else {
            info = apduControl.sendAPDU(
                APDU(
                    NfcClassByte.ZERO,
                    NfcInsByte.READ_BINARY,
                    NfcP1Byte.ZERO,
                    NfcP2Byte.ZERO, le
                )
            )
            if (!apduControl.checkResponse(info)) {
                return FILE_UNABLE_TO_READ
            }
            rawFileContent = apduControl.removeRespondCodes(info)
        }
        isRead = true
        return SUCCESS
    }

    /**
     * Computes the Hash of the [rawFileContent]
     * @param hashName The hash algorithm used to hash the file content
     * @return The hash of the file content
     * @throws NoSuchAlgorithmException if the hash algorithm is not supported by any [Provider]
     */
    fun hash(hashName : String) : ByteArray? {
        if (rawFileContent == null) {
            return null
        }
        val md = MessageDigest.getInstance(hashName)
        md.update(rawFileContent!!)
        return md.digest()
    }

    /**
     * Parses the file content according to the ICAO specification
     * @return [SUCCESS] if the file content was successfully parsed, otherwise [FAILURE]
     */
    abstract fun parse() : Int

    /**
     * Fill in the text for [row] with [description] and [value]
     * @param row The row to fill in texts
     * @param description The meaning of the [value]
     * @param value The value of the row
     */
    protected fun provideTextForRow(row : TableRow, description : String, value : String) {
        var i = true
        for (t in row.children) {
            if (i) {
                (t as TextView).text = description
            } else {
                (t as TextView).text = value
            }
            i = !i
        }
    }

    /**
     * Creates a row in a [TableLayout] to display information in the file
     * @param context The context of the view
     * @param parent The parent layout of the created row
     * @return The created row
     */
    protected fun createRow(context : Context, parent: LinearLayout) : TableRow {
        val row = TableRow(context)
        row.gravity = Gravity.CENTER
        val description = TextView(context)
        description.gravity = Gravity.CENTER
        val value = TextView(context)
        value.gravity = Gravity.CENTER
        row.addView(description)
        row.addView(value)
        parent.addView(row)
        return row
    }

    /**
     * Create views to display contents of the file in the app
     */
    abstract fun <T : LinearLayout> createViews(context: Context, parent: T)
}