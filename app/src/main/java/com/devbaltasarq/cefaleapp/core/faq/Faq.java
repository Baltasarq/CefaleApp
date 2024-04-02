// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.faq;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/** Represents a FAQ entry with its question and answer. */
public class Faq {
    /** Creates a new FAQ entry.
      * @param id the id of this entry.
      * @param q the question of this entry.
      * @param a the answer of this entry.
      */
    public Faq(String lang, String id, String q, String a)
    {
        assert lang != null && !lang.isBlank(): "missing FAQ's id";
        assert id != null && !id.isBlank(): "missing FAQ's id";
        assert q != null && !q.isBlank(): "missing FAQ's question";
        assert a != null && !a.isBlank(): "missing FAQ's answer";

        this.lang = lang;
        this.id = id.trim();
        this.q = q.trim();
        this.a = a.trim();
    }

    /** @return the id of this faq entry. */
    public String getId()
    {
        return this.id;
    }

    /** @return the question of this faq entry. */
    public String getQuestion()
    {
        return this.q;
    }

    /** @return the answer of this faq entry. */
    public String getAnswer()
    {
        return this.a;
    }

    /** @return the lang for this faq entry. */
    public String getLang()
    {
        return this.lang;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean toret = false;

        if ( this == o ) {
            toret = true;
        }
        else
        if ( o instanceof Faq other ) {
            toret = Objects.equals( this.getId(), other.getId() )
                    && Objects.equals( this.getQuestion(), other.getQuestion() )
                    && Objects.equals( this.getAnswer(), other.getAnswer() );
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.getId(), this.getQuestion(), this.getAnswer() );
    }

    public static void setAll(Map<String, Map<String, Faq>> allFaqs)
    {
        if ( all == null ) {
            all = new HashMap<>( allFaqs.size() );
        }

        all.clear();
        all.putAll( allFaqs );
    }

    /** Returns the FAQ entries for a given language.
      * @param lang the language code, such as "es", "fr"...
      * @return a Map<String, WebUrl> FAQ entries for that language.
      */
    public static Map<String, Faq> getForLang(String lang)
    {
        if ( all == null ) {
            throw new Error( "FAQ are not yet loaded." );
        }

        Map<String, Faq> toret = all.get( lang );

        // Default to es - spanish
        if ( toret == null ) {
            toret = Objects.requireNonNull( all.get( "es" ) );
        }

        return new HashMap<>( toret );
    }

    /** @return a set of all the available languages,
                such as ["es", "en"...]
      */
    public static Set<String> getAvailableLanguages()
    {
        return new HashSet<>( all.keySet() );
    }

    private final String lang;
    private final String id;
    private final String q;
    private final String a;
    private static Map<String, Map<String, Faq>> all = null;
}
