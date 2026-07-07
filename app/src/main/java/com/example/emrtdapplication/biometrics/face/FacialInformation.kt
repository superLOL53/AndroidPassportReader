package com.example.emrtdapplication.biometrics.face

import com.example.emrtdapplication.BYTE_BIT_SIZE
import com.example.emrtdapplication.UNKNOWN_STRING

/**
 * Class representing facial information in a facial data record according to ISO/IEC 19794-5
 * @param facialInformation Byte array containing encoded facial information
 * @property faceImageBlockLength Length of the facial data record
 * @property featurePoints Number of feature points in the feature point data block
 * @property gender Gender of the person in the image
 * @property eyeColor Eye color of the person in the image
 * @property hairColor Hair color of the person in the image
 * @property featureMask Bit mask of features that are present in the image
 * @property expression Facial expression of the person in the image
 * @property poseAngle The angle from which the image was taken
 * @property poseAngleUncertainty The uncertainty of the shooting angles
 * @throws IllegalArgumentException If [facialInformation]
 * does not contain an encoded facial information
 */
class FacialInformation(facialInformation: ByteArray) {
    val faceImageBlockLength: Int
    val featurePoints: Int
    val gender: String
    val eyeColor: String
    val hairColor: String
    val featureMask: Int
    val expression: String
    val poseAngle: ByteArray
    val poseAngleUncertainty: ByteArray

    init {
        if (facialInformation.size != FACIAL_INFORMATION_SIZE) {
            throw IllegalArgumentException(FACIAL_INFORMATION_SIZE_STRING)
        }
        faceImageBlockLength = (facialInformation[
                FACIAL_INFORMATION_IMAGE_BLOCK_LENGTH_INDEX_1
            ].toInt() shl (BYTE_BIT_SIZE*3)) +
                (facialInformation[
                    FACIAL_INFORMATION_IMAGE_BLOCK_LENGTH_INDEX_2
                ].toInt() shl (BYTE_BIT_SIZE*2)) +
                (facialInformation[
                    FACIAL_INFORMATION_IMAGE_BLOCK_LENGTH_INDEX_3
                ].toInt() shl BYTE_BIT_SIZE) +
                facialInformation[
                    FACIAL_INFORMATION_IMAGE_BLOCK_LENGTH_INDEX_4
                ]
        featurePoints = (facialInformation[
                FACIAL_INFORMATION_FEATURE_POINTS_INDEX_1
            ].toInt() shl BYTE_BIT_SIZE) +
            facialInformation[
                FACIAL_INFORMATION_FEATURE_POINTS_INDEX_2
            ]
        gender = setGender(facialInformation[FACIAL_INFORMATION_GENDER_INDEX])
        eyeColor = setEyeColor(facialInformation[FACIAL_INFORMATION_EYE_COLOR_INDEX])
        hairColor = setHairColor(facialInformation[FACIAL_INFORMATION_HAIR_COLOR_INDEX])
        featureMask = (facialInformation[
                FACIAL_INFORMATION_FEATURE_MASK_INDEX_1
            ].toInt() shl (BYTE_BIT_SIZE*2)) +
                (facialInformation[
                    FACIAL_INFORMATION_FEATURE_MASK_INDEX_2
                ].toInt() shl BYTE_BIT_SIZE) +
                facialInformation[
                    FACIAL_INFORMATION_FEATURE_MASK_INDEX_3
                ]
        expression = setExpression(
            facialInformation[FACIAL_INFORMATION_EXPRESSION_HIGH_BYTE],
            facialInformation[FACIAL_INFORMATION_EXPRESSION_LOW_BYTE]
        )
        poseAngle = facialInformation.slice(
            FACIAL_INFORMATION_POSE_ANGLE_START_INDEX..
                    FACIAL_INFORMATION_POSE_ANGLE_END_INDEX
        ).toByteArray()
        poseAngleUncertainty = facialInformation.slice(
            FACIAL_INFORMATION_POSE_ANGLE_UNCERTAINTY_START_INDEX..
                    FACIAL_INFORMATION_POSE_ANGLE_UNCERTAINTY_END_INDEX
        ).toByteArray()
    }

    private fun setGender(gender: Byte): String {
        return when (gender) {
            0.toByte() -> UNSPECIFIED_STRING
            1.toByte() -> MALE_STRING
            2.toByte() -> FEMALE_STRING
            3.toByte() -> UNKNOWN_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun setEyeColor(color: Byte): String {
        return when (color) {
            0.toByte() -> UNSPECIFIED_STRING
            1.toByte() -> BLACK_STRING
            2.toByte() -> BLUE_STRING
            3.toByte() -> BROWN_STRING
            4.toByte() -> GRAY_STRING
            5.toByte() -> GREEN_STRING
            6.toByte() -> MULTI_COLOURED_STRING
            7.toByte() -> PINK_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun setHairColor(color: Byte): String {
        return when (color) {
            0.toByte() -> UNSPECIFIED_STRING
            1.toByte() -> BALD_STRING
            2.toByte() -> BLACK_STRING
            3.toByte() -> BLONDE_STRING
            4.toByte() -> BROWN_STRING
            5.toByte() -> GRAY_STRING
            6.toByte() -> WHITE_STRING
            7.toByte() -> RED_STRING
            8.toByte() -> GREEN_STRING
            9.toByte() -> BLUE_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun setExpression(highByte: Byte, lowByte: Byte): String {
        if (highByte == 0.toByte()) {
            return when (lowByte) {
                0.toByte() -> UNSPECIFIED_STRING
                1.toByte() -> NEUTRAL_STRING
                2.toByte() -> CLOSED_SMILE_STRING
                3.toByte() -> SMILE_STRING
                4.toByte() -> RAISED_EYEBROWS_STRING
                5.toByte() -> EYES_LOOKING_AWAY_STRING
                6.toByte() -> SQUINTING_STRING
                7.toByte() -> FROWNING_STRING
                else -> RESERVED_STRING
            }
        } else if (highByte > 0) {
            return RESERVED_STRING
        } else {
            return VENDOR_SPECIFIC_STRING
        }
    }
}