package net.runelite.client.plugins.alfred.util

class Utility {

    companion object {
        fun retryFunction(retries: Int, ignoreException: Boolean, action: () -> Boolean): Boolean {
            var lastException: Throwable? = null
            repeat(retries) {
                try {
                    if (action()) {
                        return true
                    }
                } catch (e: Throwable) {
                    lastException = e
                }
            }
            if (!ignoreException) {
                lastException?.let { throw it }
            }
            return false
        }
    }

}