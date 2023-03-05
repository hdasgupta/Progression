package com.progression.parser.expression

import com.progression.parser.expression.ExpressionConstants.Companion.add
import com.progression.parser.expression.ExpressionConstants.Companion.div
import com.progression.parser.expression.ExpressionConstants.Companion.getConst
import com.progression.parser.expression.ExpressionConstants.Companion.hasValue
import com.progression.parser.expression.ExpressionConstants.Companion.isConst
import com.progression.parser.expression.ExpressionConstants.Companion.lit
import com.progression.parser.expression.ExpressionConstants.Companion.mul
import com.progression.parser.expression.ExpressionConstants.Companion.one
import com.progression.parser.expression.ExpressionConstants.Companion.pow
import com.progression.parser.expression.ExpressionConstants.Companion.sub
import com.progression.parser.expression.ExpressionConstants.Companion.zero


//fun main() {
//    println(div(x, mul(x.new(), y.new())))
//
//   val str = "(4x+6)x/2"
//    var operand = getOperand(str)
//
//    println( Operators.div.reduce(listOf(1.0,3.0), false))
//    println(operand)
//    println(simp(getOperand("$operand")))/*
//    val s =getOperand(diff(operand).last().operand.toString())
//    println(s)
//    println(getOperand(simp(s).last().operand.toString()))*/
///*
//    var results = simp(operand)
//    println(results)*/
//    println("x")
//}


fun op(operator: Operators, ops: Array<Operand>): Operand {
    if (operator.operandCount > 1 && ops.size == 1) {
        return ops[0]
    }
    return try {
        ExpressionConstants.Companion::class.java.getDeclaredMethod(operator.name, Array<Operand>::class.java)
            .invoke(ExpressionConstants, ops) as Operand
    } catch (t: Throwable) {
        ExpressionConstants.Companion::class.java.getDeclaredMethod(operator.name, Operand::class.java)
            .invoke(ExpressionConstants, ops[0]) as Operand
    }
}

fun calc(op: Operators, list: List<out Operand>): Operand {
    val consts = list.filter { hasValue(it) }
    val isFirst = if (consts.isNotEmpty()) consts.minOf { list.indexOf(it) } == 0 else false
    val vars = list.filter { !hasValue(it) }.toMutableList()


    return     if (consts.isNotEmpty()) {

        val values = consts.map { it.calc() }
        val num = lit(values.reduce {c1,c2-> op(op, arrayOf(DecimalLiteral(c1.toDouble()), DecimalLiteral(c2.toDouble()))).calc()})

        if(vars.isEmpty()) {
            num
        }else {
            if (isFirst)
                vars.add(0, num)
            else
                vars.add(num)

            if (vars.size ==  op.operandCount) {
                op(op, vars.toTypedArray())
            } else if(vars.size>1 ){
                op(op, vars.toTypedArray())
            } else {
                vars[0]
            }
        }
    } else {
        op(op, list.toTypedArray())
    }
}

fun dot(o1: Operation, o2: Operation, multiplier: List<Operand>): Operation {
    return add(*o1.operands.map { it1 ->
        add(*o2.operands.map { it2 ->
            mul(*listOf(it1, it2).toTypedArray()) as Operation

        }.toTypedArray()) as Operation
    }.toTypedArray()) as Operation
}
