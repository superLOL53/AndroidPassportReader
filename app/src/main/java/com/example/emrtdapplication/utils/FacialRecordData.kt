package com.example.emrtdapplication.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Class representing face biometrics data.
 * @param facialRecord Byte array containing a facial record according to ISO\IEC 19794-5
 * @property facialInformation Encoded facial information
 * @property featureList A list of facial features contained in the facial record
 * @property imageInformation Additional information about the image
 * @property image A face image encoded per ISO/IEC 19794-5
 * @throws IllegalArgumentException If the facial information in [facialRecord] could not be decoded
 */
class FacialRecordData(facialRecord : ByteArray) : BiometricData(BiometricType.FACE) {
    val facialInformation : FacialInformation = FacialInformation(facialRecord.slice(0..19).toByteArray())
    val featureList : Array<FeaturePoint>
    val imageInformation : ImageInformation
    val image : Bitmap

    init {
        try {
            val points = ArrayList<FeaturePoint>()
            for (i in 1..facialInformation.featurePoints) {
                points.add(FeaturePoint(facialRecord.slice(i * 8..i * 8 + 7).toByteArray()))
            }
            featureList = points.toTypedArray()
            imageInformation = ImageInformation(
                facialRecord.slice(20 + 8 * facialInformation.featurePoints..20 + 8 * facialInformation.featurePoints + 11)
                    .toByteArray()
            )
            image = BitmapFactory.decodeByteArray(
                facialRecord.slice(20 + 8 * facialInformation.featurePoints + 12..<facialRecord.size)
                    .toByteArray(),
                0,
                facialRecord.size - (20 + 8 * facialInformation.featurePoints + 12)
            )
        } catch (_ : Exception) {
            throw IllegalArgumentException("Unable to decode facial feature!")
        }
    }
}