// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.tratamiento;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** A collection of medicines. */
public final class MedicineRepo {
    /** Creates a repo of medicines. */
    private MedicineRepo()
    {
        this.medicinesByName = new HashMap<>();
        this.build();
    }
    
    /** Build all the infraestructure for the repo to work. */
    private void build()
    {
        for(Medicine m: this.collect()) {
            this.medicinesByName.put( m.getName(), m );
        }
        
        return;
    }
    
    /** Creates all the medicine objects. */
    private List<Medicine> collect()
    {
        final List<Medicine> TORET = new ArrayList<>();
        
        // Valproico
        TORET.add( new Medicine( Medicine.Group.E, 2, "ácido valproico",
                                300, 600, -1,
                                "somnolencia, cansancio, irritabilidad, "
                                        + "inquietud durante el sueño, "
                                        + "ataxia, temblor distal, "
                                        + "trastornos gastrointestinales, "
                                        + "aumento de peso, "
                                        + "pérdida de cabello,"
                                        + "elevación de transaminasas, "
                                        + "trombocitopenia." ));
        
        // Almotriptán
        TORET.add( new Medicine( Medicine.Group.Z, 1, "almotriptán",
                                12.5, -1, 25,
                                "Posible reacción cruzada con alérgicos "
                                        + "a sulfamidas. Opresión torácica, "
                                        + "náuseas, vómitos, mareos, vértigo, "
                                        + "somnolencia. Contraindicados en "
                                        + "pacientes con cardiopatía isquémica, "
                                        + "hipertensión, "
                                        + "enfermedad arterial periférica, "
                                        + "enfermedad de Raynaud." ));
        
        return TORET;
    }
    
    private Map<String, Medicine> medicinesByName;
    
    private static MedicineRepo repo = null;
    
    /** @return get the only instance. */
    public MedicineRepo get()
    {
        if ( repo == null ) {
            repo = new MedicineRepo();
        }
        
        return repo;
    }
}
