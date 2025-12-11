package com.example.emrtdapplication

import android.content.Context
import android.view.Gravity
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
import java.security.MessageDigest

abstract class ElementaryFileTemplate(protected val apduControl: APDUControl) {
    protected abstract var rawFileContent: ByteArray?
    abstract val shortEFIdentifier: Byte
    protected open val longEFIdentifier: Byte = 0x01
    protected abstract val EFTag: Byte
    protected var contentStart = -1
    var matchHash = false
    var isPresent = false
        private set
    var isRead = false
        private set

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
            var tmp = ByteArray(le)
            var p1 : Byte = 0
            var p2 : Byte = 0
            var readBytes = 0
            for (i in 0..le step apduControl.maxResponseLength) {
                p1 = (i/256).toByte()
                p2 = (i % 256).toByte()
                if (le - i > apduControl.maxResponseLength) {
                    readBytes = apduControl.maxResponseLength
                } else {
                    readBytes = le - i
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

    fun hash(hashName : String) : ByteArray? {
        if (rawFileContent == null) {
            return null
        }
        val md = MessageDigest.getInstance(hashName)
        md.update(rawFileContent!!)
        return md.digest()
    }

    abstract fun parse() : Int

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

    protected fun createRow(context : Context, parent: TableLayout) : TableRow {
        val row = TableRow(context)
        row.gravity = Gravity.CENTER
        val description = TextView(context)
        val value = TextView(context)
        row.addView(description)
        row.addView(value)
        parent.addView(row)
        return row
    }
}