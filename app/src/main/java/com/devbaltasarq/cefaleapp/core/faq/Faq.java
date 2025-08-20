// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.faq;


import com.devbaltasarq.cefaleapp.core.LocalizedText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/** Represents a FAQ entry with its question and answer. */
public class Faq {
    /** Creates a new FAQ entry.
      * @param id the id of this entry.
      * @param q the question of this entry.
      * @param a the answer of this entry.
      * @see LocalizedText support for L10n text.
      */
    public Faq(String id, LocalizedText q, LocalizedText a)
    {
        assert id != null && !id.isBlank(): "missing FAQ's id";
        assert q != null: "missing l10n texts for the question";
        assert a != null: "missing l10n texts for the answer";

        this.id = id.trim();
        this.q = q;
        this.a = a;
    }

    /** @return the id of this faq entry. */
    public String getId()
    {
        return this.id;
    }

    /** @return the question of this faq entry. */
    public LocalizedText getQuestion()
    {
        return this.q;
    }

    /** @return the answer of this faq entry. */
    public LocalizedText getAnswer()
    {
        return this.a;
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

    public static void setAll(Map<String, Faq> allFaqs)
    {
        all = new HashMap<>( allFaqs );
    }

    public static Map<String, Faq> getAll()
    {
        if ( all == null ) {
            throw new Error( "FAQ entries not loaded yet" );
        }

        return all;
    }

    private final String id;
    private final LocalizedText q;
    private final LocalizedText a;
    private static Map<String, Faq> all = null;
}
