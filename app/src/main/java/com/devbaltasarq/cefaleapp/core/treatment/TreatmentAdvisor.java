// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/** Determines the treatment to follow, given the morbidities. */
public abstract class TreatmentAdvisor {
    /** Creates a new TreatmentAdvisor object, given the morbidities.
      * @param MORBIDITIES the collection of morbidities.
      */
    public TreatmentAdvisor(final Collection<Morbidity.Id> MORBIDITIES)
    {
        this.morbidities = new ArrayList<>( MORBIDITIES );
    }

    /** @return the list of morbidities. */
    public List<Morbidity.Id> getMorbidities()
    {
        return new ArrayList<>( this.morbidities );
    }

    /** @return the result of the advising process. */
    public abstract List<?> createResultList();

    protected List<Medicine.Id> getMedicineIdsFromGroupList(final List<MedicineGroup> MEDICINE_GROUPS)
    {
        final List<MedicineGroup.Id> GRP_IDS
                            = new ArrayList<>( MEDICINE_GROUPS.size() );

        for(MedicineGroup GRP: MEDICINE_GROUPS) {
            GRP_IDS.add( GRP.getId() );
        }

        return this.getMedicineIdsFromGroupIdList( GRP_IDS );
    }

    protected List<Medicine.Id> getMedicineIdsFromGroupIdList(final List<MedicineGroup.Id> MEDICINE_GROUP_IDS)
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
}
