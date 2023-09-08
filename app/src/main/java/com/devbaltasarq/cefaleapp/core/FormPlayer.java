// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import android.util.Log;

import com.devbaltasarq.cefaleapp.core.form.Branch;
import com.devbaltasarq.cefaleapp.core.form.Path;
import com.devbaltasarq.cefaleapp.core.form.Question;


public class FormPlayer {
    private static final String LOG_TAG = FormPlayer.class.getSimpleName();
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
                toret = null;
            }

            return toret;
        }

        public boolean showNotesQuestion;
    }

    public FormPlayer(final Form FORM)
    {
        this.FORM = FORM;
        this.REPO = Repo.get();
        this.PATH = new Path();
        this.DIAG = new Diagnostic( this.REPO );
        this.STEPS = new Steps( this.FORM, this.REPO );
        settings = new Settings();
        this.reset();
    }

    /** Resets everything and returns to first question. */
    public void reset()
    {
        this.currentBr = this.FORM.getHead();
        this.currentQ = null;
        this.STEPS.reset();
        this.REPO.reset();

        // Set the initial path
        // PCD-JMP_SCREEN-1316
        this.PATH.clear();
        this.PATH.add( BR_SCREEN_ID );
        settings.reset();
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

    /** @return the diagnostic. */
    public Diagnostic getDiagnostic()
    {
        return this.DIAG;
    }

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
      * @return true if we are in the next question, false otherwise.
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
        final Branch BR = this.FORM.getBranchById( id );

        if ( BR == null ) {
            throw new Error( "missing branch (?): '" + id + "'" );
        }

        this.currentBr = BR;
        this.currentQ = null;
    }

    private boolean preProcessing()
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

    /** Triggered after each question, especially for storing info in the repo.
      * @return true if there was a branch change, false otherwise.
      */
    private boolean postProcessing()
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

    private Question currentQ;
    private Branch currentBr;
    private final Steps STEPS;
    private final Form FORM;
    private final Repo REPO;
    private final Diagnostic DIAG;
    private final Path PATH;
    public static Settings settings;
}
