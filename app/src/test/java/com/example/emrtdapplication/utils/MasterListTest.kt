package com.example.emrtdapplication.utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test
import java.io.File
import java.security.Security

class MasterListTest {

    @Test
    fun readMasterList() {
        Security.addProvider(BouncyCastleProvider())
        val ml = File("/home/oliver/AndroidStudioProjects/eMRTDApplication/app/src/main/assets/MasterList/ICAO_ml_01April2025.ml")
        val ba = ml.readBytes()
        val ma = MasterList
        ma.decodeMasterList(ba)

    }
}