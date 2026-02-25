package com.example.emrtdapplication.utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test
import org.spongycastle.asn1.x500.X500Name
import java.io.File
import java.security.Security

class MasterListTest {

    @Test
    fun readMasterList() {
        Security.addProvider(BouncyCastleProvider())
        val ml = File("/home/oliver/StudioProjects/AndroidPassportReader/app/src/main/assets/MasterList/ICAO_ml_01April2025.ml")
        val ba = ml.readBytes()
        MasterList(ba, X500Name("C=AT"))
    }
}