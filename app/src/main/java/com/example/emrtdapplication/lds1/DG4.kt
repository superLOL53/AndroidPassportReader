package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.biometrics.BiometricInformationGroupTemplate
import com.example.emrtdapplication.biometrics.BiometricType
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.TlvTags.DG4_FILE_TAG
import com.example.emrtdapplication.constants.TlvTags.DG4_SHORT_EF_ID
import com.example.emrtdapplication.utils.TLV

/**
 * Implements the DG4 file
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG4
 * @property efTag The tag of the DG4 file
 * @property biometricInformation The decoded biometric information contained in the DG4 file or null if
 * it could not be decoded
 */
class DG4() : ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier = DG4_SHORT_EF_ID
    override val efTag = DG4_FILE_TAG
    var biometricInformation : BiometricInformationGroupTemplate? = null
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
            biometricInformation = BiometricInformationGroupTemplate(tlv, BiometricType.IRIS)
            isParsed = biometricInformation != null && !biometricInformation!!.biometricInformationList.isNullOrEmpty()
        } catch (_ : IllegalArgumentException) {
            isParsed = false
        }
        return SUCCESS
    }
}