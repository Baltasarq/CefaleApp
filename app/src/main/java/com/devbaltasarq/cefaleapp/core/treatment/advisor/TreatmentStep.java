// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment.advisor;


import com.devbaltasarq.cefaleapp.core.MultiLanguageWrapper;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/** A Step with analgesics. */
public class TreatmentStep {
    /** Creates a treatment step.
     * @param desc the description of the step.
     * @param groupId the id of the medicine group to use in this step.
     */
    public TreatmentStep(TreatmentMessage desc, MedicineGroup.Id groupId)
    {
        final MedicineGroup GRP = Objects.requireNonNull(
                                        MedicineGroup.getAll().get( groupId ) );
        final List<Medicine.Id> MEDICINE_IDS = new ArrayList<>( GRP.size() );

        MEDICINE_IDS.addAll(
                GRP.getMedicines().stream().map( Medicine::getId ).
                        collect( Collectors.toList() ));

        this.initFrom( desc, MEDICINE_IDS.toArray( new Medicine.Id[ 0 ] ));
    }

    /** Creates a treatment step.
      * @param desc the description of the step.
      * @param medicineIds the ids of the medicine to use in this step.
      */
    public TreatmentStep(TreatmentMessage desc, Medicine.Id[] medicineIds)
    {
        this.initFrom( desc, medicineIds );
    }

    private void initFrom(TreatmentMessage desc, Medicine.Id[] medicineIds)
    {
        this.desc = desc;

        if ( medicineIds != null ) {
            this.medicineIds = new ArrayList<>( Arrays.asList( medicineIds ) );
        } else {
            this.medicineIds = new ArrayList<>( 10 );
        }

        return;
    }

    /** Adds a new medicine to the collection of medicines.
      * @param MED_ID the id of the medicine to add.
      */
    public void add(final Medicine.Id MED_ID)
    {
        this.medicineIds.add( MED_ID );
    }

    /** @return the description of this itep. */
    public String getDesc()
    {
        return this.desc.toString();
    }

    /** @return the medicine id's for this step. */
    public List<Medicine.Id> getMedicineIds()
    {
        return new ArrayList<>( this.medicineIds );
    }

    private TreatmentMessage desc;
    private List<Medicine.Id> medicineIds;
}
