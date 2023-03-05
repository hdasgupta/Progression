package com.progression.controller

import com.progression.parser.expression.DecimalLiteral
import com.progression.parser.expression.ExpressionConstants
import com.progression.parser.expression.ExpressionConstants.Companion.n
import com.progression.parser.expression.IntegerLiteral
import com.progression.parser.expression.Operand
import com.progression.parser.getOperand
import com.progression.step.Step
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.Expression
import org.springframework.expression.ExpressionParser
import org.springframework.expression.common.ExpressionUtils
import org.springframework.expression.spel.SpelParserConfiguration
import org.springframework.expression.spel.ast.SpelNodeImpl
import org.springframework.expression.spel.standard.SpelExpression
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.stream.IntStream
import kotlin.math.roundToLong
import kotlin.streams.toList

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

        if(formula!=null) {

            if(end!! >= start!!) {
                val progressions = IntStream.range(start, end+1)
                    .mapToObj {
                        val operand: Operand = getOperand(formula)
                        val replace = ExpressionConstants.replace(operand, n, DecimalLiteral(it.toDouble()))
                        Step(it, replace, (replace.calc(false).toDouble()*1000).roundToLong()/1000.0)
                    }
                    .toList()
                map["progressions"] = progressions
                map["sum"] = (progressions.map { it.out }
                    .reduce { acc, number -> acc.toDouble() + number.toDouble() }.toDouble()
                        *1000).roundToLong()/1000.0
            } else if(end ==-1) {
                val progressions = mutableListOf<Step>()
                var calc:Number
                var it = start

                do {
                    val operand: Operand = getOperand(formula)
                    val replace = ExpressionConstants.replace(operand, n, DecimalLiteral(it.toDouble()))
                    val str = replace.string()
                    // calc = parser.parseExpression(replace.string()).value as Number
                    calc = replace.calc(false)
                    progressions.add(Step(it, replace, (calc.toDouble()*1000).roundToLong()/1000.0))
                    it++
                } while ((progressions.last().out.toDouble()*1000).roundToLong() != 0L)

                map["progressions"] = progressions
                map["sum"] = (progressions.map { it.out }
                    .reduce { acc, number -> acc.toDouble() + number.toDouble() }.toDouble()
                        *1000).roundToLong()/1000.0
            }

        }

        return "ProgressionPage"
    }

    @RequestMapping(value = ["/html"])
    fun getHtml(@RequestParam formula: String, map: ModelMap): String {
        val operand: Operand = getOperand(formula)
        map["operand"] = operand

        return "operands/MainOperand"
    }

}