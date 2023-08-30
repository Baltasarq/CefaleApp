// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;


import java.util.Locale;

public class Option {
    public Option(String text, String value)
    {
        this.TEXT = text;
        this.VALUE = value;
    }

    public String getValue()
    {
        return this.VALUE;
    }

    public String getText()
    {
        return this.TEXT;
    }

    @Override
    public boolean equals(Object other)
    {
        boolean toret = ( this == other );

        if ( !toret
          && other instanceof final Option OPT )
        {
            toret = ( this.getText().equals( OPT.getText() )
                   && this.getValue().equals( OPT.getValue() ) );
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        return ( 7 * this.getText().hashCode() )
             + ( 11 * this.getValue().hashCode() );
    }

    @Override
    public String toString()
    {
        return String.format(
                    Locale.getDefault(),
                    "%s (%s)",
                    this.getText(),
                    this.getValue() );
    }

    private final String TEXT;
    private final String VALUE;
}
