package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.SUCCESS


/**
 * Implements the DG13 file
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG13
 * @property efTag The tag of the DG13 file
 *
 */
class DG13() : ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x0D
    override val efTag: Byte = 0x6D

    /**
     * Parses the contents of [rawFileContent]
     *
     * @return [SUCCESS] if the contents were successfully decoded, otherwise [FAILURE]
     */
    override fun parse(): Int {
        return SUCCESS
    }
}