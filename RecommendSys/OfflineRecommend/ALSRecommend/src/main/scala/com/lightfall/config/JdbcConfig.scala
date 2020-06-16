package com.lightfall.config

import java.util.Properties

object JdbcConfig {

    val hiveConfig: Map[String, String] = Map(
        "url" -> "jdbc:hive2://lightfall-df:10000/aneuzon",
        "driver" -> "org.apache.hive.jdbc.HiveDriver"
    )
}
