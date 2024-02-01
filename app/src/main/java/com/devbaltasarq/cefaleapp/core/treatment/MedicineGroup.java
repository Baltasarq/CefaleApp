// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** This groups a set of medicines. */
public class MedicineGroup implements Identifiable {
    public enum Id implements Nameable {
        /** Antihipertensivos */
        A,
        /** Betabloqueantes */
        B,
        /** Bloqueadores de Calcio */
        C,
        /** Antidepresivos */
        D,
        /** Neuromoduladores */
        E,
        /** Anticuerpos monoclonales (uso hospitalario) **/
        F,
        /** Toxina botulínica (uso hospitalario) **/
        G,
        /** Intervention in case of migrainne. */
        I;

        /** @return the name of the group. */
        public String getName()
        {
            return Names[ this.ordinal() ];
        }

        public static final String[] Names = {
                "Antihipertensivos",
                "Betabloqueantes",
                "Bloqueadores de canales de calcio",
                "Antidepresivos",
                "Neuromoduladores",
                "Anticuerpos monoclonales",
                "Toxina botulímica",
                "Uso sintomático"
        };
    }

    public MedicineGroup(Id grp)
    {
        this.grp = grp;
        this.medicines = new ArrayList<>( 4 );
    }

    /** @return the group id. */
    public Id getId()
    {
        return this.grp;
    }

    /** Adds a new medicine to the group. */
    public void add(int pos, Medicine m)
    {
        // Ensure the position exists
        while ( this.size() < ( pos + 1 ) ) {
            this.medicines.add( null );
        }

        // Ensure there was not a previous medicine set
        if ( this.get( pos ) != null ) {
            throw new Error(
                        String.format( "MedicineGroup.add(%d, %s): already existing %s",
                            pos,
                            m.getId().toString(),
                            this.medicines.get( pos ).getId().toString() ) );
        }

        this.medicines.set( pos, m );
    }

    /** @return the medicines in this group. */
    public List<Medicine> getMedicines()
    {
        return new ArrayList<>( this.medicines );
    }

    /** @return the number of medicines in this group. */
    public int size()
    {
        return this.medicines.size();
    }

    /** @return the medicine at position i. */
    public Medicine get(int i)
    {
        final int size = this.medicines.size();

        if ( i < 0
          || i >= size )
        {
            throw new Error( "MedicineGroup.get(): " + i + "/" + size );
        }

        return this.medicines.get( i );
    }

    @Override
    public String toString()
    {
        StringBuilder toret = new StringBuilder( this.getId().toString() );

        int i = 1;
        for(Medicine m: this.medicines) {
            toret.append( '\n' );
            toret.append( i );
            toret.append( ". " );
            toret.append( m.toString() );
            ++i;
        }

        return toret.toString();
    }

    public static Map<MedicineGroup.Id, MedicineGroup> collectAll()
    {
        if ( allGroups == null ) {
            final MedicineGroup GRP_A = new MedicineGroup( Id.A );
            final MedicineGroup GRP_B = new MedicineGroup( Id.B );
            final MedicineGroup GRP_C = new MedicineGroup( Id.C );
            final MedicineGroup GRP_D = new MedicineGroup( Id.D );
            final MedicineGroup GRP_E = new MedicineGroup( Id.E );
            final MedicineGroup GRP_F = new MedicineGroup( Id.F );
            final MedicineGroup GRP_G = new MedicineGroup( Id.G );
            final MedicineGroup GRP_I = new MedicineGroup( Id.I );

            // Add medicines
            GRP_A.add( 0, Medicine.getAll().get( Medicine.Id.CANDESARTAN ) );
            GRP_A.add( 1, Medicine.getAll().get( Medicine.Id.LISINOPRIL ) );

            GRP_B.add( 0, Medicine.getAll().get( Medicine.Id.METOPROLOL) );
            GRP_B.add( 1, Medicine.getAll().get( Medicine.Id.PROPRANOLOL) );

            GRP_C.add( 0, Medicine.getAll().get( Medicine.Id.FLUNARIZINA ) );

            GRP_D.add( 0, Medicine.getAll().get( Medicine.Id.AMITRIPTILINA ) );
            GRP_D.add( 1, Medicine.getAll().get( Medicine.Id.VENLAFAXINA ) );

            GRP_E.add( 0, Medicine.getAll().get( Medicine.Id.TOPIRAMATO ) );
            GRP_E.add( 1, Medicine.getAll().get( Medicine.Id.ZONISAMIDA ) );
            GRP_E.add( 2, Medicine.getAll().get( Medicine.Id.ACIDO_VALPROICO ) );

            GRP_F.add( 0, Medicine.getAll().get( Medicine.Id.ERENUMAB ) );
            GRP_F.add( 1, Medicine.getAll().get( Medicine.Id.FREMANEZUMAB ) );
            GRP_F.add( 2, Medicine.getAll().get( Medicine.Id.GALCANEZUMAB ) );

            GRP_G.add( 0, Medicine.getAll().get( Medicine.Id.BOTOX ) );

            // Finish
            allGroups = new HashMap<>(
                Map.ofEntries(
                    Map.entry( GRP_A.getId(), GRP_A ),
                    Map.entry( GRP_B.getId(), GRP_B ),
                    Map.entry( GRP_D.getId(), GRP_D ),
                    Map.entry( GRP_E.getId(), GRP_E ),
                    Map.entry( GRP_F.getId(), GRP_F ),
                    Map.entry( GRP_G.getId(), GRP_G ),
                    Map.entry( GRP_I.getId(), GRP_I )));
        }

        return allGroups;
    }

    private final Id grp;
    private final List<Medicine> medicines;
    private static Map<Id, MedicineGroup> allGroups = null;
}
