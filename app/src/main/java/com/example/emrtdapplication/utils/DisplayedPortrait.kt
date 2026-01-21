package com.example.emrtdapplication.utils

import org.jmrtd.lds.DisplayedImageInfo
import java.io.ByteArrayInputStream

class DisplayedPortrait(portrait : TLV) {
    val displayPortrait = DisplayedImageInfo(ByteArrayInputStream(portrait.toByteArray()))
}