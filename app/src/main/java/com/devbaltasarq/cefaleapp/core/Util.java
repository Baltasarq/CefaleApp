// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import android.text.Spanned;

import androidx.core.text.HtmlCompat;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


public class Util {
    /** @return a serial string, based on the ISO-8601 form: YYYY-MM-DD. */
    public static String buildSerial()
    {
        final Calendar TODAY = Calendar.getInstance();

        return String.format( Locale.getDefault(), "%04d%02d%02d%02d%02d%02d%03d",
                TODAY.get( Calendar.YEAR ),
                TODAY.get( Calendar.MONTH ),
                TODAY.get( Calendar.DAY_OF_MONTH ),
                TODAY.get( Calendar.HOUR ),
                TODAY.get( Calendar.MINUTE ),
                TODAY.get( Calendar.SECOND ),
                TODAY.get( Calendar.MILLISECOND ));
    }

    public static String getXMLAttributeOrThrow(final Element ELEMENT, final String ETQ_ATTR)
            throws IOException
    {
        String toret = null;

        if ( ELEMENT != null ) {
            toret = ELEMENT.getAttribute( ETQ_ATTR );

            if ( toret == null ) {
                throw new IOException( "XML attribute: '"
                        + ETQ_ATTR + "' not found in: "
                        + ELEMENT.getTagName() );
            }

            toret = toret.trim();
            if ( toret.isEmpty() ) {
                throw new IOException( "XML attribute: '"
                                        + ETQ_ATTR + "' empty in: "
                                        + ELEMENT.getTagName() );
            }
        } else {
            throw new IOException( "XML attribute: "
                                    + ETQ_ATTR + " searched in null element" );
        }

        return toret;
    }

    /** Get one sub element with the given name.
      * @param ELEMENT the element to look for the subelement in.
      * @param ETQ_ELEM the name of the subelement.
      * @return null if no subelement found, or the first one otherwise.
      */
    public static Element getXMLSubElement(final Element ELEMENT, final String ETQ_ELEM)
    {
        final NodeList NODE_LIST = ELEMENT.getElementsByTagName( ETQ_ELEM );
        Element toret = null;

        if ( NODE_LIST.getLength() > 0 ) {
            final Node NODE = NODE_LIST.item( 0 );

            if ( NODE instanceof final Element SUB_ELEMENT ) {
                toret = SUB_ELEMENT;
            }
        }

        return toret;
    }

    /** Converts HTML to rich text understandable
     * by Android's TextView.
     * @param txt The text with html tags to convert.
     * @return Spanned text.
     */
    public static Spanned richTextFromHtml(String txt)
    {
        return HtmlCompat.fromHtml( txt, HtmlCompat.FROM_HTML_MODE_LEGACY );
    }

    public static Spanned formatText(String txt)
    {
        return richTextFromHtml(
                    txt.replace( ".", ".<br/>" )
                        .replace( ":", ":<br/>" )
                        .replace( "-", "&mdash;" ));
    }
}
