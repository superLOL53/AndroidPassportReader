package com.example.emrtdapplication.utils

/**
 * Abstract class for grouping together biometric data in a Biometric Data Block
 * @property type The type of the biometric feature. See [BiometricType]
 */
abstract class BiometricData(val type: BiometricType)