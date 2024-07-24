// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire.form;


import java.util.List;


public abstract class BasicQuestion {
    public BasicQuestion(int num, String id, String gotoId)
    {
        this.num = num;
        this.id = id;
        this.gotoId = gotoId;
    }

    /** @return the id of this question. */
    public String getId()
    {
        return this.id;
    }

    /** @return the question number. */
    public int getNum()
    {
        return this.num;
    }

    /** @return the goto id for this question, i.e., next question¡s id. */
    public String getGotoId()
    {
        return this.gotoId;
    }

    /** @return true if this is not a question, but a reference to a question.
     *         false otherwise (regular quesiton). */
    public abstract boolean isReference();

    public abstract String getText();

    public abstract ValueType getValueType();

    public abstract int getNumOptions();

    public abstract Option getOption(int id);

    public abstract List<Option> getOptions();
    public abstract String getPic();

    private final int num;
    private final String id;
    private final String gotoId;
}
