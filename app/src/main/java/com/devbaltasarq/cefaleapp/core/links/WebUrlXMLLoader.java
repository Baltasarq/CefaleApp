// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.links;


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


public class WebUrlXMLLoader {
    private static final String ETQ_LINK = "link";
    private static final String ETQ_ID = "id";
    private static final String ETQ_SRC = "src";
    private static final String ETQ_URL = "url";
    private static final String ETQ_DESC = "desc";

    public static Map<String, WebUrl> loadFrom(InputStream in) throws IOException
    {
        final Map<String, WebUrl> TORET = new HashMap<>( 5 );

        try {
            final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
            final DocumentBuilder DB = DBF.newDocumentBuilder();
            final Document DOM = DB.parse( in );
            final Element DOC = DOM.getDocumentElement();
            final NodeList LINK_NODES = DOC.getElementsByTagName( ETQ_LINK );

            for(int i = 0; i < LINK_NODES.getLength(); ++i) {
                final WebUrl LINK = loadLink( (Element) LINK_NODES.item( i ) );

                TORET.put( LINK.getId(), LINK );
            }
        } catch(ParserConfigurationException | SAXException exc)
        {
            throw new IOException( exc.getMessage() );
        }

        return TORET;
    }

    private static WebUrl loadLink(final Element ENTRY_NODE) throws IOException
    {
        final String ID = Util.getXMLAttributeOrThrow( ENTRY_NODE, ETQ_ID );
        final String SRC = Util.getXMLAttributeOrThrow( ENTRY_NODE, ETQ_SRC );
        final String URL = Util.getXMLAttributeOrThrow( ENTRY_NODE, ETQ_URL );
        final String DESC = Util.getXMLAttributeOrThrow( ENTRY_NODE, ETQ_DESC );

        return new WebUrl( ID, SRC, URL, DESC );
    }
}
