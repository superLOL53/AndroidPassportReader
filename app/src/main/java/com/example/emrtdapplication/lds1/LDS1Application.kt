package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
import com.example.emrtdapplication.CHIP_AUTHENTICATION_TYPE
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.EMRTD.mrz
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.LDSApplication
import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.SUCCESS
import java.security.SecureRandom
import java.security.cert.X509Certificate
import kotlin.collections.iterator

class LDS1Application(apduControl: APDUControl) : LDSApplication(apduControl) {
    override val applicationIdentifier: ByteArray = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x02, 0x47, 0x10, 0x01)
    var bac = BAC(EMRTD.apduControl)
        private set
    var efCOM : EfCom = EfCom(apduControl)
        private set
    var efSod: EfSod = EfSod(apduControl)
        private set
    var dg1 : DG1 = DG1(apduControl)
        private set
    var dg2 : DG2 = DG2(apduControl)
        private set
    var dg3 : DG3 = DG3(apduControl)
        private set
    var dg4 : DG4 = DG4(apduControl)
        private set
    var dg5 : DG5 = DG5(apduControl)
        private set
    var dg6 : DG6 = DG6(apduControl)
        private set
    var dg7 : DG7 = DG7(apduControl)
        private set
    var dg8 : DG8 = DG8(apduControl)
        private set
    var dg9 : DG9 = DG9(apduControl)
        private set
    var dg10 : DG10 = DG10(apduControl)
        private set
    var dg11 : DG11 = DG11(apduControl)
        private set
    var dg12 : DG12 = DG12(apduControl)
        private set
    var dg13 : DG13 = DG13(apduControl)
        private set
    var dg14 : DG14 = DG14(apduControl)
        private set
    var dg15 : DG15 = DG15(apduControl)
        private set
    var dg16 : DG16 = DG16(apduControl)
        private set
    var efMap = mapOf<Byte, ElementaryFileTemplate>(
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
        readActivity.changeProgressBar("Reading EF.COM file...", 3)
        efCOM.read()
        for (ef in efMap) {
            readActivity.changeProgressBar("Reading DG${ef.key} file...", 3)
            ef.value.read()
            ef.value.parse()
        }
        readActivity.changeProgressBar("Reading EF.SOD file...", 3)
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
        readActivity.changeProgressBar("Performing Passive Authentication...", 5)
        efSod.checkHashes(efMap)
        efSod.passiveAuthentication(certs)
        readActivity.changeProgressBar("Performing Active Authentication...", 5)
        dg15.activeAuthentication(SecureRandom())
        readActivity.changeProgressBar("Performing Chip Authentication...", 5)
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
            if (chipPublicKey != null && chipInfo != null) {
                val auth = ChipAuthentication(
                    EMRTD.apduControl, null, ByteArray(0),
                    chipPublicKey, chipAuthenticationInfo = chipInfo)
                auth.authenticate()
            }
        }
    }
}