package com.example.emrtdapplication.utils

/**
 * Abstract class for grouping together the different biometric headers in a Biometric Data Block
 * @property type The type of the biometric feature. See [BiometricType]
 */
abstract class BiometricHeader(val type: BiometricType)