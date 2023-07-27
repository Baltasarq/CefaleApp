// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;


import java.util.List;


public abstract class BasicQuestion {
    public BasicQuestion(String id, String gotoId)
    {
        this.id = id;
        this.gotoId = gotoId;
    }

    public String getId()
    {
        return this.id;
    }

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

    private final String id;
    private final String gotoId;
}
