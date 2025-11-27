// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/** Stores the information gathered from the patient. */
public final class MigraineRepo {
    private static final int IMC_OBESITY_LIMIT = 29;
    private static final int IMC_ANOREXIC_LIMIT = 18;
    private static final int PRESSURE_HIGH_SUPERIOR_LIMIT = 140;
    private static final int PRESSURE_HIGH_INFERIOR_LIMIT = 90;
    private static final int PRESSURE_LOW_SUPERIOR_LIMIT = 90;

    private MigraineRepo()
    {
        this( false );
    }

    private MigraineRepo(boolean debug)
    {
        this.debug = debug;
        this.data = new EnumMap<>( Id.class );
    }

    public void reset()
    {
        this.data.clear();
    }

    public boolean isInDebugMode()
    {
        return this.debug;
    }

    /** Stores a data piece in the repo.
      * @param id the identification for the piece of data.
      * @param value an object of the Value class.
      */
    public void setValue(@NonNull Id id, Value value)
    {
        this.data.put( id, value );
    }

    /** @return true if there is a value for id, false otherwise. */
    public boolean exists(@NonNull Id id)
    {
        return ( this.data.get( id ) != null );
    }

    /** @return value (as Object) depending on its existence, null otherwise. */
    public Value getValue(final Id ID)
    {
        return this.data.get( ID );
    }

    /** @return a string value depending on its existence, throws otherwise. */
    public String getStr(@NonNull Id id) throws NoSuchElementException
    {
        Value toret = this.getValue( id );

        if ( toret == null ) {
            if ( this.isInDebugMode() ) {
                throw new NoSuchElementException( id.toString() );
            } else {
                toret = Value.StrDefault;
            }
        }

        return (String) toret.get();
    }

    /** @return an int value depending on its existence, throws otherwise. */
    public int getInt(@NonNull Id id) throws NoSuchElementException
    {
        Value toret = this.getValue( id );

        if ( toret == null ) {
            if ( this.isInDebugMode() ) {
                throw new NoSuchElementException( id.toString() );
            } else {
                toret = Value.IntDefault;
            }
        }

        return (int) toret.get();
    }

    /** @return a bool value depending on its existence, throws otherwise. */
    public boolean getBool(@NonNull Id id) throws NoSuchElementException
    {
        Value toret = this.getValue( id );

        if ( toret == null ) {
            if ( this.isInDebugMode() ) {
                throw new NoSuchElementException( id.toString() );
            } else {
                toret = Value.BoolDefault;
            }
        }

        return (boolean) toret.get();
    }

    /** @return True if there is no data, false otherwise. */
    public boolean isEmpty()
    {
        return this.data.isEmpty();
    }

    /** @return the imc if weight and height are provided, Int.Min otherwise. */
    public int calcIMC()
    {
        int toret = Integer.MIN_VALUE;

        if ( this.exists( Id.HEIGHT )
          && this.exists( Id.WEIGHT ) )
        {
            double height = ( (double) this.getInt( Id.HEIGHT ) ) / 100.0;
            double weight = this.getInt( Id.WEIGHT );

            toret = (int) ( weight / ( height * height ) );
        }

        return toret;
    }

    /** Whether the patient is depressed or not.
      * It also returns false if the depressed data piece is not found.
      * @return true if depressed, false if not or not found.
      */
    public boolean isDepressed()
    {
        return ( this.exists( Id.ISDEPRESSED )
                && this.getBool( Id.ISDEPRESSED ) );
    }

    /** @return whether the patient is obese or not.
      *         false if size data not found, either.
      */
    public boolean isObese()
    {
        int imc = this.calcIMC();

        return ( imc > 0 ) && ( imc > IMC_OBESITY_LIMIT );
    }

    /** @return whether the patient is anorexic or not.
      *         false if size data not found, either.
      */
    public boolean isAnorexic()
    {
        int imc = this.calcIMC();

        return ( imc > 0 ) && ( imc  < IMC_ANOREXIC_LIMIT );
    }

    /** @return whether the patient has hypertension or not.
      *         false if tension data not found, either.
      */
    public boolean hasHyperTension()
    {
        boolean toret = false;

        if ( this.exists( Id.LOWPRESSURE )
          && this.exists( Id.HIGHPRESSURE ))
        {
            int pressureLow = this.getInt( Id.LOWPRESSURE );
            int pressureHigh = this.getInt( Id.HIGHPRESSURE );

            toret = ( pressureHigh >= PRESSURE_HIGH_SUPERIOR_LIMIT
                    && pressureLow >= PRESSURE_LOW_SUPERIOR_LIMIT );
        }

        return toret;
    }

    /** @return whether the patient has hypotension or not.
      *         false if tension data not found, either.
      */
    public boolean hasHypoTension()
    {
        boolean toret = false;

        if ( this.exists( Id.HIGHPRESSURE ) ) {
            int pressureHigh = this.getInt( Id.HIGHPRESSURE );

            toret = ( pressureHigh < PRESSURE_HIGH_INFERIOR_LIMIT );
        }

        return toret;
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

    /** @return the int value for id, or 0 if it does not exist. */
    private int getNumOf(Id id)
    {
        int toret = 0;

        if ( this.exists( id ) ) {
            toret = this.getInt( id );
        }

        return toret;
    }

    /** @return the number of migraine episodes. */
    public int getNumMigraines()
    {
        return this.getNumOf( Id.HOWMANYMIGRAINE );
    }

    /** @return the number of tensional cephaleas. */
    public int getNumTensionalCephaleas()
    {
        return this.getNumOf( Id.HOWMANYTENSIONAL );
    }

    /** @see MigraineRepo::frequencyFromEpisodes
      * @return a Frequency for mixed migraines diagnostic,
      *         depending on the criteria above.
      */
    public Frequency getMixedFreq()
    {
        return this.frequencyFromEpisodes(
                this.getNumMigraines()
                + this.getNumTensionalCephaleas() );
    }

    /** @see MigraineRepo::frequencyFromEpisodes
      * @return a Frequency for migraines diagnostic,
      *         depending on the criteria above.
      */
    public Frequency getMigraineFreq()
    {
        return this.frequencyFromEpisodes( this.getNumMigraines() );
    }

    /** @see MigraineRepo::frequencyFromEpisodes
      * @return a Frequency for tensional cephaleas diagnostic,
      *         following the above criteria.
      */
    public Frequency getTensionalFreq()
    {
        return this.frequencyFromEpisodes( this.getNumTensionalCephaleas() );
    }

    /** @return whether the pain (tensional or migraine) is perceived as intense. */
    public boolean isPainIntense()
    {
        boolean migraineIntense =
                    this.exists( Id.ISMIGRAINEINTENSE )
                    && this.getBool( Id.ISMIGRAINEINTENSE );
        boolean tensionalIntense =
                    this.exists( Id.ISTENSIONALINTENSE )
                    && this.getBool( Id.ISTENSIONALINTENSE );

        return ( migraineIntense || tensionalIntense );
    }

    /** @return true if patient is male, false otherwise. */
    public boolean isMale()
    {
        return !this.getBool( Id.GENDER );
    }

    /** @return true if patient is female, false otherwise. */
    public boolean isFemale()
    {
        return this.getBool( Id.GENDER );
    }

    /** @return whether the patient had an aura or not. */
    public boolean hadAura()
    {
        return this.getBool( Id.HADAURA );
    }

    /** @return whether certain female conditions are present or not. */
    public boolean areFemaleConditionsPresent()
    {
        return this.isFemale()
                && this.getBool( Id.HASHISTORY )
                && ( this.getBool( Id.MENSTRUATIONWORSENS )
                  || this.getBool( Id.CONTRACEPTIVESWORSENS ) );
    }

    public static MigraineRepo get()
    {
        if ( repo == null ) {
            repo = new MigraineRepo();
        }

        return repo;
    }

    private final Map<Id, Value> data;
    private final boolean debug;
    private static MigraineRepo repo;

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

    public enum Id {
        // Data branch
        NOTES,
        GENDER,
        AGE,
        HEIGHT,
        WEIGHT,
        HIGHPRESSURE,
        LOWPRESSURE,
        HASHISTORY,
        FORMORETHANONEYEAR,
        ISDEPRESSED,

        // Screening
        WASCEPHALEALIMITANT,
        PHOTOPHOBIA,
        NAUSEA,

        // Migraine
        HADMORETHANFIVEEPISODES,
        MIGRAINEDURATION,
        ISMIGRAINEINTENSE,
        BETTERINDARKNESS,
        ISCEPHALEAONESIDED,
        ISPULSATING,
        SOUNDPHOBIA,
        EXERCISEWORSENS,
        HADAURA,
        MENSTRUATIONWORSENS,
        CONTRACEPTIVESWORSENS,
        HOWMANYMIGRAINE,

        // Continue
        AREYOUSURE,

        // Tensional
        WHOLEHEAD,
        ISCEPHALEAHELMET,
        ISTENSIONALINTENSE,
        ISSTABBING,
        ISTENSIONALWORSEONAFTERNOONS,
        ISTENSIONALRELATEDTOSTRESS,
        ISTENSIONALBETTERWHENDISTRACTED,
        INSOMNIA,
        SAD,
        SOLVABLE,
        HOWMANYTENSIONAL;

        private static List<String> strValues = null;

        /** @return the list of values, but as a list of strings. */
        public static List<String> valuesAsString()
        {
            if ( strValues == null ) {
                final Id[] VALUES = Id.values();
                strValues = new ArrayList<>( VALUES.length );

                for (final Id ID : VALUES) {
                    strValues.add( ID.toString() );
                }
            }

            return strValues;
        }

        /** Returns the equivalent value to the passed string,
         *  as in "NOTES" -> Id.NOTES.
         *
         * @param str a string version of an Id.DATA value, such as "NOTES".
         * @return an Id if the value is recognized, throws otherwise.
         */
        public static Id parse(String str) throws IllegalArgumentException
        {
            final List<String> VALUES_AS_STR = valuesAsString();
            Id toret = null;

            int i = 0;
            str = str.trim().toUpperCase();

            for(String value: VALUES_AS_STR) {
                if ( str.equals( value ) ) {
                    toret = values()[ i ];
                    break;
                }

                ++i;
            }

            if ( toret == null ) {
                throw new IllegalArgumentException( "unrecognized as Id: " + str );
            }

            return toret;
        }
    }
}
