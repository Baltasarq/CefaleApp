// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment.advisor;


import com.devbaltasarq.cefaleapp.core.MultiLanguageWrapper;
import com.devbaltasarq.cefaleapp.core.treatment.BasicId;
import com.devbaltasarq.cefaleapp.core.treatment.IdsRepo;
import com.devbaltasarq.cefaleapp.core.treatment.Nameable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class TreatmentMessage {
    public static final class Id implements Nameable {
        /** Creates a new class id.
          * @param key the string id for the class.
          */
        public Id(String key)
        {
            // Store the id as a basic id, if not created before
            final BasicId BASIC_ID = createIdRepoIfNeeded().get( key );

            if ( BASIC_ID == null ) {
                this.id = new BasicId( key, key );
                ids.add( this.id );
            } else {
                this.id = BASIC_ID;
            }
        }

        /** @return the key. */
        public String getKey()
        {
            return this.id.getKey();
        }

        /** @return the name of the class. */
        @Override
        public String getName()
        {
            return this.id.getName();
        }

        @Override
        public int hashCode()
        {
            return this.id.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            boolean toret = false;

            if ( obj instanceof TreatmentMessage.Id treatmentMessageId ) {
                toret = this.id.equals( treatmentMessageId.id );
            }

            return toret;
        }

        @Override
        public String toString()
        {
            return this.id.toString();
        }

        /** Get the id associated with the key.
         * @param key the given key.
         * @return the Id object with that key.
         */
        public static TreatmentMessage.Id get(String key)
        {
            final BasicId TORET = createIdRepoIfNeeded().get( key );

            if ( TORET == null ) {
                throw new Error( "Id.get(): no id for key: " + key );
            }

            return IdFromBasicId( TORET );
        }

        /** Returns a list with all class ids, ordered by the Id's key.
         * @return a list with all the created id's.
         */
        public static List<TreatmentMessage.Id> getAll()
        {
            final List<TreatmentMessage.Id> TORET = new ArrayList<>( createIdRepoIfNeeded().size() );

            for(final BasicId BASIC_ID: ids.getAll()) {
                TORET.add( IdFromBasicId( BASIC_ID ) );
            }

            return TORET;
        }

        private static TreatmentMessage.Id IdFromBasicId(final BasicId BASIC_ID)
        {
            return new TreatmentMessage.Id( BASIC_ID.getKey() );
        }

        private static IdsRepo createIdRepoIfNeeded()
        {
            if ( ids == null ) {
                ids = new IdsRepo();
            }

            return ids;
        }

        private final BasicId id;
        private static IdsRepo ids = null;
    }

    public TreatmentMessage(Id msgId, String lang, String msg)
    {
        this.lang = lang.trim().toLowerCase();
        this.msgId = msgId;
        this.msg = msg;
    }

    /** @return the id of this treatment message. */
    public Id getId()
    {
        return this.msgId;
    }

    /** @return the message for treatment itself. */
    public String getMsg()
    {
        return this.msg;
    }

    /** @return the lang for this message entry. */
    public String getLang()
    {
        return this.lang;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean toret = false;

        if ( this == o ) {
            toret = true;
        }
        else
        if ( o instanceof TreatmentMessage other ) {
            toret = Objects.equals( this.getId(), other.getId() )
                    && Objects.equals( this.getMsg(), other.getMsg() );
        }

        return toret;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.getId(), this.getMsg() );
    }

    @Override
    public String toString()
    {
        return this.getMsg();
    }

    public static void setAll(Map<String, Map<Id, TreatmentMessage>> allMsgs)
    {
        all = new MultiLanguageWrapper<>( allMsgs );
    }

    public static MultiLanguageWrapper<Map<Id, TreatmentMessage>> getAll()
    {
        if ( all == null ) {
            throw new Error( "Treatment message entries not loaded yet" );
        }

        return all;
    }

    /** return a treatment message, given its id and the languahe. */
    public static TreatmentMessage getFor(MultiLanguageWrapper.Lang lang, String descTxtId)
    {
        final TreatmentMessage.Id DESC_ID = TreatmentMessage.Id.get( descTxtId );

        return TreatmentMessage.getAll().getForLang( lang ).get( DESC_ID );
    }

    private final Id msgId;
    private final String lang;
    private final String msg;
    private static MultiLanguageWrapper<Map<TreatmentMessage.Id, TreatmentMessage>> all = null;
}
