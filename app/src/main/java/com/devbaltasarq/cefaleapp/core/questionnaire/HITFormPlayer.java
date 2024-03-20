// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;


public class HITFormPlayer extends FormPlayer {
    public HITFormPlayer(final Form FORM)
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
      *  x > 49: Small or no impact: your headaches are not limiting your life.
      */
    @Override
    public String getFinalReport()
    {
        String toret = "";

        if ( this.score > 60 ) {
            toret = "Impacto severo: sus cefaleas impactan y limitan siempre su día a día.";
        }
        else
        if ( this.score > 56 ) {
            toret = "Impacto importante: sus cefaleas limitan casi siempre su vida.";
        }
        else
        if ( this.score > 50 ) {
            toret = "Impacto moderado: sus cefaleas casi no limitan las tareas en su día a día.";
        } else {
            toret = "Impacto mínimo: sus cefaleas no limitan su vida.";
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
