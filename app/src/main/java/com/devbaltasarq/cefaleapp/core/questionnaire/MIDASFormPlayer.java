// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;


public class MIDASFormPlayer extends GenericFormPlayer {
    public MIDASFormPlayer(final Form FORM)
    {
        super( FORM );
    }

    @Override
    public void reset()
    {
        super.reset();
        this.score = 0;
    }

    /** Triggered before each question.
      * @return true if there was a question change, false otherwise.
      */
    @Override
    protected boolean preProcessing()
    {
        return false;
    }

    /** Triggered after each question.
      * @return true if there was a branch change, false otherwise.
      */
    @Override
    protected boolean postProcessing()
    {
        // Apply the final rule test
        /*
        Test result
            PCD-SUM_UP-MIDAS-1230: Find the final score, adding up all answers.

            Score               MIDAS disability
                0-5   pts       Minimum or no disability
                6-10  pts       Low disability
                11-20 pts       Moderate disability
                >21   pts       High disability
        */

        return false;
    }

    public boolean registerAnswer(final Value VAL)
    {
        boolean toret = ( VAL != null );

        if ( toret ) {
            this.score += (Integer) VAL.get();
        }
        return toret;
    }

    int score;
}
