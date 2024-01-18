// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import static java.util.Map.entry;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/** Represents a medicine. */
public class Medicine implements Identifiable {
    public enum Id implements Nameable {
        ACIDO_VALPROICO,
        ALMOTRIPTAN,
        AMITRIPTILINA,
        CAFERGOT,
        CANDESARTAN,
        FLUNARIZINA,
        HEMICRANEAL,
        LISINOPRIL,
        METOPROLOL,
        PROPRANOLOL,
        RIZATRIPTAN,
        SUMATRIPTAN,
        TOPIRAMATO,
        VENLAFAXINA,
        ZOLMITRIPTAN,
        ZONISAMIDA;

        /** @return the name of the group. */
        public String getName()
        {
            return Names[ this.ordinal() ];
        }

        public static final String[] Names = {
                "Ácido valproico",
                "Almotriptán",
                "Amitriptilina",
                "Cafergot",
                "Candesartán",
                "Flunarazina",
                "Hemicraneal",
                "Lisinopril",
                "Metoprolol",
                "Propranolol",
                "Rizatriptán",
                "Sumatriptán",
                "Topimarato",
                "Venlafaxina",
                "Zolmitriptán",
                "Zonisamida"
        };
    }

    /** Creates a new medicine.
      * @param id the id of this medicine.
      * @param minDosage the minimum dosage, or -1 if not given.
      * @param recommendedDosage the recommended dosage, or -1 if not given.
      * @param maxDosage the maximum dosage, or -1 if not given.
      * @param adverseEffects adverse effects, as text.
      */
    public Medicine(Id id,
                      double minDosage, double recommendedDosage, double maxDosage,
                      String adverseEffects)
    {
        this.id = id;
        this.minDosage = minDosage;
        this.recommendedDosage = recommendedDosage;
        this.maxDosage = maxDosage;
        this.adverseEffects = adverseEffects.trim();
    }

    /** @return the group this medicine pertaints to. */
    public Id getId()
    {
        return this.id;
    }

    /** Minimum dosage can be -1 if missing.
      * @return the minimum dosage, as a double, in mg/day.
      */
    public double getMinDosage()
    {
        return this.minDosage;
    }

    /** Recommended dosage can be -1 if missing.
      * @return the recommended dosage, as a double, in mg/day.
      */
    public double getRecommendedDosage()
    {
        return this.recommendedDosage;
    }
    
    /** Maximum dosage can be -1 if missing.
      * @return the maximum dosage, as a double, in mg/day.
      */
    public double getMaxDosage()
    {
        return this.maxDosage;
    }
    
    /** @return the list of adverse effects, as text. */
    public String getAdverseEffects()
    {
        return this.adverseEffects;
    }
    @Override
    public int hashCode()
    {
        return 7 * Integer.hashCode( this.getId().hashCode() );
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean toret = ( this == obj );

        if ( !toret
          && obj instanceof Medicine other )
        {
            toret = ( this.getId() == other.getId() );
        }

        return toret;
    }

    @Override
    public String toString()
    {
        String lowDosage = "";
        String higDosage = "";
        String recDosage = "";
        
        if ( this.getMinDosage() >= 0 ) {
            lowDosage = "\nDosis mínima: " + this.getMinDosage();
            
        }
        
        if ( this.getMaxDosage() >= 0 ) {
            higDosage = "\nDosis máxima: " + this.getMaxDosage();
            
        }
        
        if ( this.getRecommendedDosage() >= 0 ) {
            recDosage = "\nDosis recomendada: " + this.getRecommendedDosage();
            
        }
        
        return String.format( Locale.getDefault(),
                             """
                             %s %s%s%s
                             Efectos adversos:
                             %s""",
                                     this.getId().toString(),
                                     lowDosage,
                                     recDosage,
                                     higDosage,
                                     this.getAdverseEffects()
        );
    }

    /** Creates all the medicine objects. */
    @NonNull
    public static Map<Medicine.Id, Medicine> collectAll()
    {
        if ( allMedicines == null ) {
            allMedicines = new HashMap<>( Map.ofEntries(
                // Valproico
                entry(
                    Id.ACIDO_VALPROICO,
                    new Medicine( Id.ACIDO_VALPROICO,
                            300, 600, -1,
                            "somnolencia, cansancio, irritabilidad, "
                                + "inquietud durante el sueño, "
                                + "ataxia, temblor distal, "
                                + "trastornos gastrointestinales, "
                                + "aumento de peso, "
                                + "pérdida de cabello,"
                                + "elevación de transaminasas, "
                                + "trombocitopenia." )),

                // Almotriptán
                entry(
                    Id.ALMOTRIPTAN,
                    new Medicine( Id.ALMOTRIPTAN,
                            12.5, -1, 25,
                            "Posible reacción cruzada con alérgicos "
                                + "a sulfamidas. Opresión torácica, "
                                + "náuseas, vómitos, mareos, vértigo, "
                                + "somnolencia. Contraindicados en "
                                + "pacientes con cardiopatía isquémica, "
                                + "hipertensión, "
                                + "enfermedad arterial periférica, "
                                + "enfermedad de Raynaud." )),

                // Amitriptilina
                entry(
                    Id.AMITRIPTILINA,
                    new Medicine( Id.AMITRIPTILINA,
                            10, 25, -1,
                            "sequedad de boca, sabor metálico, "
                                + "estreñimiento retención urinaria, "
                                + "visión borrosa, trastorno de la acomodación "
                                + "hipotensión postural, somnolencia,"
                                + "aumento de apetito, aumento de peso "
                                + "elevación de transaminasas, alteración "
                                + "de la líbido. Precaución con glaucoma e "
                                + "hipertrofia prostática." )),

                // Cafergot
                entry(
                    Id.CAFERGOT,
                    new Medicine( Id.CAFERGOT,
                            2, 2, 6,
                            "Náuseas, vómitos, dolor abdominal, "
                                + "parestesias, calambres musculares, angor, "
                                + "Contraindicado en cardiopatía isquemina, "
                                + "arteriopatía periférica, migraña hemipléjica "
                                + "enfermedad de Raynaud. No se puede combinar con "
                                + "Triptán." )),

                // Candesartán
                entry(
                    Id.CANDESARTAN,
                    new Medicine( Id.CANDESARTAN,
                            8, 16, -1,
                            "Mareo, insomnio, hipotensión sin taquicardia "
                                + "refleja, ortostatismo, hiperpotasemia, "
                                + "insuficiencia renal, erupción cutánea, angioedema, tos, "
                                + "astenia, congestión nasal, dispepsia, diarrea, "
                                + "elevación de transaminasas." )),

                // Flunarizina
                entry(
                    Id.FLUNARIZINA,
                    new Medicine( Id.FLUNARIZINA,
                            2.5, 5, -1,
                            "Cefalea, astenia, somnolencia, sequedad oral, "
                                + "visión borrosa, dermatitis, aumento de peso. "
                                + "Precaución con hipotensión. "
                                + "Contraindicada si hay depresión." )),

                // Hemicraneal
                entry(
                    Id.HEMICRANEAL,
                    new Medicine( Id.HEMICRANEAL,
                            2, 6, -1,
                            "Náuseas, vómitos, dolor abdominal, parestesias, "
                                + "calambres musculares, angor. "
                                + "Contraindicado en cardiopatía isquemina, "
                                + "arteriopatía periférica, migraña hemipléjica, "
                                + "enfermedad de Raynaud. No se puede combinar con "
                                + "Triptán." )),

                // Lisinopril
                entry(
                    Id.METOPROLOL,
                    new Medicine( Id.METOPROLOL,
                            50, 100, -1,
                            "Bradicardia, hipotensión, "
                               + "ortostatismo, vasoconstricción periférica, "
                               + "broncoespasmo, astenia, "
                               + "disminución de la líbido, "
                               + "impotencia masculina, síntomas del SNC "
                               + "(mareo, depresión, pesadillas -más "
                               + "frecuentes con Propranolol-)" )),

                // Metropolol
                entry(
                    Id.LISINOPRIL,
                    new Medicine( Id.LISINOPRIL,
                            50, 100, -1,
                            "Bradicardia, hipotensión, ortostatismo, "
                                + "vasoconstricción periférica, broncoespasmo, "
                                + "astenia, disminución de la líbido, "
                                + "impotencia masculina, síntomas del SNC ("
                                + "mareo, depresión, pesadillas -más "
                                + "frecuentes con Propranolol-)." )),

                // Propranolol
                entry(
                    Id.PROPRANOLOL,
                    new Medicine( Id.PROPRANOLOL,
                            30, 60, -1,
                            "Bradicardia, hipotensión, ortostatismo, "
                                + "vasoconstricción periférica, broncoespasmo, "
                                + "astenia, disminución de la líbido, "
                                + "impotencia masculina, síntomas del SNC ("
                                + "mareo, depresión, pesadillas)." )),

                // Rizatriptán
                entry(
                    Id.RIZATRIPTAN,
                    new Medicine( Id.RIZATRIPTAN,
                            -1, 10, 20,
                            "Posibles reacción cruzada con alérgicos y "
                                + "sulfamidas. Opresión torácica, náuseas, vómitos, "
                                + "mareos, vértigo, somnolencia. "
                                + "Contraindicados en pacientes con cardiopatía "
                                + "isquémica, hipertensión, enfermedad arterial "
                                + "periférica, enfermedad de Raynaud." )),

                // Sumatriptán
                entry(
                    Id.SUMATRIPTAN,
                    new Medicine( Id.SUMATRIPTAN,
                        -1, 50, 100,
                        "20 mg. nasal, 6 mg. subcutáneo. "
                                + "Posibles reacción cruzada con alérgicos y "
                                + "sulfamidas. Opresión torácica, náuseas, vómitos, "
                                + "mareos, vértigo, somnolencia. "
                                + "Contraindicados en pacientes con cardiopatía "
                                + "isquémica, hipertensión, enfermedad arterial "
                                + "periférica, enfermedad de Raynaud." )),

                // Topitramato
                entry(
                    Id.TOPIRAMATO,
                    new Medicine( Id.TOPIRAMATO,
                        50, 100, -1,
                        "Astenia, mareos, fatiga, torpeza mental, "
                                + "parestesias distales. somnolencia, síntomas depresivos, "
                                + "diarrea, litiasis renal, glaucoma, pérdida de peso." )),

                // Venlafaxina
                entry(
                    Id.VENLAFAXINA,
                    new Medicine( Id.VENLAFAXINA,
                        37.5, 75, -1,
                        "Astenia, fatiga, hipertensión arterial, sofocos,"
                                + "anorexia, estreñimiento, náuseas, alteración de la "
                                + "líbido, anorgasmia, disfunción eréctil. "
                                + "Ocasionalmente, hipotensión, síncope, taquicardia. "
                                + "Contraindicado: uso conjunto con IMAO." )),

                // Zolmitriptán
                entry(
                    Id.ZOLMITRIPTAN,
                    new Medicine( Id.ZOLMITRIPTAN,
                        2.5, 5, 10,
                        "10 mg. nasal. "
                                + "Posible reacción cruzada con alérgicos a "
                                + "sulfamidas. Opresión torácica, náuseas, vómitos, "
                                + "mareos, vértigo, somnolencia. Contraindicados en "
                                + "pacientes con cardiopatía isquémica, hipertensión, "
                                + "enfermedad arterial periférica, enfermedad de Raynaud." )),

                // Zonisamida
                entry(
                    Id.ZONISAMIDA,
                    new Medicine( Id.ZONISAMIDA,
                        2.5, 5, 10,
                        "Anorexia, pérdida de peso, irritabilidad, agitación "
                                + "trastorno de la memoria, depresión, mareos, ataxia, "
                                + "somnolencia, dipolopia, diarrea, anhidrosis, fiebre, "
                                + "Nefrolitiasis." ))

            ));
        }

        return allMedicines;
    }
    
    private final Id id;
    private final String adverseEffects;
    private final double minDosage;
    private final double recommendedDosage;
    private final double maxDosage;
    private static Map<Id, Medicine> allMedicines;
}
