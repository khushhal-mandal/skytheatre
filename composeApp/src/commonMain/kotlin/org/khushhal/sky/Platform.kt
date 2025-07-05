package org.khushhal.sky

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform