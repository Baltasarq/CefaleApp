// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;


import java.util.Locale;

public class Option {
    public Option(String text)
    {
        this( text, "", 1.0 );
    }

    public Option(String text, String gotoId, double w)
    {
        this.text = text;
        this.gotoId = gotoId;
        this.weight = w;
    }

    public String getGotoId()
    {
        return this.gotoId;
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
          && other instanceof Option )
        {
            final Option OPT = (Option) other;

            toret = ( this.getText().equals( OPT.getText() )
                   && this.getGotoId() == OPT.getGotoId()
                   && this.getWeight() == OPT.getWeight() );
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        return ( 7 * this.getText().hashCode() )
             + ( 11 * this.getGotoId().hashCode() )
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
    private final String gotoId;
    private final double weight;
}
