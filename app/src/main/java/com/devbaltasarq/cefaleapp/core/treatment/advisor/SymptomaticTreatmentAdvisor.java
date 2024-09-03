// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment.advisor;


import com.devbaltasarq.cefaleapp.core.MultiLanguageWrapper;
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
            final MultiLanguageWrapper.Lang LANG_ES = MultiLanguageWrapper.Lang.es;
            final List<Morbidity.Id> MORBIDITIES = this.getMorbidities();
            final boolean IS_PAIN_LOW_FREQUENCY = MORBIDITIES.contains(
                    Morbidity.Id.get( "PAIN_LOW_FREQUENCY" ) );

            final boolean IS_PAIN_INTENSE = MORBIDITIES.contains(
                    Morbidity.Id.get( "PAIN_INTENSE" ) );
            final boolean AINE_ALLERGY =
                    MORBIDITIES.contains(
                            Morbidity.Id.get( "ALLERGY_ACETILSALICIDIC_ACID" ) );
            final boolean SULFAMID_ALLERGY =
                    MORBIDITIES.contains(
                            Morbidity.Id.get( "ALLERGY_SULFAMID" ) );
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
            this.treatmentSteps.clear();

            if ( !IS_PAIN_INTENSE ) {
                // Moderate pain
                if ( !AINE_ALLERGY ) {
                    // Start with AINE
                    this.treatmentSteps.add(
                            new TreatmentStep( MSG_START_WITH_AINE, GRP_AINE_ID ) );

                    // Ergotics can be usefult if migraine is esporadic or of low frequency.
                    if ( IS_PAIN_LOW_FREQUENCY ) {
                        this.treatmentSteps.add(
                                new TreatmentStep( MSG_USE_ERGOTICS_AFTER_AINE, GRP_ERGOTICS_ID ) );
                    }
                } else {
                    // Allergy to AAS or AINE
                    if ( !SULFAMID_ALLERGY ) {
                        // Empezar con Triptán
                        this.treatmentSteps.add(
                                new TreatmentStep( MSG_USE_TRIPTAN, GRP_TRIPTAN_ID ) );
                    }

                    // If Triptan goes sideways or sulfamyd allergy present.
                    // Rimegepant, Lasmiditán
                    this.treatmentSteps.add(
                            new TreatmentStep( MSG_USE_RIMEGEPANT_LASMIDITAN,
                                    new Medicine.Id[]{ ID_RIMEGEPANT, ID_LASMIDITAN } ) );
                }
            } else {
                // Intense pain
                if ( !SULFAMID_ALLERGY ) {
                    // Start with triptán
                    this.treatmentSteps.add(
                            new TreatmentStep( MSG_START_WITH_TRIPTAN, GRP_TRIPTAN_ID ) );

                    if ( !AINE_ALLERGY ) {
                        // You can combine TRIPTAN with AINE
                        this.treatmentSteps.add(
                                new TreatmentStep( MSG_COMBINE_TRIPTAN_WITH_AINE, GRP_AINE_ID ) );
                    }
                } else {
                    // Ergotics might be useful if migraine is esporadic or of low frequency.
                    if ( IS_PAIN_LOW_FREQUENCY ) {
                        this.treatmentSteps.add(
                                new TreatmentStep( MSG_USE_ERGOTICS, GRP_ERGOTICS_ID ) );
                    }

                    if ( !AINE_ALLERGY ) {
                        this.treatmentSteps.add(
                                new TreatmentStep( MSG_COMBINE_ERGOTICS_WITH_AINE, GRP_AINE_ID ) );
                    }

                    // Rimegepant, Lasmiditán
                    this.treatmentSteps.add(
                            new TreatmentStep( MSG_USE_RIMEGEPANT_LASMIDITAN,
                                    new Medicine.Id[]{ ID_RIMEGEPANT, ID_LASMIDITAN } ) );

                    if ( !AINE_ALLERGY ) {
                        this.treatmentSteps.add(
                                new TreatmentStep( MSG_COMBINE_RIMEGEPANT_LASMIDITAN_WITH_AINE, GRP_AINE_ID ) );
                    }
                }
            }

            // Other analgesics: Metamizol
            this.treatmentSteps.add(
                    new TreatmentStep( MSG_USE_METAMIZOL,
                            new Medicine.Id[]{ ID_METAMIZOL } ) );
        }

        return new ArrayList<>( this.treatmentSteps );
    }

    private final List<TreatmentStep> treatmentSteps;
}
