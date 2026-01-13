package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_TYPE
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.EMRTD.mrz
import com.example.emrtdapplication.LDSApplication
import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.LDS1ApplicationConstants.APPLICATION_ID
import com.example.emrtdapplication.constants.LDS1ApplicationConstants.INCREMENT_PROGRESS_BAR
import com.example.emrtdapplication.constants.SUCCESS
import java.math.BigInteger
import java.security.cert.X509Certificate
import kotlin.collections.iterator

class LDS1Application() : LDSApplication() {
    override val applicationIdentifier: ByteArray = BigInteger(APPLICATION_ID, 16).toByteArray().slice(1..7).toByteArray()
    var bac = BAC()
        private set
    var efCOM : EfCom = EfCom()
        private set
    var efSod: EfSod = EfSod()
        private set
    var dg1 : DG1 = DG1()
        private set
    var dg2 : DG2 = DG2()
        private set
    var dg3 : DG3 = DG3()
        private set
    var dg4 : DG4 = DG4()
        private set
    var dg5 : DG5 = DG5()
        private set
    var dg6 : DG6 = DG6()
        private set
    var dg7 : DG7 = DG7()
        private set
    var dg8 : DG8 = DG8()
        private set
    var dg9 : DG9 = DG9()
        private set
    var dg10 : DG10 = DG10()
        private set
    var dg11 : DG11 = DG11()
        private set
    var dg12 : DG12 = DG12()
        private set
    var dg13 : DG13 = DG13()
        private set
    var dg14 : DG14 = DG14()
        private set
    var dg15 : DG15 = DG15()
        private set
    var dg16 : DG16 = DG16()
        private set
    var efMap = mapOf(
        dg1.shortEFIdentifier to dg1,
        dg2.shortEFIdentifier to dg2,
        dg3.shortEFIdentifier to dg3,
        dg4.shortEFIdentifier to dg4,
        dg5.shortEFIdentifier to dg5,
        dg6.shortEFIdentifier to dg6,
        dg7.shortEFIdentifier to dg7,
        dg8.shortEFIdentifier to dg8,
        dg9.shortEFIdentifier to dg9,
        dg10.shortEFIdentifier to dg10,
        dg11.shortEFIdentifier to dg11,
        dg12.shortEFIdentifier to dg12,
        dg13.shortEFIdentifier to dg13,
        dg14.shortEFIdentifier to dg14,
        dg15.shortEFIdentifier to dg15,
        dg16.shortEFIdentifier to dg16,
    )
        private set
    var certs : Array<X509Certificate>? = null

    override fun readFiles(readActivity : ReadPassport) {
        readActivity.changeProgressBar("Reading EF.COM file...", INCREMENT_PROGRESS_BAR)
        efCOM.read()
        for (ef in efMap) {
            readActivity.changeProgressBar("Reading DG${ef.key} file...", INCREMENT_PROGRESS_BAR)
            ef.value.read()
            ef.value.parse()
        }
        readActivity.changeProgressBar("Reading EF.SOD file...", INCREMENT_PROGRESS_BAR)
        if (efSod.read() == SUCCESS) {
            efSod.parse()
        }
    }

    fun performBACProtocol() : Int {
        if (bac.init(mrz) != SUCCESS) {
            return FAILURE
        }
        return bac.bacProtocol()
    }

    fun verify(readActivity: ReadPassport) {
        readActivity.changeProgressBar("Performing Passive Authentication...", INCREMENT_PROGRESS_BAR)
        efSod.checkHashes(efMap)
        efSod.passiveAuthentication(certs)
        readActivity.changeProgressBar("Performing Active Authentication...", INCREMENT_PROGRESS_BAR)
        dg15.activeAuthentication()
        readActivity.changeProgressBar("Performing Chip Authentication...", INCREMENT_PROGRESS_BAR)
        if (dg14.isRead && dg14.isPresent && dg14.securityInfos != null) {
            var chipPublicKey : ChipAuthenticationPublicKeyInfo? = null
            var chipInfo : ChipAuthenticationInfo? = null
            for (si in dg14.securityInfos!!) {
                if (si.type == CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE) {
                    chipPublicKey = si as ChipAuthenticationPublicKeyInfo
                } else if (si.type == CHIP_AUTHENTICATION_TYPE) {
                    chipInfo = si as ChipAuthenticationInfo
                }
            }
            val auth = if (chipPublicKey != null) {
                if (chipInfo != null) {
                        ChipAuthentication(chipPublicKey, chipInfo)
                } else if (EMRTD.pace.chipAuthenticationData != null && EMRTD.pace.chipPublicKey != null) {
                    ChipAuthentication(chipPublicKey, EMRTD.pace.chipAuthenticationData!!, EMRTD.pace.chipPublicKey!!)
                } else {
                    null
                }
            } else {
                null
            }
            auth?.authenticate()
        }
    }
}