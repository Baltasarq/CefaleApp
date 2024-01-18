// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;

import com.devbaltasarq.cefaleapp.core.questionnaire.form.ValueType;

import junit.framework.TestCase;

import org.junit.Assert;


public class ValueTypeTest extends TestCase {
    public void testValuesAsString()
    {
        final ValueType[] VALUES = ValueType.values();

        int i = 0;
        for(final String STR_VALUE: ValueType.valuesAsString()) {
            Assert.assertEquals( VALUES[ i ].toString(), STR_VALUE );

            ++i;
        }

        return;
    }

    public void testParse()
    {
        final ValueType[] VALUES = ValueType.values();

        for(ValueType value: VALUES) {
            Assert.assertEquals( value, ValueType.parse( value.toString() ) );
        }
    }
}