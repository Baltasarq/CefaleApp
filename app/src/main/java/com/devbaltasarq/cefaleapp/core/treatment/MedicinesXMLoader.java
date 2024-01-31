// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment;


import androidx.annotation.NonNull;

import com.devbaltasarq.cefaleapp.core.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/** XML Loader for all medicines. */
public class MedicinesXMLoader {
    private static final String ETQ_MEDICINE = "medicine";
    private static final String ETQ_ID = "id";
    private static final String ETQ_GROUP_ID = "groupId";
    private static final String ETQ_URL = "url";
    private static final String ETQ_UNITS = "units";
    private static final String ETQ_MIN_DOSAGE = "minDosage";
    private static final String ETQ_REC_DOSAGE = "recDosage";
    private static final String ETQ_MAX_DOSAGE = "maxDosage";
    private static final String ETQ_ADVERSE_EFFECTS = "adverseEffects";

    /** Loads the medicines from XML.
      * @param in the InputStream to laod the XML from.
      * @return a map in which the keys are Medicine.Id's, and values are the medicines themselves.
      */
    @NonNull
    public static Map<Medicine.Id, Medicine> loadFromXML(InputStream in)
        throws IOException
    {
        final Map<Medicine.Id, Medicine> TORET = new HashMap<>( 10 );

        // Load from XML
        try {
            final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
            final DocumentBuilder DB = DBF.newDocumentBuilder();
            final Document DOM = DB.parse( in );
            final Element DOC = DOM.getDocumentElement();
            final NodeList MEDICINE_NODES = DOC.getElementsByTagName( ETQ_MEDICINE );

            for(int i = 0; i < MEDICINE_NODES.getLength(); ++i) {
                final Medicine MEDICINE = loadMedicineFromXML( (Element) MEDICINE_NODES.item( i ) );

                TORET.put( MEDICINE.getId(), MEDICINE );
            }
        } catch(ParserConfigurationException | SAXException exc)
        {
            throw new IOException( exc.getMessage() );
        }

        return TORET;
    }

    /** Loads a single medicine info.
      * @param medicineElement The element to read the medicine info from;
     *                         It should have a tag "medicine"
      * @return a Medicine object.
      * @throws IOException if something goes wrong.
      */
    public static Medicine loadMedicineFromXML(Element medicineElement)
            throws IOException
    {
        final String ID = Util.getXMLAttributeOrThrow( medicineElement, ETQ_ID );
        final String GROUP_ID = Util.getXMLAttributeOrThrow( medicineElement, ETQ_GROUP_ID );
        final String URL = Util.getXMLAttributeOrThrow( medicineElement, ETQ_URL );
        final Element ELEM_MIN_DOSAGE =
                        Util.getXMLSubElement( medicineElement, ETQ_MIN_DOSAGE );
        final Element ELEM_REC_DOSAGE =
                Util.getXMLSubElement( medicineElement, ETQ_REC_DOSAGE );
        final Element ELEM_MAX_DOSAGE =
                Util.getXMLSubElement( medicineElement, ETQ_MAX_DOSAGE );
        final Element ELEM_ADVERSE_EFFECTS =
                Util.getXMLSubElement( medicineElement, ETQ_ADVERSE_EFFECTS );
        final Dosage MIN_DOSAGE = parseDosageElement( Dosage.Kind.MIN, ELEM_MIN_DOSAGE );
        final Dosage REC_DOSAGE = parseDosageElement( Dosage.Kind.REC, ELEM_REC_DOSAGE );
        final Dosage MAX_DOSAGE = parseDosageElement( Dosage.Kind.MAX, ELEM_MAX_DOSAGE );

        if ( ELEM_ADVERSE_EFFECTS == null ) {
            throw new Error( "loading XML medicine: missing adverse effects info." );
        }

        final String ADVERSE_EFFECTS = formatAdverseEffects(
                        ELEM_ADVERSE_EFFECTS.getFirstChild().getNodeValue() );

        if ( ADVERSE_EFFECTS.isEmpty() ) {
            throw new Error( "loading XML medicine: empty adverse effects info." );
        }

        return new Medicine(
                        Medicine.Id.valueOf( ID ),
                        MedicineGroup.Id.valueOf( GROUP_ID ),
                        MIN_DOSAGE,
                        REC_DOSAGE,
                        MAX_DOSAGE,
                        ADVERSE_EFFECTS,
                        URL );
    }

    private static String formatAdverseEffects(String adverseEffects)
    {
        final StringBuffer TORET = new StringBuffer( adverseEffects.length() );
        String[] lines = adverseEffects.split( "\n" );

        for(String line: lines) {
            TORET.append( line.trim() );
            TORET.append( ' ' );
        }

        return TORET.toString().trim();
    }

    /** Parses the contents of a dosage element.
      * It contains a numeric quqntity as value, and an attribute "kind"
      * to distinguish between mg and number of pills.
      * @param ELEMENT the element to parse.
      * @return a Dosage object.
      */
    private static Dosage parseDosageElement(final Dosage.Kind KIND, final Element ELEMENT)
            throws IOException
    {
        Dosage toret = Dosage.getInvalid();

        if ( ELEMENT != null ) {
            final String STR_UNITS = Util.getXMLAttributeOrThrow( ELEMENT, ETQ_UNITS );
            Dosage.Units units = null;
            double qtty = -1;

            // Parse quantity
            try {
                final NumberFormat NF = NumberFormat.getInstance( Locale.US );

                String txt = ELEMENT.getFirstChild().getNodeValue();
                qtty = Objects.requireNonNull( NF.parse( txt ) ).doubleValue();
            } catch(NullPointerException | ParseException exc) {
                throw new IOException( "loading medicine from XML: "
                                        + "dosage is not a number: "
                                        + ELEMENT.getNodeValue() );
            }

            // Parse units
            try {
                units = Dosage.Units.valueOf( STR_UNITS );
            } catch(IllegalArgumentException | NullPointerException exc)
            {
                throw new IOException( "loading medicine from XML:"
                                        + "invalid units value" );
            }

            toret = new Dosage( KIND, qtty, units );
        }

        return toret;
    }
}
