// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.faq;


import com.devbaltasarq.cefaleapp.core.LocalizedText;
import com.devbaltasarq.cefaleapp.core.XMLLoader;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class FaqXMLLoader {
    private static final String ETQ_ENTRY = "entry";
    private static final String ETQ_ID = "id";
    private static final String ETQ_QUESTION = "q";
    private static final String ETQ_ANSWER = "a";

    public static Map<String, Faq> loadFrom(InputStream in) throws IOException
    {
        final Map<String, Faq> TORET = new HashMap<>( 5 );

        final Element DOC = new XMLLoader().load( in );
        final NodeList ENTRY_NODES = DOC.getElementsByTagName( ETQ_ENTRY );

        for(int i = 0; i < ENTRY_NODES.getLength(); ++i) {
            if ( ENTRY_NODES.item( i ) instanceof Element ELEMENT ) {
                final Faq FAQ_ENTRY = loadEntry( ELEMENT );
                TORET.put( FAQ_ENTRY.getId(), FAQ_ENTRY );
            }
        }

        return TORET;
    }

    private static Faq loadEntry(final Element ENTRY_NODE) throws IOException
    {
        final String ID = XMLLoader.getXMLAttributeOrThrow( ENTRY_NODE, ETQ_ID );
        final Element Q_NODE = XMLLoader.getXMLSubElementOrThrow( ENTRY_NODE, ETQ_QUESTION );
        final Element A_NODE = XMLLoader.getXMLSubElementOrThrow( ENTRY_NODE, ETQ_ANSWER );

        final LocalizedText QUESTION_L10n_TEXT =
                                XMLLoader.readXMLL10nText( Q_NODE );

        final LocalizedText ANSWER_L10N_TEXT =
                                XMLLoader.readXMLL10nText( A_NODE );

        return new Faq( ID, QUESTION_L10n_TEXT, ANSWER_L10N_TEXT );
    }
}
