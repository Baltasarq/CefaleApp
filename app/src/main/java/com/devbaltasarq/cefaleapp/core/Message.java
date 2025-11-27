// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.treatment.BasicId;
import com.devbaltasarq.cefaleapp.core.treatment.IdsRepo;
import com.devbaltasarq.cefaleapp.core.treatment.Nameable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Message {
    public static final class Id implements Nameable {
        public static Id EMPTY = new Id( "#ERROR!!" );

        /** Creates a new class id.
          * @param key the string id for the class.
          */
        public Id(String key)
        {
            // Store the id as a basic id, if not created before
            final BasicId BASIC_ID = createIdRepoIfNeeded().get( key );

            if ( BASIC_ID == null ) {
                this.id = new BasicId( key, new LocalizedText( key ) );
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
        public LocalizedText getName()
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

            if ( obj instanceof Message.Id treatmentMessageId ) {
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
        public static Message.Id get(String key)
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
        public static List<Message.Id> getAll()
        {
            final List<Message.Id> TORET = new ArrayList<>( createIdRepoIfNeeded().size() );

            for(final BasicId BASIC_ID: ids.getAll()) {
                TORET.add( IdFromBasicId( BASIC_ID ) );
            }

            return TORET;
        }

        private static Message.Id IdFromBasicId(final BasicId BASIC_ID)
        {
            return new Message.Id( BASIC_ID.getKey() );
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

    public static Message EMPTY = new Message( Id.EMPTY, LocalizedText.EMPTY );

    public Message(Id msgId, LocalizedText msg)
    {
        this.msgId = msgId;
        this.msg = msg;
    }

    /** @return the id of this treatment message. */
    public Id getId()
    {
        return this.msgId;
    }

    /** @return the message for treatment itself.
      * @see LocalizedText
      */
    public LocalizedText getMsg()
    {
        return this.msg;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean toret = false;

        if ( this == o ) {
            toret = true;
        }
        else
        if ( o instanceof Message other ) {
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

    /** @return the treatment message for the current locale. */
    @Override
    public String toString()
    {
        return this.getMsg().getForCurrentLanguage();
    }

    public static void setAll(Map<Id, Message> allMsgs)
    {
        all = new HashMap<>( allMsgs );
    }

    public static Map<Id, Message> getAll()
    {
        if ( all == null ) {
            throw new Error( "Treatment message entries not loaded yet" );
        }

        return all;
    }

    /** return a treatment message, given its id. */
    public static Message getFor(String descTxtId)
    {
        return Message.getAll().get( Message.Id.get( descTxtId ) );
    }

    private final Id msgId;
    private final LocalizedText msg;
    private static Map<Message.Id, Message> all = null;
}
