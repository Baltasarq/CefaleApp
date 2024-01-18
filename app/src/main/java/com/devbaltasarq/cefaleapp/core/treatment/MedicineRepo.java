// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/** A collection of medicines. */
public final class MedicineRepo implements Iterable<Medicine> {
    /** Creates a repo of medicines. */
    private MedicineRepo()
    {
        this.medicineGroups = new HashMap<>();
        this.medicinesById = new HashMap<>();
        this.medicines = new ArrayList<>();
        this.build();
    }
    
    /** Build all the infraestructure for the repo to work. */
    private void build()
    {
        // Medicine groups
        for(MedicineGroup grp: MedicineGroup.collectAll().values()) {
            this.medicineGroups.put( grp.getId(), grp );
        }

        // Medicines by id
        for(Medicine m: Medicine.collectAll().values()) {
            this.medicinesById.put( m.getId(), m );
        }
        
        this.syncMedicinesList();
    }

    /** When the map medicinesById changes, the list must be kept in sync. */
    private void syncMedicinesList()
    {
        this.medicines.clear();
        this.medicines.addAll( this.medicinesById.values() );
    }

    /** @return the medicine group, by the given group id.
      * @param id the id of the group.
      */
    public MedicineGroup getGroup(MedicineGroup.Id id)
    {
        return this.medicineGroups.get( id );
    }

    /** @return a list of all medicine groups. */
    public List<MedicineGroup> getAllGroups()
    {
        return new ArrayList<>( this.medicineGroups.values() );
    }

    /** @return the medicine, by the given name.
      * @param id the id of the medicine.
      */
    public Medicine getById(Medicine.Id id)
    {
        return this.medicinesById.get( id );
    }

    @NonNull @Override
    public Iterator<Medicine> iterator() {
        return this.medicines.iterator();
    }

    /** @return the number of medicines. */
    public int size()
    {
        return this.medicines.size();
    }

    /** @return the medicine at position i. */
    public Medicine get(int i)
    {
        if ( i < 0
          || i >= this.size() )
        {
            throw new Error( "MedicineRepo.get(): " + i + "/" + this.size() );
        }

        return this.medicines.get( i );
    }

    @Override
    public String toString()
    {
        StringBuilder toret = new StringBuilder( 20 * this.medicines.size() );

        for(Medicine m: this.medicines) {
            toret.append( m.toString() );
        }

        return toret.toString();
    }

    private final Map<MedicineGroup.Id, MedicineGroup> medicineGroups;
    private final Map<Medicine.Id, Medicine> medicinesById;
    private final List<Medicine> medicines;
    
    private static MedicineRepo repo = null;
    
    /** @return get the only instance. */
    public MedicineRepo get()
    {
        if ( repo == null ) {
            repo = new MedicineRepo();
        }
        
        return repo;
    }
}
