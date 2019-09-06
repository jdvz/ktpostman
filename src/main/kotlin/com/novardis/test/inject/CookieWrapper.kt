package com.novardis.test.inject

import org.apache.http.client.CookieStore
import org.apache.http.cookie.Cookie
import org.apache.http.impl.client.BasicCookieStore
import java.io.Serializable
import java.util.*

class CookieWrapper : Bean, CookieStore, Serializable {
    val cookieStore = BasicCookieStore()
    override fun clear() {
        cookieStore.clear()
    }

    override fun addCookie(cookie: Cookie?) {
        cookieStore.addCookie(cookie)
    }

    override fun clearExpired(date: Date?): Boolean {
        return cookieStore.clearExpired(date)
    }

    override fun getCookies(): MutableList<Cookie> {
        return cookieStore.cookies
    }
}