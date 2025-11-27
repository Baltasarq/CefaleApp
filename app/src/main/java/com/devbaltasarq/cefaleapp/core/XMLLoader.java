// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import androidx.annotation.NonNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class XMLLoader {
    private final static String ETQ_TEXT = "text";
    private final static String ETQ_LANG = "lang";

    public Element load(InputStream in) throws IOException
    {
        try {
            final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
            final DocumentBuilder DB = DBF.newDocumentBuilder();
            final Document DOM = DB.parse( in );

            return DOM.getDocumentElement();
        } catch(SAXException | ParserConfigurationException exc) {
            throw new IOException( exc );
        }
    }

    /** Text can have repeated spaces in the middle that must be removed.
      * @param TEXT the text to eliminate duplicated spaces from.
      * @return a new String with just single spaces.
      */
    public @NonNull static String removeInnerSpaces(@NonNull final String TEXT)
    {
        enum Status { OnSpace, OnText }
        String text = TEXT.trim();
        final var TORET = new StringBuilder( text.length() );

        Status status = Status.OnText;

        for(char ch: TEXT.trim().toCharArray()) {
            if ( status == Status.OnText
              && Character.isWhitespace( ch ) )
            {
                status = Status.OnSpace;
                TORET.append( ' ' );
            }
            else
            if ( !Character.isWhitespace( ch ) )
            {
                status = Status.OnText;
                TORET.append( ch );
            }
        }

        return TORET.toString();
    }

    /** Reads L18n text inside a given element.
      * @param CONTENTS a given element, in which to find "text" subelements.
      * @return a map associating languages and text.
      * @throws IOException if the lang attribute is not found.
      */
    public @NonNull static LocalizedText readXMLL10nText(@NonNull final Element CONTENTS)
            throws IOException
    {
        final var TORET = new LocalizedText();
        final NodeList NODES = CONTENTS.getChildNodes();

        for(int i = 0; i < NODES.getLength(); ++i) {
            final Node SUB_NODE = NODES.item( i );

            if ( SUB_NODE.getNodeName().equals( ETQ_TEXT )
              && SUB_NODE instanceof final Element SUB_TEXT )
            {
                final String TEXT = SUB_TEXT.getTextContent();
                final Language LANG = Language.valueOf(
                                                getXMLAttributeOrThrow(
                                                        SUB_TEXT,
                                                        ETQ_LANG ));

                TORET.add( LANG, removeInnerSpaces( TEXT ) );
            }
        }

        return TORET;
    }

    /** Looks for a given attribute inside a given element.
      * @param ELEMENT the element in which to look for the attribute.
      * @param ETQ_ATTR the label of the attribute to search.
      * @return the text of the attribute.
      * @throws IOException when the element is null,
      *                      the attribute is not found, or its empty.
      */
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

    /** Get one sub element with the given name.
     * @param ELEMENT the element to look for the subelement in.
     * @param ETQ_ELEM the name of the subelement.
     * @return the first one with than name.
     * @throws IOException if not found.
     */
    public static Element getXMLSubElementOrThrow(final Element ELEMENT, final String ETQ_ELEM)
            throws IOException
    {
        final Element TORET = getXMLSubElement( ELEMENT, ETQ_ELEM );

        if ( TORET == null ) {
            throw new IOException( "missing subelement: " + ETQ_ELEM );
        }

        return TORET;
    }
}
