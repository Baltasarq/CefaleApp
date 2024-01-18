// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Morbidity implements Identifiable {
    public enum Id implements Nameable {
        HYPERTENSION,
        HIPOTENSION,
        BRADYCARDIA,
        BRONQUIAL_ASTHMA,
        RAYNAUD,
        DIABETES,
        OBESITY,
        ANOREXIA,
        DEPRESSION,
        GLAUCOMA,
        LITIASIS;

        /** @return the name of the group. */
        public String getName()
        {
            return Names[ this.ordinal() ];
        }

        public static final String[] Names = {
                "Hipertensión",
                "Hipotensión",
                "Bradicardia – bloqueo aurículoventricular",
                "Asma bronquial – Hiperreactividad bronquial",
                "Arteriopatía periférica – Enfermedad de Raynaud",
                "Diabetes Mellitus tratada con insulina",
                "Obesidad – Bulimia",
                "Anorexia – Bajo peso",
                "Depresión – distimia depresiva",
                "Glaucoma",
                "Litiasis"
        };
    }

    public Morbidity(Id id,
                     String desc,
                     Collection<MedicineGroup.Id> incompatibleGroups,
                     Collection<Medicine.Id> incompatibleMedicines,
                     Collection<Medicine.Id> advisedMedicines)
    {
        this.id = id;
        this.desc = desc;
        this.incompatibleGroups = new ArrayList<>( incompatibleGroups );
        this.incompatibleMedicines = new ArrayList<>( incompatibleMedicines );
        this.advisedMedicines = new ArrayList<>( advisedMedicines );
    }

    /** @return the id of this morbidity. */
    public Id getId()
    {
        return this.id;
    }

    /** @return the description of this morbidity. */
    public String getDesc()
    {
        return this.desc;
    }

    /** @return the part of the incompatible medicines: groups of medicines. */
    public List<MedicineGroup.Id> getIncompatibleGroups()
    {
        return new ArrayList<>( this.incompatibleGroups );
    }

    /** @return part of the incompatible medicines: medicines. */
    public List<Medicine.Id> getIncompatibleMedicines()
    {
        return new ArrayList<>( this.incompatibleMedicines );
    }

    /** @return all the incompatible medicines. */
    public List<Medicine.Id> getAllIncompatibleMedicines()
    {
        final List<Medicine.Id> TORET = new ArrayList<>( this.incompatibleMedicines );

        // Collect all the medicines from the incompatible groups.
        for(MedicineGroup.Id groupId: this.incompatibleGroups) {
            final MedicineGroup GROUP = MedicineGroup.collectAll().get( groupId );

            for(Medicine medicine: GROUP.getMedicines()) {
                TORET.add( medicine.getId() );
            }
        }

        return TORET;
    }

    /** @return the advised medicines. */
    public List<Medicine.Id> getAdvisedMedicines()
    {
        return new ArrayList<>( this.advisedMedicines );
    }

    @Override
    public String toString()
    {
        String name = this.getId().getName();
        String[] nameParts = name.split( " " );

        return nameParts[ 0 ] + ": " + this.getDesc();
    }

    public static List<Morbidity> collectAll()
    {
        if ( morbidities == null ) {
            morbidities = new ArrayList<>(
                List.of(
                    new Morbidity(
                        Id.HYPERTENSION,
                        """
                        Si la hipertensión no está tratada, el tratamiento indicada es con:
                        - Candesartán o Lisinopril,
                        - Propranolol o Metoprolol,
                        - Amitriptilina,
                        - Topiramato o zonisamida.
                        Si la hipertensión ya está tratada y controlada, valorar tratamiento con:
                        - Flunarizina.
                        - Amitriptilina.
                        - Topiramato o zonisamida.
                        """,
                        List.of(),
                        List.of( Medicine.Id.VENLAFAXINA ),
                        List.of(
                            Medicine.Id.CANDESARTAN,
                            Medicine.Id.LISINOPRIL,
                            Medicine.Id.PROPRANOLOL,
                            Medicine.Id.METOPROLOL,
                            Medicine.Id.AMITRIPTILINA,
                            Medicine.Id.TOPIRAMATO,
                            Medicine.Id.ZONISAMIDA,
                            Medicine.Id.FLUNARIZINA,
                            Medicine.Id.AMITRIPTILINA )),
                    new Morbidity(
                        Id.HIPOTENSION,
                        """
                        Contraindicados:
                        - fármacos betabloqueantes.
                        - fármacos anti-hipertensivos.
                        El tratamiento preventivo indicado sería:
                        - Venlafaxina.
                        - Flunarizina.
                        - Topiramato o zonisamida.
                        """,
                        List.of(
                            MedicineGroup.Id.A,
                            MedicineGroup.Id.B ),
                        List.of(),
                        List.of (
                            Medicine.Id.VENLAFAXINA,
                            Medicine.Id.FLUNARIZINA,
                            Medicine.Id.TOPIRAMATO,
                            Medicine.Id.ZONISAMIDA ) ),
                    new Morbidity(
                        Id.BRADYCARDIA,
                        """
                        Contraindicados:
                        - fármacos betabloqueantes.
                        El tratamiento preventivo indicado sería:
                        - Flunarizina.
                        - Amitriptilina o venlafaxina.
                        - Topiramato o zonisamida.
                        - Ácido valproico.
                        - Candesartán o lisinopril.
                        """,
                        List.of( MedicineGroup.Id.B ),
                        List.of(),
                        List.of (
                            Medicine.Id.FLUNARIZINA,
                            Medicine.Id.AMITRIPTILINA,
                            Medicine.Id.VENLAFAXINA,
                            Medicine.Id.TOPIRAMATO,
                            Medicine.Id.ZONISAMIDA,
                            Medicine.Id.ACIDO_VALPROICO,
                            Medicine.Id.CANDESARTAN,
                            Medicine.Id.LISINOPRIL ) ),
                        new Morbidity(
                            Id.BRONQUIAL_ASTHMA,
                            """
                            Contraindicados los fármacos betabloqueantes.
                            El tratamiento preventivo indicado sería:
                            - Flunarizina.
                            - Amitriptilina o venlafaxina.
                            - Topiramato o zonisamida.
                            - Ácido valproico.
                            - Candesartán o lisinopril.
                            """,
                            List.of( MedicineGroup.Id.B ),
                            List.of(),
                            List.of (
                                Medicine.Id.FLUNARIZINA,
                                Medicine.Id.AMITRIPTILINA,
                                Medicine.Id.VENLAFAXINA,
                                Medicine.Id.TOPIRAMATO,
                                Medicine.Id.ZONISAMIDA,
                                Medicine.Id.ACIDO_VALPROICO,
                                Medicine.Id.CANDESARTAN,
                                Medicine.Id.LISINOPRIL ) ),
                        new Morbidity(
                            Id.RAYNAUD,
                            """
                             Contraindicados los fármacos betabloqueantes.
                             El tratamiento preventivo indicado sería:
                             - Flunarizina.
                             - Amitriptilina o venlafaxina.
                             - Topiramato o zonisamida.
                             - Ácido valproico.
                             - Candesartán o lisinopril.
                            """,
                            List.of( MedicineGroup.Id.B ),
                            List.of(),
                            List.of (
                                Medicine.Id.FLUNARIZINA,
                                Medicine.Id.AMITRIPTILINA,
                                Medicine.Id.VENLAFAXINA,
                                Medicine.Id.TOPIRAMATO,
                                Medicine.Id.ZONISAMIDA,
                                Medicine.Id.ACIDO_VALPROICO,
                                Medicine.Id.CANDESARTAN,
                                Medicine.Id.LISINOPRIL ) ),
                        new Morbidity(
                            Id.DIABETES,
                            """
                            Contraindicados los fármacos betabloqueantes.
                            El tratamiento preventivo indicado sería con:
                            - Flunarizina.
                            - Amitriptilina o venlafaxina.
                            - Topiramato o zonisamida.
                            - Ácido valproico.
                            - Candesartán o lisinopril.
                            """,
                            List.of( MedicineGroup.Id.B ),
                            List.of(),
                            List.of (
                                Medicine.Id.FLUNARIZINA,
                                Medicine.Id.AMITRIPTILINA,
                                Medicine.Id.VENLAFAXINA,
                                Medicine.Id.TOPIRAMATO,
                                Medicine.Id.ZONISAMIDA,
                                Medicine.Id.ACIDO_VALPROICO,
                                Medicine.Id.CANDESARTAN,
                                Medicine.Id.LISINOPRIL ) ),
                        new Morbidity(
                            Id.OBESITY,
                            """
                             IMC > 30 kg/m\u00b2.
                             Contraindicada flunarizina.
                             Contraindicada amitriptilina.
                             Contraindicado el ácido valproico.
                             El tratamiento preventivo inicial indicado sería con:
                             Flunarizina.
                             Amitriptilina o venlafaxina.
                             Topiramato o zonisamida.
                             Candesartán o lisinopril.
                             """,
                            List.of(),
                            List.of(
                                Medicine.Id.FLUNARIZINA,
                                Medicine.Id.AMITRIPTILINA,
                                Medicine.Id.ACIDO_VALPROICO ),
                            List.of (
                                Medicine.Id.FLUNARIZINA,
                                Medicine.Id.AMITRIPTILINA,
                                Medicine.Id.VENLAFAXINA,
                                Medicine.Id.TOPIRAMATO,
                                Medicine.Id.ZONISAMIDA,
                                Medicine.Id.CANDESARTAN,
                                Medicine.Id.LISINOPRIL ) ),
                        new Morbidity(
                            Id.ANOREXIA,
                            """
                            Contraindicado topiramato.
                            Contraindicado zonisamida.
                            Contraindicada venlafaxina.
                            El tratamiento preventivo inicial indicado sería con:
                            - Flunarizina.
                            - Amitriptilina.
                            - Ácido valproico.
                            - Candesartán o lisinopril.
                            """,
                            List.of(),
                            List.of(
                                Medicine.Id.TOPIRAMATO,
                                Medicine.Id.ZONISAMIDA,
                                Medicine.Id.VENLAFAXINA ),
                            List.of (
                                Medicine.Id.FLUNARIZINA,
                                Medicine.Id.AMITRIPTILINA,
                                Medicine.Id.CANDESARTAN,
                                Medicine.Id.LISINOPRIL ) ),
                        new Morbidity(
                            Id.DEPRESSION,
                            """
                             Contraindicada Flunarizina.
                             El tratamiento preventivo inicial indicado sería con:
                             - Amitriptilina o venlafaxina.
                             - Ácido valproico.
                             - Topiramato o zonisamida.
                             - Candesartán o lisinopril.
                             """,
                            List.of(),
                            List.of(
                                Medicine.Id.FLUNARIZINA ),
                            List.of (
                                Medicine.Id.AMITRIPTILINA,
                                Medicine.Id.VENLAFAXINA,
                                Medicine.Id.ACIDO_VALPROICO,
                                Medicine.Id.TOPIRAMATO,
                                Medicine.Id.ZONISAMIDA,
                                Medicine.Id.CANDESARTAN,
                                Medicine.Id.LISINOPRIL ))
            ));
        }

        return morbidities;
    }

    private final Id id;
    private final String desc;
    private final List<MedicineGroup.Id> incompatibleGroups;
    private final List<Medicine.Id> incompatibleMedicines;
    private final List<Medicine.Id> advisedMedicines;

    private static List<Morbidity> morbidities = null;
}
