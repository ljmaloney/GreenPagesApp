package com.green.yp.greenpagesapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform