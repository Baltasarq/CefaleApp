// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.form;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/** Represents a question in the form. */
public class Question extends BasicQuestion {
    /** Builds a question. */
    public static class Builder {
        public Builder()
        {
            this.gotoId = "";
            this.pic = "";
            this.type = ValueType.BOOL;
            this.options = new ArrayList<>();
        }

        public Builder setId(String id)
        {
            this.id = id.trim();
            return this;
        }

        public Builder setPic(String id)
        {
            this.pic = id.trim();
            return this;
        }

        public Builder setValueType(String id)
        {
            this.type = ValueType.parse( id.trim() );
            return this;
        }

        public Builder setValueType(ValueType vt)
        {
            this.type = vt;
            return this;
        }

        public Builder setGotoId(String id)
        {
            this.gotoId = id.trim();
            return this;
        }

        public Builder setText(String text)
        {
            this.text = text.trim();
            return this;
        }

        public Builder addOption(Option opt)
        {
            this.options.add( opt );
            return this;
        }

        public Question create()
        {
            return new Question( this.id,
                    this.type,
                    this.gotoId,
                    this.text,
                    this.pic,
                    this.options );
        }

        private String id;
        private String pic;
        private String gotoId;
        private String text;
        private ValueType type;
        private final List<Option> options;
    }

    /** Creates a regular question. */
    protected Question(String id, ValueType dtype, String gotoId, String text, String pic,
                       Collection<Option> options)
    {
        super( id, gotoId );

        this.TYPE = dtype;
        this.pic = pic;
        this.text = text;
        this.options = new ArrayList<>( options );
    }

    public boolean isReference()
    {
        return false;
    }

    public String getText()
    {
        return this.text;
    }

    /** @see Question::getIdComponents
     * @return the branch this question lives in.
     */
    public String getBranchFromId()
    {
        return buildIdComponents( this.getId() )[ 0 ];
    }

    /** @see Question::getIdComponents
     * @return the data this question produces.
     */
    public String getDataFromId()
    {
        return buildIdComponents( this.getId() )[ 1 ];
    }

    public ValueType getValueType()
    {
        return this.TYPE;
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

    public String getPic()
    {
        return this.pic;
    }

    @Override
    public boolean equals(Object other)
    {
        boolean toret = ( this == other );

        if ( !toret
          && other instanceof final Question Q )
        {
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

    public boolean isEnd()
    {
        return this.getGotoId().equals( ETQ_END );
    }

    public Question copyWithGotoId(String gotoId)
    {
        Question.Builder QB = new Question.Builder();

        QB.setId( this.getId() )
                .setGotoId( gotoId )
                .setText( this.getText() )
                .setPic( this.getPic() )
                .setValueType( this.getValueType() );

        return QB.create();
    }

    /** @see Question::getIdComponents
     * @return the branch this question lives in.
     */
    public static String getBranchFromId(String id)
    {
        return buildIdComponents( id )[ 0 ];
    }

    /** @see Question::getIdComponents
     * @return the data this question produces.
     */
    public static String getDataFromId(String id)
    {
        return buildIdComponents( id )[ 1 ];
    }

    /**
     * ID's in each XML question node are composed by the branch
     * the question lives in and the data it produces.
     * @return a small array of two positions: 0 is the branch,
     * 1 is the data produced.
     */
    private static String[] buildIdComponents(String id)
    {
        final String[] TORET = new String[ 2 ];
        int underPos = id.indexOf( '_' );

        TORET[ 0 ] = id.substring( 0, underPos );
        TORET[ 1 ] = id.substring( underPos + 1 );

        return TORET;
    }

    private static String ETQ_END = "/";

    private final ValueType TYPE;
    private final String pic;
    private final String text;
    private final List<Option> options;
}
