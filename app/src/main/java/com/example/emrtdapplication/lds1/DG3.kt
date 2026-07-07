package com.example.emrtdapplication.lds1

import android.util.Log
import com.example.emrtdapplication.ANDROID_LOG_INFO_TAG
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.FAILURE
import com.example.emrtdapplication.SUCCESS
import com.example.emrtdapplication.biometrics.BiometricInformationGroupTemplate
import com.example.emrtdapplication.biometrics.BiometricType
import com.example.emrtdapplication.constants.TlvTags.DG3_FILE_TAG
import com.example.emrtdapplication.constants.TlvTags.DG3_SHORT_EF_ID
import com.example.emrtdapplication.utils.TLV

/**
 * Implements the DG3 file and inherits from [ElementaryFileTemplate]
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG2
 * @property efTag The tag of the DG2 file
 * @property biometricInformation The decoded biometric information
 * contained in the DG3 file or null if it could not be decoded
 */
class DG3: ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier = DG3_SHORT_EF_ID
    override val efTag = DG3_FILE_TAG
    var biometricInformation: BiometricInformationGroupTemplate? = null
        private set

    /**
     * Parses the contents of [rawFileContent]
     *
     * @return [SUCCESS] if the contents were successfully decoded, otherwise [FAILURE]
     */
    override fun parse(): Int {
        isParsed = false
        if (rawFileContent == null) {
            return FAILURE
        }
        var tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != efTag ||
            tlv.list == null || tlv.list!!.tlvSequence.isEmpty()) {
            return FAILURE
        }
        tlv = tlv.list!!.tlvSequence[0]
        try {
            biometricInformation = BiometricInformationGroupTemplate(
                tlv,
                BiometricType.FINGERPRINT
            )
            isParsed = biometricInformation != null &&
                    !biometricInformation!!.biometricInformationList.isNullOrEmpty()
        } catch (_: IllegalArgumentException) {
            Log.i(ANDROID_LOG_INFO_TAG, UNABLE_TO_DECODE_FINGERPRINT_DATA)
            isParsed = false
        }
        return SUCCESS
    }
}