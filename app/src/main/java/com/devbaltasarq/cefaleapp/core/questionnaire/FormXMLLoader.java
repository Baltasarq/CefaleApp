// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import com.devbaltasarq.cefaleapp.core.LocalizedText;
import com.devbaltasarq.cefaleapp.core.XMLLoader;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.BasicQuestion;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Branch;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Option;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Question;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.ReferenceQuestion;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;


public class FormXMLLoader {
    private final static String ETQ_Q = "q";
    private final static String ETQ_BRANCH = "branch";
    private final static String ETQ_CONTENTS = "contents";
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

        final Element DOC = new XMLLoader().load( in );
        final NodeList BR_NODES = DOC.getElementsByTagName( ETQ_BRANCH );

        for(int i = 0; i < BR_NODES.getLength(); ++i) {
            TORET.addBranch( loadBranch( TORET, (Element) BR_NODES.item( i ) ) );
        }

        String headBranchId = XMLLoader.getXMLAttributeOrThrow( DOC, ETQ_HEAD );
        TORET.setHeadId( headBranchId );

        return TORET;
    }

    private static Branch loadBranch(Form form, Element branchElement) throws IOException
    {
        // Load branch's info
        String id = XMLLoader.getXMLAttributeOrThrow( branchElement, ETQ_ID );
        String headQuestionId = XMLLoader.getXMLAttributeOrThrow( branchElement, ETQ_HEAD );
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
            String id = XMLLoader.getXMLAttributeOrThrow( Q_NODE, ETQ_ID );
            String gotoId = XMLLoader.getXMLAttributeOrThrow( Q_NODE, ETQ_GOTO );

            // Is it a reference to another question?
            boolean isReference = false;

            if ( Q_NODE.hasAttribute( ETQ_REFERENCE ) ) {
                isReference = Boolean.parseBoolean(
                                        XMLLoader.getXMLAttributeOrThrow(
                                                        Q_NODE,
                                                        ETQ_REFERENCE ) );
            }

            BasicQuestion bq = null;

            if ( isReference ) {
                bq = new ReferenceQuestion( numQuestion, id, gotoId );
            } else {
                final Element CONTENTS = XMLLoader.getXMLSubElementOrThrow( Q_NODE, ETQ_CONTENTS );
                final Element SUMMARY = XMLLoader.getXMLSubElementOrThrow( Q_NODE, ETQ_SUMMARY );

                // Load the attributes of the regular question
                QB.setId( id );
                QB.setNum( numQuestion );
                QB.setGotoId( gotoId );
                QB.setText( XMLLoader.readXMLL10nText( CONTENTS ) );
                QB.setSummary( XMLLoader.readXMLL10nText( SUMMARY ) );

                if ( Q_NODE.hasAttribute( ETQ_PIC ) ) {
                    QB.setPic(
                            XMLLoader.getXMLAttributeOrThrow(
                                            Q_NODE,
                                            ETQ_PIC ) );
                }

                if ( Q_NODE.hasAttribute( ETQ_TYPE ) ) {
                    QB.setValueType(
                            XMLLoader.getXMLAttributeOrThrow(
                                            Q_NODE,
                                            ETQ_TYPE ) );
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
        final LocalizedText TEXT = XMLLoader.readXMLL10nText( OPT_NODE );
        final String VALUE = XMLLoader.getXMLAttributeOrThrow( OPT_NODE, ETQ_VALUE );

        return new Option( TEXT, VALUE );
    }

    private static int numQuestion;
}
