// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.faq;


import com.devbaltasarq.cefaleapp.core.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class FaqXMLLoader {
    private static final String ETQ_ENTRY = "entry";
    private static final String ETQ_TXT = "txt";
    private static final String ETQ_ID = "id";
    private static final String ETQ_LANG = "lang";
    private static final String ETQ_QUESTION = "q";
    private static final String ETQ_ANSWER = "a";

    public static Map<String, Map<String, Faq>> loadFrom(InputStream in) throws IOException
    {
        final Map<String, Map<String, Faq>> TORET = new HashMap<>( 5 );

        try {
            final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
            final DocumentBuilder DB = DBF.newDocumentBuilder();
            final Document DOM = DB.parse( in );
            final Element DOC = DOM.getDocumentElement();
            final NodeList ENTRY_NODES = DOC.getElementsByTagName( ETQ_ENTRY );

            for(int i = 0; i < ENTRY_NODES.getLength(); ++i) {
                final List<Faq> FAQ_ENTRIES = loadEntries( (Element) ENTRY_NODES.item( i ) );

                for(final Faq FAQ_ENTRY: FAQ_ENTRIES) {
                    final Map<String, Faq> ENTRIES = TORET.getOrDefault( FAQ_ENTRY.getLang(),
                                                        new HashMap<>( 30 ) );

                    if ( ENTRIES.isEmpty() ) {
                        TORET.put( FAQ_ENTRY.getLang(), ENTRIES );
                    }

                    ENTRIES.put( FAQ_ENTRY.getId(), FAQ_ENTRY );
                }
            }
        } catch(ParserConfigurationException | SAXException exc)
        {
            throw new IOException( exc.getMessage() );
        }

        return TORET;
    }

    private static List<Faq> loadEntries(final Element ENTRY_NODE) throws IOException
    {
        final String ID = Util.getXMLAttributeOrThrow( ENTRY_NODE, ETQ_ID );
        final List<Faq> TORET = new ArrayList<>( 5 );
        final NodeList TXT_NODES = ENTRY_NODE.getElementsByTagName( ETQ_TXT );

        for(int i = 0; i < TXT_NODES.getLength(); ++i) {
            final Element NODE = (Element) TXT_NODES.item( i );
            final String LANG = Util.getXMLAttributeOrThrow( NODE, ETQ_LANG );
            final String QUESTION = Util.getXMLAttributeOrThrow( NODE, ETQ_QUESTION );
            final String ANSWER = Util.getXMLAttributeOrThrow( NODE, ETQ_ANSWER );

            TORET.add( new Faq( LANG, ID, QUESTION, ANSWER ) );
        }

        return TORET;
    }
}
