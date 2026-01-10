package com.example.emrtdapplication.constants

/**
 * Constants for the PACEInfo class
 */
object PACEInfoConstants {
    const val UNDEFINED : Byte = -1
    const val DH_GM : Byte = 1
    const val ECDH_GM : Byte = 2
    const val DH_IM : Byte = 3
    const val ECDH_IM : Byte = 4
    const val ECDH_CAM : Byte = 6
    const val DES_CBC_CBC : Byte = 1
    const val AES_CBC_CMAC_128 : Byte = 2
    const val AES_CBC_CMAC_192 : Byte = 3
    const val AES_CBC_CMAC_256 : Byte = 4
    const val MOD_P_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP : Byte = 0
    const val MOD_P_2048_BIT_GROUP_WITH_224_BIT_PRIME_ORDER_SUBGROUP : Byte = 1
    const val MOD_P_2048_BIT_GROUP_WITH_256_BIT_PRIME_ORDER_SUBGROUP : Byte = 2
    const val NIST_P192 : Byte = 8
    const val BRAIN_POOL_P192R1 : Byte = 9
    const val NIST_P224 : Byte = 10
    const val BRAIN_POOL_P224R1 : Byte = 11
    const val NIST_P256 : Byte = 12
    const val BRAIN_POOL_P256R1 : Byte = 13
    const val BRAIN_POOL_P320R1 : Byte = 14
    const val NIST_P384 : Byte = 15
    const val BRAIN_POOL_P384R1 : Byte = 16
    const val BRAIN_POOL_P512R1 : Byte = 17
    const val NIST_P521 : Byte = 18
}