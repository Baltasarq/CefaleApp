// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.links;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/** Represents a FAQ entry with its question and answer. */
public class WebUrl {
    /** Creates a new WebUrl entry.
      * @param id the id of this entry.
      * @param src the source of this entry.
      * @param url the answer of this entry.
      * @param desc the description of this entry.
      */
    public WebUrl(String id, String src, String url, String desc)
    {
        assert id != null && !id.isBlank(): "missing WebURL's id";
        assert src != null && !src.isBlank(): "missing WebURL's source";
        assert url != null && !url.isBlank(): "missing WebURL's url";
        assert desc != null && !desc.isBlank(): "missing WebURL's desc";

        this.id = id.trim();
        this.src = src.trim();
        this.url = url.trim();
        this.desc = desc;
    }

    /** @return the id of this faq entry. */
    public String getId()
    {
        return this.id;
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
                    && Objects.equals( this.getUrl(), other.getUrl() );
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.getId(), this.getSource(), this.getUrl() );
    }

    public static void setAll(Map<String, WebUrl> allWebUrls)
    {
        if ( all == null ) {
            all = new HashMap<>( allWebUrls.size() );
        }

        all.clear();
        all.putAll( allWebUrls );
    }

    /** @return a Map<String, WebUrl> with all WebUrl's entries. */
    public static Map<String, WebUrl> getAll()
    {
        if ( all == null ) {
            throw new Error( "FAQ are not yet loaded." );
        }

        return new HashMap<>( all );
    }

    private final String desc;
    private final String id;
    private final String src;
    private final String url;
    private static Map<String, WebUrl> all = null;
}
