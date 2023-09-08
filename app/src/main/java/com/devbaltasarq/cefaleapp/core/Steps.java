// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.form.Question;
import com.devbaltasarq.cefaleapp.core.form.Value;

import java.util.ArrayList;
import java.util.List;


/** Stores the string ids of the asked questions */
public class Steps {
    private static final String ETQ_MMHG_UNITS = "mmHg.";
    private static final String ETQ_IMC_UNITS = "Kg/m<sup>2</sup>.";
    private static final String ETQ_AGE_UNITS = "años";
    private static final String ETQ_HEIGHT_UNITS = "cm.";
    private static final String ETQ_WEIGHT_UNITS = "kg.";
    private static final String ETQ_NO_VALUE = "n/a";

    public Steps(Form form, Repo repo)
    {
        this.FORM = form;
        this.REPO = repo;
        this.Q_IDS = new ArrayList<>( form.calcNumQuestions() );
        this.reset();
    }

    public void reset()
    {
        this.Q_IDS.clear();
    }

    public void add(Question q)
    {
        this.Q_IDS.add( q.getId().trim() );
    }

    public List<String> getQuestionHistory()
    {
        return new ArrayList<>( this.Q_IDS );
    }

    public int size()
    {
        return this.Q_IDS.size();
    }

    public int calcIMC()
    {
        double height = ( (double) this.REPO.getInt( Repo.Id.HEIGHT ) ) / 100.0;
        double weight = this.REPO.getInt( Repo.Id.WEIGHT );

        return (int) ( weight / ( height * height ) );
    }

    /** Returns a string describing the units or an empty string.
     * @param ID The Repo.Id value.
     * @return a string with the corresponding units, or an empty string.
     */
    private String unitsLabelFor(final Repo.Id ID)
    {
        String toret = "";

        if ( ID == Repo.Id.HIGHPRESSURE
                || ID == Repo.Id.LOWPRESSURE )
        {
            toret = ETQ_MMHG_UNITS;
        }
        else
        if ( ID == Repo.Id.AGE ) {
            toret = ETQ_AGE_UNITS;
        }
        else
        if ( ID == Repo.Id.HEIGHT ) {
            toret = ETQ_HEIGHT_UNITS;
        }
        else
        if ( ID == Repo.Id.WEIGHT ) {
            toret = ETQ_WEIGHT_UNITS;
        }

        return toret;
    }

    private String reportHypertension()
    {
        int pressureLow = this.REPO.getInt( Repo.Id.LOWPRESSURE );
        int pressureHigh = this.REPO.getInt( Repo.Id.HIGHPRESSURE );
        String toret = "<b>Hipertensión</b>: ";

        if ( pressureHigh >= 140
          && pressureLow >= 90 )
        {
            toret += "Sí (se aconseja comida sin sal y control periódico de presión)";
        } else {
            toret += "No";
        }

        toret += "<br/>";
        return toret;
    }

    private String reportIMC()
    {
        int imc = this.calcIMC();
        String toret = "<b>IMC</b>: " + imc + " " + ETQ_IMC_UNITS;

        if ( imc > 29 ) {
            toret += " <i>(sobrepeso, se aconseja dieta hipocalórica)</i>";
        }
        else
        if ( imc < 18 ) {
            toret += " <i>(por debajo del peso apropiado, se aconseja dieta hipercalórica)</i>";
        }

        toret += "<br/>";
        return toret;
    }

    @Override
    public String toString()
    {
        final StringBuilder TORET = new StringBuilder();

        TORET.append( this.reportHypertension() );
        TORET.append( this.reportIMC() );

        // Add all the steps
        for(final String RES_STEP: this.getQuestionHistory()) {
            final Question Q = this.FORM.locate( RES_STEP );
            final Repo.Id ID = Repo.Id.parse( Q.getDataFromId() );

            if ( ID == Repo.Id.NOTES ) {
                continue;
            }

            final Value VALUE = this.REPO.getValue( ID );

            TORET.append( "<b>" );
            TORET.append( Q.getSummary() );
            TORET.append( "</b>" );
            TORET.append( ':' );
            TORET.append( ' ' );

            if ( ID == Repo.Id.GENDER  ) {
                TORET.append( ( (boolean) VALUE.get() ) ? "mujer": "hombre" );
            } else {
                TORET.append( VALUE == null? ETQ_NO_VALUE : VALUE.toString() );
            }

            // Units
            TORET.append( ' ' );
            TORET.append( unitsLabelFor( ID ) );
            TORET.append( "\n<br/>" );
        }

        return TORET.toString();
    }

    private final Form FORM;
    private final Repo REPO;
    private final List<String> Q_IDS;
}
