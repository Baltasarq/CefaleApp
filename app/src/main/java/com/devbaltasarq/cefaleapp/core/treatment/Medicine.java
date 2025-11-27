// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import androidx.annotation.NonNull;

import com.devbaltasarq.cefaleapp.core.Identifiable;
import com.devbaltasarq.cefaleapp.core.LocalizedText;
import com.devbaltasarq.cefaleapp.core.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/** Represents a medicine. */
public class Medicine implements Identifiable {
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

            key = key.trim().toUpperCase();

            // Create the id, if not created before
            final BasicId BASIC_ID = createIdRepoIfNeeded().get( key );

            if ( BASIC_ID == null ) {
                this.id = new BasicId( key, name );
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

        /** Returns a list with all ids, ordered by the Id's key.
         * (i.e., creation order).
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
        private static IdsRepo ids;
    }

    /** Creates a new medicine.
      * @param ID the id of this medicine.
      * @param GROUP_ID the id of the medicine group this medicine pertains to.
      * @param adverseEffects adverse effects, as text.
      * @throws IllegalArgumentException if params are null or invalid.
      */
    public Medicine(final Id ID,
                      final MedicineGroup.Id GROUP_ID,
                      int groupPos,
                      LocalizedText adverseEffects,
                      LocalizedText posology,
                      String url)
    {
        if ( ID == null ) {
            throw new IllegalArgumentException( "Medicine: invalid id" );
        }

        if ( GROUP_ID == null ) {
            throw new IllegalArgumentException( "Medicine: " + ID
                                                + ": invalid id group" );
        }

        if ( adverseEffects == null
          || adverseEffects.isEmpty() )
        {
            throw new IllegalArgumentException( "Medicine: " + ID
                                                + " no adverse effects specified" );
        }

        if ( posology == null
          || posology.isEmpty() )
        {
            throw new IllegalArgumentException( "Medicine: " + ID
                    + " no posology specified" );
        }

        if ( url == null
          || url.isEmpty() )
        {
            throw new IllegalArgumentException( "Medicine: " + ID
                                                + ": no url specified" );
        }

        this.id = ID;
        this.groupId = GROUP_ID;
        this.groupPos = groupPos;
        this.adverseEffects = adverseEffects;
        this.posology = posology;
        this.url = url;
    }

    /** @return the group this medicine pertaints to. */
    public Id getId()
    {
        return this.id;
    }

    /** @return the list of adverse effects, as text. */
    public LocalizedText getAdverseEffects()
    {
        return this.adverseEffects;
    }

    /** @return the posology, as text. */
    public LocalizedText getPosology()
    {
        return this.posology;
    }

    /** @return the group id this medicine pertains to. */
    public MedicineGroup.Id getGroupId()
    {
        return this.groupId;
    }

    /** @return the group position of this medicine in the group. */
    public int getGroupPos()
    {
        return this.groupPos;
    }

    /** @return the group this medicine pertains to. */
    public MedicineGroup getGroup()
    {
        return MedicineGroup.getAll().get( this.groupId );
    }

    /** @return the online url. */
    public String getUrl()
    {
        return this.url;
    }

    @Override
    public int hashCode()
    {
        return 7 * Integer.hashCode( this.getId().hashCode() );
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean toret = ( this == obj );

        if ( !toret
          && obj instanceof Medicine other )
        {
            toret = ( this.getId() == other.getId() );
        }

        return toret;
    }

    @Override
    public String toString()
    {
        final String MSG_POSOLOGY = Message.getFor( "medicineMsgPosology" )
                                                .getMsg().getForCurrentLanguage();
        final String MSG_ADVERSE_EFFECTS = Message.getFor( "medicineMsgAdverseEffects" )
                                                .getMsg().getForCurrentLanguage();
        return String.format( Locale.getDefault(),
                             """
                             %s
                             %s:
                             %s
                             %s:
                             %s""",
                                     this.getId().toString(),
                                     MSG_POSOLOGY,
                                     this.getPosology(),
                                     MSG_ADVERSE_EFFECTS,
                                     this.getAdverseEffects()
        );
    }

    /** @return all the medicine objects. */
    @NonNull
    public static Map<Medicine.Id, Medicine> getAll()
    {
        if ( allMedicines == null ) {
            throw new Error( "Medicine.getAll() invoked before loading medicines" );
        }

        return new HashMap<>( allMedicines );
    }

    /** Sets all the medicine objects, probably loaded from XML.
     * This must be called before getAll() can be called.
     * @see TreatmentXMLoader , Medicine::getAll
     * @param medicines all the medicine objects.
     */
    public static void setAll(Map<Medicine.Id, Medicine> medicines)
    {
        if ( allMedicines == null ) {
            allMedicines = new HashMap<>( medicines );
        } else {
            allMedicines.clear();
            allMedicines.putAll( medicines );
        }

        return;
    }
    
    private final Id id;
    private final int groupPos;
    private final MedicineGroup.Id groupId;
    private final LocalizedText posology;
    private final LocalizedText adverseEffects;
    private final String url;
    private static Map<Id, Medicine> allMedicines;
}
