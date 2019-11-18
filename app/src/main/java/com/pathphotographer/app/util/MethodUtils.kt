package com.pathphotographer.app.util

object MethodUtils {

    /*
     * A kotlin infix helper allowing us to try a method "safely" without crashing or logging any exception.
     *
     * This is not making distinctions or logging anything to Crashlytics, so USE WITH EXTREME CAUTION!
     */
    infix fun <T> trySilent(method: () -> T?): T? {
        try {
            return method()
        } catch (e: Exception) {
            return null
        }
    }
}
