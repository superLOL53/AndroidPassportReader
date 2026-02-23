package com.example.emrtdapplication.common

import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.TLV
import org.junit.Test
import org.spongycastle.asn1.x9.ECNamedCurveTable
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import java.math.BigInteger
import kotlin.test.assertEquals

class ChipAuthenticationTest {

    @Test
    fun PACECAMTest() {
        val params = ECNamedCurveTable.getByName("brainpoolp256r1")
        val domainParams = ECDomainParameters(params.curve, params.g, params.n, params.h)
        val pkx = BigInteger("A234236AA9B9621E8EFB73B5245C0E09" +
                "D2576E5277183C1208BDD55280CAE8B3", 16).toByteArray().slice(1..32).toByteArray()
        val pky = BigInteger("04F365713A356E65A451E165ECC9AC0A" +
                "C46E3771342C8FE5AEDD092685338E23",16).toByteArray()
        val pkmapic = ECPublicKeyParameters(domainParams.curve.decodePoint(byteArrayOf(0x04) + pkx + pky), domainParams)
        val caic = BigInteger("85DC3FA93D0952BFA82F5FD189EE75BD" +
                "82F11D1F0B8ED4BF5319AC9B53C426B3", 16).toByteArray().slice(1..32).toByteArray()
        val pkic = TLV(BigInteger("3062060904007F" +
                "0007020201023052300C060704007F0007010202" +
                "010D034200041872709494399E7470A6431BE25E83EE" +
                "E24FEA568C2ED28DB48E05DB3A610DC8" +
                "84D256A40E35EFCB59BF6753D3A489D2" +
                "8C7A4D973C2DA138A6E7A4A08F68E16F02010D", 16).toByteArray()
        )
        val ca = ChipAuthentication(ChipAuthenticationPublicKeyInfo(pkic) , caic, pkmapic)
        val success = ca.authenticate()
        assertEquals(SUCCESS, success)
    }
}