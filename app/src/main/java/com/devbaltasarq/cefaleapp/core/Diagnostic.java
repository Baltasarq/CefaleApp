// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import android.util.Log;


public class Diagnostic {
    private static final String LOG_TAG = Diagnostic.class.getSimpleName();

    public enum Frequency { ESPORADIC, LOW, HIGH, CHRONIC;
        @Override
        public String toString()
        {
            return STR_FREQ[ this.ordinal() ];
        }

        private static final String[] STR_FREQ = {
                "esporádica",
                "de baja frecuencia",
                "de alta frecuencia",
                "crónica"
        };
    }

    public Diagnostic(final Repo REPO)
    {
        this.REPO = REPO;
    }

    /** Determines the total of various bool questions.
      * @param ids the ids of the questions involved.
      * @return the number of them that are true.
      */
    private int calcSumOf(Repo.Id[] ids)
    {
        int toret = 0;

        for(Repo.Id id: ids) {
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
        return this.calcSumOf( new Repo.Id[] { Repo.Id.WASCEPHALEALIMITANT,
                                        Repo.Id.HADPHOTOPHOBIA,
                                        Repo.Id.HADNAUSEA } );
    }

    /** PCD_HOW_MANY_MIGRAINE_1151 and PCD-HOW_MANY_TENSIONAL-1147
      * - 1 to 3 days with migraine is labelled as "occasional" and it's no treated.
      * - 4 to 7 days with migraine is labelled as "low frequency"
      * - 8 to 14 days with migraine is labelled as "high frequency"
      * - 15 or more days with migraine is labelled as "chronic frequency"
      * @return a Frequency depending in the criteria above.
      */
    private Frequency frequencyFromEpisodes(int episodes)
    {
        Frequency toret = Frequency.ESPORADIC;

        if ( episodes >= 15 ) {
            toret = Frequency.CHRONIC;
        }
        else
        if ( episodes >= 8 ) {
            toret = Frequency.HIGH;
        }
        else
        if ( episodes >= 4 ) {
            toret = Frequency.LOW;
        }

        return toret;
    }

    /** @see Diagnostic::frequencyFromEpisodes
     * @return a Frequency for mixed migraines diagnostic,
     *         depending on the criteria above.
     */
    private Frequency getMixedFreq()
    {
        return this.frequencyFromEpisodes(
                this.getInt( Repo.Id.HOWMANYMIGRAINE )
                + this.getInt( Repo.Id.HOWMANYTENSIONAL ) );
    }

    /** @see Diagnostic::frequencyFromEpisodes
      * @return a Frequency for migraines diagnostic,
      *         depending on the criteria above.
      */
    private Frequency getMigraineFreq()
    {
        return this.frequencyFromEpisodes(
                    this.getInt( Repo.Id.HOWMANYMIGRAINE ) );
    }

    /** @see Diagnostic::frequencyFromEpisodes
      * @return a Frequency for tensional cephaleas diagnostic,
      *         following the above criteria.
      */
    private Frequency getTensionalFreq()
    {
        return this.frequencyFromEpisodes(
                this.getInt( Repo.Id.HOWMANYTENSIONAL ) );
    }

    /** return true if the patient has migraines, false otherwise. */
    private boolean isMigraine()
    {
        boolean toret = false;
        final boolean PHOTO = this.getBool( Repo.Id.HADPHOTOPHOBIA );
        final boolean SOUND = this.getBool( Repo.Id.SOUNDPHOBIA );
        final boolean NAUSEA = this.getBool( Repo.Id.HADNAUSEA );
        int mainCriteria = this.calcSumOf( new Repo.Id[] {
                                Repo.Id.ISCEPHALEAONESIDED,
                                Repo.Id.ISPULSATING,
                                Repo.Id.ISMIGRAINEINTENSE,
                                Repo.Id.EXERCISEWORSENS
        });

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
            toret = ( this.isFemale()
              && this.getBool( Repo.Id.HASHISTORY )
              && this.getBool( Repo.Id.MENSTRUATIONWORSENS )
              && this.getBool( Repo.Id.CONTRACEPTIVESWORSENS ) );
        }

        return toret;
    }

    public boolean shouldManCheckedDoctor()
    {
        int mainCriteria = this.calcSumOf( new Repo.Id[] {
                Repo.Id.ISCEPHALEAONESIDED,
                Repo.Id.ISPULSATING,
                Repo.Id.ISMIGRAINEINTENSE,
                Repo.Id.EXERCISEWORSENS
        });

        return ( !this.isMigraine()
              && this.isMale()
              && mainCriteria >= 1
              && this.getBool( Repo.Id.HASHISTORY ) );
    }

    /** return true if the patient has tensional cephaleas, false otherwise. */
    private boolean isTensional()
    {
        boolean toret = this.calcSumOf( new Repo.Id[]{
                                Repo.Id.ISSTABBING,
                                Repo.Id.ISTENSIONALWORSEONAFTERNOONS,
                                Repo.Id.ISTENSIONALRELATEDTOSTRESS,
                                Repo.Id.ISTENSIONALBETTERWHENDISTRACTED,
                                Repo.Id.SOUNDPHOBIA,
                                Repo.Id.INSOMNIA }) > 1;

        if ( !toret ) {
            toret = this.getBool( Repo.Id.WHOLEHEAD )
                    && this.getBool( Repo.Id.ISCEPHALEAHELMET )
                    && !this.getBool( Repo.Id.ISTENSIONALINTENSE )
                    && this.getBool( Repo.Id.SAD )
                    && this.getBool( Repo.Id.ISDEPRESSED );
        }

        return toret;
    }

    public boolean isMale()
    {
        return !this.getBool( Repo.Id.GENDER );
    }

    public boolean isFemale()
    {
        return this.getBool( Repo.Id.GENDER );
    }

    /** @return whether the patient had an aura or not. */
    private boolean hadAura()
    {
        return this.getBool( Repo.Id.HADAURA );
    }

    /** @return a boolean value given its id. */
    private boolean getBool(Repo.Id id)
    {
        boolean toret = false;

        if ( this.REPO.exists( id ) ) {
            toret = this.REPO.getBool( id );
        } else {
            Log.e( LOG_TAG, "missing from REPO " + id );
        }

        return toret;
    }

    private int getInt(Repo.Id id)
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
        final boolean IS_MIXED = IS_MIGRAINE && IS_TENSIONAL;
        Frequency freq;

        // No cephalea
        if ( !IS_MIGRAINE
          && !IS_TENSIONAL )
        {
            TORET.append( "<b>Sin evidencias de cefaleas</b>." );
        } else {
            TORET.append( "<b>" );
            // Kind of cephalea
            if ( IS_MIXED ) {
                TORET.append( "cefalea mixta" );
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
              && this.hadAura() )
            {
                TORET.append( " con aura" );
            }

            // Frequency
            if ( IS_MIXED ) {
                freq = this.getMixedFreq();
            }
            else
            if ( IS_MIGRAINE ) {
                freq = this.getMigraineFreq();
            } else {
                freq = this.getTensionalFreq();
            }

            TORET.append( ' ' );
            TORET.append( freq.toString() );
            TORET.append( "</b><i>" );

            // You should ask your doctor
            if ( IS_MIXED
              || IS_MIGRAINE )
            {
                if ( !this.REPO.getBool( Repo.Id.HADMORETHANFIVEEPISODES ) ) {
                    TORET.append( '\n' );
                    TORET.append( " (Probable, migraña de menos de cinco episodios. " );
                    TORET.append( "Consulte con su médico.)" );
                    TORET.append( '\n' );
                }

                if ( !this.REPO.getBool( Repo.Id.MIGRAINEDURATION ) ) {
                    TORET.append( '\n' );
                    TORET.append( " (No cumple con la duración de un episodio de migraña. " );
                    TORET.append( "Consulte con su médico.)" );
                    TORET.append( '\n' );
                }
            }

            if ( this.shouldManCheckedDoctor() ) {
                TORET.append( '\n' );
                TORET.append( " (Síntoms relevantes. Consulte con su médico.)" );
                TORET.append( '\n' );
            }

            TORET.append( "</i>" );
        }

        return TORET.toString();
    }

    final private Repo REPO;
}
