// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.BasicQuestion;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Branch;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Option;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Question;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.ReferenceQuestion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class FormXMLLoader {
    private final static String ETQ_Q = "q";
    private final static String ETQ_BRANCH = "branch";
    private final static String ETQ_TEXT = "text";
    private final static String ETQ_OPT = "opt";
    private final static String ETQ_TYPE = "type";
    private final static String ETQ_VALUE = "value";
    private final static String ETQ_HEAD = "head";
    private final static String ETQ_GOTO = "goto";
    private final static String ETQ_PIC = "pic";
    private final static String ETQ_SUMMARY = "summary";
    private final static String ETQ_REFERENCE = "is_ref";
    private final static String ETQ_ID = "id";

    public static Form loadFrom(InputStream in) throws IOException
    {
        final Form TORET = new Form();

        numQuestion = 1;

        try {
            final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
            final DocumentBuilder DB = DBF.newDocumentBuilder();
            final Document DOM = DB.parse( in );
            final Element DOC = DOM.getDocumentElement();
            final NodeList BR_NODES = DOC.getElementsByTagName( ETQ_BRANCH );

            for(int i = 0; i < BR_NODES.getLength(); ++i) {
                TORET.addBranch( loadBranch( TORET, (Element) BR_NODES.item( i ) ) );
            }

            String headBranchId = Util.getXMLAttributeOrThrow( DOC, ETQ_HEAD );
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
        String id = Util.getXMLAttributeOrThrow( branchElement, ETQ_ID );
        String headQuestionId = Util.getXMLAttributeOrThrow( branchElement, ETQ_HEAD );
        final Branch TORET = new Branch( form, id );

        // Load branch
        loadQuestionsForBranch( branchElement, TORET );
        TORET.setHeadId( headQuestionId );
        return TORET;
    }

    private static void loadQuestionsForBranch(Element parent, Branch branch) throws IOException
    {
        final NodeList Q_NODES = parent.getElementsByTagName( ETQ_Q );

        for(int i = 0; i < Q_NODES.getLength(); ++i) {
            final Element Q_NODE = (Element) Q_NODES.item( i );
            final Question.Builder QB = new Question.Builder();
            final NodeList OPT_NODES = Q_NODE.getElementsByTagName( ETQ_OPT );

            // Load basic data
            String id = Util.getXMLAttributeOrThrow( Q_NODE, ETQ_ID );
            String gotoId = Util.getXMLAttributeOrThrow( Q_NODE, ETQ_GOTO );

            // Is it a reference to another question?
            boolean isReference = false;

            if ( Q_NODE.hasAttribute( ETQ_REFERENCE ) ) {
                isReference = Boolean.parseBoolean( Util.getXMLAttributeOrThrow( Q_NODE, ETQ_REFERENCE ) );
            }

            BasicQuestion bq = null;

            if ( isReference ) {
                bq = new ReferenceQuestion( numQuestion, id, gotoId );
            } else {
                // Load the attributes of the regular question
                QB.setId( id );
                QB.setNum( numQuestion );
                QB.setGotoId( gotoId );
                QB.setText( Util.getXMLAttributeOrThrow( Q_NODE, ETQ_TEXT ) );
                QB.setSummary( Util.getXMLAttributeOrThrow( Q_NODE, ETQ_SUMMARY ) );

                if ( Q_NODE.hasAttribute( ETQ_PIC ) ) {
                    QB.setPic( Util.getXMLAttributeOrThrow( Q_NODE, ETQ_PIC ) );
                }

                if ( Q_NODE.hasAttribute( ETQ_TYPE ) ) {
                    QB.setValueType( Util.getXMLAttributeOrThrow( Q_NODE, ETQ_TYPE ) );
                }

                for(int j = 0; j < OPT_NODES.getLength(); ++j) {
                    QB.addOption( loadOpt( (Element) OPT_NODES.item( j ) ) );
                }

                bq = QB.create();
            }

            ++numQuestion;
            branch.addQuestion( bq );
        }

        return;
    }

    private static Option loadOpt(final Element OPT_NODE)
            throws IOException
    {
        final String TEXT = Util.getXMLAttributeOrThrow( OPT_NODE, ETQ_TEXT );
        final String VALUE = Util.getXMLAttributeOrThrow( OPT_NODE, ETQ_VALUE );

        return new Option( TEXT, VALUE );
    }

    private static int numQuestion;
}
