// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import com.devbaltasarq.cefaleapp.core.questionnaire.form.Branch;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Question;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;


/** Represents a generic player for any form. */
public abstract class FormPlayer {
    public FormPlayer(final Form FORM)
    {
        this.FORM = FORM;
    }

    /** Resets everything and returns to first question. */
    public void reset()
    {
        this.currentBr = this.getForm().getHead();
        this.currentQ = null;
    }

    /** @return the form being played. */
    public Form getForm()
    {
        return this.FORM;
    }

    /** Triggered before each question.
     * @return true if there was a question change, false otherwise.
     */
    protected abstract boolean preProcessing();

    /** Triggered after each question.
      * @return true if there was a branch change, false otherwise.
      */
    protected abstract boolean postProcessing();

    /** Registers a new answer.
      * @param VAL the value to store.
      * @return true if the value wasn't null, false otherwise.
      */
    public abstract boolean registerAnswer(final Value VAL);

    public Question getCurrentQuestion()
    {
        if ( this.currentQ == null ) {
            this.findFirstQuestion();
        }

        return this.currentQ;
    }

    public Branch getCurrentBranch()
    {
        return this.currentBr;
    }

    /** Get to the next question.
     * @return true if there is a next question, false otherwise.
     */
    public boolean findNextQuestion()
    {
        boolean toret = false;

        if ( !this.postProcessing() ) {
            if ( !this.currentQ.isEnd() ) {
                toret = true;

                do {
                    // Get the next question available, if any
                    this.jumpToNextQuestion();
                } while( this.preProcessing()
                      && !this.currentQ.isEnd() );
            }
        }

        if ( this.currentQ == null ) {
            toret = true;
            this.findFirstQuestion();
        }

        return toret;
    }

    /** Go to the first question of the current branch. */
    private void findFirstQuestion()
    {
        // Go to the head of the branch
        if ( this.currentQ == null ) {
            this.currentQ = this.currentBr.getHead();

            while ( this.preProcessing()
                    && !this.currentQ.isEnd() )
            {
                this.jumpToNextQuestion();
            }
        }

        return;
    }

    /** Go to the next question of the current question. */
    private void jumpToNextQuestion()
    {
        if ( !this.currentQ.isEnd() ) {
            this.currentQ = this.currentBr.getQuestionById(
                                        this.currentQ.getGotoId() );
        }

        return;
    }

    public void changeToBranch(String id)
    {
        // Set the branch
        final Branch BR = this.getForm().getBranchById( id );

        if ( BR == null ) {
            throw new Error( "missing branch (?): '" + id + "'" );
        }

        this.currentBr = BR;
        this.currentQ = null;
    }

    public abstract String getFinalReport();

    private Question currentQ;
    private Branch currentBr;
    private final Form FORM;
}
