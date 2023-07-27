// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;


import java.util.Locale;

public class Option {
    public Option(String text, String value)
    {
        this( text, 1.0, value );
    }

    public Option(String text, double w, String value)
    {
        this.text = text;
        this.weight = w;
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

    public double getWeight()
    {
        return this.weight;
    }

    public String getText()
    {
        return this.text;
    }

    @Override
    public boolean equals(Object other)
    {
        boolean toret = ( this == other );

        if ( !toret
          && other instanceof final Option OPT )
        {
            toret = ( this.getText().equals( OPT.getText() )
                   && this.getWeight() == OPT.getWeight() );
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        return ( 7 * this.getText().hashCode() )
             + ( 11 * Double.hashCode( this.getWeight() ) );
    }

    @Override
    public String toString()
    {
        return String.format(
                    Locale.getDefault(),
                    "%s (w: %5.2f%%)",
                    this.getText(),
                    this.getWeight() );
    }

    private final String text;
    private final String value;
    private final double weight;
}
