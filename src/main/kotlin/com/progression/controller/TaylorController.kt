package com.progression.controller

import com.progression.parser.expression.DecimalLiteral
import com.progression.parser.expression.ExpressionConstants
import com.progression.parser.expression.Operand
import com.progression.parser.expression.format
import com.progression.parser.getOperand
import com.progression.step.Step
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class TaylorController {

// "((-1^(n+1))*(((pi*$degree)/180)^n))/(n!) "

    val parser = SpelExpressionParser()

    @RequestMapping(value = ["/sine"])
    fun sine(
        @RequestParam degree: Double? = null,
        @RequestParam formula: String? = null,
        map: ModelMap
    ): String {

        map["degree"] = degree

        if(formula != null)
            infiniteSeries(formula, 0, map)

        return "SinePage"
    }

    @RequestMapping(value = ["/cosine"])
    fun cosine(
        @RequestParam degree: Double? = null,
        @RequestParam formula: String? = null,
        map: ModelMap
    ): String {

        map["degree"] = degree

        if(formula != null)
            infiniteSeries(formula, 0, map)

        return "CosinePage"
    }

    @RequestMapping(value = ["/exp"])
    fun exponential(
        @RequestParam pow: Double? = null,
        @RequestParam formula: String? = null,
        map: ModelMap
    ): String {

        map["pow"] = pow

        if(formula != null)
            infiniteSeries(formula, 0, map)

        return "ExpPage"
    }

    @RequestMapping(value = ["/log"])
    fun logarithm(
        @RequestParam x: Double? = null,
        @RequestParam formula: String? = null,
        map: ModelMap
    ): String {

        map["x"] = x

        if(formula != null)
            infiniteSeries(formula, 1, map)

        return "LogPage"
    }

    fun infiniteSeries(formula: String, start: Int, map: ModelMap): Unit {
        map["formula"] = formula

        try {

            val progressions = mutableListOf<Step>()
            var calc: Double
            var it = start

            do {
                val operand: Operand = getOperand(formula)
                val replace = ExpressionConstants.replace(
                    operand,
                    ExpressionConstants.n, DecimalLiteral(it.toDouble())
                )
                //calc = parser.parseRaw(replace.string()) as Number
                calc = replace.calc(false).toDouble()
                progressions.add(Step(it, replace, calc.format(6)))
                it++
            } while (progressions.last().out.toDouble() != 0.0)

            map["sum"] = progressions.map { it.out.toDouble() }
                .reduce { acc, number -> acc + number }
                .format(2)
            map["progressions"] = progressions
        } catch (e: Throwable) {

        }

    }

}