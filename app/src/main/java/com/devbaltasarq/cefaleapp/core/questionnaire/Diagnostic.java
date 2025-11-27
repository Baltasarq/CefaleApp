// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import android.util.Log;

import java.util.Map;
import java.util.EnumMap;
import java.util.Objects;
import java.util.function.Function;

import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.ValueType;
import com.devbaltasarq.cefaleapp.core.Message;
import com.devbaltasarq.cefaleapp.core.LocalizedText;


public class Diagnostic {
    private static final String LOG_TAG = Diagnostic.class.getSimpleName();
    private static final String MSG_ID_WITH_AURA = "conclusionMsgWithAura";
    private static final String MSG_ID_CHK_DOCTOR = "conclusionMsgChkDoctor";

    public enum ConclusionId {
        NO_CONCLUSION,
        MIGRAINE_COMPATIBLE_SIT3,
        MIGRAINE_COMPATIBLE_SIT2,
        MIGRAINE_COMPATIBLE_SIT1,
        TENSIONAL,
        MIGRAINE,
        MIXED_TENSIONAL,
        MIXED_MIGRAINE
    }

    public Diagnostic(final MigraineRepo REPO)
    {
        this( REPO, Message::getFor );
    }

    public Diagnostic(final MigraineRepo REPO, Function<String, Message> getMsg)
    {
        this.REPO = REPO;
        this.getMsg = getMsg;
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
    public ConclusionId calcMigraineCompatibility()
    {
        final int MAIN_CRITERIA = this.calcMainCriteria();
        ConclusionId toret = ConclusionId.NO_CONCLUSION;

        if ( MAIN_CRITERIA == 1 ) {
            toret = ConclusionId.MIGRAINE_COMPATIBLE_SIT3;

            if ( this.isMigraineCompatSIT1() ) {
                toret = ConclusionId.MIGRAINE_COMPATIBLE_SIT1;
            }
            else
            if ( this.isMigraineCompatSIT2() ) {
                toret = ConclusionId.MIGRAINE_COMPATIBLE_SIT2;
            }
        }

        return toret;
    }

    public boolean isJustMigraineCompatible()
    {
        boolean toret = false;

        if ( this.calcMainCriteria() == 1 ) {
            toret = ( this.calcMigraineCompatibility()
                                            != ConclusionId.NO_CONCLUSION );
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
    public ConclusionId getMixedPredominant()
    {
        ConclusionId toret = ConclusionId.NO_CONCLUSION;

        if ( this.isMixed() ) {
            toret = ConclusionId.MIXED_TENSIONAL;

            if ( this.REPO.getNumMigraines() > this.REPO.getNumTensionalCephaleas() )
            {
                toret = ConclusionId.MIXED_MIGRAINE;
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
            if ( this.REPO.isInDebugMode() ) {
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
    public ConclusionId decide()
    {
        this.preDecide();
        return this.makeDecision();
    }

    private ConclusionId makeDecision()
    {
        final boolean IS_MIGRAINE = this.isMigraine();
        final boolean IS_TENSIONAL = this.isTensional();
        final boolean IS_MIXED = this.isMixed();
        ConclusionId toret = ConclusionId.MIGRAINE_COMPATIBLE_SIT3;

        if ( !IS_MIGRAINE
          && !IS_TENSIONAL )
        {
            toret = this.calcMigraineCompatibility();

            if ( toret == ConclusionId.NO_CONCLUSION ) {
                toret = ConclusionId.MIGRAINE_COMPATIBLE_SIT3;
            }
        } else {
            // Kind of cephalea
            if ( IS_MIXED ) {
                toret = this.getMixedPredominant();
            }
            else
            if ( IS_MIGRAINE ) {
                toret = ConclusionId.MIGRAINE;
            }
            else
            if ( IS_TENSIONAL ) {
                toret = ConclusionId.TENSIONAL;
            }

        }

        return toret;
    }

    public boolean hasAura()
    {
        return ( ( this.isMigraine()
                || this.calcMigraineCompatibility() != ConclusionId.MIGRAINE_COMPATIBLE_SIT3 )
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
        final ConclusionId CONCLUSION = this.decide();
        final ConclusionMessage CONCLUSION_MSG = new ConclusionMessage( this.getMsg );
        final String MSG_ID_EXPL_LESS_FIVE_EPISODES = "conclusionMsgExplLessFiveEpisodes";
        final String MSG_ID_EXPL_DOES_NOT_FULFILL = "conclusionMsgExplDoesNotFulfill";
        final String MSG_ID_EXPL_RELEVANT_SYMPTOMS = "conclusionMsgExplRelevantSymptoms";
        MigraineRepo.Frequency freq = this.getFreq();

        TORET.append( "<b>" );

        // Diagnostic
        TORET.append( CONCLUSION_MSG.getFor( CONCLUSION ).getForCurrentLanguage() );

        if ( !this.isJustMigraineCompatible() ) {
            // Has aura?
            if ( CONCLUSION == ConclusionId.MIGRAINE
              || CONCLUSION == ConclusionId.MIGRAINE_COMPATIBLE_SIT1 )
            {
                if ( this.hasAura() ) {
                    TORET.append( " " + this.getMsg.apply( MSG_ID_WITH_AURA ) );
                }
            }

            // Frequency
            if ( CONCLUSION == ConclusionId.MIGRAINE ) {
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
                    TORET.append( " ("
                                + this.getMsg.apply( MSG_ID_EXPL_LESS_FIVE_EPISODES )
                                + " " );
                    TORET.append( this.getMsg.apply( MSG_ID_CHK_DOCTOR ) + ".)" );
                    TORET.append( '\n' );
                }

                if ( !this.REPO.getBool( MigraineRepo.Id.MIGRAINEDURATION ) ) {
                    TORET.append( '\n' );
                    TORET.append( " ("
                                + this.getMsg.apply( MSG_ID_EXPL_DOES_NOT_FULFILL )
                                + " " );
                    TORET.append( this.getMsg.apply( MSG_ID_CHK_DOCTOR ) + ".)" );
                    TORET.append( '\n' );
                }
            }

            if ( this.shouldCheckDoctor() ) {
                TORET.append( '\n' );
                TORET.append( " ("
                            + this.getMsg.apply( MSG_ID_EXPL_RELEVANT_SYMPTOMS )
                            + " " );
                TORET.append( this.getMsg.apply( MSG_ID_CHK_DOCTOR ) + ".)" );
                TORET.append( '\n' );
            }

            TORET.append( "</i>" );
        } else {
            TORET.append( "</b>" );
        }

        return TORET.toString();
    }

    final private Function<String, Message> getMsg;
    final private MigraineRepo REPO;

    public static class ConclusionMessage {
        private static final String MSG_ID_MIGRAINE = "conclusionMsgMigraine";
        private static final String MSG_ID_TENSIONAL = "conclusionMsgTensional";
        private static final String MSG_ID_MIXED_TENSIONAL = "conclusionMsgMixedTensional";
        private static final String MSG_ID_MIXED_MIGRAINE = "conclusionMsgMixedMigraine";
        private static final String MSG_ID_SIT1_TXT = "conclusionMsgSit1";
        private static final String MSG_ID_SIT2_TXT = "conclusionMsgSit2";
        private static final String MSG_ID_SIT3_TXT = "conclusionMsgSit3";

        public ConclusionMessage()
        {
            this( Message::getFor );
        }

        public ConclusionMessage(Function<String, Message> getMsg)
        {
            this.msg = new EnumMap<>( ConclusionId.class );
            this.msg.putAll( Map.of(
                    ConclusionId.NO_CONCLUSION, Message.EMPTY,
                    ConclusionId.MIGRAINE_COMPATIBLE_SIT3, getMsg.apply( MSG_ID_SIT3_TXT ),
                    ConclusionId.MIGRAINE_COMPATIBLE_SIT2, getMsg.apply( MSG_ID_SIT2_TXT ),
                    ConclusionId.MIGRAINE_COMPATIBLE_SIT1, getMsg.apply( MSG_ID_SIT1_TXT ),
                    ConclusionId.TENSIONAL, getMsg.apply( MSG_ID_TENSIONAL ),
                    ConclusionId.MIGRAINE, getMsg.apply( MSG_ID_MIGRAINE ),
                    ConclusionId.MIXED_TENSIONAL, getMsg.apply( MSG_ID_MIXED_TENSIONAL ),
                    ConclusionId.MIXED_MIGRAINE, getMsg.apply( MSG_ID_MIXED_MIGRAINE) ));
        }

        public LocalizedText getFor(ConclusionId conclusionId)
        {
            if ( conclusionId == ConclusionId.NO_CONCLUSION ) {
                throw new Error( "tried to use Conclusion.NO_CONCLUSION !!" );
            }

            final Message MSG = Objects.requireNonNull( this.msg.get( conclusionId ) );
            return MSG.getMsg();
        }

        private final Map<ConclusionId, Message> msg;
    }
}
