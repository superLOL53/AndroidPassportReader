package com.example.emrtdapplication;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.emrtdapplication.common.PaceEC;
import com.example.emrtdapplication.utils.APDU;
import com.example.emrtdapplication.utils.APDUControl;
import com.example.emrtdapplication.utils.NfcClassByte;
import com.example.emrtdapplication.utils.NfcInsByte;
import com.example.emrtdapplication.utils.NfcP1Byte;
import com.example.emrtdapplication.utils.NfcP2Byte;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {

    private APDUControl apduControl;

    private APDU apdu = new APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.SELECT_DF, NfcP2Byte.ZERO);


    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void paceProbing() {

        //apduControl.sendAPDU(apdu);
        PaceEC pace = new PaceEC();
        pace.paceProtocol();
        //EfSod ef = new EfSod(new APDUControl());
        //ef.parse();
    }


}
/*
Shared Secret H: 60332EF2 450B5D24 7EF6D386 8397D398
        852ED6E8 CAF6FFEE F6BF85CA 57057FD5,
        0840CA74 15BAF3E4 3BD414D3 5AA4608B
        93A2CAF3 A4E3EA4E 82C9C13D 03EB7181
Mapped Generatore G^: 8CED63C9 1426D4F0 EB1435E7 CB1D74A4
        6723A0AF 21C89634 F65A9AE8 7A9265E2,
        8C879506 743F8611 AC33645C 5B985C80
        B5F09A0B 83407C1B 6A4D857A E76FE522

        7C9CBFE9 8F9FBDDA 8D143506 FA7D9306
        F4CB17E3 C71707AF F5E1C1A1 23702496
        84D64EE3 7AF44B8D BD9D45BF 6023919C
        BAA027AB 97ACC771 666C8E98 FF483301
        BFA4872D EDE9034E DFACB708 14166B7F
        36067682 9B826BEA 57291B5A D69FBC84
        EF1E7790 32A30580 3F743417 93E86974
        2D401325 B37EE856 5FFCDEE6 18342DC5
Authentication Token: B46DD9BD 4D98381F
                        917F37B5 C0E6D8D1*/