// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import com.devbaltasarq.cefaleapp.core.questionnaire.form.Question;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;

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
    private static final int IMC_OBESITY_LIMIT = 29;
    private static final int IMC_ANOREXIC_LIMIT = 18;

    public Steps(Form form, MigraineRepo repo)
    {
        this.FORM = form;
        this.REPO = repo;
        this.Q_IDS = new ArrayList<>( form.calcNumQuestions() );
        this.reset();
    }

    /** Reset the question history. */
    public void reset()
    {
        this.Q_IDS.clear();
    }

    /** Add a question to the history. */
    public void add(Question q)
    {
        this.Q_IDS.add( q.getId().trim() );
    }

    /** @return the history of questions. */
    public List<String> getQuestionHistory()
    {
        return new ArrayList<>( this.Q_IDS );
    }

    /** @return true if there are no questions stored, false otherwise. */
    public boolean isEmpty()
    {
        return ( this.size() == 0 );
    }

    /** @return the number of the questions in history. */
    public int size()
    {
        return this.Q_IDS.size();
    }

    public int calcIMC()
    {
        double height = ( (double) this.REPO.getInt( MigraineRepo.Id.HEIGHT ) ) / 100.0;
        double weight = this.REPO.getInt( MigraineRepo.Id.WEIGHT );

        return (int) ( weight / ( height * height ) );
    }

    /** Returns a string describing the units or an empty string.
     * @param ID The Repo.Id value.
     * @return a string with the corresponding units, or an empty string.
     */
    private String unitsLabelFor(final MigraineRepo.Id ID)
    {
        String toret = "";

        if ( ID == MigraineRepo.Id.HIGHPRESSURE
                || ID == MigraineRepo.Id.LOWPRESSURE )
        {
            toret = ETQ_MMHG_UNITS;
        }
        else
        if ( ID == MigraineRepo.Id.AGE ) {
            toret = ETQ_AGE_UNITS;
        }
        else
        if ( ID == MigraineRepo.Id.HEIGHT ) {
            toret = ETQ_HEIGHT_UNITS;
        }
        else
        if ( ID == MigraineRepo.Id.WEIGHT ) {
            toret = ETQ_WEIGHT_UNITS;
        }

        return toret;
    }

    private String reportHypertension()
    {
        String toret = "<b>Hipertensión</b>: ";

        if ( this.hasHyperTension() ) {
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

        if ( imc > IMC_OBESITY_LIMIT) {
            toret += " <i>(sobrepeso, se aconseja dieta hipocalórica)</i>";
        }
        else
        if ( imc < IMC_ANOREXIC_LIMIT) {
            toret += " <i>(por debajo del peso apropiado, se aconseja dieta hipercalórica)</i>";
        }

        toret += "<br/>";
        return toret;
    }

    public boolean isDepressed()
    {
        return this.REPO.getBool( MigraineRepo.Id.ISDEPRESSED );
    }

    public boolean isObese()
    {
        return ( this.calcIMC() > IMC_OBESITY_LIMIT);
    }

    public boolean isAnorexic()
    {
        return ( this.calcIMC() < IMC_ANOREXIC_LIMIT);
    }

    public boolean hasHyperTension()
    {
        int pressureLow = this.REPO.getInt( MigraineRepo.Id.LOWPRESSURE );
        int pressureHigh = this.REPO.getInt( MigraineRepo.Id.HIGHPRESSURE );

        return ( pressureHigh >= 140
              && pressureLow >= 90 );
    }

    public boolean hasHypoTension()
    {
        int pressureHigh = this.REPO.getInt( MigraineRepo.Id.HIGHPRESSURE );

        return ( pressureHigh < 90 );
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
            final MigraineRepo.Id ID = MigraineRepo.Id.parse( Q.getDataFromId() );

            if ( ID == MigraineRepo.Id.NOTES
              || ID == MigraineRepo.Id.AREYOUSURE )
            {
                continue;
            }

            final Value VALUE = this.REPO.getValue( ID );

            TORET.append( "<b>" );
            TORET.append( Q.getSummary() );
            TORET.append( "</b>" );
            TORET.append( ':' );
            TORET.append( ' ' );

            if ( ID == MigraineRepo.Id.GENDER  ) {
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
    private final MigraineRepo REPO;
    private final List<String> Q_IDS;
}
