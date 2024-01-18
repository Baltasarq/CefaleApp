// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import android.util.Log;

import com.devbaltasarq.cefaleapp.core.questionnaire.form.Branch;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Path;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Question;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;


/** A player for the main migraine test form. */
public class MigraineFormPlayer extends GenericFormPlayer {
    private static final String LOG_TAG = MigraineFormPlayer.class.getSimpleName();
    private static final String BR_SCREEN_ID = "screen";
    private static final String BR_MIGRAINE_ID = "migraine";
    private static final String BR_TENSIONAL_ID = "tensional";
    private static final String BR_CONTINUE_ID = "continue";

    public static class Settings implements Cloneable {
        public Settings()
        {
            this.reset();
        }

        public void reset()
        {
            this.showNotesQuestion = true;
        }

        public Settings clone()
        {
            Settings toret;

            try {
                toret = (Settings) super.clone();
            } catch (CloneNotSupportedException exc) {
                throw new Error( "Settings cannot be cloneable." );
            }

            return toret;
        }

        public boolean showNotesQuestion;
    }

    public MigraineFormPlayer(final Form FORM)
    {
        super( FORM );

        this.REPO = Repo.get();
        this.PATH = new Path();
        this.DIAG = new Diagnostic( this.REPO );
        this.STEPS = new Steps( this.getFORM(), this.REPO );
        settings = new Settings();
    }

    @Override
    public void reset()
    {
        super.reset();

        this.STEPS.reset();
        this.REPO.reset();

        // Set the initial path
        // PCD-JMP_SCREEN-1316
        this.PATH.clear();
        this.PATH.add( BR_SCREEN_ID );
        settings.reset();
    }

    /** @return the repository of data. */
    public Repo getRepo()
    {
        return this.REPO;
    }

    /** @return the diagnostic. */
    public Diagnostic getDiagnostic()
    {
        return this.DIAG;
    }

    @Override
    protected boolean preProcessing()
    {
        final String Q_ID = this.getCurrentQuestion().getId();
        final String Q_DATA = this.getCurrentQuestion().getDataFromId();
        final Repo REPO = this.getRepo();
        boolean toret = false;

        if ( this.getDiagnostic().isMale()
          && ( Q_ID.equals( "migraine_menstruationWorsens" )
            || Q_ID.equals( "migraine_contraceptivesWorsens" ) ) )
        {
            // PCD_MENSTRUATIONWORSENS_NOTMALE_2017
            // PCD_CONTRACEPTIVESWORSENS_NOTMALE_2024
            toret = true;
            Log.i( LOG_TAG, "PCD_MENSTRUATIONWORSENS_NOTMALE_2017"
                    + " or PCD_CONTRACEPTIVESWORSENS_NOTMALE_2024"
                    + " skipping question" );
        }
        else
        if ( Q_ID.equals( "data_notes" )
          && !settings.showNotesQuestion )
        {
            // PCD_SHOWNOTES_2220
            toret = true;
            Log.i( LOG_TAG, "PCD_SHOWNOTES_2220 skipping notes" );
        }
        else
        if ( Repo.Id.parse( Q_DATA ) == Repo.Id.EXERCISEWORSENS
          && REPO.exists( Repo.Id.EXERCISEWORSENS ) )
        {
            // PCD_EXERCISEWORSENS_2222
            toret = true;
            Log.i( LOG_TAG, "PCD_EXERCISEWORSENS_2222"
                    + " already asked, skipping question" );
        }
        else
        if ( Repo.Id.parse( Q_DATA ) == Repo.Id.SOUNDPHOBIA
          && REPO.exists( Repo.Id.SOUNDPHOBIA ) )
        {
            // PCD_SOUNDPHOBIA_2228
            toret = true;
            Log.i( LOG_TAG, "PCD_SOUNDPHOBIA_2228"
                    + " already asked, skipping question" );
        }

        return toret;
    }

    @Override
    protected boolean postProcessing()
    {
        final Branch BR = this.getCurrentBranch();
        final Question Q = this.getCurrentQuestion();
        boolean toret = false;

        if ( Q != null ) {
            // Store the question as answered
            this.getSteps().add( Q );

            // Check end of branch
            if ( Q.isEnd() ) {
                // Check "continuity"
                if ( Repo.Id.parse( Q.getDataFromId() ) == Repo.Id.AREYOUSURE ) {
                    if ( !this.REPO.getBool( Repo.Id.AREYOUSURE ) ) {
                        // PCD-CONTINUE-1338 // do not continue
                        this.PATH.clear();
                    }
                }
                else
                // End of the screening, set the adequate questions path
                if ( BR.getId().equals( BR_SCREEN_ID ) ) {
                    int totalScreen = this.getDiagnostic().calcTotalScreen();

                    this.PATH.clear();

                    if ( totalScreen >= 2 ) {
                        // PCD-JGTE_2-migraine-1559
                        this.PATH.add( BR_MIGRAINE_ID );

                        if ( this.REPO.getBool( Repo.Id.ISDEPRESSED ) ) {
                            // PCD-CONTINUE-1338
                            this.PATH.add( BR_CONTINUE_ID );
                            this.PATH.add( BR_TENSIONAL_ID );
                        }
                    }
                    else
                    if ( totalScreen == 1 ) {
                        // PCD-JE_1-MIGRAINE-TENSIONAL-1602
                        this.PATH.add( BR_MIGRAINE_ID );
                        // PCD-CONTINUE-1338
                        this.PATH.add( BR_CONTINUE_ID );
                        this.PATH.add( BR_TENSIONAL_ID );
                    } else {
                        // PCD-JZ-TENSIONAL-1511
                        this.PATH.add( BR_TENSIONAL_ID );
                    }
                }

                if ( this.PATH.size() > 0 ) {
                    // PCD-JMP_SCREEN-1316
                    // PCD-CONTINUE-1338 // yes, continue
                    this.changeToBranch( this.PATH.next() );
                    this.PATH.remove();
                    toret = true;
                }
            }
        }

        return toret;
    }

    public Steps getSteps()
    {
        return this.STEPS;
    }

    public String getFinalReport()
    {
        return this.DIAG.toString()
                + "<br/><br/>" + this.STEPS.toString();
    }

    public boolean registerAnswer(final Value VAL)
    {
        final Question Q = this.getCurrentQuestion();
        boolean toret = ( VAL != null );

        if ( toret ) {
            final Repo.Id ID = Repo.Id.parse( Q.getDataFromId() );

            this.REPO.setValue( ID, VAL );
        }

        return toret;
    }

    private final Steps STEPS;
    private final Repo REPO;
    private final Diagnostic DIAG;
    private final Path PATH;
    public static Settings settings;
}
