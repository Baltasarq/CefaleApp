// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;


import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.ValueType;

import junit.framework.TestCase;
import org.junit.Assert;


public class ValueTest extends TestCase {
    public void setUp() throws Exception
    {
        super.setUp();

        this.createV1();
        this.createV2();
    }

    public void testGet()
    {
        Assert.assertEquals( true, this.v1.get() );
        Assert.assertEquals( 42, this.v2.get() );
    }

    public void testGetDataType()
    {
        Assert.assertEquals( ValueType.BOOL, this.v1.getValueType() );
        Assert.assertEquals( ValueType.INT, this.v2.getValueType() );
    }

    public void testHashCode()
    {
        final Value V11 = new Value( "true", ValueType.BOOL );
        final Value V22 = new Value( "42", ValueType.INT );

        Assert.assertEquals( this.v1.hashCode(), this.v1.hashCode() );
        Assert.assertEquals( this.v2.hashCode(), this.v2.hashCode() );
        Assert.assertEquals( this.v1.hashCode(), V11.hashCode() );
        Assert.assertEquals( this.v2.hashCode(), V22.hashCode() );
        Assert.assertNotEquals( this.v1.hashCode(), V22.hashCode() );
        Assert.assertNotEquals( this.v2.hashCode(), V11.hashCode() );
    }

    public void testEquals()
    {
        final Value V11 = new Value( "true", ValueType.BOOL );
        final Value V22 = new Value( "42", ValueType.INT );

        Assert.assertEquals( this.v1, this.v1 );
        Assert.assertEquals( this.v2, this.v2 );
        Assert.assertEquals( this.v1, V11 );
        Assert.assertEquals( this.v2, V22 );
        Assert.assertNotEquals( this.v1, V22 );
        Assert.assertNotEquals( this.v2, V11 );
    }

    public void testToString()
    {
        final String EXPECTED_STR_V1 = "/"
                + this.v1.getValueType()
                + " value: "
                + this.v1.get()
                + ".";

        final String EXPECTED_STR_V2 = "/"
                + this.v2.getValueType()
                + " value: "
                + this.v2.get()
                + ".";

        Assert.assertEquals( EXPECTED_STR_V1, this.v1.toString() );
        Assert.assertEquals( EXPECTED_STR_V2, this.v2.toString() );
    }

    private void createV1()
    {
        this.v1 = new Value( "true", ValueType.BOOL );
    }

    private void createV2()
    {
        this.v2 = new Value( "42", ValueType.INT );
    }

    private Value v1;
    private Value v2;
}
