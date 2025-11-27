// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import com.devbaltasarq.cefaleapp.core.Message;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;


public class HIT6FormPlayer extends FormPlayer {
    public HIT6FormPlayer(final Form FORM)
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

    /** @return the test result
      *
      * PCD-SUM_UP-HIT-2148: Find x (the final score), adding up each score for a question.
      *
      *  x > 60 Severe impact: your headaches are deeply impacting your life.
      *  56 < x < 59: Important impact: your headaches are limiting your life.
      *  50 < x < 55: Somewhat impacting: your headaches are conditioning your life.
      *  x <= 49: Small or no impact: your headaches are not limiting your life.
      */
    @Override
    public String getFinalReport()
    {
        final String MSG_HIT6_SCORE = "hit6MsgScore";
        final String MSG_HIT6_SEVERE = "hit6MsgSevere";
        final String MSG_HIT6_IMPORTANT = "hit6MsgImportant";
        final String MSG_HIT6_MODERATE = "hit6MsgModerate";
        final String MSG_HIT6_MINIMUM = "hit6MsgMinimum";
        String toret = "";

        if ( this.score > 60 ) {
            toret += Message.getFor( MSG_HIT6_SEVERE ).getMsg()
                                                    .getForCurrentLanguage();
        }
        else
        if ( this.score > 56 ) {
            toret += Message.getFor( MSG_HIT6_IMPORTANT ).getMsg()
                                                    .getForCurrentLanguage();
        }
        else
        if ( this.score > 50 ) {
            toret += Message.getFor( MSG_HIT6_MODERATE ).getMsg()
                                                    .getForCurrentLanguage();
        } else {
            toret += Message.getFor( MSG_HIT6_MINIMUM ).getMsg()
                                                    .getForCurrentLanguage();
        }

        toret += "(" + Message.getFor( MSG_HIT6_SCORE ).getMsg()
                                                .getForCurrentLanguage()
                + ": " + this.score + ".)";

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
