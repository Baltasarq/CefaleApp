package com.devbaltasarq.cefaleapp.core.form;

import com.devbaltasarq.cefaleapp.core.Language;
import com.devbaltasarq.cefaleapp.core.LocalizedText;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Option;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Question;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.ValueType;

import junit.framework.TestCase;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class QuestionTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();

        this.createQ1();
        this.createQ2();
        this.createQ3();
        this.qs = new ArrayList<>();
        this.qs.addAll( Arrays.asList( this.q1, this.q2, this.q3 ) );
    }

    public void testGetText()
    {
        final LocalizedText TXT_MSG1 = new LocalizedText();
        final LocalizedText TXT_MSG2 = new LocalizedText();
        final LocalizedText TXT_MSG3 = new LocalizedText();

        TXT_MSG1.add( Language.es, "¿Tu nombre?" );
        TXT_MSG2.add( Language.es, "¿Tu sexo?" );
        TXT_MSG3.add( Language.es, "¿Duele?" );

        final LocalizedText[] questionTexts = { TXT_MSG1, TXT_MSG2, TXT_MSG3 };

        for(int i = 0; i < this.qs.size(); ++i) {
            Assert.assertEquals( questionTexts[ i ], this.qs.get( i ).getText() );
        }
    }

    public void testGetId()
    {
        String[] questionIds = { "data_name", "migraine_gender", "migraine_pain" };

        for(int i = 0; i < this.qs.size(); ++i) {
            Assert.assertEquals( questionIds[ i ], this.qs.get( i ).getId() );
        }
    }

    public void testGetBranchFromId()
    {
        String[] questionBranchs = { "data", "migraine", "migraine" };

        for(int i = 0; i < this.qs.size(); ++i) {
            Assert.assertEquals( questionBranchs[ i ], this.qs.get( i ).getBranchFromId() );
        }
    }

    public void testGetDataFromId()
    {
        String[] questionDatas = { "name", "gender", "pain" };

        for(int i = 0; i < this.qs.size(); ++i) {
            Assert.assertEquals( questionDatas[ i ], this.qs.get( i ).getDataFromId() );
        }
    }

    public void testGetDataType()
    {
        ValueType[] questionTypes = { ValueType.STR, ValueType.BOOL, ValueType.BOOL };

        for(int i = 0; i < this.qs.size(); ++i) {
            Assert.assertEquals( questionTypes[ i ], this.qs.get( i ).getValueType() );
        }
    }

    public void testGetGotoId()
    {
        String[] questionGotoIds = { "", "migraine_age", "/" };

        for(int i = 0; i < this.qs.size(); ++i) {
            Assert.assertEquals( questionGotoIds[ i ], this.qs.get( i ).getGotoId() );
        }
    }

    public void testGetNumOptions()
    {
        int[] numOptions = { 0, 0, 2 };

        for(int i = 0; i < this.qs.size(); ++i) {
            Assert.assertEquals( numOptions[ i ], this.qs.get( i ).getNumOptions() );
        }
    }

    public void testNextQuestionId()
    {
        Assert.assertEquals( "/", this.q3.getGotoId() );
    }

    public void testEquals()
    {
        Assert.assertEquals( this.q1, this.q1 );
        Assert.assertNotEquals( this.q1, this.q2 );
    }

    public void testTestHashCode()
    {
        Assert.assertEquals( this.q1.hashCode(), this.q1.hashCode() );
        Assert.assertNotEquals( this.q1.hashCode(), this.q2.hashCode() );
    }

    private void createQ1()
    {
        final Question.Builder BUILD = new Question.Builder();
        final LocalizedText TXT_MSG = new LocalizedText();

        TXT_MSG.add( Language.es, "¿Tu nombre?" );
        BUILD.setId( "data_name" )
                .setValueType( ValueType.STR.toString() )
                .setText( TXT_MSG );

        this.q1 = BUILD.create();
    }

    private void createQ2()
    {
        final Question.Builder BUILD = new Question.Builder();
        final LocalizedText TXT_MSG = new LocalizedText();

        TXT_MSG.add( Language.es, "¿Tu sexo?" );

        BUILD.setId( "migraine_gender" )
                .setGotoId( "migraine_age" )
                .setValueType( ValueType.BOOL.toString() )
                .setText( TXT_MSG );

        this.q2 = BUILD.create();
    }

    private void createQ3()
    {
        final Question.Builder BUILD = new Question.Builder();
        final LocalizedText TXT_MSG = new LocalizedText();
        final LocalizedText TXT_SI = new LocalizedText();
        final LocalizedText TXT_NO = new LocalizedText();

        TXT_MSG.add( Language.es, "¿Duele?" );
        TXT_SI.add( Language.es, "Sí" );
        TXT_NO.add( Language.es, "No" );

        BUILD.setId( "migraine_pain" )
                .setValueType( ValueType.BOOL.toString() )
                .setGotoId( "/" )
                .setText( TXT_MSG )
                .addOption( new Option( TXT_SI, "true" ) )
                .addOption( new Option( TXT_NO, "false" ) );

        this.q3 = BUILD.create();
    }

    private Question q1;
    private Question q2;
    private Question q3;
    private List<Question> qs;
}
