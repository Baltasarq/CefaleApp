// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/** Gives multi language support. It is expected to have a Map<String, T>,
  *  in which T can be whatever data type.
  *  @param <T> The type, normally a container, paired with each language.
  */
public class MultiLanguageWrapper<T> {
    public enum Lang { es, en, gal }

    public MultiLanguageWrapper(Map<String, T> data)
    {
        this.data = Objects.requireNonNull( data );
    }

    public Set<String> getAvailableLanguages()
    {
        return new HashSet<>( this.data.keySet() );
    }

    /** Returns the FAQ entries for a given language.
     * @param lang the language code, in the enumerator format.
     * @return a Map<String, WebUrl> FAQ entries for that language.
     */
    public T getForLang(Lang lang)
    {
        T toret = this.data.get( lang.toString() );

        // Default to es - spanish
        if ( toret == null ) {
            toret = Objects.requireNonNull( this.data.get( "es" ) );
        }

        return toret;
    }

    private Map<String, T> data = null;
}
