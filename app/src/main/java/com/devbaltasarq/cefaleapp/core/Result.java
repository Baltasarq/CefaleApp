// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.form.Value;
import com.devbaltasarq.cefaleapp.core.result.ResultStep;

import java.util.ArrayList;
import java.util.List;


public class Result {
    public Result(int numQuestions)
    {
        this.STEPS = new ArrayList<>( numQuestions );
        this.reset();
    }

    public void reset()
    {
        this.STEPS.clear();
        this.diagnostic = "N/A";
        this.accProbability = 1;
    }

    public void add(Value val, double weight)
    {
        this.accProbability *= weight;
        this.STEPS.add( new ResultStep( val, this.accProbability) );
    }

    public double getTotalProbability()
    {
        return this.accProbability;
    }

    public String getDiagnostic()
    {
        return this.diagnostic;
    }

    public ResultStep getLastStep()
    {
        ResultStep toret = null;

        if ( this.STEPS.size() > 0 ) {
            toret = this.STEPS.get( this.STEPS.size() -1 );
        }

        return toret;
    }

    public List<ResultStep> getSteps()
    {
        return new ArrayList<>( this.STEPS );
    }

    public int size()
    {
        return this.STEPS.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder TORET = new StringBuilder();

        for(final ResultStep RES_STEP: this.getSteps()) {
            if ( RES_STEP != null ) {
                TORET.append( RES_STEP );
                TORET.append( '\n' );
                TORET.append( '\n' );
            }
        }

        TORET.append( "\nProbabilidad acumulada: " );
        TORET.append( this.getTotalProbability() );
        return TORET.toString();
    }

    private String diagnostic;
    private double accProbability;
    private final List<ResultStep> STEPS;
}
