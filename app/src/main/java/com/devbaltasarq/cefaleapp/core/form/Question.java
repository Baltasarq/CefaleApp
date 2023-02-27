// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/** Represents a question in the form. */
public class Question {
    /** Builds a question. */
    public static class Builder {
        public Builder()
        {
            this.options = new ArrayList<>();
        }

        public Builder setText(String text)
        {
            this.text = text;
            return this;
        }

        public Builder addOption(Option opt)
        {
            this.options.add( opt );
            return this;
        }

        public Question create()
        {
            return new Question( this.text, this.options );
        }

        private String text;
        private final ArrayList<Option> options;
    }

    protected Question(String text, Collection<Option> options)
    {
        this.text = text;
        this.options = new ArrayList<>( options );
    }

    public String getText()
    {
        return this.text;
    }

    public int getNumOptions()
    {
        return this.options.size();
    }

    public Option getOption(int i)
    {
        return this.options.get( i );
    }

    public List<Option> getOptions()
    {
        return new ArrayList<>( this.options );
    }

    @Override
    public boolean equals(Object other)
    {
        boolean toret = ( this == other );

        if ( !toret
          && other instanceof Question )
        {
            final Question Q = (Question) other;

            toret = ( this.getText().equals( Q.getText() )
                   && this.getNumOptions() == Q.getNumOptions() );

            if ( toret ) {
                for(int i = 0; i < this.getNumOptions(); ++i) {
                    if ( !( this.getOption( i ).equals( Q.getOption( i ) ) ) )
                    {
                        toret = false;
                        break;
                    }
                }
            }
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        int toret = 11 * this.getText().hashCode();

        for(final Option OPT: this.getOptions()) {
            toret += 11 * OPT.hashCode();
        }

        return toret;
    }

    private final String text;
    private final ArrayList<Option> options;
}
