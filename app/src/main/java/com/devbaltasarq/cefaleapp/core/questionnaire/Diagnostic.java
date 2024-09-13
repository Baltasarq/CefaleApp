// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import android.util.Log;

import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.ValueType;


public class Diagnostic {
    private static final String LOG_TAG = Diagnostic.class.getSimpleName();
    private static final String MSG_MIGRAINE = "migraña";
    private static final String MSG_TENSIONAL = "cefalea tensional";
    private static final String MSG_WITH_AURA = "con aura";
    private static final String MSG_MIXED_TENSIONAL = "cefalea mixta con predominio tensional";
    private static final String MSG_MIXED_MIGRAINE = "cefalea mixta con predominio migrañoso";
    private static final String MSG_CHK_DOCTOR = "Consulte con su médico";
    private static final String MSG_SIT1_TXT = """
        Su cefalea podría ser una migraña.
        No cumple estrictamente los criterios clínicos para una confirmación
        diagnóstica, por lo que debe consultar con su Médico de Familia
        o con su neurólogo.""";
    private static final String MSG_SIT2_TXT = """
        Su cefalea es clínicamente compatible con una cefalea migrañosa.
        No cumple estrictamente los criterios clínicos
        para una confirmación diagnóstica, por lo que debe consultar
        con su Médico de Familia o con su neurólogo.""";
    private static final String MSG_SIT3_TXT = """
        Su cefalea no cumple con los criterios clínicos de migraña,
        y debe consultar con su Médico de Familia o con su neurólogo.""";

    public enum Conclusion {
        NO_CONCLUSION( "" ),
        MIGRAINE_COMPATIBLE_SIT3( MSG_SIT3_TXT ),
        MIGRAINE_COMPATIBLE_SIT2( MSG_SIT2_TXT ),
        MIGRAINE_COMPATIBLE_SIT1( MSG_SIT1_TXT ),
        TENSIONAL( MSG_TENSIONAL ),
        MIGRAINE( MSG_MIGRAINE ),
        MIXED_TENSIONAL( MSG_MIXED_TENSIONAL ),
        MIXED_MIGRAINE( MSG_MIXED_MIGRAINE );

        Conclusion(String desc)
        {
            this.desc = desc;
        }

        public String getDesc()
        {
            if ( this == NO_CONCLUSION ) {
                throw new Error( "tried to use Conclusion.NO_CONCLUSION !!" );
            }

            return this.desc;
        }

        private final String desc;
    }

    public Diagnostic(final MigraineRepo REPO)
    {
        this.REPO = REPO;
    }

    /** Determines the total of various bool questions.
      * @param ids the ids of the questions involved.
      * @return the number of them that are true.
      */
    private int calcSumOf(MigraineRepo.Id[] ids)
    {
        int toret = 0;

        for(MigraineRepo.Id id: ids) {
            if ( this.getBool( id ) ) {
                ++toret;
            }
        }

        return toret;
    }

    /** Calculates the result of the screening questions.
      * i.e, the number of questions that had "true" as answer.
      * @return an integer with the number of "true" answers.
      */
    public int calcTotalScreen()
    {
        return this.calcSumOf( new MigraineRepo.Id[] { MigraineRepo.Id.WASCEPHALEALIMITANT,
                                        MigraineRepo.Id.PHOTOPHOBIA,
                                        MigraineRepo.Id.NAUSEA} );
    }

    /** Calculates the result of the main criteria questions.
      * i.e, the number of questions that had "true" as answer.
      * (This questions determine the possibility of having a migraine.)
      * @return an integer with the number of "true" answers.
      */
    public int calcMainCriteria()
    {
        return this.calcSumOf( new MigraineRepo.Id[] {
                MigraineRepo.Id.ISCEPHALEAONESIDED,
                MigraineRepo.Id.ISPULSATING,
                MigraineRepo.Id.ISMIGRAINEINTENSE,
                MigraineRepo.Id.EXERCISEWORSENS });
    }

    /** return true if the patient has migraines, false otherwise.
      * @see Diagnostic::calcMainCriteria
      */
    public boolean isMigraine()
    {
        final boolean PHOTO = this.getBool( MigraineRepo.Id.PHOTOPHOBIA );
        final boolean SOUND = this.getBool( MigraineRepo.Id.SOUNDPHOBIA );
        final boolean NAUSEA = this.getBool( MigraineRepo.Id.NAUSEA );
        final int MAIN_CRITERIA = this.calcMainCriteria();
        boolean toret = false;

        if ( MAIN_CRITERIA >= 4 ) {
            toret = ( this.REPO.areFemaleConditionsPresent()
                    || ( NAUSEA || ( PHOTO && SOUND ) )
                    || ( NAUSEA && PHOTO )
                    || ( NAUSEA && SOUND )
                    || SOUND );
        }
        else
        if ( MAIN_CRITERIA == 2
          || MAIN_CRITERIA == 3 )
        {
            toret = ( this.REPO.areFemaleConditionsPresent()
                    || NAUSEA
                    || ( PHOTO && SOUND )
                    || ( NAUSEA && PHOTO )
                    || ( NAUSEA && SOUND )
                    || PHOTO
                    || SOUND );
        }

        return toret;
    }

    /** SIT1: 1 main criteriq (taken for granted)
      *     + vomits
      *     + sound- and photo- fobia.
      * @return true if migraine is compatible with SIT1, false otherwise.
      * @see Diagnostic::calcMainCriteria
      */
    private boolean isMigraineCompatSIT1()
    {
        final boolean PHOTO_PHOBIA = this.getBool( MigraineRepo.Id.PHOTOPHOBIA );
        final boolean SOUND_PHOBIA = this.getBool( MigraineRepo.Id.SOUNDPHOBIA );
        final boolean NAUSEA = this.getBool( MigraineRepo.Id.NAUSEA );

        return ( NAUSEA
                && PHOTO_PHOBIA
                && SOUND_PHOBIA );
    }

    /** SIT2: 1 main criteriq (taken for granted)
     *     + sound- and photo- phobia.
     *          OR vomits
     *          OR vomits and photophobia
     *     + female conditions
     * @return true if migraine is compatible with SIT2, false otherwise.
     * @see Diagnostic::calcMainCriteria
     */
    private boolean isMigraineCompatSIT2()
    {
        final boolean PHOTO_PHOBIA = this.getBool( MigraineRepo.Id.PHOTOPHOBIA );
        final boolean SOUND_PHOBIA = this.getBool( MigraineRepo.Id.SOUNDPHOBIA );
        final boolean NAUSEA = this.getBool( MigraineRepo.Id.NAUSEA );

        return ( ( NAUSEA
                || ( PHOTO_PHOBIA && SOUND_PHOBIA )
                || ( NAUSEA && PHOTO_PHOBIA ) )
                && this.REPO.areFemaleConditionsPresent() );
    }

    /** @return the degree of migraine compatibility. */
    public Conclusion calcMigraineCompatibility()
    {
        final int MAIN_CRITERIA = this.calcMainCriteria();
        Conclusion toret = Conclusion.NO_CONCLUSION;

        if ( MAIN_CRITERIA == 1 ) {
            toret = Conclusion.MIGRAINE_COMPATIBLE_SIT3;

            if ( this.isMigraineCompatSIT1() ) {
                toret = Conclusion.MIGRAINE_COMPATIBLE_SIT1;
            }
            else
            if ( this.isMigraineCompatSIT2() ) {
                toret = Conclusion.MIGRAINE_COMPATIBLE_SIT2;
            }
        }

        return toret;
    }

    public boolean isJustMigraineCompatible()
    {
        boolean toret = false;

        if ( this.calcMainCriteria() == 1 ) {
            toret = ( this.calcMigraineCompatibility()
                                            != Conclusion.NO_CONCLUSION );
        }

        return toret;
    }

    /** @return true if the patient has tensional cephaleas, false otherwise. */
    public boolean isTensional()
    {
        boolean toret = this.calcSumOf( new MigraineRepo.Id[]{
                MigraineRepo.Id.ISSTABBING,
                MigraineRepo.Id.ISTENSIONALWORSEONAFTERNOONS,
                MigraineRepo.Id.ISTENSIONALRELATEDTOSTRESS,
                MigraineRepo.Id.ISTENSIONALBETTERWHENDISTRACTED,
                MigraineRepo.Id.SOUNDPHOBIA,
                MigraineRepo.Id.INSOMNIA }) > 1;

        if ( !toret ) {
            toret = this.getBool( MigraineRepo.Id.WHOLEHEAD )
                    && this.getBool( MigraineRepo.Id.ISCEPHALEAHELMET )
                    && !this.getBool( MigraineRepo.Id.ISTENSIONALINTENSE )
                    && this.getBool( MigraineRepo.Id.SAD )
                    && this.getBool( MigraineRepo.Id.ISDEPRESSED );
        }

        return toret;
    }

    /** @return true if the patient has tensional cephaleas and migraines, false otherwise. */
    public boolean isMixed()
    {
        return this.isMigraine() && this.isTensional();
    }

    /** @return a MixedCephalea value of:
      *     MIGRAINE_COMPATIBLE_SIT3 if the cephalea is not mixed between migrain and tensional.
      *     MIXED_TENSIONAL if the cephalea is mixed and tensional predominant.
      *     MIXED_MIGRAINE if the cephalea is mixed and migraine predominant.
      */
    public Conclusion getMixedPredominant()
    {
        Conclusion toret = Conclusion.NO_CONCLUSION;

        if ( this.isMixed() ) {
            toret = Conclusion.MIXED_TENSIONAL;

            if ( this.REPO.getNumMigraines() > this.REPO.getNumTensionalCephaleas() )
            {
                toret = Conclusion.MIXED_MIGRAINE;
            }
        }

        return toret;
    }

    public boolean shouldCheckDoctor()
    {
        int mainCriteria = this.calcSumOf( new MigraineRepo.Id[] {
                MigraineRepo.Id.ISCEPHALEAONESIDED,
                MigraineRepo.Id.ISPULSATING,
                MigraineRepo.Id.ISMIGRAINEINTENSE,
                MigraineRepo.Id.EXERCISEWORSENS });

        return ( this.calcTotalScreen() == 1
           || ( !this.isMigraine()
              && this.REPO.isMale()
              && mainCriteria >= 1
              && this.getBool( MigraineRepo.Id.HASHISTORY ) ));
    }

    /** @return a boolean value given its id, assuming not existing means "false" */
    private boolean getBool(MigraineRepo.Id id)
    {
        boolean toret = false;

        if ( this.REPO.exists( id ) ) {
            toret = this.REPO.getBool( id );
        } else {
            if ( Util.DEBUG ) {
                Log.e( LOG_TAG, "missing from REPO " + id );
            }
        }

        return toret;
    }

    /** Apply initial rules before taking a decision. */
    private void preDecide()
    {
        boolean INTENSE_PAIN = REPO.getBool( MigraineRepo.Id.ISMIGRAINEINTENSE );
        boolean IN_DARK = REPO.getBool( MigraineRepo.Id.BETTERINDARKNESS );
        boolean IS_LIMITANT = REPO.getBool( MigraineRepo.Id.WASCEPHALEALIMITANT );

        // -- PCD-PATIENTS_MINIMIZE_PAIN-0910
        if ( !INTENSE_PAIN
          && IN_DARK
          && IS_LIMITANT )
        {
            REPO.setValue( MigraineRepo.Id.ISMIGRAINEINTENSE,
                    new Value( true, ValueType.BOOL ));
        }
    }

    /** Decides what kind of migraine the patient has.
      * @return The conclusion of the decision.
      * @see Diagnostic::Conclusion
      */
    public Conclusion decide()
    {
        this.preDecide();
        return this.makeDecision();
    }

    private Conclusion makeDecision()
    {
        final boolean IS_MIGRAINE = this.isMigraine();
        final boolean IS_TENSIONAL = this.isTensional();
        final boolean IS_MIXED = this.isMixed();
        Conclusion toret = Conclusion.MIGRAINE_COMPATIBLE_SIT3;

        if ( !IS_MIGRAINE
          && !IS_TENSIONAL )
        {
            toret = this.calcMigraineCompatibility();

            if ( toret == Conclusion.NO_CONCLUSION ) {
                toret = Conclusion.MIGRAINE_COMPATIBLE_SIT3;
            }
        } else {
            // Kind of cephalea
            if ( IS_MIXED ) {
                toret = this.getMixedPredominant();
            }
            else
            if ( IS_MIGRAINE ) {
                toret = Conclusion.MIGRAINE;
            }
            else
            if ( IS_TENSIONAL ) {
                toret = Conclusion.TENSIONAL;
            }

        }

        return toret;
    }

    public boolean hasAura()
    {
        return ( ( this.isMigraine()
                || this.calcMigraineCompatibility() != Conclusion.MIGRAINE_COMPATIBLE_SIT3 )
              && this.REPO.hadAura() );
    }

    public MigraineRepo.Frequency getFreq()
    {
        MigraineRepo.Frequency toret = this.REPO.getTensionalFreq();

        // Frequency (chronic, sporadic...)
        if ( this.isMixed() ) {
            toret = this.REPO.getMixedFreq();
        }
        else
        if ( this.isMigraine() ) {
            toret = this.REPO.getMigraineFreq();
        }

        return toret;
    }

    @Override
    public String toString()
    {
        final StringBuilder TORET = new StringBuilder();
        final Conclusion CONCLUSION = this.decide();
        MigraineRepo.Frequency freq = this.getFreq();

        TORET.append( "<b>" );

        // Diagnostic
        TORET.append( CONCLUSION.getDesc() );

        if ( !this.isJustMigraineCompatible() ) {
            // Has aura?
            if ( CONCLUSION == Conclusion.MIGRAINE
              || CONCLUSION == Conclusion.MIGRAINE_COMPATIBLE_SIT1 )
            {
                if ( this.hasAura() ) {
                    TORET.append( " " + MSG_WITH_AURA );
                }
            }

            // Frequency
            if ( CONCLUSION == Conclusion.MIGRAINE ) {
                TORET.append( " " );
                TORET.append( freq.toString() );
            }

            TORET.append( "</b><i>" );

            // You should ask your doctor
            if ( this.isMixed()
              || this.isMigraine() )
            {
                if ( !this.REPO.getBool( MigraineRepo.Id.HADMORETHANFIVEEPISODES ) ) {
                    TORET.append( '\n' );
                    TORET.append( " (Probable, migraña de menos de cinco episodios. " );
                    TORET.append( MSG_CHK_DOCTOR + ".)" );
                    TORET.append( '\n' );
                }

                if ( !this.REPO.getBool( MigraineRepo.Id.MIGRAINEDURATION ) ) {
                    TORET.append( '\n' );
                    TORET.append( " (No cumple con la duración de un episodio de migraña. " );
                    TORET.append( MSG_CHK_DOCTOR + ".)" );
                    TORET.append( '\n' );
                }
            }

            if ( this.shouldCheckDoctor() ) {
                TORET.append( '\n' );
                TORET.append( " (Síntomas relevantes. ");
                TORET.append( MSG_CHK_DOCTOR + ".)" );
                TORET.append( '\n' );
            }

            TORET.append( "</i>" );
        } else {
            TORET.append( "</b>" );
        }

        return TORET.toString();
    }

    final private MigraineRepo REPO;
}
