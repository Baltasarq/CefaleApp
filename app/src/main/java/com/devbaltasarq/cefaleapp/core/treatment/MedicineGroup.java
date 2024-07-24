// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/** This groups a set of medicines. */
public class MedicineGroup implements Identifiable {
    public static final class Id implements Nameable {
        /** Creates a new group id.
          * @param key the char for the group.
          * @param name the name of the group.
          */
        public Id(String key, String name)
        {
            final String KEY = key.toUpperCase();

            // Store the id as a basic id, if not created before
            final BasicId BASIC_ID = createIdRepoIfNeeded().get( KEY );

            if ( BASIC_ID == null ) {
                this.id = new BasicId( KEY, name );
                ids.add( this.id );
            } else {
                this.id = BASIC_ID;
            }
        }

        /** @return the associated key. */
        public String getKey()
        {
            return this.id.getKey();
        }

        /** @return the name of the group. */
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

            if ( obj instanceof Id medicineGroupId ) {
                toret = this.id.equals( medicineGroupId.id );
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
            final String KEY = key.toUpperCase();
            final BasicId TORET = createIdRepoIfNeeded().get( KEY );

            if ( TORET == null ) {
                throw new Error( "Id.get(): no id for key: " + key );
            }

            return IdFromBasicId( TORET );
        }

        /** Returns a list with all ids, ordered by the Id's key.
         * (i.e., creation order).
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
            return new Id( BASIC_ID.getKey(),
                            BASIC_ID.getName() );
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

    public MedicineGroup(Id grpId, MedicineClass.Id clsId)
    {
        this.grpId = grpId;
        this.clsId = clsId;
        this.medicines = new HashMap<>( 4 );
    }

    /** @return the group id. */
    public Id getId()
    {
        return this.grpId;
    }

    /** @return the medicine class id. */
    public MedicineClass.Id getClsId()
    {
        return this.clsId;
    }

    /** @return the medicine class object. */
    public MedicineClass getCls()
    {
        return Objects.requireNonNull( MedicineClass.getAll().get( this.clsId ) );
    }

    /** Inserts a medicine for a given key in the group.
      * Throws an error if a medicine already exists for that key.
      * @param key the key for this medicine in the group.
      * @param m the given medicine.
      */
    public void insert(int key, Medicine m)
    {
        final Medicine EXISTING_MEDICINE = this.medicines.get( key );
        final int MEDICINE_KEY = m.getGroupPos();

        // Ensure it honors the key in the given medicine
        if ( key != MEDICINE_KEY ) {
            throw new Error( "trying to set medicine in "
                            + key
                            + " while medicine says to have key: " + MEDICINE_KEY );
        }

        // Ensure there was not a previous medicine set
        if ( EXISTING_MEDICINE != null ) {
            throw new Error(
                        String.format( "MedicineGroup.add(%d, %s): already existing %s",
                            key,
                            m.getId().toString(),
                            EXISTING_MEDICINE.getId().toString() ) );
        }

        this.medicines.put( key, m );
    }

    /** @return the medicines in this group. */
    public List<Medicine> getMedicines()
    {
        return new ArrayList<>( this.medicines.values() );
    }

    /** @return the number of medicines in this group. */
    public int size()
    {
        return this.medicines.size();
    }

    /** @return the medicine for the given key. */
    public @NonNull Medicine get(int key)
    {
        final Medicine TORET = this.medicines.get( key );

        if ( TORET == null ) {
            throw new Error( "not found medicine for: " + key );
        }

        return TORET;
    }

    @Override
    public int hashCode()
    {
        return this.grpId.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean toret = false;

        if ( obj instanceof MedicineGroup mgroup ) {
            toret = this.getId().equals( mgroup.getId() );
        }

        return toret;
    }

    @Override
    public String toString()
    {
        StringBuilder toret = new StringBuilder( this.getId().toString() );

        int i = 1;
        for(Medicine m: this.medicines.values()) {
            toret.append( '\n' );
            toret.append( i );
            toret.append( ". " );
            toret.append( m.toString() );
            ++i;
        }

        return toret.toString();
    }

    /** Sets all the medicine groups, probably loaded from XML.
      * This must be called before getAll() can be called.
      * @see TreatmentXMLoader , MedicineGroup::getAll
      * @param medicineGroups all the medicine groups.
      */
    public static void setAll(Map<Id, MedicineGroup> medicineGroups)
    {
        if ( allGroups == null ) {
            allGroups = new HashMap<>( medicineGroups );
        } else {
            allGroups.clear();
            allGroups.putAll( medicineGroups );
        }

        return;
    }

    /** @return all the existing medicine groups. */
    public static Map<MedicineGroup.Id, MedicineGroup> getAll()
    {
        if ( allGroups == null ) {
            throw new Error( "MedicineGroup.getAll(): trying to collect before loading" );
        }

        return new HashMap<>( allGroups );
    }

    private final Id grpId;
    private final MedicineClass.Id clsId;
    private final Map<Integer, Medicine> medicines;
    private static Map<Id, MedicineGroup> allGroups = null;
}
