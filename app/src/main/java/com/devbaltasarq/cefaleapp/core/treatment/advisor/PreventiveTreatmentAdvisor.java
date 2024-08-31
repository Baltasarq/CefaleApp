// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment.advisor;


import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;
import com.devbaltasarq.cefaleapp.core.treatment.TreatmentAdvisor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/** Creates a list of advised medicines for a preventive treatment. */
public class PreventiveTreatmentAdvisor extends TreatmentAdvisor {
    /** Creates a new PreventiveTreatmentAdvisor object, given the morbidities.
     * @param MORBIDITIES the collection of morbidities.
     */
    public PreventiveTreatmentAdvisor(final Collection<Morbidity.Id> MORBIDITIES)
    {
        super( MORBIDITIES );

        this.advisedMedicineIds = new HashSet<>( MORBIDITIES.size() );
    }

    /** @return the collection of compatible medicines, as a list of medicine. */
    public List<Medicine> createResultList()
    {
        if ( this.advisedMedicineIds.isEmpty() ) {
            // Add all existing medicines
            this.advisedMedicineIds.addAll( Medicine.getAll().keySet() );

            for (final Morbidity.Id MORBIDITY_ID: this.getMorbidities()) {
                final Morbidity MORBIDITY = Objects.requireNonNull(
                        Morbidity.getAll().get( MORBIDITY_ID ) );

                // Remove incompatible medicines
                MORBIDITY.getIncompatibleMedicines().forEach(
                        this.advisedMedicineIds::remove );

                this.getMedicineIdsFromGroupIdList(
                        MORBIDITY.getIncompatibleMedicineGroups() ).forEach(
                            this.advisedMedicineIds::remove );
            }
        }

        return new ArrayList<>(
                Util.objListFromIdList(
                        Medicine.getAll(),
                        this.advisedMedicineIds ));
    }

    private final Set<Medicine.Id> advisedMedicineIds;
}
