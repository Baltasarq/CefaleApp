// CefaleApp (c) 2026 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import android.os.Build;
import java.util.Locale;


public class LocaleCompat {
    public enum Id {
        ES, EN, PT, INVARIANT
    }

    @SuppressWarnings("deprecation")
    public static Locale of(Id id)
    {
        Locale toret = null;

        switch( id ) {
            case INVARIANT -> toret = Locale.ROOT;
            case ES -> {
                if ( Build.VERSION.SDK_INT >= 36 ) {
                    toret = Locale.of( "es" );
                } else {
                    toret = new Locale( "es" );
                }
            }
            case EN -> toret = Locale.ENGLISH;
            case PT -> {
                if ( Build.VERSION.SDK_INT >= 36 ) {
                    toret = Locale.of( "pt" );
                } else {
                    toret = new Locale( "pt" );
                }
            }
            default -> throw new Error( "Missing Id: " + id );
        }

        return toret;
    }
}
