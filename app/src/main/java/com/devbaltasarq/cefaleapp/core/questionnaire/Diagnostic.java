// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import android.util.Log;


public class Diagnostic {
    private static final String LOG_TAG = Diagnostic.class.getSimpleName();

    public enum MixedCephalea { NOT_MIXED, MIXED_PREDOMINANT_TENSIONAL, MIXED_PREDOMINANT_MIGRAINE }

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
     * i.e, the number of question that had "true" as answer.
     * @return an integer with the number of "true" answers.
     */
    public int calcTotalScreen()
    {
        return this.calcSumOf( new MigraineRepo.Id[] { MigraineRepo.Id.WASCEPHALEALIMITANT,
                                        MigraineRepo.Id.HADPHOTOPHOBIA,
                                        MigraineRepo.Id.HADNAUSEA } );
    }

    /** return true if the patient has migraines, false otherwise. */
    public boolean isMigraine()
    {
        boolean toret = false;
        final boolean PHOTO = this.getBool( MigraineRepo.Id.HADPHOTOPHOBIA );
        final boolean SOUND = this.getBool( MigraineRepo.Id.SOUNDPHOBIA );
        final boolean NAUSEA = this.getBool( MigraineRepo.Id.HADNAUSEA );
        int mainCriteria = this.calcSumOf( new MigraineRepo.Id[] {
                                MigraineRepo.Id.ISCEPHALEAONESIDED,
                                MigraineRepo.Id.ISPULSATING,
                                MigraineRepo.Id.ISMIGRAINEINTENSE,
                                MigraineRepo.Id.EXERCISEWORSENS });

        if ( mainCriteria == 4 ) {
            toret = ( ( NAUSEA || ( PHOTO && SOUND ) )
                    || ( NAUSEA && PHOTO )
                    || ( NAUSEA && SOUND )
                    || SOUND );
        }
        else
        if ( mainCriteria == 2
          || mainCriteria == 3 )
        {
            toret = ( NAUSEA
                    || ( PHOTO && SOUND )
                    || ( NAUSEA && PHOTO )
                    || ( NAUSEA && SOUND )
                    || PHOTO
                    || SOUND );
        }
        else
        if ( mainCriteria == 1 ) {
            toret = ( this.REPO.isFemale()
              && this.getBool( MigraineRepo.Id.HASHISTORY )
              && this.getBool( MigraineRepo.Id.MENSTRUATIONWORSENS )
              && this.getBool( MigraineRepo.Id.CONTRACEPTIVESWORSENS ) );
        }

        return toret;
    }

    /** return true if the patient has tensional cephaleas, false otherwise. */
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

    /** @return true if the patient has tensional cephalics or migraines, false otherwise. */
    public boolean isMixed()
    {
        return this.isMigraine() || this.isTensional();
    }

    /** @return a MixedCephalea value of:
      *     NOT_MIXED if the cephalea is not mixed between migrain and tensional.
      *     MIXED_PREDOMINANT_TENSIONAL if the cephalea is mixed and tensional predominant.
      *     MIXED_PREDOMINANT_MIGRAINE if the cephalea is mixed and migraine predominant.
      */
    public MixedCephalea getMixedPredominant()
    {
        MixedCephalea toret = MixedCephalea.NOT_MIXED;

        if ( this.isMixed() ) {
            toret = MixedCephalea.MIXED_PREDOMINANT_TENSIONAL;

            if ( this.REPO.getNumMigraines() > this.REPO.getNumTensionalCephaleas() )
            {
                toret = MixedCephalea.MIXED_PREDOMINANT_MIGRAINE;
            }
        }

        return toret;
    }

    /** @return a string describing the mixed cephalea of the parameter.
      * @param mixedCephalea the type of mixed cephalea.
      */
    private String stringFromMixedPredominant(MixedCephalea mixedCephalea)
    {
        String toret = "cefalea";

        if ( mixedCephalea != MixedCephalea.NOT_MIXED ) {
            toret = "cefalea mixta con predominio ";

            if ( mixedCephalea == MixedCephalea.MIXED_PREDOMINANT_MIGRAINE ) {
                toret += "migrañoso";
            } else {
                toret += "tensional";
            }
        }

        return toret;
    }

    /** @return a string describing the mixed cephalea. */
    public String stringFromMixedPredominant()
    {
        return this.stringFromMixedPredominant( this.getMixedPredominant() );
    }

    public boolean shouldCheckDoctor()
    {
        int mainCriteria = this.calcSumOf( new MigraineRepo.Id[] {
                MigraineRepo.Id.ISCEPHALEAONESIDED,
                MigraineRepo.Id.ISPULSATING,
                MigraineRepo.Id.ISMIGRAINEINTENSE,
                MigraineRepo.Id.EXERCISEWORSENS
        });

        return ( !this.isMigraine()
              && this.REPO.isMale()
              && mainCriteria >= 1
              && this.getBool( MigraineRepo.Id.HASHISTORY ) );
    }

    /** @return a boolean value given its id, assuming not existing means "false" */
    private boolean getBool(MigraineRepo.Id id)
    {
        boolean toret = false;

        if ( this.REPO.exists( id ) ) {
            toret = this.REPO.getBool( id );
        } else {
            Log.e( LOG_TAG, "missing from REPO " + id );
        }

        return toret;
    }

    /** @return an int value given its id, assuming not existing means "false" */
    private int getInt(MigraineRepo.Id id)
    {
        int toret = 0;

        if ( this.REPO.exists( id ) ) {
            toret = this.REPO.getInt( id );
            Log.e( LOG_TAG, "missing from REPO " + id );
        }

        return toret;
    }

    @Override
    public String toString()
    {
        final StringBuilder TORET = new StringBuilder();
        final boolean IS_MIGRAINE = this.isMigraine();
        final boolean IS_TENSIONAL = this.isTensional();
        final boolean IS_MIXED = this.isMixed();
        MigraineRepo.Frequency freq;

        // No cephalea
        if ( !IS_MIGRAINE
          && !IS_TENSIONAL )
        {
            TORET.append( "<b>Sin evidencias de cefaleas</b>." );
        } else {
            TORET.append( "<b>" );
            // Kind of cephalea
            if ( IS_MIXED ) {
                TORET.append( this.stringFromMixedPredominant() );
            }
            else
            if ( IS_MIGRAINE ) {
                TORET.append( "migraña" );
            }
            else
            if ( IS_TENSIONAL ) {
                TORET.append( "cefalea tensional" );
            }

            // Had an aura
            if ( IS_MIGRAINE
              && this.REPO.hadAura() )
            {
                TORET.append( " con aura" );
            }

            // Frequency
            if ( IS_MIXED ) {
                freq = this.REPO.getMixedFreq();
            }
            else
            if ( IS_MIGRAINE ) {
                freq = this.REPO.getMigraineFreq();
            } else {
                freq = this.REPO.getTensionalFreq();
            }

            TORET.append( ' ' );
            TORET.append( freq.toString() );
            TORET.append( "</b><i>" );

            // You should ask your doctor
            if ( IS_MIXED
              || IS_MIGRAINE )
            {
                if ( !this.REPO.getBool( MigraineRepo.Id.HADMORETHANFIVEEPISODES ) ) {
                    TORET.append( '\n' );
                    TORET.append( " (Probable, migraña de menos de cinco episodios. " );
                    TORET.append( "Consulte con su médico.)" );
                    TORET.append( '\n' );
                }

                if ( !this.REPO.getBool( MigraineRepo.Id.MIGRAINEDURATION ) ) {
                    TORET.append( '\n' );
                    TORET.append( " (No cumple con la duración de un episodio de migraña. " );
                    TORET.append( "Consulte con su médico.)" );
                    TORET.append( '\n' );
                }
            }

            if ( this.shouldCheckDoctor() ) {
                TORET.append( '\n' );
                TORET.append( " (Síntoms relevantes. Consulte con su médico.)" );
                TORET.append( '\n' );
            }

            TORET.append( "</i>" );
        }

        return TORET.toString();
    }

    final private MigraineRepo REPO;
}
