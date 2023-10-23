// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.tratamiento;


/** Represents a medicine. */
public class Medicine {
    public enum Group { 
        /** Antihipertensivos */
        A,
        /** Betabloqueantes */
        B,
        /** Antidepresivos */
        D,
        /** Neuromoduladores */
        E,
        /** Anticuerpos monoclonales (uso hospitalario) **/
        F,
        /** Toxina botulínica (uso hospitalario) **/
        G,
        /** Sin grupo */
        Z
    };
    
    /** Creates a new medicine.
      * @param group the group this medicine pertains to, or Group.Z.
      * @param id the number inside the group.
      * @param name the name of this medicine, as text.
      * @param minDosage the minimum dosage, or -1 if not given.
      * @param recommendedDosage the recommended dosage, or -1 if not given.
      * @param maxDosage the maximum dosage, or -1 if not given.
      * @param adverseEffects adverse effects, as text.
      */
    public Medicine(Group group, int id, String name,
                      double minDosage, double recommendedDosage, double maxDosage,
                      String adverseEffects)
    {
        this.group = group;
        this.id = id;
        this.name = name.trim();
        this.minDosage = minDosage;
        this.recommendedDosage = recommendedDosage;
        this.maxDosage = maxDosage;
        this.adverseEffects = adverseEffects.trim();
    }
    
    /** @return the group this medicine pertaints to. */
    public Group getGroup()
    {
        return group;
    }
    
    /** @return the id inside the group. */
    public int getId()
    {
        return id;
    }
    
    /** @return the complete id as a string, as in "A.1" */
    public String getIdAsString()
    {
        return this.getGroup() + "." + this.getId();
    }

    /** @return the name, as in "aspirin". */
    public String getName()
    {
        return this.name;
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
        
        return String.format("""
                             %s %s%s%s%s
                             Efectos adversos:
                             %s""",
                                     this.getIdAsString(),
                                     this.getName(),
                                     lowDosage,
                                     recDosage,
                                     higDosage,
                                     this.getAdverseEffects()
        );
    }
    
    private final String adverseEffects;
    private final double minDosage;
    private final double recommendedDosage;
    private final double maxDosage;
    private final int id;
    private final Group group;
    private final String name;
}
