// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import java.util.Locale;


/** Provides support for language identification. */
public enum Language {
    invariant( Locale.ROOT ),
    es( new Locale( "es" ) ),
    en( Locale.ENGLISH ),
    pt( new Locale( "pt" ));

    Language(Locale l)
    {
        this.locale = l;
    }

    /** @return the corresponding Java Locale for this language. */
    public Locale getLocale()
    {
        return this.locale;
    }

    /** @return the ISO 639 code for this language. */
    public String getISOLanguage()
    {
        return this.locale.getLanguage();
    }

    @Override
    public String toString()
    {
        return this.getISOLanguage();
    }

    /** Gets the Language instance corresponding to the current Locale.
     * @return the corresponding Language instance.
     * @see Language::langFromLocale
     */
    public static Language langFromDefaultLocale()
    {
        return langFromLocale( Locale.getDefault() );
    }

    /** Gets the Language instance corresponding to a given Locale.
      * @param l the given Locale.
      * @return the corresponding Language instance.
      * @see Language::langFromLanguageCode
      */
    public static Language langFromLocale(Locale l)
    {
        return langFromLanguageCode( l.getLanguage() );
    }

    /** Gets the Language instance corresponding to a language code.
      * @param languageCode the ISO 639 language code, like "es"
      * @return a Language instance, or the invariant if not found.
      */
    public static Language langFromLanguageCode(String languageCode)
    {
        Language toret = Language.invariant;

        try {
            toret = valueOf( languageCode );
        } catch (IllegalArgumentException e) {
            if ( languageCode.equals( GALICIAN ) ) {
                toret = es;
            }
        }

        return toret;
    }

    public static int size()
    {
        return values().length;
    }

    public static final String INVARIANT = "invariant";
    public static final String SPANISH = "es";
    public static final String ENGLISH = "en";
    public static final String PORTUGUESE = "pt";
    public static final String GALICIAN = "gl";

    private final Locale locale;
}
