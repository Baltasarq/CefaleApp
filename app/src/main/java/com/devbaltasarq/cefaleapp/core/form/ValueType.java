// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;


import java.util.ArrayList;
import java.util.List;


public enum ValueType { BOOL, INT, STR;
    /** @return the list of values, but as a list of strings. */
    public static List<String> valuesAsString()
    {
        final ValueType[] VALUES = ValueType.values();
        final List<String> TORET = new ArrayList<>( VALUES.length );

        for(final ValueType VALUE: VALUES) {
            TORET.add( VALUE.toString() );
        }

        return TORET;
    }

    /** Returns the equivalent value to the passed string,
     *  as in "BOOL" -> DataType.BOOL.
     *
     * @param str a string version of a DataType value, such as "BOOL".
     * @return a DataType if the value is recognized, throws otherwise.
     */
    public static ValueType parse(String str) throws IllegalArgumentException
    {
        final List<String> VALUES_AS_STR = valuesAsString();
        ValueType toret = null;

        int i = 0;
        str = str.trim().toUpperCase();

        for(String value: VALUES_AS_STR) {
            if ( str.equals( value ) ) {
                toret = values()[ i ];
            }

            ++i;
        }

        if ( toret == null ) {
            throw new IllegalArgumentException( "unrecognized as DataType: " + str );
        }

        return toret;
    }
}
