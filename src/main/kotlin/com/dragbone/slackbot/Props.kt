package com.dragbone.slackbot

import java.util.*


class Props {
    private val props = Properties()

    init {
        // create and load default properties
        val ips = Props::class.java.getResourceAsStream("/version.properties") ?: Props::class.java.getResourceAsStream("version.properties")
        props.load(ips)
        ips.close()
    }

    enum class Values {
        Version, Revision, BuildTime, ApplicationName
    }

    operator fun get(value: Values): String {
        return props.getProperty(value.toString())
    }
}