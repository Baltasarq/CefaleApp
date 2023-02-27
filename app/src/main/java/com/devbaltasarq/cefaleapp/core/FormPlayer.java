// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.form.Option;
import com.devbaltasarq.cefaleapp.core.form.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class FormPlayer {
    public FormPlayer(final Form form)
    {
        this.form = form;
        this.results = new Option[ this.form.getNumQuestions() ];
        this.reset();
    }

    public void reset()
    {
        this.numQ = 0;
        this.totalWeight = 1;
        Arrays.fill( this.results, null );
    }

    public Question getCurrentQuestion()
    {
        this.chkFinished();
        return this.form.getQuestion( this.numQ );
    }

    public void setChosenOption(int i)
    {
        this.chkFinished();

        final Question Q = this.form.getQuestion( this.numQ );
        final Option OPT = Q.getOption( i );
        int nextQId = OPT.getGotoId();

        // Store result
        this.totalWeight *= OPT.getWeight();
        this.results[ this.numQ ] = OPT;

        // Go to next question
        if ( nextQId < 0 ) {
            ++this.numQ;
        } else {
            this.numQ = OPT.getGotoId();
        }

        return;
    }

    public int getCurrentNumQuestion()
    {
        this.chkFinished();
        return this.numQ;
    }

    public double getTotalWeight()
    {
        return totalWeight;
    }

    public List<Option> getResults()
    {
        final ArrayList<Option> TORET = new ArrayList<>();

        for(final Option OPT: this.results ) {
            if ( OPT != null ) {
                TORET.add( OPT );
            }
        }

        return TORET;
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
        return this.numQ >= this.form.getNumQuestions()
            || this.form.getQuestion( this.numQ ).getNumOptions() == 0;
    }

    private void chkFinished()
    {
        if ( this.isFinished() ) {
            throw new IllegalStateException( "form finished" );
        }
    }

    private int numQ;
    private double totalWeight;
    private final Form form;
    private final Option[] results;
}
