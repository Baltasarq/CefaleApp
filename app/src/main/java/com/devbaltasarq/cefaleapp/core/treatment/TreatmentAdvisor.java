// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/** Determines the treatment to follow, given the morbidities. */
public class TreatmentAdvisor {
    /** Creates a new Treatment object, given the morbidities.
      * @param morbidities the collection of morbidities.
      */
    public TreatmentAdvisor(Collection<Morbidity> morbidities)
    {
        this.morbidities = new ArrayList<>( morbidities );
        this.resultingMedicines = new ArrayList<>();
    }

    /** @return the collection of compatible medicines, as a list of medicine. */
    public List<Medicine> determineMedicines()
    {
        if ( this.resultingMedicines.isEmpty() ) {
            for (final Morbidity MORBIDITY: this.morbidities) {
                // Add advised medicines
                this.resultingMedicines.addAll(
                        this.getMedicinesFromId(
                                MORBIDITY.getAdvisedMedicines() ));
                this.resultingMedicines.addAll(
                        this.getMedicinesFromGroupId(
                                MORBIDITY.getAdvisedMedicineGroups() ));
            }

            for (final Morbidity MORBIDITY: this.morbidities) {
                // Remove incompatible medicines
                this.resultingMedicines.removeAll(
                        this.getMedicinesFromId(
                                MORBIDITY.getIncompatibleMedicines() ));
                this.resultingMedicines.removeAll(
                        this.getMedicinesFromGroupId(
                                MORBIDITY.getIncompatibleMedicineGroups() ));
            }
        }

        return this.resultingMedicines;
    }

    private List<Medicine> getMedicinesFromId(final List<Medicine.Id> MEDICINE_IDS)
    {
        final List<Medicine> TORET = new ArrayList<>( MEDICINE_IDS.size() );

        for(final Medicine.Id ID: MEDICINE_IDS) {
            TORET.add( Objects.requireNonNull( Medicine.getAll().get( ID ) ) );
        }

        return TORET;
    }

    private List<Medicine> getMedicinesFromGroupId(final List<MedicineGroup.Id> MEDICINE_GROUP_IDS)
    {
        final List<Medicine> TORET = new ArrayList<>( MEDICINE_GROUP_IDS.size() * 3 );

        for(final MedicineGroup.Id ID: MEDICINE_GROUP_IDS) {
            final MedicineGroup GROUP = MedicineGroup.getAll().get( ID );
            final List<Medicine> MEDICINES = Objects.requireNonNull( GROUP ).getMedicines();

            TORET.addAll( MEDICINES );
        }

        return TORET;
    }

    private List<Morbidity> morbidities;
    private List<Medicine> resultingMedicines;
}
