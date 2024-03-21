// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import android.content.Context;
import android.content.Intent;
import android.text.Spanned;

import androidx.core.text.HtmlCompat;

import com.devbaltasarq.cefaleapp.core.treatment.BasicId;
import com.devbaltasarq.cefaleapp.core.treatment.Identifiable;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.Nameable;
import com.devbaltasarq.cefaleapp.ui.treatment.MedicineActivity;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


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

    /** Shos the medicine activity with the given medicine loaded.
      * @param CTX the activity this call is originated.
      * @param M the given medicine to show.
      */
    public static void showMedicine(final Context CTX, final Medicine M)
    {
        final Intent INTENT = new Intent( CTX, MedicineActivity.class );

        MedicineActivity.medicine = M;
        CTX.startActivity( INTENT );
    }

    /** Sorts identifiables (medicines, morbidities...) taking into account locales.
      * @param IDENTIFIABLES the medicine list to sort.
      */
    public static void sortIdentifiableI18n(final List<? extends Identifiable> IDENTIFIABLES)
    {
        final Collator COLLATOR = Collator.getInstance( new Locale("es","ES" ));

        IDENTIFIABLES.sort( (m1, m2) ->
                            COLLATOR.compare(
                                    m1.getId().getName(),
                                    m2.getId().getName() ) );
    }

    /** Generates a new list with all corresponding objects.
      * @param ALL the collection of all objects.
      * @param IDS the collection of all ids.
      * @return a new list with the objects corresponding to the given identifiers.
      * @param <T> The identifiable objects, i.e. Mordbity, Medicine...
      * @param <U> The id object, i.e. Medicine.Id, Morbidity.Id...
      */
    public static <T extends Identifiable, U extends Nameable> List<T > getObjListFromIdList(
                                        final Map<U, T> ALL,
                                        final Collection<U> IDS)
    {
        return IDS.stream().map( id
                                    -> Objects.requireNonNull( ALL.get( id ) ) )
                           .collect( Collectors.toList() );
    }
}
