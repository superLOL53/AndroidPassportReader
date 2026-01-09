package com.example.emrtdapplication.lds1

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.BiometricInformationGroupTemplate
import com.example.emrtdapplication.utils.BiometricType
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.FingerprintRecordData
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV

/**
 * Implements the DG4 file and inherits from [ElementaryFileTemplate]
 *
 * @property apduControl Class for communicating with the eMRTD
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG4
 * @property efTag The tag of the DG4 file
 * @property biometricInformation The decoded biometric information contained in the DG4 file or null if
 * it could not be decoded
 */
class DG4(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x04
    override val efTag: Byte = 0x76
    var biometricInformation : BiometricInformationGroupTemplate? = null
        private set

    /**
     * Parses the contents of [rawFileContent]
     * @return [SUCCESS] if the contents were successfully decoded, otherwise [FAILURE]
     */
    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        var tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != efTag ||
            tlv.list == null || tlv.list!!.tlvSequence.isEmpty()) {
            return FAILURE
        }
        tlv = tlv.list!!.tlvSequence[0]
        biometricInformation = BiometricInformationGroupTemplate(tlv, BiometricType.IRIS)
        if (biometricInformation != null && biometricInformation!!.biometricInformationList != null) {
            for (bit in biometricInformation!!.biometricInformationList!!) {
                if (bit != null && bit.biometricHeaderTemplate.biometricSubType == null) {
                    biometricInformation = null
                    return FAILURE
                }
            }
        }
        return SUCCESS
    }

    /**
     * Dynamically create a view for every biometric information in this file.
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        if (biometricInformation != null && biometricInformation!!.biometricInformationList != null) {
            for (bios in biometricInformation!!.biometricInformationList) {
                try {
                    if (bios == null) continue
                    val biometricData = bios.biometricDataBlock.biometricData as FingerprintRecordData
                    val image = biometricData.fingerprintData.representationBlocks[0].geImageData()
                    val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                    val view = ImageView(context)
                    view.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    view.setImageBitmap(bitmap)
                    parent.addView(view)
                } catch (_ : Exception) {
                }
            }
        }
    }
}