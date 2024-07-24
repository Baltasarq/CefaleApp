// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment;


import com.devbaltasarq.cefaleapp.core.MultiLanguageWrapper;
import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineRepo;
import com.devbaltasarq.cefaleapp.core.treatment.advisor.TreatmentMessage;
import com.devbaltasarq.cefaleapp.core.treatment.advisor.TreatmentStep;

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
        this.resPrevMedicineIds = new HashSet<>( morbidities.size() );
        this.symptomaticTreatmentSteps = new ArrayList<>( MedicineClass.getAll().size() * 3 );
    }

    /** @return the collection of compatible medicines, as a list of medicine. */
    public List<Medicine> determinePrevMedicines()
    {
        if ( this.resPrevMedicineIds.isEmpty() ) {
            // Add all existing medicines
            this.resPrevMedicineIds.addAll( Medicine.getAll().keySet() );

            for (final Morbidity.Id MORBIDITY_ID: this.morbidities) {
                final Morbidity MORBIDITY = Objects.requireNonNull(
                        Morbidity.getAll().get( MORBIDITY_ID ) );

                // Remove incompatible medicines
                MORBIDITY.getIncompatibleMedicines().forEach(
                                            this.resPrevMedicineIds::remove );

                this.getMedicineIdsFromGroupIdList(
                        MORBIDITY.getIncompatibleMedicineGroups() ).forEach(
                                                this.resPrevMedicineIds::remove );
            }
        }

        return new ArrayList<>(
                Util.objListFromIdList(
                        Medicine.getAll(),
                        this.resPrevMedicineIds ));
    }

    /** @return the collection of compatible medicines, as a list of medicine. */
    public List<TreatmentStep> determineAnalgMedicines(final MigraineRepo REPO)
    {
        if ( this.symptomaticTreatmentSteps.isEmpty() ) {
            final MultiLanguageWrapper.Lang LANG_ES = MultiLanguageWrapper.Lang.es;
            final boolean IS_PAIN_LOW_FREQUENCY = this.morbidities.contains(
                    Morbidity.Id.get( "PAIN_LOW_FREQUENCY" ) );

            final boolean IS_PAIN_INTENSE = this.morbidities.contains(
                            Morbidity.Id.get( "PAIN_INTENSE" ) );
            final boolean AINE_ALLERGY =
                    this.morbidities.contains(
                            Morbidity.Id.get( "ALLERGY_ACETILSALILICO_ACID" ) );
            final boolean SULFAMID_ALLERGY =
                    this.morbidities.contains(
                            Morbidity.Id.get( "ALLERGY_ANTIBIOTIC" ) );
            final MedicineGroup.Id GRP_AINE_ID = Objects.requireNonNull(
                                                    MedicineGroup.Id.get( "AINE" ) );
            final MedicineGroup.Id GRP_TRIPTAN_ID = Objects.requireNonNull(
                                                    MedicineGroup.Id.get( "TRIPTANES" ) );
            final MedicineGroup.Id GRP_ERGOTICS_ID = Objects.requireNonNull(
                    MedicineGroup.Id.get( "ERGOTICOS" ) );
            final Medicine.Id ID_METAMIZOL = Objects.requireNonNull(
                                            Medicine.Id.get( "METAMIZOL" ) );
            final Medicine.Id ID_TRAMADOL = Objects.requireNonNull(
                                            Medicine.Id.get( "TRAMADOL" ) );
            final Medicine.Id ID_RIMEGEPANT = Objects.requireNonNull(
                    Medicine.Id.get( "RIMEGEPANT" ) );
            final Medicine.Id ID_LASMIDITAN = Objects.requireNonNull(
                    Medicine.Id.get( "LASMIDITAN" ) );
            final TreatmentMessage MSG_USE_TRIPTAN = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "useTRIPTAN" ));
            final TreatmentMessage MSG_START_WITH_AINE = Objects.requireNonNull(
                                            TreatmentMessage.getFor( LANG_ES, "startWithAine" ));
            final TreatmentMessage MSG_USE_ERGOTICS_AFTER_AINE = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "useErgoticsAfterAINE" ));
            final TreatmentMessage MSG_USE_RIMEGEPANT_LASMIDITAN = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "useRimegepantLasmiditan" ));
            final TreatmentMessage MSG_USE_METAMIZOL = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "useMetamizol" ));
            final TreatmentMessage MSG_USE_TRAMADOL_IF_NOTHING_ELSE_WORKS = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "useTramadolIfNothingElseWorks" ));
            final TreatmentMessage MSG_START_WITH_TRIPTAN = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "startWithTRIPTAN" ));
            final TreatmentMessage MSG_COMBINE_TRIPTAN_WITH_AINE = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "combineTRIPTANWithAINE" ));
            final TreatmentMessage MSG_USE_ERGOTICS = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "useERGOTICS" ));
            final TreatmentMessage MSG_COMBINE_ERGOTICS_WITH_AINE = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "combineERGOTICSWithAINE" ));
            final TreatmentMessage MSG_COMBINE_RIMEGEPANT_LASMIDITAN_WITH_AINE = Objects.requireNonNull(
                    TreatmentMessage.getFor( LANG_ES, "combineRimegepantLasmiditanWithAINE" ));

            // Create a list with all analgesic medicines
            this.symptomaticTreatmentSteps.clear();

            if ( !IS_PAIN_INTENSE ) {
                // Moderate pain
                if ( !AINE_ALLERGY ) {
                    // Start with AINE
                    this.symptomaticTreatmentSteps.add(
                            new TreatmentStep( MSG_START_WITH_AINE, GRP_AINE_ID ) );

                    // Ergotics can be usefult if migraine is esporadic or of low frequency.
                    if ( IS_PAIN_LOW_FREQUENCY ) {
                        this.symptomaticTreatmentSteps.add(
                            new TreatmentStep( MSG_USE_ERGOTICS_AFTER_AINE, GRP_ERGOTICS_ID ) );
                    }
                } else {
                    // Allergy to AAS or AINE
                    if ( !SULFAMID_ALLERGY ) {
                        // Empezar con Triptán
                        this.symptomaticTreatmentSteps.add(
                                new TreatmentStep( MSG_USE_TRIPTAN, GRP_TRIPTAN_ID ) );
                    }

                    // If Triptan goes sideways or sulfamyd allergy present.
                    // Rimegepant, Lasmiditán
                    this.symptomaticTreatmentSteps.add(
                            new TreatmentStep( MSG_USE_RIMEGEPANT_LASMIDITAN,
                                    new Medicine.Id[]{ ID_RIMEGEPANT, ID_LASMIDITAN } ) );
                }
            } else {
                // Intense pain
                if ( !SULFAMID_ALLERGY ) {
                    // Start with triptán
                    this.symptomaticTreatmentSteps.add(
                            new TreatmentStep( MSG_START_WITH_TRIPTAN, GRP_TRIPTAN_ID ) );

                    if ( !AINE_ALLERGY ) {
                        // You can combine TRIPTAN with AINE
                        this.symptomaticTreatmentSteps.add(
                                new TreatmentStep( MSG_COMBINE_TRIPTAN_WITH_AINE, GRP_AINE_ID ) );
                    }
                } else {
                    // Ergotics might be useful if migraine is esporadic or of low frequency.
                    if ( IS_PAIN_LOW_FREQUENCY ) {
                        this.symptomaticTreatmentSteps.add(
                            new TreatmentStep( MSG_USE_ERGOTICS, GRP_ERGOTICS_ID ) );
                    }

                    if ( !AINE_ALLERGY ) {
                        this.symptomaticTreatmentSteps.add(
                                new TreatmentStep( MSG_COMBINE_ERGOTICS_WITH_AINE, GRP_AINE_ID ) );
                    }

                    // Rimegepant, Lasmiditán
                    this.symptomaticTreatmentSteps.add(
                            new TreatmentStep( MSG_USE_RIMEGEPANT_LASMIDITAN,
                                    new Medicine.Id[]{ ID_RIMEGEPANT, ID_LASMIDITAN } ) );

                    if ( !AINE_ALLERGY ) {
                        this.symptomaticTreatmentSteps.add(
                                new TreatmentStep( MSG_COMBINE_RIMEGEPANT_LASMIDITAN_WITH_AINE, GRP_AINE_ID ) );
                    }
                }
            }

            // Other analgesics: Metamizol
            this.symptomaticTreatmentSteps.add(
                    new TreatmentStep( MSG_USE_METAMIZOL,
                            new Medicine.Id[]{ ID_METAMIZOL } ) );

            // If nothing else works, Tramadol (try not)
            this.symptomaticTreatmentSteps.add(
                    new TreatmentStep( MSG_USE_TRAMADOL_IF_NOTHING_ELSE_WORKS,
                            new Medicine.Id[]{ ID_TRAMADOL } ) );
        }

        return new ArrayList<>( this.symptomaticTreatmentSteps );
    }

    private List<Medicine.Id> getMedicineIdsFromGroupList(final List<MedicineGroup> MEDICINE_GROUPS)
    {
        final List<MedicineGroup.Id> GRP_IDS
                            = new ArrayList<>( MEDICINE_GROUPS.size() );

        for(MedicineGroup GRP: MEDICINE_GROUPS) {
            GRP_IDS.add( GRP.getId() );
        }

        return this.getMedicineIdsFromGroupIdList( GRP_IDS );
    }

    private List<Medicine.Id> getMedicineIdsFromGroupIdList(final List<MedicineGroup.Id> MEDICINE_GROUP_IDS)
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
    private final Set<Medicine.Id> resPrevMedicineIds;
    private final List<TreatmentStep> symptomaticTreatmentSteps;
}
