// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire.form;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/** Represents a question in the form. */
public class Question extends BasicQuestion {
    /** Builds a question. */
    public static class Builder {
        public Builder()
        {
            this.num = 0;
            this.gotoId = "";
            this.pic = "";
            this.summary = "";
            this.type = ValueType.BOOL;
            this.options = new ArrayList<>();
        }

        public Builder setNum(int num)
        {
            this.num = num;
            return this;
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

        public Builder setSummary(String id)
        {
            this.summary = id.trim();
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
            return new Question(
                    this.num,
                    this.id,
                    this.type,
                    this.gotoId,
                    this.text,
                    this.pic,
                    this.summary,
                    this.options );
        }

        private int num;
        private String id;
        private String pic;
        private String summary;
        private String gotoId;
        private String text;
        private ValueType type;
        private final List<Option> options;
    }

    /** Creates a regular question. */
    protected Question(int num, String id, ValueType dtype, String gotoId,
                       String text, String pic, String summary,
                       Collection<Option> options)
    {
        super( id, gotoId );

        this.num = num;
        this.TYPE = dtype;
        this.PIC = pic;
        this.TEXT = text;
        this.SUMMARY = summary;
        this.OPTS = new ArrayList<>( options );
    }

    /** @return is this a reference? Objects of Question are never.
      * @see BasicQuestion, ReferenceQuestion
      */
    public boolean isReference()
    {
        return false;
    }

    public String getText()
    {
        return this.TEXT;
    }

    public String getSummary()
    {
        return this.SUMMARY;
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

    /** @return the type of the response. Typical: bool, yes / no.*/
    public ValueType getValueType()
    {
        return this.TYPE;
    }

    /** @return the total number of options. */
    public int getNumOptions()
    {
        return this.OPTS.size();
    }

    /** Returns an option, given its order.
      * @param id the order of the option.
      * @return the Option object.
      * @see Option, Question::getOptions, Question::getNumOptions
      */
    public Option getOption(int id)
    {
        return this.OPTS.get( id );
    }

    /** @return the different options available. */
    public List<Option> getOptions()
    {
        return new ArrayList<>( this.OPTS);
    }

    /** @return the picture name to use in this question. */
    public String getPic()
    {
        return this.PIC;
    }

    /** @return the question number. */
    public int getNum()
    {
        return this.num;
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
        return ETQ_END.contains( this.getGotoId() );
    }

    public Question copyWith(int num, String branchId, String gotoId)
    {
        String newId = "";

        if ( branchId == null ) {
            newId = this.getId();
        } else {
            newId = branchId + "_" + this.getDataFromId();
        }

        if ( gotoId == null ) {
            gotoId = this.getGotoId();
        }

        final Question.Builder QB = new Question.Builder();

        QB.setId( newId )
                .setNum( num )
                .setGotoId( gotoId )
                .setText( this.getText() )
                .setSummary( this.getSummary() )
                .setPic( this.getPic() )
                .setValueType( this.getValueType() );

        for(Option opt: this.getOptions()) {
            QB.addOption( new Option( opt.getText(), opt.getValue() ) );
        }

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

    private static String ETQ_END = "/\\-";

    private final int num;
    private final ValueType TYPE;
    private final String PIC;
    private final String TEXT;
    private final String SUMMARY;
    private final List<Option> OPTS;
}
