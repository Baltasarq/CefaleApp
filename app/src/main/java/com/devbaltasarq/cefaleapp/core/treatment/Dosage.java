// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import java.util.Locale;

public class Dosage {
    /** Minimum, recommended or maximum dosage. */
    public enum Kind { MIN, REC, MAX, INVALID;
        @Override
        public String toString()
        {
            return super.toString().toLowerCase();
        }
    }

    /** The meaning of the quantity of the dosage. */
    public enum Units { MG, PILLS;
        @Override
        public String toString()
        {
            return super.toString().toLowerCase();
        }
    }

    /** Creates a new dosage. */
    public Dosage(Kind kind, double quantity, Units units)
    {
        this.kind = kind;
        this.quantity = quantity;
        this.units = units;
    }

    public boolean isValid()
    {
        return ( this.kind != Kind.INVALID );
    }

    /** @return the kind of the dosage. */
    public Kind getKind()
    {
        return this.kind;
    }

    /** @return the quantity of the dosage. */
    public double getQuantity()
    {
        return this.quantity;
    }

    /** @return the units of the dosage. */
    public Units getUnits()
    {
        return this.units;
    }

    public static Dosage getInvalid()
    {
        if ( invalid == null ) {
            invalid = new Dosage( Kind.INVALID, -1, Units.MG );
        }

        return invalid;
    }

    /** Retusn the dosage information, without the kind, formatted.
      * @param pillsLabel an i18n string for "pills".
      * @return info of the form: "2.5 mg." or "2 pills"
      */
    public String getFormatted(String pillsLabel)
    {
        String strUnits = pillsLabel;

        if ( this.getUnits() == Units.MG ) {
            strUnits = Units.MG.toString().toLowerCase() + ".";
        }

        return String.format( Locale.getDefault(),
                "%5.2f %s",
                this.getQuantity(),
                strUnits
        );
    }

    @Override
    public String toString()
    {
        return String.format(   Locale.getDefault(),
                                "%s %5.2f %s",
                                this.getKind().toString(),
                                this.getQuantity(),
                                this.getUnits().toString() );
    }

    private final Kind kind;
    private final double quantity;
    private final Units units;
    private static Dosage invalid = null;
}
