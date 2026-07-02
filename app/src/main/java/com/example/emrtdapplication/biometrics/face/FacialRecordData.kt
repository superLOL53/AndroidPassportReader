package com.example.emrtdapplication.biometrics.face

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.emrtdapplication.biometrics.BiometricData
import com.example.emrtdapplication.biometrics.BiometricType
import com.example.emrtdapplication.constants.FacialInformationConstants.FACIAL_INFORMATION_SIZE
import com.example.emrtdapplication.constants.FacialRecordDataConstants.UNABLE_TO_DECODE_STRING
import com.example.emrtdapplication.constants.FeaturePointConstants.FEATURE_POINT_SIZE
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_SIZE

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
    val facialInformation : FacialInformation = FacialInformation(facialRecord.slice(0..<FACIAL_INFORMATION_SIZE).toByteArray())
    val featureList : Array<FeaturePoint>
    val imageInformation : ImageInformation
    val image : Bitmap

    init {
        try {
            val points = ArrayList<FeaturePoint>()
            for (i in 0..<facialInformation.featurePoints) {
                points.add(FeaturePoint(facialRecord.slice(FACIAL_INFORMATION_SIZE + i * FEATURE_POINT_SIZE..<FACIAL_INFORMATION_SIZE + i * FEATURE_POINT_SIZE + FEATURE_POINT_SIZE).toByteArray()))
            }
            featureList = points.toTypedArray()
            imageInformation = ImageInformation(
                facialRecord.slice(FACIAL_INFORMATION_SIZE + FEATURE_POINT_SIZE * facialInformation.featurePoints..<FACIAL_INFORMATION_SIZE + FEATURE_POINT_SIZE * facialInformation.featurePoints + IMAGE_INFORMATION_SIZE)
                    .toByteArray()
            )
            image = BitmapFactory.decodeByteArray(
                facialRecord.slice(FACIAL_INFORMATION_SIZE + FEATURE_POINT_SIZE * facialInformation.featurePoints + IMAGE_INFORMATION_SIZE..<facialRecord.size)
                    .toByteArray(),
                0,
                facialRecord.size - (FACIAL_INFORMATION_SIZE + FEATURE_POINT_SIZE * facialInformation.featurePoints + IMAGE_INFORMATION_SIZE)
            )
        } catch (_ : Exception) {
            throw IllegalArgumentException(UNABLE_TO_DECODE_STRING)
        }
    }
}