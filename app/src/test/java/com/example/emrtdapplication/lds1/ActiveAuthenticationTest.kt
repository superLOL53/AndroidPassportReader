package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.common.ActiveAuthenticationInfo
import com.example.emrtdapplication.utils.TLV
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith


class ActiveAuthenticationTest {

    @Before
    fun setUp() {
        //TODO
        assertFailsWith<IllegalArgumentException> {ActiveAuthenticationInfo(TLV(byteArrayOf()))}
    }

    @Test
    fun testActiveAuthentication() {
        //TODO
        assertFailsWith<IllegalArgumentException> {ActiveAuthenticationInfo(TLV(byteArrayOf()))}
    }
}