// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment.advisor;


import com.devbaltasarq.cefaleapp.core.Message;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineClass;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineGroup;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;
import com.devbaltasarq.cefaleapp.core.treatment.TreatmentAdvisor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/** Creates a list of steps for a symptomatic treatment. */
public class SymptomaticTreatmentAdvisor extends TreatmentAdvisor {
    /** Creates a new SymptomaticTreatmentAdvisor object, given the morbidities.
      * @param MORBIDITIES the collection of morbidities.
      */
    public SymptomaticTreatmentAdvisor(final Collection<Morbidity.Id> MORBIDITIES)
    {
        super( MORBIDITIES );

        this.treatmentSteps = new ArrayList<>( MedicineClass.getAll().size() * 3 );
    }

    /** @return the collection of compatible medicines, as a list of medicine. */
    public List<TreatmentStep> createResultList()
    {
        if ( this.treatmentSteps.isEmpty() ) {
            final List<Morbidity.Id> MORBIDITIES = this.getMorbidities();
            final boolean IS_PAIN_MODERATE = MORBIDITIES.contains(
                    Morbidity.Id.get( "PAIN_MODERATE" ) );

            final boolean IS_PAIN_INTENSE = MORBIDITIES.contains(
                    Morbidity.Id.get( "PAIN_INTENSE" ) );
            final boolean AINE_ALLERGY =
                    MORBIDITIES.contains(
                            Morbidity.Id.get( "ALLERGY_ACETILSALICIDIC_ACID" ) );
            final boolean SULFADRUG_ALLERGY =
                    MORBIDITIES.contains(
                            Morbidity.Id.get( "ALLERGY_SULFADRUG" ) );
            final MedicineGroup.Id GRP_AINE_ID = Objects.requireNonNull(
                    MedicineGroup.Id.get( "AINE" ) );
            final MedicineGroup.Id GRP_TRIPTAN_ID = Objects.requireNonNull(
                    MedicineGroup.Id.get( "TRIPTANES" ) );
            final MedicineGroup.Id GRP_ERGOTICS_ID = Objects.requireNonNull(
                    MedicineGroup.Id.get( "ERGOTICOS" ) );
            final Medicine.Id ID_METAMIZOL = Objects.requireNonNull(
                    Medicine.Id.get( "METAMIZOL" ) );
            final Medicine.Id ID_RIMEGEPANT = Objects.requireNonNull(
                    Medicine.Id.get( "RIMEGEPANT" ) );
            final Medicine.Id ID_LASMIDITAN = Objects.requireNonNull(
                    Medicine.Id.get( "LASMIDITAN" ) );
            final Message MSG_USE_TRIPTAN = Objects.requireNonNull(
                    Message.getFor( "useTRIPTAN" ));
            final Message MSG_START_WITH_AINE = Objects.requireNonNull(
                    Message.getFor( "startWithAine" ));
            final Message MSG_USE_ERGOTICS_AFTER_AINE = Objects.requireNonNull(
                    Message.getFor( "useErgoticsAfterAINE" ));
            final Message MSG_USE_RIMEGEPANT_LASMIDITAN = Objects.requireNonNull(
                    Message.getFor( "useRimegepantLasmiditan" ));
            final Message MSG_USE_METAMIZOL = Objects.requireNonNull(
                    Message.getFor( "useMetamizol" ));
            final Message MSG_START_WITH_TRIPTAN = Objects.requireNonNull(
                    Message.getFor( "startWithTRIPTAN" ));
            final Message MSG_COMBINE_TRIPTAN_WITH_AINE = Objects.requireNonNull(
                    Message.getFor( "combineTRIPTANWithAINE" ));
            final Message MSG_USE_ERGOTICS = Objects.requireNonNull(
                    Message.getFor( "useERGOTICS" ));
            final Message MSG_COMBINE_ERGOTICS_WITH_AINE = Objects.requireNonNull(
                    Message.getFor( "combineERGOTICSWithAINE" ));
            final Message MSG_COMBINE_RIMEGEPANT_LASMIDITAN_WITH_AINE = Objects.requireNonNull(
                    Message.getFor( "combineRimegepantLasmiditanWithAINE" ));

            // Create a list with all analgesic medicines
            this.treatmentSteps.clear();

            if ( IS_PAIN_MODERATE ) {
                // Moderate pain
                if ( !AINE_ALLERGY ) {
                    // Start with AINE
                    this.treatmentSteps.add(
                            new TreatmentStep( MSG_START_WITH_AINE, GRP_AINE_ID ) );

                    // Ergotics can be useful if migraine is esporadic or of low frequency.
                    this.treatmentSteps.add(
                            new TreatmentStep( MSG_USE_ERGOTICS_AFTER_AINE, GRP_ERGOTICS_ID ) );
                } else {
                    // Allergy to sulf-drug
                    if ( !SULFADRUG_ALLERGY ) {
                        // Empezar con Triptán
                        this.treatmentSteps.add(
                                new TreatmentStep( MSG_USE_TRIPTAN, GRP_TRIPTAN_ID ) );
                    }

                    // Ergotics can be usefult if migraine is esporadic or of low frequency.
                    this.treatmentSteps.add(
                            new TreatmentStep( MSG_USE_ERGOTICS_AFTER_AINE, GRP_ERGOTICS_ID ) );
                }
            }
            else
            if ( IS_PAIN_INTENSE ) {
                // Intense pain
                if ( !SULFADRUG_ALLERGY ) {
                    // Start with triptán
                    this.treatmentSteps.add(
                            new TreatmentStep( MSG_START_WITH_TRIPTAN, GRP_TRIPTAN_ID ) );

                    if ( !AINE_ALLERGY ) {
                        // You can combine TRIPTAN with AINE
                        this.treatmentSteps.add(
                                new TreatmentStep( MSG_COMBINE_TRIPTAN_WITH_AINE, GRP_AINE_ID ) );
                    }
                } else {
                    if ( !AINE_ALLERGY ) {
                        // Start with AINE
                        this.treatmentSteps.add(
                                new TreatmentStep( MSG_START_WITH_AINE, GRP_AINE_ID ) );
                    }

                    // Ergotics might be useful if migraine is esporadic or of low frequency.
                    this.treatmentSteps.add(
                            new TreatmentStep( MSG_USE_ERGOTICS, GRP_ERGOTICS_ID ) );
                }
            }

            // Other analgesics: Lasmisitan, rimegepant, metamizol
            this.treatmentSteps.add(
                    new TreatmentStep( MSG_USE_RIMEGEPANT_LASMIDITAN,
                            new Medicine.Id[]{ ID_RIMEGEPANT, ID_LASMIDITAN } ) );

            this.treatmentSteps.add(
                    new TreatmentStep( MSG_USE_METAMIZOL,
                            new Medicine.Id[]{ ID_METAMIZOL } ) );
        }

        return new ArrayList<>( this.treatmentSteps );
    }

    private final List<TreatmentStep> treatmentSteps;
}
