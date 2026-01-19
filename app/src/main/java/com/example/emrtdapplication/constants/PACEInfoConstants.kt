package com.example.emrtdapplication.constants

/**
 * Constants for the PACEInfo class
 */
object PACEInfoConstants {
    /**
     * Unknown or undefined protocol
     */
    const val UNDEFINED : Byte = -1

    /**
     * Constant identifier for the asymmetric protocol used by the PACE protocol
     */
    const val DH_GM : Byte = 1

    /**
     * Constant identifier for the asymmetric protocol used by the PACE protocol
     */
    const val ECDH_GM : Byte = 2

    /**
     * Constant identifier for the asymmetric protocol used by the PACE protocol
     */
    const val DH_IM : Byte = 3

    /**
     * Constant identifier for the asymmetric protocol used by the PACE protocol
     */
    const val ECDH_IM : Byte = 4

    /**
     * Constant identifier for the asymmetric protocol used by the PACE protocol
     */
    const val ECDH_CAM : Byte = 6

    /**
     * Constant identifier for the symmetric protocol used by the eMRTD
     */
    const val DES_CBC_CBC : Byte = 1

    /**
     * Constant identifier for the symmetric protocol used by the eMRTD
     */
    const val AES_CBC_CMAC_128 : Byte = 2

    /**
     * Constant identifier for the symmetric protocol used by the eMRTD
     */
    const val AES_CBC_CMAC_192 : Byte = 3

    /**
     * Constant identifier for the symmetric protocol used by the eMRTD
     */
    const val AES_CBC_CMAC_256 : Byte = 4

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val MOD_P_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP : Byte = 0

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val MOD_P_2048_BIT_GROUP_WITH_224_BIT_PRIME_ORDER_SUBGROUP : Byte = 1

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val MOD_P_2048_BIT_GROUP_WITH_256_BIT_PRIME_ORDER_SUBGROUP : Byte = 2

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val NIST_P192 : Byte = 8

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val BRAIN_POOL_P192R1 : Byte = 9

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val NIST_P224 : Byte = 10

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val BRAIN_POOL_P224R1 : Byte = 11

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val NIST_P256 : Byte = 12

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val BRAIN_POOL_P256R1 : Byte = 13

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val BRAIN_POOL_P320R1 : Byte = 14

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val NIST_P384 : Byte = 15

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val BRAIN_POOL_P384R1 : Byte = 16

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val BRAIN_POOL_P512R1 : Byte = 17

    /**
     * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
     */
    const val NIST_P521 : Byte = 18
}