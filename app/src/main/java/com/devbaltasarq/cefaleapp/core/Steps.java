// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.form.Question;
import com.devbaltasarq.cefaleapp.core.form.Value;

import java.util.ArrayList;
import java.util.List;


public class Steps {
    public Steps(Form form, Repo repo)
    {
        this.FORM = form;
        this.REPO = repo;
        this.QS = new ArrayList<>( form.calcNumQuestions() );
        this.reset();
    }

    public void reset()
    {
        this.QS.clear();
    }

    public void add(String qid)
    {
        this.QS.add( qid.trim() );
    }

    public List<String> getQuestionHistory()
    {
        return new ArrayList<>( this.QS );
    }

    public int size()
    {
        return this.QS.size();
    }

    public int calcIMC()
    {
        double height = ( (double) this.REPO.getInt( Repo.Id.HEIGHT ) ) / 100.0;
        double weight = this.REPO.getInt( Repo.Id.WEIGHT );

        return (int) ( weight / ( height * height ) );
    }

    @Override
    public String toString()
    {
        final StringBuilder TORET = new StringBuilder();

        // Add hypertension
        int pressureLow = this.REPO.getInt( Repo.Id.LOWPRESSURE );
        int pressureHigh = this.REPO.getInt( Repo.Id.HIGHPRESSURE );

        TORET.append( "Hipertensión: " );
        if ( pressureHigh >= 140
          && pressureLow >= 90 )
        {
            TORET.append( "Sí" );
        } else {
            TORET.append( "No" );
        }

        TORET.append( '\n' );

        // Add IMC
        TORET.append( "IMC: " );
        TORET.append( this.calcIMC() );
        TORET.append( '\n' );

        // Add all the steps
        for(final String RES_STEP: this.getQuestionHistory()) {
            final Question Q = this.FORM.locate( RES_STEP );
            final Repo.Id ID = Repo.Id.parse( Q.getDataFromId() );

            if ( ID == Repo.Id.NOTES ) {
                continue;
            }

            final Value VALUE = this.REPO.getValue( ID );

            TORET.append( Q.getSummary() );
            TORET.append( ':' );
            TORET.append( ' ' );

            if ( ID == Repo.Id.GENDER  ) {
                TORET.append( ( (boolean) VALUE.get() ) ? "mujer": "hombre" );
            } else {
                TORET.append( VALUE.toString() );
            }

            TORET.append( '\n' );
        }

        return TORET.toString();
    }

    private final Form FORM;
    private final Repo REPO;
    private final List<String> QS;
}
