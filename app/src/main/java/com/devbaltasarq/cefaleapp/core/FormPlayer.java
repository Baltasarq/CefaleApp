// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.form.Branch;
import com.devbaltasarq.cefaleapp.core.form.Option;
import com.devbaltasarq.cefaleapp.core.form.Question;
import com.devbaltasarq.cefaleapp.core.form.Value;
import com.devbaltasarq.cefaleapp.core.form.ValueType;
import com.devbaltasarq.cefaleapp.core.result.ResultStep;


public class FormPlayer {
    public FormPlayer(final Form FORM)
    {
        this.FORM = FORM;
        this.REPO = Repo.get();
        this.RESULT = new Result( this.FORM.calcNumQuestions() );
        this.reset();
    }

    /** Resets everything and returns to first question. */
    public void reset()
    {
        this.currentBr = this.FORM.getHead();
        this.currentQ = null;
        this.RESULT.reset();
        this.REPO.reset();
    }

    /** @return the form of questions. */
    public Form getForm()
    {
        return this.FORM;
    }

    /** @return the repository of data. */
    public Repo getRepo()
    {
        return this.REPO;
    }

    public Question getCurrentQuestion()
    {
        if ( this.currentQ == null ) {
            this.gotoNextQuestion();
        }

        return this.currentQ;
    }

    public Branch getCurrentBranch()
    {
        return this.currentBr;
    }

    public void setEnteredVal(Value val)
    {
        this.RESULT.add( val, 1.0 );
    }

    /** Got to the next question.
      * @return true if we are in the next question, false otherwise.
      */
    public boolean gotoNextQuestion()
    {
        final Question Q = this.currentQ;
        boolean toret = false;

        if ( Q == null ) {
            toret = true;
            this.currentQ = this.currentBr.getHead();
        }
        else
        if ( !Q.isEnd() ) {
            toret = true;
            this.currentQ = this.currentBr.getQuestionById( Q.getGotoId() );
        }

        return toret;
    }

    public void changeToBranch(String id)
    {
        // Set the branch
        final Branch BR = this.FORM.getBranchById( id );

        if ( BR == null ) {
            throw new Error( "missing branch (?): '" + id + "'" );
        }

        this.currentBr = BR;
        this.currentQ = null;
    }

    public Question getFinalQuestion()
    {
        return this.currentQ;
    }

    public ResultStep getLastResult()
    {
        final ResultStep TORET = this.RESULT.getLastStep();

        if ( TORET == null ) {
            throw new Error( "missing or incorrect last result" );
        }

        return TORET;
    }

    public Result getResult()
    {
        return this.RESULT;
    }

    public String getResultsAsText()
    {
        return this.RESULT.toString();
    }

    public boolean isFinished()
    {
        return this.currentQ.isEnd();
    }

    private Question currentQ;
    private Branch currentBr;
    private final Result RESULT;
    private final Form FORM;
    private final Repo REPO;
}
