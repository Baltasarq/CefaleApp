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

    private String reportHypotensionIfPresent()
    {
        String toret = "";

        if ( this.REPO.hasHypoTension() ) {
            toret += "<b>Hipotensión</b>: "
                    + "Sí<br/>";
        }

        return toret;
    }

    private String reportHypertensionIfPresent()
    {
        String toret = "";

        if ( this.REPO.hasHyperTension() ) {
            toret += "<b>Hipertensión</b>: "
                     + "Sí (se aconseja comida sin sal "
                     + "y control periódico de tensión)<br/>";
        }

        return toret;
    }

    private String reportIMCIfNeeded()
    {
        boolean isObese = this.REPO.isObese();
        boolean isAnorexic = this.REPO.isAnorexic();
        String toret = "";

        if ( isObese
          || isAnorexic )
        {
            toret = "<b>IMC</b>: " + this.REPO.calcIMC() + " " + ETQ_IMC_UNITS;

            if ( this.REPO.isObese() ) {
                toret += " <i>(sobrepeso, se aconseja dieta hipocalórica)</i>";
            }
            else
            if ( this.REPO.isAnorexic() ) {
                toret += " <i>(por debajo del peso apropiado, se aconseja dieta hipercalórica)</i>";
            }

            toret += "<br/>";
        }

        return toret;
    }

    /** @return all the info from the recorded steps. */
    private String stringFromAllSteps()
    {
        final StringBuilder TORET = new StringBuilder();

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

    @Override
    public String toString()
    {
        final StringBuilder TORET = new StringBuilder();

        TORET.append( this.reportHypotensionIfPresent() );
        TORET.append( this.reportHypertensionIfPresent() );
        TORET.append( this.reportIMCIfNeeded() );
        TORET.append( this.stringFromAllSteps() );

        return TORET.toString();
    }

    private final Form FORM;
    private final MigraineRepo REPO;
    private final List<String> Q_IDS;
}
