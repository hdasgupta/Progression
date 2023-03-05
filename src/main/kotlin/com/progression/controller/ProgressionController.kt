package com.progression.controller

import com.progression.parser.expression.*
import com.progression.parser.expression.ExpressionConstants.Companion.n
import com.progression.parser.getOperand
import com.progression.step.Step
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.stream.IntStream
import kotlin.math.roundToLong

@Controller
class ProgressionController {

    val parser = SpelExpressionParser()

    @RequestMapping(value = ["/progression"])
    fun progression(
        @RequestParam formula: String? = null,
        @RequestParam start: Int? = 0,
        @RequestParam end: Int? = -1,
        map: ModelMap
    ): String {

        map["formula"] = formula
        map["start"] = start ?: 0
        map["end"] = end ?: -1

        try {
            if (formula != null) {

                if (end!! >= start!!) {
                    val progressions = IntStream.range(start, end + 1)
                        .mapToObj {
                            val operand: Operand = getOperand(formula)
                            val replace = ExpressionConstants.replace(operand, n, DecimalLiteral(it.toDouble()))
                            Step(it, replace, replace.calc(false).toDouble().format(6))
                        }
                        .toList()
                    map["progressions"] = progressions
                    map["sum"] = progressions.map { it.out.toDouble() }
                        .reduce { acc, number -> acc + number }.format(2)
                } else if (end == -1) {
                    val progressions = mutableListOf<Step>()
                    var calc: Double
                    var it = start

                    do {
                        val operand: Operand = getOperand(formula)
                        val replace = ExpressionConstants.replace(operand, n, DecimalLiteral(it.toDouble()))
                        val str = replace.string()
                        // calc = parser.parseExpression(replace.string()).value as Number
                        calc = replace.calc(false).toDouble()
                        progressions.add(Step(it, replace, calc.format(6)))
                        it++
                    } while (progressions.last().out.toDouble() != 0.0)

                    map["progressions"] = progressions
                    map["sum"] = progressions.map { it.out.toDouble() }
                        .reduce { acc, number -> acc + number }.format(2)
                }

            }
        } catch (e: Throwable) {

        }

        return "ProgressionPage"
    }

    @RequestMapping(value = ["/html"])
    fun getHtml(@RequestParam formula: String, map: ModelMap): String {
        try {
            val operand: Operand = getOperand(formula)
            map["operand"] = operand
        } catch (e: Throwable) {
            
        }

        return "operands/MainOperand"
    }

}