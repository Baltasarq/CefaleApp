package com.devbaltasarq.cefaleapp.core.form;

import com.devbaltasarq.cefaleapp.core.questionnaire.Diagnostic;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineRepo;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.ValueType;

import junit.framework.TestCase;


public class DiagTest extends TestCase {
    public void setUp() throws Exception
    {
        super.setUp();

        this.repo = MigraineRepo.get();
        this.repo.reset();
        this.diag = new Diagnostic( this.repo );
    }

    public void testCalcTotalScreen()
    {
        // No screen points
        this.repo.setValue( MigraineRepo.Id.PHOTOPHOBIA, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.WASCEPHALEALIMITANT, new Value( false, ValueType.BOOL ) );
        assertEquals( 0, this.diag.calcTotalScreen() );

        // One screen points
        this.repo.setValue( MigraineRepo.Id.PHOTOPHOBIA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.WASCEPHALEALIMITANT, new Value( false, ValueType.BOOL ) );
        assertEquals( 1, this.diag.calcTotalScreen() );

        // Two screen points
        this.repo.setValue( MigraineRepo.Id.PHOTOPHOBIA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.WASCEPHALEALIMITANT, new Value( false, ValueType.BOOL ) );
        assertEquals( 2, this.diag.calcTotalScreen() );

        // Two screen points
        this.repo.setValue( MigraineRepo.Id.PHOTOPHOBIA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.WASCEPHALEALIMITANT, new Value( true, ValueType.BOOL ) );
        assertEquals( 3, this.diag.calcTotalScreen() );
    }

    public void testNoCephaleaIsSIT3()
    {
        assertEquals( Diagnostic.Conclusion.MIGRAINE_COMPATIBLE_SIT3, this.diag.decide() );
    }

    public void testMigraineFourConditions()
    {
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 5, ValueType.INT ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.ISCEPHALEAONESIDED, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISPULSATING, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.EXERCISEWORSENS, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE, decision );

        this.repo.setValue( MigraineRepo.Id.HADAURA, new Value( true, ValueType.BOOL ) );
        System.out.println( this.diag.toString() );
        assertTrue( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE, decision );
    }

    public void testMigraineThreeConditions()
    {
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 5, ValueType.INT ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.ISCEPHALEAONESIDED, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.EXERCISEWORSENS, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE, decision );

        this.repo.setValue( MigraineRepo.Id.HADAURA, new Value( true, ValueType.BOOL ) );
        System.out.println( this.diag.toString() );
        assertTrue( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE, decision );
    }

    public void testMigraineTwoConditions()
    {
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 5, ValueType.INT ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.ISCEPHALEAONESIDED, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.SOUNDPHOBIA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.PHOTOPHOBIA, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE, decision );

        this.repo.setValue( MigraineRepo.Id.HADAURA, new Value( true, ValueType.BOOL ) );
        System.out.println( this.diag.toString() );
        assertTrue( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE, decision );
    }

    public void testMigraineOneConditionSIT1()
    {
        this.repo.setValue( MigraineRepo.Id.ISCEPHALEAONESIDED, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.PHOTOPHOBIA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.SOUNDPHOBIA, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE_COMPATIBLE_SIT1, decision );

        this.repo.setValue( MigraineRepo.Id.HADAURA, new Value( true, ValueType.BOOL ) );
        System.out.println( this.diag.toString() );
        assertTrue( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE_COMPATIBLE_SIT1, decision );
    }

    public void testMigraineOneConditionSIT2()
    {
        this.repo.setValue( MigraineRepo.Id.ISCEPHALEAONESIDED, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.GENDER, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HASHISTORY, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MENSTRUATIONWORSENS, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE_COMPATIBLE_SIT2, decision );
    }

    public void testMigraineOneConditionSIT3()
    {
        this.repo.setValue( MigraineRepo.Id.ISCEPHALEAONESIDED, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE_COMPATIBLE_SIT3, decision );
    }

    public void testTensional()
    {
        this.repo.setValue( MigraineRepo.Id.HOWMANYTENSIONAL, new Value( 7, ValueType.INT ) );
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.FORMORETHANONEYEAR, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.WHOLEHEAD, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISCEPHALEAHELMET, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.SAD, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISDEPRESSED, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.TENSIONAL, decision );
    }

    public void testTensional2()
    {
        this.repo.setValue( MigraineRepo.Id.HOWMANYTENSIONAL, new Value( 7, ValueType.INT ) );
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.FORMORETHANONEYEAR, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.ISSTABBING, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISTENSIONALBETTERWHENDISTRACTED, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.TENSIONAL, decision );
    }

    public void testMixedPredominantTensional()
    {
        // Tensional
        this.repo.setValue( MigraineRepo.Id.HOWMANYTENSIONAL, new Value( 7, ValueType.INT ) );
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.FORMORETHANONEYEAR, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.ISSTABBING, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISTENSIONALBETTERWHENDISTRACTED, new Value( true, ValueType.BOOL ) );

        // Migraine
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 5, ValueType.INT ) );

        this.repo.setValue( MigraineRepo.Id.ISCEPHALEAONESIDED, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.EXERCISEWORSENS, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );

        // Conclusion
        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIXED_TENSIONAL, decision );
    }

    public void testMixedPredominantMigraine()
    {
        // Tensional
        this.repo.setValue( MigraineRepo.Id.HOWMANYTENSIONAL, new Value( 5, ValueType.INT ) );
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.FORMORETHANONEYEAR, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.ISSTABBING, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISTENSIONALBETTERWHENDISTRACTED, new Value( true, ValueType.BOOL ) );

        // Migraine
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 7, ValueType.INT ) );

        this.repo.setValue( MigraineRepo.Id.ISCEPHALEAONESIDED, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.EXERCISEWORSENS, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );

        // Conclusion
        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertFalse( this.diag.hasAura() );
        assertEquals( Diagnostic.Conclusion.MIXED_MIGRAINE, decision );
    }

    public void testExample1()
    {
        this.repo.setValue( MigraineRepo.Id.GENDER, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HASHISTORY, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.FORMORETHANONEYEAR, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 9, ValueType.INT ) );

        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.WASCEPHALEALIMITANT, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.BETTERINDARKNESS, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.PHOTOPHOBIA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.SOUNDPHOBIA, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE_COMPATIBLE_SIT1, decision );
    }

    public void testExample2()
    {
        this.repo.setValue( MigraineRepo.Id.GENDER, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HASHISTORY, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.FORMORETHANONEYEAR, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.NAUSEA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 9, ValueType.INT ) );

        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.BETTERINDARKNESS, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.PHOTOPHOBIA, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.SOUNDPHOBIA, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE_COMPATIBLE_SIT3, decision );
    }

    public void testExample3()
    {
        this.repo.setValue( MigraineRepo.Id.GENDER, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HASHISTORY, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.FORMORETHANONEYEAR, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 9, ValueType.INT ) );

        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.WASCEPHALEALIMITANT, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.PHOTOPHOBIA, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE_COMPATIBLE_SIT3, decision );
    }

    public void testExample4()
    {
        this.repo.setValue( MigraineRepo.Id.GENDER, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HASHISTORY, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.FORMORETHANONEYEAR, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 9, ValueType.INT ) );

        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.WASCEPHALEALIMITANT, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.BETTERINDARKNESS, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.MENSTRUATIONWORSENS, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE_COMPATIBLE_SIT3, decision );
    }

    public void testExample5()
    {
        this.repo.setValue( MigraineRepo.Id.GENDER, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HASHISTORY, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.FORMORETHANONEYEAR, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HADMORETHANFIVEEPISODES, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MIGRAINEDURATION, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.HOWMANYMIGRAINE, new Value( 9, ValueType.INT ) );

        this.repo.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE, new Value( false, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.WASCEPHALEALIMITANT, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.BETTERINDARKNESS, new Value( true, ValueType.BOOL ) );

        this.repo.setValue( MigraineRepo.Id.ISPULSATING, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.MENSTRUATIONWORSENS, new Value( true, ValueType.BOOL ) );
        this.repo.setValue( MigraineRepo.Id.CONTRACEPTIVESWORSENS, new Value( true, ValueType.BOOL ) );

        Diagnostic.Conclusion decision = this.diag.decide();
        System.out.println( this.diag.toString() );
        assertEquals( Diagnostic.Conclusion.MIGRAINE, decision );
    }

    private MigraineRepo repo;
    private Diagnostic diag;
}
