package com.example.emrtdapplication.utils

import org.jmrtd.lds.DisplayedImageInfo
import java.io.ByteArrayInputStream

class DisplayedSignature(signature : TLV) {
    val displaySignature = DisplayedImageInfo(ByteArrayInputStream(signature.toByteArray()))
}