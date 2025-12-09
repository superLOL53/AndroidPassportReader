package com.example.emrtdapplication.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image


class FacialRecordData(private val facialrecord : ByteArray) {
    val facialInformation : FacialInformation
    val featureList : Array<FeaturePoint>
    val imageInformation : ImageInformation
    val image : Bitmap

    init {
        facialInformation = FacialInformation(facialrecord.slice(0..19).toByteArray())
        val points = ArrayList<FeaturePoint>()
        for (i in 1..facialInformation.featurePoints) {
            points.add(FeaturePoint(facialrecord.slice(i*8..i*8+7).toByteArray()))
        }
        featureList = points.toTypedArray()
        imageInformation = ImageInformation(facialrecord.slice(20+8*facialInformation.featurePoints..20+8*facialInformation.featurePoints+11).toByteArray())
        image = BitmapFactory.decodeByteArray(facialrecord.slice(20+8*facialInformation.featurePoints+12..<facialrecord.size).toByteArray(), 0, facialrecord.size-(20+8*facialInformation.featurePoints+12))

    }
}