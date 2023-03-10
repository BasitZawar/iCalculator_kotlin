package com.cyber.tarzan.calculator.ui.main.helper

import com.cyber.tarzan.calculator.util.AngleType
import org.junit.Assert.*
import org.junit.Test

class EvaluateUnitTest {

    @Test
    fun testPrepareExpression() {
        assertEquals("", prepareExpression(""))
        assertEquals("5/6", prepareExpression("5÷6"))
        assertEquals("5+${Math.E}", prepareExpression("5+e"))
        assertEquals("5+-5", prepareExpression("5-5"))
        assertEquals("5/-5", prepareExpression("5÷-5"))
        assertEquals("5^-5", prepareExpression("5^-5"))
    }

    @Test
    fun testGetResult() {
        val deg = AngleType.DEG.name
        val rad = AngleType.RAD.name
        assertEquals("10", roundMyAnswer(getResult("5+5", deg)))
        assertEquals("1", roundMyAnswer(getResult("sin(90)", deg)))
        assertEquals("0", roundMyAnswer(getResult("cos(90)", deg)))
        assertEquals("1", roundMyAnswer(getResult("sin(${Math.PI}/2)", rad)))
        assertEquals("0", roundMyAnswer(getResult("cos(${Math.PI}/2)", rad)))
        assertEquals("0", roundMyAnswer(getResult("tan(0)", deg)))
        assertEquals("0", roundMyAnswer(getResult("tan(${Math.PI})", rad)))
        assertEquals("90", roundMyAnswer(getResult("sin⁻¹(1)", deg)))
        assertEquals("0", roundMyAnswer(getResult("cos⁻¹(1)", deg)))
        assertEquals("45", roundMyAnswer(getResult("tan⁻¹(1)", deg)))
        assertEquals("1", roundMyAnswer(getResult("log(10)", deg)))
        assertEquals("1", roundMyAnswer(getResult("ln(${Math.E})", deg)))
        assertEquals("2", roundMyAnswer(getResult("√4", deg)))
        assertEquals("2", roundMyAnswer(getResult("√√16", deg)))
        assertEquals("-2", roundMyAnswer(getResult("+-√4", deg)))
        assertEquals("-2", roundMyAnswer(getResult("+-∛8", deg)))
        assertEquals("120", roundMyAnswer(getResult("5!", deg)))
        assertEquals("0.05", roundMyAnswer(getResult("5%", deg)))
        assertEquals("220", roundMyAnswer(getResult("200+10%", deg)))
        assertEquals("25", roundMyAnswer(getResult("5^2", deg)))
        assertEquals("5", roundMyAnswer(getResult("25^(1/2)", deg)))
        assertEquals("50", roundMyAnswer(getResult("5000/100", deg)))
        assertEquals("3.333333", roundMyAnswer(getResult("10/3", deg)))
        assertEquals("500000", roundMyAnswer(getResult("5000*100", deg)))
        assertEquals("5100", roundMyAnswer(getResult("5000+100", deg)))
        assertEquals("41", roundMyAnswer(getResult("5+9*8/2", deg)))
        assertEquals("46", roundMyAnswer(getResult("2*5+9*8/2", deg)))
        assertEquals("56", roundMyAnswer(getResult("(5+9)*8/2", deg)))
        assertEquals("5", roundMyAnswer(getResult("5+", deg)))
    }

}