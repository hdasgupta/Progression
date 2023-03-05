package com.progression

import com.progression.parser.getOperand
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class Test1 {

    @Test
    fun test() {
        val exp = "n*23";
        println(getOperand(exp))
    }
}