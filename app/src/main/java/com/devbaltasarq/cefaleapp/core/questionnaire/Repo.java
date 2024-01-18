// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import androidx.annotation.NonNull;

import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/** Stores the information gathered from the patient. */
public final class Repo {
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
        HADPHOTOPHOBIA,
        HADNAUSEA,

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

    private Repo()
    {
        this.data = new EnumMap<>( Id.class );
    }

    public void reset()
    {
        this.data.clear();
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

    public String getStr(@NonNull Id id) throws NoSuchElementException
    {
        final Value TORET = this.getValue( id );

        if ( TORET == null ) {
            throw new NoSuchElementException( id.toString() );
        }

        return (String) TORET.get();
    }

    public int getInt(@NonNull Id id) throws NoSuchElementException
    {
        final Value TORET = this.getValue( id );

        if ( TORET == null ) {
            throw new NoSuchElementException( id.toString() );
        }

        return (int) TORET.get();
    }

    public boolean getBool(@NonNull Id id) throws NoSuchElementException
    {
        final Value TORET = this.getValue( id );

        if ( TORET == null ) {
            throw new NoSuchElementException( id.toString() );
        }

        return (boolean) TORET.get();
    }

    public static Repo get()
    {
        if ( repo == null ) {
            repo = new Repo();
        }

        return repo;
    }

    private static Repo repo;

    private final Map<Id, Value> data;
}
