// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire.form;


import androidx.annotation.NonNull;

public class Value {
    public Value(Object value, ValueType dtype)
    {
        this.VALUE = value.toString().trim();
        this.VTYPE = dtype;
    }

    public @NonNull Object get()
    {
        Object toret = null;

        switch ( this.VTYPE ) {
            case BOOL:
                toret = this.getBool();
                break;
            case INT:
                toret = this.getInt();
                break;
            case STR:
                toret = this.VALUE;
                break;
            default:
                throw new Error( "Value.get(): no data type recognized: " + this.VTYPE);
        }

        return toret;
    }

    /** @return get the type of this value. */
    public ValueType getValueType()
    {
        return this.VTYPE;
    }

    private boolean getBool()
    {
        final String VALUE = this.VALUE.toLowerCase();

        if ( !VALUE.equals( Boolean.TRUE.toString() )
          && !VALUE.equals( Boolean.FALSE.toString() ) )
        {
            throw new Error( "Value.getBool(): no boolean: " + VALUE );
        }

        return Boolean.parseBoolean( VALUE );
    }

    private int getInt()
    {
        int toret = -1;

        try {
            toret = Integer.parseInt( this.VALUE );
        } catch(NumberFormatException exc) {
            throw new Error( "Value.getInt(): not an integer: " + this.VALUE );
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        return ( 11 * this.VALUE.hashCode() ) + ( 11 * this.VTYPE.hashCode() );
    }

    @Override
    public boolean equals(Object other)
    {
        boolean toret = false;

        if ( other instanceof Value value ) {
            toret = ( this.VALUE.equals( value.VALUE )
                   && this.VTYPE.equals( value.VTYPE ) );
        }

        return toret;
    }

    @Override
    public String toString()
    {
        String toret;

        if ( this.getValueType() == ValueType.BOOL ) {
            toret = this.getBool() ? "Sí": "No";
        }
        else {
            toret = this.get().toString();
        }

        return toret;
    }

    private final String VALUE;
    private final ValueType VTYPE;
}
