package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.graphics.text.LineBreaker
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.emrtdapplication.ABSENT
import com.example.emrtdapplication.ACTIVE_AUTHENTICATION_INFORMATION
import com.example.emrtdapplication.ACTIVE_AUTHENTICATION_TYPE
import com.example.emrtdapplication.ADDITIONAL_BIOMETRIC_APPLICATION
import com.example.emrtdapplication.AES_CBC_CMAC_128_STRING
import com.example.emrtdapplication.AES_CBC_CMAC_192_STRING
import com.example.emrtdapplication.AES_CBC_CMAC_256_STRING
import com.example.emrtdapplication.ALGORITHM_PARAMETER_OID
import com.example.emrtdapplication.ASYMMETRIC_PROTOCOL
import com.example.emrtdapplication.BP_P192
import com.example.emrtdapplication.BP_P224
import com.example.emrtdapplication.BP_P256
import com.example.emrtdapplication.BP_P320
import com.example.emrtdapplication.BP_P384
import com.example.emrtdapplication.BP_P512
import com.example.emrtdapplication.CHIP_AUTHENTICATION_INFORMATION
import com.example.emrtdapplication.CHIP_AUTHENTICATION_PUBLIC_KEY_INFORMATION
import com.example.emrtdapplication.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
import com.example.emrtdapplication.CHIP_AUTHENTICATION_TYPE
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.DES_CBC_CBC_STRING
import com.example.emrtdapplication.DOMAIN_PARAMETER
import com.example.emrtdapplication.DOMAIN_PARAMETER_ID
import com.example.emrtdapplication.EF_DIR_INFORMATION
import com.example.emrtdapplication.EF_DIR_TYPE
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.KEY_ID
import com.example.emrtdapplication.MAX_LINES_IN_DISPLAY
import com.example.emrtdapplication.MOD_1024_WITH_160_SUBGROUP
import com.example.emrtdapplication.MOD_2048_WITH_224_SUBGROUP
import com.example.emrtdapplication.MOD_2048_WITH_256_SUBGROUP
import com.example.emrtdapplication.NIST_P192_STRING
import com.example.emrtdapplication.NIST_P224_STRING
import com.example.emrtdapplication.NIST_P256_STRING
import com.example.emrtdapplication.NIST_P384_STRING
import com.example.emrtdapplication.NIST_P521_STRING
import com.example.emrtdapplication.PACE_DH_GM_STRING
import com.example.emrtdapplication.PACE_DH_IM_STRING
import com.example.emrtdapplication.PACE_DOMAIN_PARAMETER_INFO_TYPE
import com.example.emrtdapplication.PACE_ECDH_CAM_STRING
import com.example.emrtdapplication.PACE_ECDH_GM_STRING
import com.example.emrtdapplication.PACE_ECDH_IM_STRING
import com.example.emrtdapplication.PACE_INFO_TYPE
import com.example.emrtdapplication.PARAMETER_ID
import com.example.emrtdapplication.PRESENT
import com.example.emrtdapplication.PROTOCOL_OID
import com.example.emrtdapplication.PROTOCOL_VERSION
import com.example.emrtdapplication.PUBLIC_KEY_ALGORITHM_ID
import com.example.emrtdapplication.R
import com.example.emrtdapplication.SIGNATURE_ALGORITHM_OID
import com.example.emrtdapplication.SYMMETRIC_PROTOCOL
import com.example.emrtdapplication.TERMINAL_AUTHENTICATION_INFORMATION
import com.example.emrtdapplication.TERMINAL_AUTHENTICATION_TYPE
import com.example.emrtdapplication.TRAVEL_RECORDS_APPLICATION
import com.example.emrtdapplication.UNKNOWN_STRING
import com.example.emrtdapplication.VERSION
import com.example.emrtdapplication.VISA_RECORDS_APPLICATION
import com.example.emrtdapplication.common.AES_CBC_CMAC_128
import com.example.emrtdapplication.common.AES_CBC_CMAC_192
import com.example.emrtdapplication.common.AES_CBC_CMAC_256
import com.example.emrtdapplication.common.ActiveAuthenticationInfo
import com.example.emrtdapplication.common.BRAIN_POOL_P192R1
import com.example.emrtdapplication.common.BRAIN_POOL_P224R1
import com.example.emrtdapplication.common.BRAIN_POOL_P256R1
import com.example.emrtdapplication.common.BRAIN_POOL_P320R1
import com.example.emrtdapplication.common.BRAIN_POOL_P384R1
import com.example.emrtdapplication.common.BRAIN_POOL_P512R1
import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
import com.example.emrtdapplication.common.DES_CBC_CBC
import com.example.emrtdapplication.common.DH_GM
import com.example.emrtdapplication.common.DH_IM
import com.example.emrtdapplication.common.ECDH_CAM
import com.example.emrtdapplication.common.ECDH_GM
import com.example.emrtdapplication.common.ECDH_IM
import com.example.emrtdapplication.common.EFDIRInfo
import com.example.emrtdapplication.common.MOD_P_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP
import com.example.emrtdapplication.common.MOD_P_2048_BIT_GROUP_WITH_224_BIT_PRIME_ORDER_SUBGROUP
import com.example.emrtdapplication.common.MOD_P_2048_BIT_GROUP_WITH_256_BIT_PRIME_ORDER_SUBGROUP
import com.example.emrtdapplication.common.NIST_P192
import com.example.emrtdapplication.common.NIST_P224
import com.example.emrtdapplication.common.NIST_P256
import com.example.emrtdapplication.common.NIST_P384
import com.example.emrtdapplication.common.NIST_P521
import com.example.emrtdapplication.common.PACEDomainParameterInfo
import com.example.emrtdapplication.common.PACEInfo
import com.example.emrtdapplication.common.TerminalAuthenticationInfo

object DG14Display: CreateView() {
    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg14.securityInfos == null) return
        if (!EMRTD.showDetails) return
        for (si in EMRTD.ldS1Application.dg14.securityInfos) {
            when (si.type) {
                PACE_INFO_TYPE ->
                    displayPIType(context, parent, si as PACEInfo)
                PACE_DOMAIN_PARAMETER_INFO_TYPE ->
                    displayPDType(context, parent, si as PACEDomainParameterInfo)
                ACTIVE_AUTHENTICATION_TYPE ->
                    displayAAType(context, parent, si as ActiveAuthenticationInfo)
                CHIP_AUTHENTICATION_TYPE ->
                    displayCAType(context, parent, si as ChipAuthenticationInfo)
                CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE ->
                    displayCAPKIType(context, parent, si as ChipAuthenticationPublicKeyInfo)
                TERMINAL_AUTHENTICATION_TYPE ->
                    displayTAType(context, parent, si as TerminalAuthenticationInfo)
                EF_DIR_TYPE ->
                    displayEDIType(context, parent, si as EFDIRInfo)
            }
        }
    }

    private fun <T: LinearLayout> displayPIType(
        context: Context, parent: T,
        paceInfo: PACEInfo
    ) {
        createHeader(
            context,
            parent,
            context.getString(
                R.string.pace_protocol_information
            )
        )
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(
            row,
            PROTOCOL_OID,
            paceInfo.objectIdentifier
        )
        row = createRow(context, parent)
        provideTextForRow(
            row,
            VERSION,
            paceInfo.version.toString(10)
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            ASYMMETRIC_PROTOCOL,
            decodeAsymmetricProtocol(paceInfo.asymmetricProtocol)
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            SYMMETRIC_PROTOCOL,
            decodeSymmetricProtocol(paceInfo.symmetricProtocol)
        )
        if (paceInfo.parameterId != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                DOMAIN_PARAMETER_ID,
                "${paceInfo.parameterId!!}"
            )
            row = createRow(context, table)
            provideTextForRow(
                row,
                DOMAIN_PARAMETER,
                decodeParameters(paceInfo.parameterId!!)
            )
        }
    }

    private fun decodeSymmetricProtocol(byte: Byte): String {
        return when(byte) {
            DES_CBC_CBC -> DES_CBC_CBC_STRING
            AES_CBC_CMAC_128 -> AES_CBC_CMAC_128_STRING
            AES_CBC_CMAC_192 -> AES_CBC_CMAC_192_STRING
            AES_CBC_CMAC_256 -> AES_CBC_CMAC_256_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun decodeAsymmetricProtocol(byte: Byte): String {
        return when(byte) {
            DH_GM -> PACE_DH_GM_STRING
            ECDH_GM -> PACE_ECDH_GM_STRING
            DH_IM -> PACE_DH_IM_STRING
            ECDH_IM -> PACE_ECDH_IM_STRING
            ECDH_CAM -> PACE_ECDH_CAM_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun decodeParameters(byte: Byte): String {
        return when(byte) {
            MOD_P_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP -> MOD_1024_WITH_160_SUBGROUP
            MOD_P_2048_BIT_GROUP_WITH_224_BIT_PRIME_ORDER_SUBGROUP -> MOD_2048_WITH_224_SUBGROUP
            MOD_P_2048_BIT_GROUP_WITH_256_BIT_PRIME_ORDER_SUBGROUP -> MOD_2048_WITH_256_SUBGROUP
            NIST_P192 -> NIST_P192_STRING
            BRAIN_POOL_P192R1 -> BP_P192
            NIST_P224 -> NIST_P224_STRING
            BRAIN_POOL_P224R1 -> BP_P224
            NIST_P256 -> NIST_P256_STRING
            BRAIN_POOL_P256R1 -> BP_P256
            BRAIN_POOL_P320R1 -> BP_P320
            NIST_P384 -> NIST_P384_STRING
            BRAIN_POOL_P384R1 -> BP_P384
            BRAIN_POOL_P512R1 -> BP_P512
            NIST_P521 -> NIST_P521_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun <T: LinearLayout> displayPDType(
        context: Context,
        parent: T,
        paceDomainParameterInfo: PACEDomainParameterInfo
    ) {
        createHeader(context, parent, DOMAIN_PARAMETER)
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(
            row,
            PROTOCOL_OID,
            paceDomainParameterInfo.objectIdentifier
        )
        row = createRow(context, parent)
        provideTextForRow(
            row,
            ALGORITHM_PARAMETER_OID,
            paceDomainParameterInfo.algorithmIdentifier.algorithm.id
        )
        if (paceDomainParameterInfo.parameterId != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                PARAMETER_ID,
                paceDomainParameterInfo.parameterId.toString(10)
            )
        }
    }

    private fun <T: LinearLayout> displayAAType(
        context: Context,
        parent: T,
        activeAuthenticationInfo: ActiveAuthenticationInfo
    ) {
        createHeader(context, parent, ACTIVE_AUTHENTICATION_INFORMATION)
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(
            row,
            PROTOCOL_OID,
            activeAuthenticationInfo.objectIdentifier
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            VERSION,
            activeAuthenticationInfo.version.toString(10)
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            SIGNATURE_ALGORITHM_OID,
            activeAuthenticationInfo.signatureAlgorithm
        )
    }

    private fun <T: LinearLayout> displayCAType(
        context: Context,
        parent: T,
        chipAuthenticationInfo: ChipAuthenticationInfo
    ) {
        createHeader(context, parent, CHIP_AUTHENTICATION_INFORMATION)
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(
            row,
            PROTOCOL_OID,
            chipAuthenticationInfo.objectIdentifier
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            VERSION,
            chipAuthenticationInfo.version.toString(10)
        )
        if (chipAuthenticationInfo.keyId != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                KEY_ID,
                chipAuthenticationInfo.keyId!!.toString(10)
            )
        }
    }

    private fun <T: LinearLayout> displayCAPKIType(
        context: Context,
        parent: T,
        chipAuthenticationPublicKeyInfo: ChipAuthenticationPublicKeyInfo
    ) {
        createHeader(context, parent, CHIP_AUTHENTICATION_PUBLIC_KEY_INFORMATION)
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(
            row,
            PROTOCOL_OID,
            chipAuthenticationPublicKeyInfo.objectIdentifier
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            PUBLIC_KEY_ALGORITHM_ID,
            chipAuthenticationPublicKeyInfo.publicKeyInfo.algorithm.algorithm.id
        )
        if (chipAuthenticationPublicKeyInfo.keyId != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                KEY_ID,
                chipAuthenticationPublicKeyInfo.keyId!!.toString()
            )
        }
        var text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        text.gravity = Gravity.CENTER
        if (alternate) {
            text.setBackgroundColor(
                context.resources.getColor(R.color.gray,
                    null)
            )
        } else {
            text.setBackgroundColor(
                context.resources.getColor(R.color.black,
                    null)
            )
        }
        alternate = !alternate
        text.text = context.getString(R.string.public_key)
        parent.addView(text)
        text = TextView(context)
        text.gravity = Gravity.CENTER
        text.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        text.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
        text.maxLines = MAX_LINES_IN_DISPLAY
        if (alternate) {
            text.setBackgroundColor(
                context.resources.getColor(R.color.gray,
                    null)
            )
        } else {
            text.setBackgroundColor(
                context.resources.getColor(R.color.black,
                    null)
            )
        }
        alternate = !alternate
        text.text =
            chipAuthenticationPublicKeyInfo.publicKeyInfo.publicKeyData.bytes.toHexString(
                HexFormat {
                    upperCase = true
                    bytes.byteSeparator=" "
                }
            )
        parent.addView(text)
    }

    private fun <T: LinearLayout> displayTAType(
        context: Context,
        parent: T,
        terminalAuthenticationInfo: TerminalAuthenticationInfo
    ) {
        createHeader(context, parent, TERMINAL_AUTHENTICATION_INFORMATION)
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(
            row,
            PROTOCOL_OID,
            terminalAuthenticationInfo.objectIdentifier
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            PROTOCOL_VERSION,
            terminalAuthenticationInfo.version.toString()
        )
    }

    private fun <T: LinearLayout> displayEDIType(
        context: Context,
        parent: T,
        efDirInfo: EFDIRInfo
    ) {
        createHeader(context, parent, EF_DIR_INFORMATION)
        val table = createTable(context, parent)
        var row = createRow(context, table)
        var status = if (efDirInfo.efDir.hasVisaRecordsApplication) {
            PRESENT
        } else {
            ABSENT
        }
        provideTextForRow(row, VISA_RECORDS_APPLICATION, status)
        row = createRow(context, table)
        status = if (efDirInfo.efDir.hasTravelRecordsApplication) {
            PRESENT
        } else {
            ABSENT
        }
        provideTextForRow(row, TRAVEL_RECORDS_APPLICATION, status)
        row = createRow(context, table)
        status = if (efDirInfo.efDir.hasAdditionalBiometricsApplication) {
            PRESENT
        } else {
            ABSENT
        }
        provideTextForRow(row, ADDITIONAL_BIOMETRIC_APPLICATION, status)
    }
}