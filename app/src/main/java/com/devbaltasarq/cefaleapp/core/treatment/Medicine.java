// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


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

        /** @return the name of the medicine. */
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
      * @param ID the id of this medicine.
      * @param GROUP_ID the id of the medicine group this medicine pertains to.
      * @param MIN_DOSAGE the minimum dosage, or -1 if not given.
      * @param REC_DOSAGE the recommended dosage, or -1 if not given.
      * @param MAX_DOSAGE the maximum dosage, or -1 if not given.
      * @param adverseEffects adverse effects, as text.
      * @throws IllegalArgumentException if params are null or invalid.
      */
    public Medicine(final Id ID,
                      final MedicineGroup.Id GROUP_ID,
                      final Dosage MIN_DOSAGE,
                      final Dosage REC_DOSAGE,
                      final Dosage MAX_DOSAGE,
                      String adverseEffects,
                      String url)
    {
        if ( ID == null ) {
            throw new IllegalArgumentException( "Medicine: invalid id" );
        }

        if ( GROUP_ID == null ) {
            throw new IllegalArgumentException( "Medicine: " + ID
                                                + ": invalid id group" );
        }

        if ( ( MIN_DOSAGE == null
            || !MIN_DOSAGE.isValid() )
          && ( REC_DOSAGE == null
            || !REC_DOSAGE.isValid() )
          && ( MAX_DOSAGE == null
            || !MAX_DOSAGE.isValid() ) )
        {
            throw new IllegalArgumentException( "Medicine: " + ID
                                                + ": no dosage specified" );
        }

        if ( adverseEffects == null
          || adverseEffects.isEmpty() )
        {
            throw new IllegalArgumentException( "Medicine: " + ID
                                                + " no adverse effects specified" );
        }

        if ( url == null
          || url.isEmpty() )
        {
            throw new IllegalArgumentException( "Medicine: " + ID
                                                + ": no url specified" );
        }

        this.id = ID;
        this.groupId = GROUP_ID;
        this.minDosage = MIN_DOSAGE;
        this.recDosage = REC_DOSAGE;
        this.maxDosage = MAX_DOSAGE;
        this.adverseEffects = adverseEffects.trim();
        this.url = url;
    }

    /** @return the group this medicine pertaints to. */
    public Id getId()
    {
        return this.id;
    }

    /** Minimum dosage can be -1 if missing.
      * @return the minimum dosage, as a double, in mg/day.
      */
    public Dosage getMinDosage()
    {
        return this.minDosage;
    }

    /** Recommended dosage can be -1 if missing.
      * @return the recommended dosage, as a double, in mg/day.
      */
    public Dosage getRecommendedDosage()
    {
        return this.recDosage;
    }
    
    /** Maximum dosage can be -1 if missing.
      * @return the maximum dosage, as a double, in mg/day.
      */
    public Dosage getMaxDosage()
    {
        return this.maxDosage;
    }
    
    /** @return the list of adverse effects, as text. */
    public String getAdverseEffects()
    {
        return this.adverseEffects;
    }

    /** @return the group this medicine pertains to. */
    public MedicineGroup getGroup()
    {
        return MedicineGroup.collectAll().get( this.groupId );
    }

    /** @return the online url. */
    public String getUrl()
    {
        return this.url;
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
        
        if ( this.getMinDosage().isValid() ) {
            lowDosage = "\nDosis mínima: " + this.getMinDosage();
            
        }
        
        if ( this.getMaxDosage().isValid() ) {
            higDosage = "\nDosis máxima: " + this.getMaxDosage();
            
        }
        
        if ( this.getRecommendedDosage().isValid() ) {
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

    /** @return all the medicine objects. */
    @NonNull
    public static Map<Medicine.Id, Medicine> getAll()
    {
        if ( allMedicines == null ) {
            throw new Error( "Medicine.getAll() invoked before loading medicines" );
        }

        return allMedicines;
    }

    public static void setAllMedicines(Map<Medicine.Id, Medicine> medicines)
    {
        if ( allMedicines == null ) {
            allMedicines = new HashMap<>( medicines );
        } else {
            allMedicines.clear();
            allMedicines.putAll( medicines );
        }

        return;
    }
    
    private final Id id;
    private final MedicineGroup.Id groupId;
    private final String adverseEffects;
    private final Dosage minDosage;
    private final Dosage recDosage;
    private final Dosage maxDosage;
    private final String url;
    private static Map<Id, Medicine> allMedicines;
}
