package org.carthigan.again

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform