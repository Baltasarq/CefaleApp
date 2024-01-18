// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire.form;


import java.util.List;


/** This class represents a reference to another question. */
public class ReferenceQuestion extends BasicQuestion {
    public ReferenceQuestion(String id, String gotoId)
    {
        super( id, gotoId );
    }

    @Override
    public boolean isReference()
    {
        return true;
    }

    @Override
    public String getText()
    {
        throw new Error( "tried to access getText() in a Reference" );
    }

    @Override
    public ValueType getValueType()
    {
        throw new Error( "tried to access getValueType() in a Reference" );
    }

    @Override
    public int getNumOptions()
    {
        throw new Error( "tried to access getNumOptions() in a Reference" );
    }

    @Override
    public Option getOption(int id)
    {
        throw new Error( "tried to access getOption() in a Reference" );
    }

    @Override
    public List<Option> getOptions()
    {
        throw new Error( "tried to access getOptions() in a Reference" );
    }

    @Override
    public String getPic()
    {
        throw new Error( "tried to access getPic() in a Reference" );
    }

    @Override
    public boolean equals(Object other)
    {
        boolean toret = false;

        if ( other instanceof final ReferenceQuestion Q ) {
            toret = this.isReference()
                    && Q.isReference()
                    && Q.getId().equals( this.getId() )
                    && Q.getGotoId().equals( this.getGotoId() );
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        return ( 11 * this.getId().hashCode() )
                + ( 11 * this.getGotoId().hashCode() );
    }
}
