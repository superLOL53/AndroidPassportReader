package com.example.emrtdapplication.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory


class FacialRecordData(facialRecord : ByteArray) {
    val facialInformation : FacialInformation = FacialInformation(facialRecord.slice(0..19).toByteArray())
    val featureList : Array<FeaturePoint>
    val imageInformation : ImageInformation
    val image : Bitmap

    init {
        val points = ArrayList<FeaturePoint>()
        for (i in 1..facialInformation.featurePoints) {
            points.add(FeaturePoint(facialRecord.slice(i*8..i*8+7).toByteArray()))
        }
        featureList = points.toTypedArray()
        imageInformation = ImageInformation(facialRecord.slice(20+8*facialInformation.featurePoints..20+8*facialInformation.featurePoints+11).toByteArray())
        image = BitmapFactory.decodeByteArray(facialRecord.slice(20+8*facialInformation.featurePoints+12..<facialRecord.size).toByteArray(), 0, facialRecord.size-(20+8*facialInformation.featurePoints+12))

    }
}