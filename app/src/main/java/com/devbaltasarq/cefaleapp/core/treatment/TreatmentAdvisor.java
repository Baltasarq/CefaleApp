// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment;


import com.devbaltasarq.cefaleapp.core.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/** Determines the treatment to follow, given the morbidities. */
public class TreatmentAdvisor {
    /** Creates a new Treatment object, given the morbidities.
      * @param morbidities the collection of morbidities.
      */
    public TreatmentAdvisor(Collection<Morbidity.Id> morbidities)
    {
        this.morbidities = new ArrayList<>( morbidities );
        this.resultingMedicines = new HashSet<>( morbidities.size() );
    }

    /** @return the collection of compatible medicines, as a list of medicine. */
    public List<Medicine> determineMedicines()
    {
        if ( this.resultingMedicines.isEmpty() ) {
            // Add all existing medicines
            this.resultingMedicines.addAll( Medicine.getAll().keySet() );

            for (final Morbidity.Id MORBIDITY_ID: this.morbidities) {
                final Morbidity MORBIDITY = Objects.requireNonNull(
                        Morbidity.getAll().get( MORBIDITY_ID ) );

                // Remove incompatible medicines
                MORBIDITY.getIncompatibleMedicines().forEach(
                                            this.resultingMedicines::remove );

                this.getMedicineIdsFromGroupId(
                        MORBIDITY.getIncompatibleMedicineGroups() ).forEach(
                                                this.resultingMedicines::remove );
            }
        }

        return new ArrayList<>(
                Util.getObjListFromIdList(
                        Medicine.getAll(),
                        this.resultingMedicines ) );
    }

    private List<Medicine.Id> getMedicineIdsFromGroupId(final List<MedicineGroup.Id> MEDICINE_GROUP_IDS)
    {
        final List<Medicine.Id> TORET = new ArrayList<>( MEDICINE_GROUP_IDS.size() * 3 );

        for(final MedicineGroup.Id ID: MEDICINE_GROUP_IDS) {
            final MedicineGroup GROUP = MedicineGroup.getAll().get( ID );
            final List<Medicine> MEDICINES = Objects.requireNonNull( GROUP ).getMedicines();

            TORET.addAll(
                    MEDICINES.stream().map( Medicine::getId )
                            .collect( Collectors.toList() ) );
        }

        return TORET;
    }

    private final List<Morbidity.Id> morbidities;
    private final Set<Medicine.Id> resultingMedicines;
}
