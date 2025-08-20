// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import java.util.Locale;


/** Provides support for language identification. */
public enum Language {
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
    static public Language langFromDefaultLocale()
    {
        return langFromLocale( Locale.getDefault() );
    }

    /** Gets the Language instance corresponding to a given Locale.
      * @param l the given Locale.
      * @return the corresponding Language instance.
      * @see Language::langFromLanguageCode
      */
    static public Language langFromLocale(Locale l)
    {
        return langFromLanguageCode( l.getLanguage() );
    }

    /** Gets the Language instance corresponding to a language code.
      * @param languageCode the ISO 639 language code, like "es"
      * @return a Language instance. It never returns null.
      */
    static public Language langFromLanguageCode(String languageCode)
    {
        Language toret = null;

        try {
            toret = valueOf( languageCode );
        } catch (IllegalArgumentException e) {
            toret = en;

            if ( languageCode.equals( GALICIAN ) ) {
                toret = es;
            }
        }

        return toret;
    }

    static public int size()
    {
        return values().length;
    }

    static public final String GALICIAN = "gl";
    static public final String PORTUGUESE = "pt";
    static public final String SPANISH = "es";

    private final Locale locale;
}
