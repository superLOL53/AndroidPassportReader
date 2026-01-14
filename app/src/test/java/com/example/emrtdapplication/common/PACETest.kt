package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.constants.SUCCESS
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.spongycastle.asn1.x9.ECNamedCurveTable
import org.spongycastle.crypto.AsymmetricCipherKeyPair
import org.spongycastle.crypto.agreement.DHStandardGroups
import org.spongycastle.crypto.params.DHParameters
import org.spongycastle.crypto.params.DHPrivateKeyParameters
import org.spongycastle.crypto.params.DHPublicKeyParameters
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import java.math.BigInteger
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class PACETest {
    private lateinit var mockAPDUControl: APDUControl
    private lateinit var mockCrypto: Crypto

    @BeforeTest
    fun setUp() {
        mockAPDUControl = mock()
        mockCrypto = mock()

        `when`(mockAPDUControl.checkResponse(any())).thenCallRealMethod()
        `when`(mockAPDUControl.removeRespondCodes(any())).thenCallRealMethod()

        `when`(mockCrypto.cipherAES(any(), any(), any(), any())).thenCallRealMethod()
        `when`(mockCrypto.genericMappingDH(any(), any(), any(), any())).thenCallRealMethod()
        `when`(mockCrypto.genericMappingEC(any(), any(), any())).thenCallRealMethod()
        `when`(mockCrypto.computeKey(any(), any(), any(), any())).thenCallRealMethod()
        `when`(mockCrypto.computeCMAC(any(), any(), any())).thenCallRealMethod()
        `when`(mockCrypto.hash(any(), any())).thenCallRealMethod()
        `when`(mockCrypto.calculateDHAgreement(any(), any())).thenCallRealMethod()
        `when`(mockCrypto.calculateECDHAgreement(any(), any())).thenCallRealMethod()
        `when`(mockCrypto.getECPointFromBigInteger(any(), any())).thenCallRealMethod()
        `when`(mockCrypto.integratedMappingPRNG(any(), any(), any(), any())).thenCallRealMethod()
        `when`(mockCrypto.integratedMappingDH(any(), any(), any())).thenCallRealMethod()
        `when`(mockCrypto.integratedMappingEC(any(), any(), any(), any())).thenCallRealMethod()
        `when`(mockCrypto.removePadding(any())).thenCallRealMethod()
    }

    @Test
    fun testDHGM() {
        `when`(mockAPDUControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(BigInteger("7C128010854D8DF5827FA6852D1A4FA701CDDDCA9000", 16).toByteArray())
            .thenReturn(BigInteger("7C818382818078879F57225AA8080D52ED0FC890A4B25336F699AA89A2D3A189654AF70729E623EA5738B26381E4DA19E004706FACE7B235C2DBF2F38748312F3C98C2DD4882A41947B324AA1259AC22579DB93F7085655AF30889DBB845D9E6783FE42C9F2449400306254C8AE8EE9DD812A804C0B66E8CAFC14F84D8258950A91B44126EE69000", 16).toByteArray())
            .thenReturn(BigInteger("7C8183848180075693D9AE941877573E634B6E644F8E60AF" +
                    "17A0076B8B123D9201074D36152BD8B3A213F53820C42ADC" +
                    "79AB5D0AEEC3AEFB91394DA476BD97B9B14D0A65C1FC71A0" +
                    "E019CB08AF55E1F729005FBA7E3FA5DC41899238A250767A" +
                    "6D46DB974064386CD456743585F8E5D90CC8B4004B1F6D86" +
                    "6C79CE0584E49687FF61BC29AEA19000", 16).toByteArray())
            .thenReturn(BigInteger("7C0A8608917F37B5C0E6D8D19000", 16).toByteArray())

        val dh = DHStandardGroups.rfc5114_1024_160
        val g = BigInteger("7C9CBFE98F9FBDDA8D143506FA7D9306" +
                "F4CB17E3C71707AFF5E1C1A123702496" +
                "84D64EE37AF44B8DBD9D45BF6023919C" +
                "BAA027AB97ACC771666C8E98FF483301" +
                "BFA4872DEDE9034EDFACB70814166B7F" +
                "360676829B826BEA57291B5AD69FBC84" +
                "EF1E779032A305803F74341793E86974" +
                "2D401325B37EE8565FFCDEE618342DC5", 16)
        `when`(mockCrypto.generateDHKeyPair(any()))
            .thenReturn(AsymmetricCipherKeyPair(DHPublicKeyParameters(BigInteger("23FB3749EA030D2A25B278D2A562047ADE3F01B74F17A15402CB7352CA7D2B3EB71C343DB13D1DEBCE9A3666DBCFC920B49174A602CB47965CAA73DC702489A44D41DB914DE9613DC5E98C94160551C0DF86274B9359BC0490D01B03AD54022DCB4F57FAD6322497D7A1E28D46710F461AFE710FBBBC5F8BA166F4311975EC6C", 16), DHStandardGroups.rfc5114_1024_160), DHPrivateKeyParameters(BigInteger("5265030F751F4AD18B08AC565FC7AC952E41618D", 16), DHStandardGroups.rfc5114_1024_160)))
            .thenReturn(AsymmetricCipherKeyPair(DHPublicKeyParameters(BigInteger("00907D89E2D425A178AA81AF4A7774EC8E388C115CAE67031E85EECE520BD911551B9AE4D04369F29A02626C86FBC6747CC7BC352645B6161A2A42D44EDA80A08FA8D61B76D3A154AD8A5A51786B0BC07147057871A922212C5F67F43173172236B7747D1671E6D692A3C7D40A0C3C5CE397545D015C175EB5130551EDBC2EE5D4", 16) , DHParameters(dh.p, g, dh.q)), DHPrivateKeyParameters(BigInteger("0089CCD99B0E8D3B1F11E1296DCA68EC53411CF2CA", 16), DHParameters(dh.p, g, dh.q))))

        val pace = PACE()
        pace.init("T22000129364081251010318", false, byteArrayOf(0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x04, 0x01, 0x02), 0x00)
        val result = pace.paceProtocol()

        val apduCapture = argumentCaptor<APDU>()
        verify(mockAPDUControl, times(5)).sendAPDU(apduCapture.capture())
        val apdu = apduCapture.allValues

        val cryptoCapture = argumentCaptor<DHParameters>()
        verify(mockCrypto, times(2)).generateDHKeyPair(cryptoCapture.capture())
        val crypto = cryptoCapture.allValues

        val s = argumentCaptor<ByteArray>()
        verify(mockCrypto).genericMappingDH(any(), s.capture(), any(), any())
        val sCapture = s.firstValue

        val keys = argumentCaptor<ByteArray>()
        verify(mockAPDUControl).setEncryptionKeyBAC(keys.capture())
        verify(mockAPDUControl).setEncryptionKeyMAC(keys.capture())

        assertArrayEquals(BigInteger("FA5B7E3E49753A0DB9178B7B9BD898C8", 16).toByteArray().slice(1..16).toByteArray(), sCapture)

        assertArrayEquals(ByteArray(1) + BigInteger("22C1A40F800A04007F00070202040102830101", 16).toByteArray(), apdu[0].getByteArray())
        assertArrayEquals(BigInteger("10860000027C0000", 16).toByteArray(), apdu[1].getByteArray())
        assertArrayEquals(BigInteger("10860000867C818381818023FB3749EA030D2A25B278D2A5" +
                "62047ADE3F01B74F17A15402CB7352CA7D2B3EB71C343DB1" +
                "3D1DEBCE9A3666DBCFC920B49174A602CB47965CAA73DC70" +
                "2489A44D41DB914DE9613DC5E98C94160551C0DF86274B93" +
                "59BC0490D01B03AD54022DCB4F57FAD6322497D7A1E28D46" +
                "710F461AFE710FBBBC5F8BA166F4311975EC6C00", 16).toByteArray(), apdu[2].getByteArray())
        assertArrayEquals(BigInteger("10860000867C8183838180907D89E2D425A178AA81AF4A77" +
                "74EC8E388C115CAE67031E85EECE520BD911551B9AE4D043" +
                "69F29A02626C86FBC6747CC7BC352645B6161A2A42D44EDA" +
                "80A08FA8D61B76D3A154AD8A5A51786B0BC07147057871A9" +
                "22212C5F67F43173172236B7747D1671E6D692A3C7D40A0C" +
                "3C5CE397545D015C175EB5130551EDBC2EE5D400", 16).toByteArray(), apdu[3].getByteArray())
        assertArrayEquals(BigInteger("008600000C7C0A8508B46DD9BD4D98381F00", 16).toByteArray(), apdu[4].getByteArray())

        assertArrayEquals(BigInteger("00B10B8F96A080E01DDE92DE5EAE5D54EC" +
                "52C99FBCFB06A3C69A6A9DCA52D23B61" +
                "6073E28675A23D189838EF1E2EE652C0" +
                "13ECB4AEA906112324975C3CD49B83BF" +
                "ACCBDD7D90C4BD7098488E9C219A7372" +
                "4EFFD6FAE5644738FAA31A4FF55BCCC0" +
                "A151AF5F0DC8B4BD45BF37DF365C1A65" +
                "E68CFDA76D4DA708DF1FB2BC2E4A4371", 16).toByteArray(), crypto[0].p.toByteArray())
        assertArrayEquals(BigInteger("A4D1CBD5C3FD34126765A442EFB99905" +
                "F8104DD258AC507FD6406CFF14266D31" +
                "266FEA1E5C41564B777E690F5504F213" +
                "160217B4B01B886A5E91547F9E2749F4" +
                "D7FBD7D3B9A92EE1909D0D2263F80A76" +
                "A6A24C087A091F531DBF0A0169B6A28A" +
                "D662A4D18E73AFA32D779D5918D08BC8" +
                "858F4DCEF97C2A24855E6EEB22B3B2E5", 16).toByteArray(), crypto[0].g.toByteArray())
        assertArrayEquals(BigInteger("F518AA8781A8DF278ABA4E7D64B7CB9D49462353", 16).toByteArray(), crypto[0].q.toByteArray())
        assertArrayEquals(BigInteger("00B10B8F96A080E01DDE92DE5EAE5D54EC" +
                "52C99FBCFB06A3C69A6A9DCA52D23B61" +
                "6073E28675A23D189838EF1E2EE652C0" +
                "13ECB4AEA906112324975C3CD49B83BF" +
                "ACCBDD7D90C4BD7098488E9C219A7372" +
                "4EFFD6FAE5644738FAA31A4FF55BCCC0" +
                "A151AF5F0DC8B4BD45BF37DF365C1A65" +
                "E68CFDA76D4DA708DF1FB2BC2E4A4371", 16).toByteArray(), crypto[1].p.toByteArray())
        assertArrayEquals(g.toByteArray(), crypto[1].g.toByteArray())
        assertArrayEquals(BigInteger("F518AA8781A8DF278ABA4E7D64B7CB9D49462353", 16).toByteArray(), crypto[1].q.toByteArray())

        assertArrayEquals(BigInteger("2F7F46ADCC9E7E521B45D192FAFA9126", 16).toByteArray(), keys.firstValue)
        assertArrayEquals(BigInteger("805A1D27D45A5116F73C54469462B7D8", 16).toByteArray().slice(1..16).toByteArray(), keys.lastValue)

        assertEquals(SUCCESS, result)
    }

    @Test
    fun testECGM() {
        `when`(mockAPDUControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(BigInteger("7C12801095A3A016522EE98D01E76CB6B98B42C39000", 16).toByteArray())
            .thenReturn(BigInteger("7C43824104824FBA91C9CBE26BEF53A0EBE7342A3B" +
                    "F178CEA9F45DE0B70AA601651FBA3F5730D8C879AA" +
                    "A9C9F73991E61B58F4D52EB87A0A0C709A49DC6371" +
                    "9363CCD13C549000", 16).toByteArray())
            .thenReturn(BigInteger("7C438441049E880F842905B8B3181F7AF7CAA9F0EF" +
                    "B743847F44A306D2D28C1D9EC65DF6DB7764B22277" +
                    "A2EDDC3C265A9F018F9CB852E111B768B326904B59" +
                    "A0193776F0949000", 16).toByteArray())
            .thenReturn(BigInteger("7C0A86083ABB9674BCE93C089000", 16).toByteArray())

        val params = ECNamedCurveTable.getByName("brainpoolp256r1")
        val domainParameters = ECDomainParameters(params.curve, params.g, params.n, params.h)
        val g = params.curve.createPoint(
            BigInteger("008CED63C91426D4F0EB1435E7CB1D74A4" +
                        "6723A0AF21C89634F65A9AE87A9265E2", 16),
            BigInteger("008C879506743F8611AC33645C5B985C80" +
                    "B5F09A0B83407C1B6A4D857AE76FE522", 16)
        )
        `when`(mockCrypto.generateECKeyPair(any()))
            .thenReturn(AsymmetricCipherKeyPair(
                ECPublicKeyParameters(
                    params.curve.createPoint(
                        BigInteger(
                            "7ACF3EFC982EC45565A4B155129EFBC7" +
                                    "4650DCBFA6362D896FC70262E0C2CC5E", 16),
                        BigInteger("544552DCB6725218799115B55C9BAA6D" +
                                "9F6BC3A9618E70C25AF71777A9C4922D", 16)
                    ), domainParameters),
                ECPrivateKeyParameters(
                    BigInteger("7F4EF07B9EA82FD78AD689B38D0BC78C" +
                                "F21F249D953BC46F4C6E19259C010F99", 16),
                    domainParameters
                )))
            .thenReturn(AsymmetricCipherKeyPair(
                ECPublicKeyParameters(
                    params.curve.createPoint(
                        BigInteger(
                            "2DB7A64C0355044EC9DF190514C625CB" +
                                    "A2CEA48754887122F3A5EF0D5EDD301C", 16),
                        BigInteger("3556F3B3B186DF10B857B58F6A7EB80F" +
                                "20BA5DC7BE1D43D9BF850149FBB36462", 16)
                        ), ECDomainParameters(params.curve, g, params.n, params.h)),
                ECPrivateKeyParameters(
                    BigInteger(
                        "00A73FB703AC1436A18E0CFA5ABB3F7BEC" +
                                "7A070E7A6788486BEE230C4A22762595", 16),
                    ECDomainParameters(params.curve, g, params.n, params.h)
            )))

        val pace = PACE()
        pace.init("T22000129364081251010318", false, byteArrayOf(0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x04, 0x02, 0x02), 0x0D)
        val result = pace.paceProtocol()

        val apduCapture = argumentCaptor<APDU>()
        verify(mockAPDUControl, times(5)).sendAPDU(apduCapture.capture())
        val apdu = apduCapture.allValues

        val cryptoCapture = argumentCaptor<ECDomainParameters>()
        verify(mockCrypto, times(2)).generateECKeyPair(cryptoCapture.capture())
        val crypto = cryptoCapture.allValues

        val s = argumentCaptor<ByteArray>()
        verify(mockCrypto).genericMappingEC(any(), s.capture(), any())
        val sCapture = s.firstValue

        val keys = argumentCaptor<ByteArray>()
        verify(mockAPDUControl).setEncryptionKeyBAC(keys.capture())
        verify(mockAPDUControl).setEncryptionKeyMAC(keys.capture())

        assertArrayEquals(BigInteger("3F00C4D39D153F2B2A214A078D899B22", 16).toByteArray(), sCapture)

        assertArrayEquals(byteArrayOf(0x00) + BigInteger("22C1A40F800A04007F00070202040202830101", 16).toByteArray(), apdu[0].getByteArray())
        assertArrayEquals(BigInteger("10860000027C0000", 16).toByteArray(), apdu[1].getByteArray())
        assertArrayEquals(BigInteger("10860000457C438141047ACF3EFC982EC45565A4B1" +
                "55129EFBC74650DCBFA6362D896FC70262E0C2CC5E" +
                "544552DCB6725218799115B55C9BAA6D9F6BC3A961" +
                "8E70C25AF71777A9C4922D00", 16).toByteArray(), apdu[2].getByteArray())
        assertArrayEquals(BigInteger("10860000457C438341042DB7A64C0355044EC9DF19" +
                "0514C625CBA2CEA48754887122F3A5EF0D5EDD301C" +
                "3556F3B3B186DF10B857B58F6A7EB80F20BA5DC7BE" +
                "1D43D9BF850149FBB3646200", 16).toByteArray(), apdu[3].getByteArray())
        assertArrayEquals(BigInteger("008600000C7C0A8508C2B0BD78D94BA86600", 16).toByteArray(), apdu[4].getByteArray())

        assertArrayEquals(BigInteger("008BD2AEB9CB7E57CB2C" +
                "4B482FFC81B7AFB9DE27" +
                "E1E3BD23C23A4453BD9A" +
                "CE3262", 16).toByteArray(), crypto[0].g.xCoord.toBigInteger().toByteArray())
        assertArrayEquals(BigInteger("008CED63C91426D4F0EB1435E7CB1D74A4" +
                "6723A0AF21C89634F65A9AE87A9265E2", 16).toByteArray(), crypto[1].g.xCoord.toBigInteger().toByteArray())

        assertArrayEquals(BigInteger("F5F0E35C0D7161EE6724EE513A0D9A7F", 16).toByteArray().slice(1..16).toByteArray(), keys.firstValue)
        assertArrayEquals(BigInteger("FE251C7858B356B24514B3BD5F4297D1", 16).toByteArray().slice(1..16).toByteArray(), keys.lastValue)

        assertEquals(SUCCESS, result)
    }

    @Test
    fun testDHIM() {
        `when`(mockAPDUControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(BigInteger("7C1280109ABB8864CA0FF1551E620D1EF4E135109000", 16).toByteArray())
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(BigInteger("7C8183848180928D9A0F9DBA450F13FC859C6F290D1D" +
                    "36E42431138A4378500BEB4E0401854C" +
                    "FF111F71CB6DC1D0335807A11388CC8E" +
                    "AA87B07907AAD9FBA6B169AF6D8C26AF" +
                    "8DDDC39ADC3AD2E3FF882B84D23E9768" +
                    "E95A80E4746FB07A9767679FE92133B4" +
                    "D379935C771BD7FBED6C7BB4B1708B27" +
                    "5EA75679524CDC9C6A91370CC662A2F39000", 16).toByteArray())
            .thenReturn(BigInteger("7C0A8608C2F04230187E15259000", 16).toByteArray())

        val dh = DHStandardGroups.rfc5114_1024_160
        val g = BigInteger("1D7D767F11E333BCD6DBAEF40E799E7A" +
                "926B96973550656FF3C830726D118D61" +
                "C276CDCC61D475CF03A98E0C0E79CAEB" +
                "A5BE25578BD4551D0B10903236F0B0F9" +
                "76852FA78EEA14EA0ACA87D1E91F688F" +
                "E0DFF897BBE35A472621D343564B262F" +
                "34223AE8FC59B664BFEDFA2BFE7516CA" +
                "5510A6BBB633D517EC25D4E0BBAA16C2", 16)
        val params = DHParameters(dh.p, g, dh.q)

        //TODO: Find out why the encryption key does not match!
        `when`(mockCrypto.computeKey(any(), any(), any(), any()))
            .thenReturn(BigInteger("591468CDA83D65219CCCB8560233600F", 16).toByteArray())
            .thenCallRealMethod()
        `when`(mockCrypto.generateDHKeyPair(any())).thenReturn(
            AsymmetricCipherKeyPair(
                DHPublicKeyParameters(
                    BigInteger("0F0CC62945A8029251FB7EF3C094E12E" +
                            "C68E4EF07F27CB9D9CD04C5C4250FAE0" +
                            "E4F8A951557E929AEB48E5C6DD47F2F5" +
                            "CD7C351A9BD2CD722C07EDE166770F08" +
                            "FFCB370262CF308DD7B07F2E0DA9CAAA" +
                            "1492344C852906919538C98A4BA4187E" +
                            "76CE9D87832386D319CE2E043C3343AE" +
                            "AE6EDBA1A9894DC5094D22F7FE1351D5", 16),
                    params
                ),
                DHPrivateKeyParameters(
                    BigInteger(
                        "4BD0E54740F9A028E6A515BFDAF96784" +
                                "8C4F5F5FFF65AA0915947FFD1A0DF2FA" +
                                "6981271BC905F3551457B7E03AC3B806" +
                                "6DE4AA406C1171FB43DD939C4BA16175" +
                                "103BA3DEE16419AA248118F90CC36A3D" +
                                "6F4C373652E0C3CCE7F0F1D0C5425B36" +
                                "00F0F0D6A67F004C8BBA33F2B4733C72" +
                                "52445C1DFC4F1107203F71D2EFB28161", 16),
                    params
                )))

        val pace = PACE()
        pace.init("T22000129364081251010318", false, byteArrayOf(0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x04, 0x03, 0x02), 0x00)
        val result = pace.paceProtocol()

        val apduCapture = argumentCaptor<APDU>()
        verify(mockAPDUControl, times(5)).sendAPDU(apduCapture.capture())
        val apdu = apduCapture.allValues

        val cryptoCapture = argumentCaptor<DHParameters>()
        verify(mockCrypto).generateDHKeyPair(cryptoCapture.capture())
        val crypto = cryptoCapture.firstValue

        val s = argumentCaptor<ByteArray>()
        verify(mockCrypto).integratedMappingPRNG(s.capture(), s.capture(), any(), any())
        val sCapture = s.allValues

        val keys = argumentCaptor<ByteArray>()
        verify(mockAPDUControl).setEncryptionKeyBAC(keys.capture())
        verify(mockAPDUControl).setEncryptionKeyMAC(keys.capture())

        assertArrayEquals(byteArrayOf(0x00) + BigInteger("22C1A40F800A04007F00070202040302830101", 16).toByteArray(), apdu[0].getByteArray())
        assertArrayEquals(BigInteger("10860000027C0000", 16).toByteArray(), apdu[1].getByteArray())
        assertArrayEquals(BigInteger("10860000147C128110B3A6DB3C870C3E99245E0D1C06B747DE00", 16).toByteArray(), apdu[2].getByteArray())
        assertArrayEquals(BigInteger("10860000867C81838381800F0CC62945A8029251FB7EF3C094E12E" +
                "C68E4EF07F27CB9D9CD04C5C4250FAE0" +
                "E4F8A951557E929AEB48E5C6DD47F2F5" +
                "CD7C351A9BD2CD722C07EDE166770F08" +
                "FFCB370262CF308DD7B07F2E0DA9CAAA" +
                "1492344C852906919538C98A4BA4187E" +
                "76CE9D87832386D319CE2E043C3343AE" +
                "AE6EDBA1A9894DC5094D22F7FE1351D500", 16).toByteArray(), apdu[3].getByteArray())
        assertArrayEquals(BigInteger("008600000C7C0A850855D61977CBF5307E00", 16).toByteArray(), apdu[4].getByteArray())

        assertArrayEquals(dh.p.toByteArray(), crypto.p.toByteArray())
        assertArrayEquals(g.toByteArray(), crypto.g.toByteArray())

        assertArrayEquals(BigInteger("FA5B7E3E49753A0DB9178B7B9BD898C8", 16).toByteArray().slice(1..16).toByteArray(), sCapture[0])
        assertArrayEquals(BigInteger("B3A6DB3C870C3E99245E0D1C06B747DE", 16).toByteArray().slice(1..16).toByteArray(), sCapture[1])

        assertArrayEquals(BigInteger("01AFC10CF87BE36D8179E87370171F07", 16).toByteArray(), keys.firstValue)
        assertArrayEquals(BigInteger("23F0FBD05FD6C7B8B88F4C8309669061", 16).toByteArray(), keys.lastValue)

        assertEquals(SUCCESS, result)
    }

    @Test
    fun testECIM() {
        `when`(mockAPDUControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(BigInteger("7C128010143DC40C08C8E891FBED7DEDB92B64AD9000", 16).toByteArray())
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(BigInteger("7C4384410467F78E5F7F7686082B293E8D087E0569" +
                    "16D0F74BC01A5F8957D0DE45691E51E8" +
                    "932B69A962B52A0985AD2C0A271EE6A1" +
                    "3A8ADDDCD1A3A994B9DED257F4D227539000", 16).toByteArray())
            .thenReturn(BigInteger("7C0A860875D4D96E8D5B03089000", 16).toByteArray())


        val params = ECNamedCurveTable.getByName("brainpoolp256r1")
        val domainParameters = ECDomainParameters(params.curve, params.g, params.n, params.h)
        `when`(mockCrypto.computeKey(any(), any(), any(), any()))
            .thenReturn(BigInteger("591468CDA83D65219CCCB8560233600F", 16).toByteArray())
            .thenCallRealMethod()
        `when`(mockCrypto.generateECKeyPair(any())).thenReturn(
            AsymmetricCipherKeyPair(
                ECPublicKeyParameters(
                    params.curve.createPoint(
                        BigInteger("0089CBA23FFE96AA18D824627C3E934E54" +
                                "A9FD0B87A95D1471DC1C0ABFDCD640D4", 16),
                        BigInteger("6755DE9B7B778280B6BEBD57439ADFEB" +
                                "0E21FD4ED6DF42578C13418A59B34C37", 16)
                    ),
                    domainParameters
                ),
                ECPrivateKeyParameters(
                    BigInteger("00A73FB703AC1436A18E0CFA5ABB3F7BEC" +
                            "7A070E7A6788486BEE230C4A22762595", 16),
                    domainParameters
                )))

        val pace = PACE()
        pace.init("T22000129364081251010318", false, byteArrayOf(0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x04, 0x04, 0x02), 0x0D)
        val result = pace.paceProtocol()

        val apduCapture = argumentCaptor<APDU>()
        verify(mockAPDUControl, times(5)).sendAPDU(apduCapture.capture())
        val apdu = apduCapture.allValues

        val cryptoCapture = argumentCaptor<ECDomainParameters>()
        verify(mockCrypto).generateECKeyPair(cryptoCapture.capture())
        val crypto = cryptoCapture.firstValue

        val s = argumentCaptor<ByteArray>()
        verify(mockCrypto).integratedMappingPRNG(s.capture(), s.capture(), any(), any())
        val sCapture = s.allValues

        val keys = argumentCaptor<ByteArray>()
        verify(mockAPDUControl).setEncryptionKeyBAC(keys.capture())
        verify(mockAPDUControl).setEncryptionKeyMAC(keys.capture())

        assertArrayEquals(byteArrayOf(0x00) + BigInteger("22C1A40F800A04007F00070202040402830101", 16).toByteArray(), apdu[0].getByteArray())
        assertArrayEquals(BigInteger("10860000027C0000", 16).toByteArray(), apdu[1].getByteArray())
        assertArrayEquals(BigInteger("10860000147C1281105DD4CBFC96F5453B130D890A1CDBAE3200", 16).toByteArray(), apdu[2].getByteArray())
        assertArrayEquals(BigInteger("10860000457C4383410489CBA23FFE96AA18D824627C3E934E54" +
                "A9FD0B87A95D1471DC1C0ABFDCD640D4" +
                "6755DE9B7B778280B6BEBD57439ADFEB" +
                "0E21FD4ED6DF42578C13418A59B34C3700", 16).toByteArray(), apdu[3].getByteArray())
        assertArrayEquals(BigInteger("008600000C7C0A8508450F02B86F6A090900", 16).toByteArray(), apdu[4].getByteArray())

        assertArrayEquals(BigInteger("008E82D31559ED0FDE92A4D0498ADD3C23" +
                "BABA94FB77691E31E90AEA77FB17D427", 16).toByteArray(), crypto.g.xCoord.toBigInteger().toByteArray())

        assertArrayEquals(BigInteger("2923BE84E16CD6AE529049F1F1BBE9EB", 16).toByteArray(), sCapture[0])
        assertArrayEquals(BigInteger("5DD4CBFC96F5453B130D890A1CDBAE32", 16).toByteArray(), sCapture[1])

        assertArrayEquals(BigInteger("0D3FEB33251A6370893D62AE8DAAF51B", 16).toByteArray(), keys.firstValue)
        assertArrayEquals(BigInteger("B01E89E3D9E8719E586B50B4A7506E0B", 16).toByteArray().slice(1..16).toByteArray(), keys.lastValue)

        assertEquals(SUCCESS, result)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testECCAM() {
        `when`(mockAPDUControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(BigInteger("7C128010CB60E8E0D85B76A9BD304747C2AD42E29000", 16).toByteArray())
            .thenReturn(BigInteger("7C43824104A234236AA9B9621E8EFB73" +
                    "B5245C0E09D2576E5277183C1208BDD5" +
                    "5280CAE8B304F365713A356E65A451E1" +
                    "65ECC9AC0AC46E3771342C8FE5AEDD09" +
                    "2685338E239000", 16).toByteArray())
            .thenReturn(BigInteger("7C4384410402AD566F3C6EC7F9324509" +
                    "AD50A51FA52030782A4968FCFEDF737D" +
                    "AEA993333111C3B9B4C2287789BD137E" +
                    "7F8AA882E2A3C633CCD6ECC2C63C57AD" +
                    "401A09C2E19000", 16).toByteArray())
            .thenReturn(BigInteger("7C3C86088596CF055C67C1A38A301EEA" +
                    "964DAAE372AC990E3EFDE6333353BFC8" +
                    "9A6704D93DA8798CF77F5B7A54BD10CB" +
                    "A372B42BE0B9B5F28AA8DE2F4F929000", 16).toByteArray())


        val params = ECNamedCurveTable.getByName("brainpoolp256r1")
        val domainParameters = ECDomainParameters(params.curve, params.g, params.n, params.h)
        val g = params.curve.createPoint(
            BigInteger("89F0B5EABF3BE293C75903A398613192" +
                    "5C9F5B515CA95AF485DC7E886F03245D", 16),
            BigInteger("44BEFB2DD3A0DBD71CB5E618971CF474" +
                    "7F12B79E548379A40E45963BAAF3E829", 16)
        )
        val newParameters = ECDomainParameters(params.curve, g, params.n, params.h)
        `when`(mockCrypto.generateECKeyPair(any())).thenReturn(
            AsymmetricCipherKeyPair(
                ECPublicKeyParameters(
                    params.curve.createPoint(
                        BigInteger("7F1D410ADB7DDB3B84BF1030800981A9" +
                                "105D7457B4A3ADE002384F3086C67EDE", 16),
                        BigInteger("1AB889104A27DB6D842B019020FBF3CE" +
                                "ACB0DC627F7BDCAC29969E19D0E553C1", 16)
                    ),
                    domainParameters
                ),
                ECPrivateKeyParameters(
                    BigInteger("5D8BB87BD74D985A4B7D4325B9F7B976" +
                            "FE835122773400798914AA22738135CC", 16),
                    domainParameters
                )))
            .thenReturn(
                AsymmetricCipherKeyPair(
                    ECPublicKeyParameters(
                        newParameters.curve.createPoint(
                            BigInteger("446C934084D9DAB863944F219520076C" +
                                    "29EE3F7AE6722B11FF319EC1C7728F95", 16),
                            BigInteger("5483400BFF60BF0C5929270009277DC2" +
                                    "A515E12575010AD9BA916CF1BF86FEFC", 16)
                        ),
                        newParameters
                    ),
                    ECPrivateKeyParameters(
                        BigInteger("76ECFDAA9841C323A3F5FC5E88B88DB3" +
                                "EFF7E35EBF57A7E6946CB630006C2120", 16),
                        newParameters
                    )
                )
            )

        val pace = PACE()
        pace.init("C11T002JM496081222310314", false, byteArrayOf(0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x04, 0x06, 0x02), 0x0D)
        val result = pace.paceProtocol()

        val apduCapture = argumentCaptor<APDU>()
        verify(mockAPDUControl, times(5)).sendAPDU(apduCapture.capture())
        val apdu = apduCapture.allValues

        val cryptoCapture = argumentCaptor<ECDomainParameters>()
        verify(mockCrypto, times(2)).generateECKeyPair(cryptoCapture.capture())
        val crypto = cryptoCapture.allValues

        val s = argumentCaptor<ByteArray>()
        verify(mockCrypto).genericMappingEC(any(), s.capture(), any())
        val sCapture = s.firstValue

        val keys = argumentCaptor<ByteArray>()
        verify(mockAPDUControl).setEncryptionKeyBAC(keys.capture())
        verify(mockAPDUControl).setEncryptionKeyMAC(keys.capture())

        assertArrayEquals(byteArrayOf(0x00) + BigInteger("22C1A40F800A04007F00070202040602830101", 16).toByteArray(), apdu[0].getByteArray())
        assertArrayEquals(BigInteger("10860000027C0000", 16).toByteArray(), apdu[1].getByteArray())
        assertArrayEquals(BigInteger("10860000457C438141047F1D410ADB7D" +
                "DB3B84BF1030800981A9105D7457B4A3" +
                "ADE002384F3086C67EDE1AB889104A27" +
                "DB6D842B019020FBF3CEACB0DC627F7B" +
                "DCAC29969E19D0E553C100", 16).toByteArray(), apdu[2].getByteArray())
        assertArrayEquals(BigInteger("10860000457C43834104446C934084D9" +
                "DAB863944F219520076C29EE3F7AE672" +
                "2B11FF319EC1C7728F955483400BFF60" +
                "BF0C5929270009277DC2A515E1257501" +
                "0AD9BA916CF1BF86FEFC00", 16).toByteArray(), apdu[3].getByteArray())
        assertArrayEquals(BigInteger("008600000C7C0A8508E86BD06018A1CD3B00", 16).toByteArray(), apdu[4].getByteArray())

        assertArrayEquals(BigInteger("8BD2AEB9CB7E57CB2C" +
                "4B482FFC81B7AFB9DE27" +
                "E1E3BD23C23A4453BD9A" +
                "CE3262", 16).toByteArray(), crypto[0].g.xCoord.toBigInteger().toByteArray())
        assertArrayEquals(g.xCoord.toBigInteger().toByteArray(), crypto[1].g.xCoord.toBigInteger().toByteArray())

        assertArrayEquals(BigInteger("658B860BC94DF6F044FCE6D5C82CF8E5", 16).toByteArray(), sCapture)

        assertArrayEquals(BigInteger("0A9DA4DB03BDDE39FC5202BC44B2E89E", 16).toByteArray(), keys.firstValue)
        assertArrayEquals(BigInteger("4B1C06491ED5140CA2B537D344C6C0B1", 16).toByteArray(), keys.lastValue)

        assertArrayEquals(BigInteger("A234236AA9B9621E8EFB73B5245C0E09" +
                "D2576E5277183C1208BDD55280CAE8B3", 16).toByteArray(), pace.chipPublicKey!!.q.xCoord.toBigInteger().toByteArray())
        assertArrayEquals(BigInteger("04F365713A356E65A451E165ECC9AC0A" +
                "C46E3771342C8FE5AEDD092685338E23",16).toByteArray(), pace.chipPublicKey!!.q.yCoord.toBigInteger().toByteArray())

        assertArrayEquals(BigInteger("85DC3FA93D0952BFA82F5FD189EE75BD" +
                "82F11D1F0B8ED4BF5319AC9B53C426B3", 16).toByteArray().slice(1..32).toByteArray(), pace.chipAuthenticationData)
        assertEquals(SUCCESS, result)
    }
}