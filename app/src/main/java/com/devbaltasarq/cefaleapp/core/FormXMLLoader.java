// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.form.BasicQuestion;
import com.devbaltasarq.cefaleapp.core.form.Branch;
import com.devbaltasarq.cefaleapp.core.form.Option;
import com.devbaltasarq.cefaleapp.core.form.Question;
import com.devbaltasarq.cefaleapp.core.form.ReferenceQuestion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class FormXMLLoader {
    private final static String EtqQ = "q";
    private final static String EtqBranch = "branch";
    private final static String EtqText = "text";
    private final static String EtqOpt = "opt";
    private final static String EtqType = "type";
    private final static String EtqValue = "value";
    private final static String EtqHead = "head";
    private final static String EtqWeight = "w";
    private final static String EtqGoto = "goto";
    private final static String EtqPic = "pic";
    private final static String EtqReference = "isref";
    private final static String EtqId = "id";

    public static Form loadFromFile(InputStream in) throws IOException
    {
        final Form TORET = new Form();

        try {
            final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
            final DocumentBuilder DB = DBF.newDocumentBuilder();
            final Document DOM = DB.parse( in );
            final Element DOC = DOM.getDocumentElement();
            final NodeList BR_NODES = DOC.getElementsByTagName( EtqBranch );

            for(int i = 0; i < BR_NODES.getLength(); ++i) {
                TORET.addBranch( loadBranch( TORET, (Element) BR_NODES.item( i ) ) );
            }

            String headBranchId = getAttribute( DOC, EtqHead );
            TORET.setHeadId( headBranchId );
        } catch(ParserConfigurationException | SAXException exc)
        {
            throw new IOException( exc.getMessage() );
        }

        return TORET;
    }

    private static Branch loadBranch(Form form, Element branchElement) throws IOException
    {
        // Load branch's info
        String id = getAttribute( branchElement, EtqId );
        String headQuestionId = getAttribute( branchElement, EtqHead );
        final Branch TORET = new Branch( form, id );

        // Load branch
        loadQuestionsForBranch( branchElement, TORET );
        TORET.setHeadId( headQuestionId );
        return TORET;
    }

    private static void loadQuestionsForBranch(Element parent, Branch branch) throws IOException
    {
        final NodeList Q_NODES = parent.getElementsByTagName( EtqQ );

        for(int i = 0; i < Q_NODES.getLength(); ++i) {
            final Element Q_NODE = (Element) Q_NODES.item( i );
            final Question.Builder QB = new Question.Builder();
            final NodeList OPT_NODES = Q_NODE.getElementsByTagName( EtqOpt );

            // Load basic data
            String id = getAttribute( Q_NODE, EtqId );
            String gotoId = getAttribute( Q_NODE, EtqGoto );

            // Is it a reference to another question?
            boolean isReference = false;

            if ( Q_NODE.hasAttribute( EtqReference ) ) {
                isReference = Boolean.parseBoolean( getAttribute( Q_NODE, EtqReference ) );
            }

            BasicQuestion bq = null;

            if ( isReference ) {
                bq = new ReferenceQuestion( id, gotoId );
            } else {
                // Load the attributes of the regular question
                QB.setId( id );
                QB.setGotoId( gotoId );
                QB.setText( getAttribute( Q_NODE, EtqText ) );

                if ( Q_NODE.hasAttribute( EtqPic ) ) {
                    QB.setPic( getAttribute( Q_NODE, EtqPic ) );
                }

                if ( Q_NODE.hasAttribute( EtqType ) ) {
                    QB.setValueType( getAttribute( Q_NODE, EtqType ) );
                }

                for(int j = 0; j < OPT_NODES.getLength(); ++j) {
                    QB.addOption( loadOpt( (Element) OPT_NODES.item( j ) ) );
                }

                bq = QB.create();
            }

            branch.addQuestion( bq );
        }

        return;
    }

    private static String getAttribute(Element element, String idAttr)
            throws IOException
    {
        final String TORET = element.getAttribute( idAttr ).trim();

        if ( TORET.isEmpty() ) {
            String id = "";

            if ( element.hasAttribute( EtqId ) ) {
                id = element.getAttribute( EtqId );
            }

            throw new IOException( "XML: '"
                            + idAttr + "' not found or empty in: "
                            + element.getTagName()
                            + "/" + id );
        }

        return TORET;
    }

    private static Option loadOpt(final Element OPT_NODE)
            throws IOException
    {
        String text = getAttribute( OPT_NODE, EtqText );
        String value = getAttribute( OPT_NODE, EtqValue );
        double weight = 1.0;

        // Load weight
        if ( OPT_NODE.hasAttribute( EtqWeight ) ) {
            String strWeight = getAttribute( OPT_NODE, EtqWeight );

            if ( !strWeight.isEmpty() ) {
                try {
                    weight = Double.parseDouble( strWeight );
                } catch(NumberFormatException exc) {
                    throw new IOException( "parsing option's weight: " + exc.getMessage() );
                }
            }
        }

        return new Option( text, weight, value );
    }
}
