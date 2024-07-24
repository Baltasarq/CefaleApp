// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** This groups a set of groups. */
public class MedicineClass implements Identifiable {
    public static final class Id implements Nameable {
        /** Creates a new class id.
         * @param key the string id for the class.
         * @param name the name of the class.
         */
        public Id(String key, String name)
        {
            // Store the id as a basic id, if not created before
            final BasicId BASIC_ID = createIdRepoIfNeeded().get( key );

            if ( BASIC_ID == null ) {
                this.id = new BasicId( key, name );
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

            if ( obj instanceof Id medicineClassId ) {
                toret = this.id.equals( medicineClassId.id );
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
        public static Id get(String key)
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
        public static List<Id> getAll()
        {
            final List<Id> TORET = new ArrayList<>( createIdRepoIfNeeded().size() );

            for(final BasicId BASIC_ID: ids.getAll()) {
                TORET.add( IdFromBasicId( BASIC_ID ) );
            }

            return TORET;
        }

        private static Id IdFromBasicId(final BasicId BASIC_ID)
        {
            return new Id( BASIC_ID.getKey(), BASIC_ID.getName() );
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

    public MedicineClass(Id clsId)
    {
        this.clsId = clsId;
        this.groups = new HashMap<>( 4 );
    }

    /** @return the class id. */
    public Id getId()
    {
        return this.clsId;
    }

    /** Inserts a class for a given key in the class.
      * Throws an error if a group already exists for that key.
      * @param mgrp the given group.
      */
    public void insert(MedicineGroup mgrp)
    {
        final MedicineGroup.Id GROUP_KEY = mgrp.getId();
        final MedicineGroup EXISTING_GROUP = this.groups.get( GROUP_KEY );


        // Ensure there was not a previous medicine set
        if ( EXISTING_GROUP != null ) {
            throw new Error(
                        String.format( "MedicineClass.add(%s): already existing %s",
                            GROUP_KEY,
                            EXISTING_GROUP.getId().toString() ) );
        }

        this.groups.put( GROUP_KEY, mgrp );
    }

    /** @return the groups in this class. */
    public List<MedicineGroup> getGroups()
    {
        return new ArrayList<>( this.groups.values() );
    }

    /** @return the number of groups in this class. */
    public int size()
    {
        return this.groups.size();
    }

    /** @return the group for the given key. */
    public @NonNull MedicineGroup get(MedicineGroup.Id key)
    {
        final MedicineGroup TORET = this.groups.get( key );

        if ( TORET == null ) {
            throw new Error( "not found medicine for: " + key );
        }

        return TORET;
    }

    @Override
    public int hashCode()
    {
        return this.clsId.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean toret = false;

        if ( obj instanceof MedicineClass mcls ) {
            toret = this.getId().equals( mcls.getId() );
        }

        return toret;
    }

    @Override
    public String toString()
    {
        StringBuilder toret = new StringBuilder( this.getId().toString() );

        int i = 1;
        for(MedicineGroup mgrp: this.groups.values()) {
            toret.append( '\n' );
            toret.append( i );
            toret.append( ". " );
            toret.append( mgrp.toString() );
            ++i;
        }

        return toret.toString();
    }

    /** Sets all the medicine classes, probably loaded from XML.
      * This must be called before getAll() can be called.
      * @see TreatmentXMLoader , MedicineGroup::getAll
      * @param medicineClasses all the medicine classes.
      */
    public static void setAll(Map<Id, MedicineClass> medicineClasses)
    {
        if ( allClasses == null ) {
            allClasses = new HashMap<>( medicineClasses );
        } else {
            allClasses.clear();
            allClasses.putAll( medicineClasses );
        }

        return;
    }

    /** @return all the existing medicine classes. */
    public static Map<MedicineClass.Id, MedicineClass> getAll()
    {
        if ( allClasses == null ) {
            throw new Error( "MedicineGroup.getAll(): trying to collect before loading" );
        }

        return new HashMap<>( allClasses );
    }

    private final Id clsId;
    private final Map<MedicineGroup.Id, MedicineGroup> groups;
    private static Map<Id, MedicineClass> allClasses = null;
}
