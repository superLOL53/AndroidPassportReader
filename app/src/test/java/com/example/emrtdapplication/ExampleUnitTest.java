package com.example.emrtdapplication;

import org.junit.Test;

import static org.junit.Assert.*;
import com.example.emrtdapplication.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void APDUControlBACTest() {
        APDUControl apduControl = APDUControl.INSTANCE;
        apduControl.setEncryptionKeyBAC(new byte[] {(byte) 0x97, (byte) 0x9E, (byte) 0xC1, (byte) 0x3B, (byte) 0x1C, (byte) 0xBF, (byte) 0xE9, (byte) 0xDC, (byte) 0xD0, (byte) 0x1A, (byte) 0xB0, (byte) 0xFE, (byte) 0xD3, (byte) 0x07, (byte) 0xEA, (byte) 0xE5});
        apduControl.setEncryptionKeyMAC(new byte[] {(byte) 0xF1, (byte) 0xCB, (byte) 0x1F, (byte) 0x1F, (byte) 0xB5, (byte) 0xAD, (byte) 0xF2, (byte) 0x08, (byte) 0x80, (byte) 0x6B, (byte) 0x89, (byte) 0xDC, (byte) 0x57, (byte) 0x9D, (byte) 0xC1, (byte) 0xF8});
        apduControl.setSequenceCounter(new byte[] {(byte) 0x88, (byte) 0x70, (byte) 0x22, (byte) 0x12, (byte) 0x0C, (byte) 0x06, (byte) 0xC2, (byte) 0x26});
        apduControl.setUseBAC(true);
        APDU apdu = new APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, (byte) 0x02, (short) 0, new byte[] {1, 0x1E});
        byte[] response = apduControl.sendAPDU(apdu);
        assertArrayEquals(new byte[] {(byte) 0x0C, (byte) 0xA4, (byte) 0x02, (byte) 0x0C, (byte) 0x15, (byte) 0x87, (byte) 0x09, (byte) 0x01, (byte) 0x63, (byte) 0x75, (byte) 0x43, (byte) 0x29, (byte) 0x08, (byte) 0xC0, (byte) 0x44, (byte) 0xF6, (byte) 0x8E, (byte) 0x08, (byte) 0xBF, (byte) 0x8B, (byte) 0x92, (byte) 0xD6, (byte) 0x35, (byte) 0xFF, (byte) 0x24, (byte) 0xF8, (byte) 0x00}, response);
    }
}