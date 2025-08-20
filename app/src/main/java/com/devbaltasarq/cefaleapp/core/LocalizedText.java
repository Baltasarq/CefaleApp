// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/** Represents text in various languages.
  * Each text is identified by a Language.
  * @see Language
 */
public class LocalizedText {
    public LocalizedText()
    {
        this.texts = new HashMap<>();
    }

    /** Adds a new text associated to a given language.
      * @param lang a Language instance, identifying the language.
      * @param text the text corresponding to the language given.
      * @see Language
      * @throws Error if a text already exists for that language.
     */
    public void add(Language lang, String text)
    {
        String contents = this.texts.get( lang );

        text = text.trim();

        if ( text.isEmpty() ) {
            throw new Error( "nonsense: l10n text empty for: " + lang );
        }

        if ( contents == null ) {
            this.texts.put( lang, text.trim() );
        } else {
            throw new Error(
                        "Found text for lang: '"
                        + lang
                        + "' -> "
                        + contents );
        }

        return;
    }

    /** @return Get the version of the text for a given language.
      * @param lang the language for the expected text.
      * @throws IllegalArgumentException if the text doesn't exist.
      */
    public String get(Language lang)
    {
        String toret = this.texts.get( lang );

        if ( toret == null ) {
            throw new IllegalArgumentException(
                            "L18nText: get() no text for lang: " + lang );
        }

        return toret;
    }

    /** @return an iterable that holds all available languages. */
    public Set<Language> getAvailableLanguages()
    {
        return new HashSet<>( this.texts.keySet() );
    }

    @Override
    public boolean equals(Object lt2)
    {
        boolean toret = false;

        if ( lt2 instanceof LocalizedText lt ) {
            final Set<Language> KEYS1 = this.getAvailableLanguages();
            final Set<Language> KEYS2 = lt.getAvailableLanguages();

            toret = KEYS1.size() == KEYS2.size()
                    && KEYS1.containsAll( KEYS2 )
                    && KEYS2.containsAll( KEYS1 );

            if ( toret ) {
                for(final Language LANG: this.getAvailableLanguages()) {
                    final String TEXT1 = this.get( LANG );
                    final String TEXT2 = lt.get( LANG );

                    if ( !TEXT1.equals( TEXT2 ) ) {
                        toret = false;
                        break;
                    }
                }
            }
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        int toret = 0;

        for(Map.Entry<Language, String> entry: this.texts.entrySet()) {
            toret += 11 * ( entry.getKey().hashCode()
                            + entry.getValue().hashCode() );
        }

        return toret;
    }

    /** @return the english localized version of this text, if present. */
    @Override
    public String toString()
    {
        String toret = "!!";

        if ( this.texts.containsKey( Language.en ) ) {
            toret = this.get( Language.en );
        }

        return toret;
    }

    private final Map<Language, String> texts;
}
