package com.pathphotographer.app.util

import java.util.*

object IdUtils {

    fun generateId(): String = UUID.randomUUID().toString()
}