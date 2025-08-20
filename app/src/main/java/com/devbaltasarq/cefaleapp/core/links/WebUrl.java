// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.links;


import com.devbaltasarq.cefaleapp.core.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/** Represents a FAQ entry with its question and answer. */
public class WebUrl {
    /** Creates a new WebUrl entry.
      * @param id the id of this entry.
      * @param lang the language of this link.
      * @param src the source of this entry.
      * @param url the answer of this entry.
      * @param desc the description of this entry.
      */
    public WebUrl(String id, String lang, String src, String url, String desc)
    {
        assert id != null && !id.isBlank(): "missing WebURL's id";
        assert src != null && !src.isBlank(): "missing WebURL's source";
        assert url != null && !url.isBlank(): "missing WebURL's url";
        assert desc != null && !desc.isBlank(): "missing WebURL's desc";


        this.id = id.trim();
        this.lang = Language.valueOf( lang.trim().toLowerCase() );
        this.src = src.trim();
        this.url = url.trim();
        this.desc = desc;
    }

    /** @return the id of this faq entry. */
    public String getId()
    {
        return this.id;
    }

    /** @return the language of this faq entry. */
    public Language getLanguage()
    {
        return this.lang;
    }

    /** @return the source of this faq entry. */
    public String getSource()
    {
        return this.src;
    }

    /** @return the url of this faq entry. */
    public String getUrl()
    {
        return this.url;
    }

    /** @return the desc for this faq entry. */
    public String getDesc()
    {
        return this.desc;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean toret = false;

        if ( this == o ) {
            toret = true;
        }
        else
        if ( o instanceof WebUrl other ) {
            toret = Objects.equals( this.getId(), other.getId() )
                    && Objects.equals( this.getLanguage(), other.getLanguage() );
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(
                        this.getId(),
                        this.getLanguage(),
                        this.getSource(),
                        this.getUrl() );
    }

    public static void setAll(Map<Language, Map<String, WebUrl>> allWebUrls)
    {
        if ( all == null ) {
            all = new HashMap<>( allWebUrls.size() );
        }

        all.clear();
        all.putAll( allWebUrls );
    }

    /** @return a Map with all WebUrl's entries,
      *         indexed by language and by id.
      */
    public static Map<String, WebUrl> getAll(final Language LANG)
    {
        if ( all == null ) {
            throw new Error( "FAQ are not yet loaded." );
        }

        return new HashMap<>( all.get( LANG ) );
    }

    private final String id;
    private final Language lang;
    private final String src;
    private final String url;
    private final String desc;
    private static Map<Language, Map<String, WebUrl>> all = null;
}
