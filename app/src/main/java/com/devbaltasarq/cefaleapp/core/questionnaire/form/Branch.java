// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire.form;


import com.devbaltasarq.cefaleapp.core.questionnaire.Form;

import java.util.HashMap;
import java.util.Map;


/** Represents a branch, i.e., a collection of related questions.
  * For instance: data, migraine, depression.
  */
public class Branch {
    public Branch(Form form, String id)
    {
        this.ID = id.trim().toLowerCase();
        this.FORM = form;
        this.QS_BY_ID = new HashMap<>();
    }

    /** @return the id for this branch. */
    public String getId()
    {
        return this.ID;
    }

    public Question getHead()
    {
        return this.head;
    }

    public int getNumQuestions()
    {
        return this.QS_BY_ID.size();
    }

    /** Sets the head question's id.
      * This must be set AFTER the questions are loaded,
      * since it uses the map to look for the question's id.
      * @param headId the id of the first question of the branch.
      */
    public void setHeadId(String headId)
    {
        this.head = this.getQuestionById( headId );
    }

    public void addQuestion(BasicQuestion bq)
    {
        Question q = null;

        if ( bq.isReference() ) {
            final Question ORG_Q = this.FORM.locate( bq.getId() );
            q = ORG_Q.copyWith( bq.getNum(), this.getId(), bq.getGotoId() );
        } else {
            q = ( (Question) bq ).copyWith( null, null );
        }

        if ( this.head == null ) {
            this.head = q;
        }

        this.QS_BY_ID.put( q.getId(), q );
    }

    /** Returns a question, given its id.
      * @param id the id of the question to look for.
      * @return the corresponding question, throws MissingElement otherwise.
      */
    public Question getQuestionById(String id)
    {
        return this.QS_BY_ID.get( id );
    }

    private Question head;
    private final String ID;
    private final Form FORM;
    private final Map<String, Question> QS_BY_ID;
}
