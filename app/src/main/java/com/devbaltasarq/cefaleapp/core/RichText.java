// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import android.text.Spanned;
import android.text.SpannedString;
import androidx.core.text.HtmlCompat;


/** This is a string that can be shown conveniently in Android's textboxes. */
public class RichText {
    /** Creates a new rich text string.
      * @param txt The text with html tags to convert to Spanned.
      */
    public RichText(String txt)
    {
        this.txt = new SpannedString(
                            HtmlCompat.fromHtml(
                                        txt,
                                        HtmlCompat.FROM_HTML_MODE_LEGACY ) );
    }

    /** @return the rich text string
      * understandable by Android's TextView, as Spanned.
      */
    public Spanned get()
    {
        return this.txt;
    }

    @Override
    public String toString()
    {
        return this.txt.toString();
    }

    /** Formats plain text, so it's shown reliably as HTML.
      * @param txt plain text.
      * @return rich text, as spanned.
      */
    public static Spanned formatText(String txt)
    {
        return new RichText(
                        txt.replace( ".", ".<br/>" )
                            .replace( ":", ":<br/>" )
                            .replace( "-", "&mdash;" )).get();
    }

    public final SpannedString txt;
}
