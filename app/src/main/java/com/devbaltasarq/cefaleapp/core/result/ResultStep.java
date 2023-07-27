// FormPlayer (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.result;


import com.devbaltasarq.cefaleapp.core.form.Value;


/** Represents the data acquired in each question. */
public class ResultStep {
    public ResultStep(Value val, double probability)
    {
        this.value = val;
        this.probability = probability;
    }

    /** @return the acquired data piece. */
    public Value getValue()
    {
        return this.value;
    }

    /** @return the accumulated probablity. */
    public double getProbability()
    {
        return this.probability;
    }

    @Override
    public String toString()
    {
        return this.getValue() + " (" + this.getProbability() + ")";
    }

    @Override
    public int hashCode()
    {
        return ( 11 * this.getValue().hashCode() )
               + ( 11 * Double.hashCode( this.getProbability() ) );
    }

    @Override
    public boolean equals(Object other)
    {
        boolean toret = false;

        if ( other instanceof final ResultStep OTHER_RES ) {
            toret = ( OTHER_RES.getProbability() == this.getProbability()
                   && OTHER_RES.equals( this ) );
        }

        return toret;
    }

    private final Value value;
    private final double probability;
}
