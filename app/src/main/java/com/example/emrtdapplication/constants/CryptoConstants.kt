package com.example.emrtdapplication.constants

object CryptoConstants {
    const val PAD_START_BYTE : Byte = 0x80.toByte()
    const val KEY_3DES_COUNT_ONES : Byte = 0xFE.toByte()
    const val AES_CBC_NO_PADDING = "AES/CBC/NoPadding"
    const val AES = "AES"
    const val DES_EDE = "DESede"
    const val DES_EDE_CBC_NO_PADDING = "DESede/CBC/NoPadding"
    const val BYTE_TO_BITS = 8
    const val MAC_SIZE = 8
    const val EC_POINT_TAG_SINGLE_COORDINATE : Byte = 0x03
    const val C_0_128 = "a668892a7c41e3ca739f40b057d85904"
    const val C_1_128 = "a4e136ac725f738b01c1f60217c188ad"
    const val C_0_256 = "d463d65234124ef7897054986dca0a174e28df758cbaa03f240616414d5a1676"
    const val C_1_256 = "54bd7255f0aaf831bec3423fcf39d69b6cbf066677d0faae5aadd99df8e53517"
    const val MAPPING_CONSTANT = 64
}