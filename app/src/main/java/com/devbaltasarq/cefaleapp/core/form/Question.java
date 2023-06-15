// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/** Represents a question in the form. */
public class Question {
    /** Builds a question. */
    public static class Builder {
        public Builder(int num)
        {
            this.num = num;
            this.options = new ArrayList<>();
        }

        public Builder setId(String id)
        {
            this.id = id;
            return this;
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
            return new Question( this.id, this.num, this.text, this.options );
        }

        private final int num;
        private String id;
        private String text;
        private final ArrayList<Option> options;
    }

    protected Question(String id, int num, String text, Collection<Option> options)
    {
        this.id = id;
        this.num = num;
        this.text = text;
        this.options = new ArrayList<>( options );
    }

    public int getOrdinal()
    {
        return this.num;
    }

    public String getText()
    {
        return this.text;
    }

    public String getId()
    {
        return this.id;
    }

    public int getNumOptions()
    {
        return this.options.size();
    }

    public Option getOption(int id)
    {
        return this.options.get( id );
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

    private final String id;
    private final int num;
    private final String text;
    private final ArrayList<Option> options;
}
