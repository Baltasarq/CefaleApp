// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.form.Option;
import com.devbaltasarq.cefaleapp.core.form.Question;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class Form {
    private final static String EtqQ = "q";
    private final static String EtqText = "text";
    private final static String EtqOpt = "opt";
    private final static String EtqWeight = "w";
    private final static String EtqGoto = "goto";
    private final static String EtqId = "id";

    protected Form()
    {
        this.qsById = new HashMap<>();
    }

    public Question getFirstQuestion()
    {
        return this.head;
    }

    public void addQuestion(Question q)
    {
        if (this.head == null ) {
            this.head = q;
        }

        this.qsById.put( q.getId(), q );
    }

    public Question getQuestionById(String id)
    {
        return this.qsById.get( id );
    }

    public int getNumQuestions()
    {
        return this.qsById.size();
    }

    public static Form buildFromFile(InputStream in) throws IOException
    {
        final Form TORET = new Form();

        try {
            final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
            final DocumentBuilder DB = DBF.newDocumentBuilder();
            final Document DOM = DB.parse( in );
            final Element DOC = DOM.getDocumentElement();
            final NodeList Q_NODES = DOC.getElementsByTagName( EtqQ );

            for(int i = 0; i < Q_NODES.getLength(); ++i) {
                final Element Q_NODE = (Element) Q_NODES.item( i );
                final Question.Builder QB = new Question.Builder( i );
                final NodeList OPT_NODES = Q_NODE.getElementsByTagName( EtqOpt );

                QB.setId( Q_NODE.getAttribute( EtqId ) );
                QB.setText( Q_NODE.getAttribute( EtqText ) );
                for(int j = 0; j < OPT_NODES.getLength(); ++j) {
                    QB.addOption( buildOptFromXml( (Element) OPT_NODES.item( j ) ) );
                }

                TORET.addQuestion( QB.create() );
            }
        } catch(ParserConfigurationException | SAXException exc)
        {
            throw new IOException( exc.getMessage() );
        }

        return TORET;
    }

    private static Option buildOptFromXml(final Element OPT_NODE)
            throws IOException
    {
        String strWeight = OPT_NODE.getAttribute( EtqWeight );
        String strGotoId = OPT_NODE.getAttribute( EtqGoto );
        String text = OPT_NODE.getAttribute( EtqText );
        String gotoId = "";
        double weight = 1.0;

        if ( text.isEmpty() ) {
            throw new IOException( "option without text !!" );
        }

        // Load weight
        if ( !strWeight.isEmpty() ) {
            try {
                weight = Double.parseDouble( strWeight );
            } catch(NumberFormatException exc) {
                throw new IOException( "parsing option's weight: " + exc.getMessage() );
            }
        }

        // Load gotoId
        if ( !strGotoId.isEmpty() ) {
                gotoId = strGotoId.trim();
        }

        return new Option( text, gotoId, weight );
    }

    private Question head;
    private final Map<String, Question> qsById;
}
