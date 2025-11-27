// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import com.devbaltasarq.cefaleapp.core.Identifiable;
import com.devbaltasarq.cefaleapp.core.LocalizedText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Morbidity implements Identifiable {
    public static final class Id implements Nameable {
        /** Creates a new medicine id.
         * @param key the char for the group.
         * @param name the name of the group.
         */
        public Id(String key, LocalizedText name)
        {
            if ( key == null
              || key.isBlank() )
            {
                throw new Error( "Id(): empty key" );
            }

            final String KEY = key.trim().toUpperCase();

            // Store the id as a basic id, if not created before
            final BasicId BASIC_ID = createIdRepoIfNeeded().get( KEY );

            if ( BASIC_ID == null ) {
                this.id = new BasicId( KEY, name );
                ids.add( this.id );
            } else {
                this.id = BASIC_ID;
            }
        }

        /** @return the associated char (the key). */
        public String getKey()
        {
            return this.id.getKey();
        }

        /** @return the name of the group. */
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

            if ( obj instanceof Id medicineId ) {
                toret = this.id.equals( medicineId.id );
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
         * @return the Id object with that char.
         */
        public static Id get(String key)
        {
            final BasicId TORET = createIdRepoIfNeeded().get( key );

            if ( TORET == null ) {
                throw new Error( "Id.get(): no id for key: " + key );
            }

            return IdFromBasicId( TORET );
        }

        /** Returns a list with all ids, ordered by the Id's key.
         * @return a list with all the created id's.
         */
        public static List<Id> getAll()
        {
            final List<Id> TORET = new ArrayList<>( createIdRepoIfNeeded().size() );

            for(final BasicId BASIC_ID: ids.getAll() ) {
                TORET.add( IdFromBasicId( BASIC_ID ) );
            }

            return TORET;
        }

        private static IdsRepo createIdRepoIfNeeded()
        {
            if ( ids == null ) {
                ids = new IdsRepo();
            }

            return ids;
        }

        /** Converts a BasicId object to Id.
         * @param BASIC_ID the given basic id.
         * @return the equivalent Id object.
         */
        private static Id IdFromBasicId(final BasicId BASIC_ID)
        {
            return new Id(
                    BASIC_ID.getKey(),
                    BASIC_ID.getName() );
        }

        private final BasicId id;
        private static IdsRepo ids = null;
    }

    public Morbidity(Id id, LocalizedText desc)
    {
        this.id = id;
        this.desc = desc;
        this.advisedGroups = new ArrayList<>();
        this.advisedMedicines = new ArrayList<>();
        this.incompatibleMedicines = new ArrayList<>();
        this.incompatibleGroups = new ArrayList<>();
    }

    /** @return the id of this morbidity. */
    public Id getId()
    {
        return this.id;
    }

    /** @return the description of this morbidity. */
    public LocalizedText getDesc()
    {
        return this.desc;
    }

    /** Sets the incompatible groups.
      * @param ids a collection with the ids of the incompatible groups.
      */
    public void setIncompatibleMedicineGroups(Collection<MedicineGroup.Id> ids)
    {
        this.incompatibleGroups.clear();
        this.incompatibleGroups.addAll( ids );
    }

    /** @return the part of the incompatible medicines: groups of medicines. */
    public List<MedicineGroup.Id> getIncompatibleMedicineGroups()
    {
        return new ArrayList<>( this.incompatibleGroups );
    }

    /** Set the incompatible medicines.
      * @param ids the ids of the incompatible medicines.
      */
    public void setIncompatibleMedicines(Collection<Medicine.Id> ids)
    {
        this.incompatibleMedicines.clear();
        this.incompatibleMedicines.addAll( ids );
    }

    /** @return part of the incompatible medicines: medicines. */
    public List<Medicine.Id> getIncompatibleMedicines()
    {
        return new ArrayList<>( this.incompatibleMedicines );
    }

    /** @return all the incompatible medicines. */
    public List<Medicine.Id> getAllIncompatibleMedicines()
    {
        final List<Medicine.Id> TORET = new ArrayList<>( this.incompatibleMedicines );

        // Collect all the medicines from the incompatible groups.
        for(MedicineGroup.Id groupId: this.incompatibleGroups) {
            final MedicineGroup GROUP = MedicineGroup.getAll().get( groupId );

            for(Medicine medicine: GROUP.getMedicines()) {
                TORET.add( medicine.getId() );
            }
        }

        return TORET;
    }

    /** Set the advised medicine groups.
      * @param ids the ids of the advised medicine groups.
      */
    public void setAdvisedMedicineGroups(Collection<MedicineGroup.Id> ids)
    {
        this.advisedGroups.clear();
        this.advisedGroups.addAll( ids );
    }

    /** @return the advised medicine groups. */
    public List<MedicineGroup.Id> getAdvisedMedicineGroups()
    {
        return new ArrayList<>( this.advisedGroups);
    }

    /** @return the advised medicines. */
    public List<Medicine.Id> getAdvisedMedicines()
    {
        return new ArrayList<>( this.advisedMedicines );
    }

    public void setAdvisedMedicines(Collection<Medicine.Id> ids)
    {
        this.advisedMedicines.clear();
        this.advisedMedicines.addAll( ids );
    }

    /** @return all the incompatible medicines. */
    public List<Medicine.Id> getAllAdvisedMedicines()
    {
        final List<Medicine.Id> TORET = new ArrayList<>( this.advisedMedicines );

        // Collect all the medicines from the advised groups.
        for(MedicineGroup.Id groupId: this.advisedGroups) {
            final MedicineGroup GROUP = MedicineGroup.getAll().get( groupId );

            for(Medicine medicine: GROUP.getMedicines()) {
                TORET.add( medicine.getId() );
            }
        }

        return TORET;
    }

    @Override
    public String toString()
    {
        String name = this.getId().getName().toString();
        String[] nameParts = name.split( " " );

        return nameParts[ 0 ] + ": " + this.getDesc();
    }

    public static Map<Morbidity.Id, Morbidity> getAll()
    {
        if ( allMorbidities == null ) {
            throw new Error( "morbidities not yet loaded" );
        }

        return new HashMap<>( allMorbidities );
    }

    /** Sets all the medicine groups, probably loaded from XML.
     * This must be called before getAll() can be called.
     * @see TreatmentXMLoader , MedicineGroup::getAll
     * @param morbidities all the medicine groups.
     */
    public static void setAll(Map<Id, Morbidity> morbidities)
    {
        if ( allMorbidities == null ) {
            allMorbidities = new HashMap<>( morbidities );
        } else {
            allMorbidities.clear();
            allMorbidities.putAll( morbidities );
        }

        return;
    }

    private final Id id;
    private final LocalizedText desc;
    private final List<MedicineGroup.Id> incompatibleGroups;
    private final List<MedicineGroup.Id> advisedGroups;
    private final List<Medicine.Id> incompatibleMedicines;
    private final List<Medicine.Id> advisedMedicines;

    private static Map<Morbidity.Id, Morbidity> allMorbidities = null;
}
