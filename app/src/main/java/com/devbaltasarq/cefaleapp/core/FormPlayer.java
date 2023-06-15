// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.form.Option;
import com.devbaltasarq.cefaleapp.core.form.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FormPlayer {
    public FormPlayer(final Form form)
    {
        this.form = form;
        this.results = new ArrayList<>( this.form.getNumQuestions() );
        this.reset();
    }

    public void reset()
    {
        this.currentQ = this.form.getFirstQuestion();
        this.totalWeight = 1;
        this.results.clear();
    }

    public Question getCurrentQuestion()
    {
        this.chkFinished();
        return this.currentQ;
    }

    public void setChosenOption(int id)
    {
        this.chkFinished();

        final Question Q = this.currentQ;
        final Option OPT = Q.getOption( id );
        final String nextQId = OPT.getGotoId();

        // Store result
        this.totalWeight *= OPT.getWeight();
        this.results.add( OPT );

        // Go to next question
        this.currentQ = this.form.getQuestionById( OPT.getGotoId() );
    }

    public double getTotalWeight()
    {
        return totalWeight;
    }

    public Question getResult()
    {
        return this.currentQ;
    }

    public List<Option> getResults()
    {
        return new ArrayList<>( this.results );
    }

    public String getResultsAsText()
    {
        final StringBuilder TORET = new StringBuilder();

        for(final Option OPT: this.results ) {
            if ( OPT != null ) {
                TORET.append( OPT );
                TORET.append( '\n' );
                TORET.append( '\n' );
            }
        }

        TORET.append( "\nCerteza acumulada: " );
        TORET.append( this.getTotalWeight() );
        return TORET.toString();
    }

    public boolean isFinished()
    {
        return this.currentQ.getNumOptions() == 0;
    }

    private void chkFinished()
    {
        if ( this.isFinished() ) {
            throw new IllegalStateException( "form finished" );
        }
    }

    private Question currentQ;
    private double totalWeight;
    private final List<Option> results;
    private final Form form;
}
