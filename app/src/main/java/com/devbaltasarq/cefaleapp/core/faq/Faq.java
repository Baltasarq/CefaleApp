// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.faq;


import com.devbaltasarq.cefaleapp.core.MultiLanguageWrapper;

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
        all = new MultiLanguageWrapper<>( allFaqs );
    }

    public static MultiLanguageWrapper<Map<String, Faq>> getAll()
    {
        if ( all == null ) {
            throw new Error( "FAQ entries not loaded yet" );
        }

        return all;
    }

    private final String lang;
    private final String id;
    private final String q;
    private final String a;
    private static MultiLanguageWrapper<Map<String, Faq>> all = null;
}
