package com.progression

import com.progression.parser.expression.Operation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProgressionMain

fun main(vararg args: String) {
    runApplication<ProgressionMain>(*args)
}