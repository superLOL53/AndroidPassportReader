package com.example.emrtdapplication.constants

/**
 * Constant encoding a zero byte (0x00)
 */
const val ZERO_BYTE : Byte = 0x0

/**
 * File was successfully read from the eMRTD
 */
const val FILE_SUCCESSFUL_READ = 0

/**
 * General return value for successful completion
 */
const val SUCCESS = 0

/**
 * General return value for unsuccessful completion
 */
const val FAILURE = -1

/**
 * File was unable to be selected on the eMRTD
 */
const val FILE_UNABLE_TO_SELECT = -1

/**
 * Unable to read file from the eMRTD
 */
const val FILE_UNABLE_TO_READ = -2

/**
 * General error code for an invalid argument
 */
const val INVALID_ARGUMENT = -4

/**
 * Additional maximum overhead for secure messaging APDUs due to encryption
 */
const val ADDITIONAL_ENCRYPTION_LENGTH = 50

