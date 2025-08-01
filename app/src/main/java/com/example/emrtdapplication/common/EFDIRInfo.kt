package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo

class EFDIRInfo(rawFileContent: ByteArray) : SecurityInfo(rawFileContent) {
    var efDir = Directory(rawFileContent)
}