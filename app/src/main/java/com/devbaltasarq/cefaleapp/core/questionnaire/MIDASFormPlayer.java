// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import com.devbaltasarq.cefaleapp.core.Message;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;


public class MIDASFormPlayer extends FormPlayer {
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
        return false;
    }

    /** @return the final report.
      *       Test result
      *             PCD-SUM_UP-MIDAS-1230: Find the final score, adding up all answers.
      *
      *             Score               MIDAS disability
      *                 0-5   pts       Minimum or no disability
      *                 6-10  pts       Low disability
      *                 11-20 pts       Moderate disability
      *                 >21   pts       High disability
      */
    @Override
    public String getFinalReport()
    {
        final String MSG_ID_HIGH_DISABILITY = "midasMsgHighDisability";
        final String MSG_ID_MODERATE_DISABILITY = "midasMsgModerateDisability";
        final String MSG_ID_LOW_DISABILITY = "midasMsgLowDisability";
        final String MSG_ID_NO_DISABILITY = "midasMsgNoDisability";
        String toret = "";

        if ( this.score > 21 ) {
            toret = Message.getFor( MSG_ID_HIGH_DISABILITY ).getMsg()
                                                    .getForCurrentLanguage();
        }
        else
        if ( this.score > 10 ) {
            toret = Message.getFor( MSG_ID_MODERATE_DISABILITY ).getMsg()
                                                    .getForCurrentLanguage();
        }
        else
        if ( this.score > 5 ) {
            toret = Message.getFor( MSG_ID_LOW_DISABILITY ).getMsg()
                                                    .getForCurrentLanguage();
        } else {
            toret = Message.getFor( MSG_ID_NO_DISABILITY ).getMsg()
                                                    .getForCurrentLanguage();
        }

        return toret;
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
