// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;
import com.devbaltasarq.cefaleapp.ui.CefaleAppActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class TreatmentActivity extends CefaleAppActivity {
    protected void init()
    {
        // Listeners
        final FloatingActionButton FAB_START = this.findViewById( R.id.btStartMedicineDiscards );

        FAB_START.setOnClickListener( (v) -> this.onStartMedicineDiscards() );

        // Populate with info
        final LinearLayout LY_MORBIDITIES = this.findViewById( R.id.lyMorbidities );

        this.entries = new HashMap<>();
        this.prepareMorbidityIds();
        this.populateLayout( LY_MORBIDITIES, Morbidity.getAll() );
        this.buildCheckboxDependencies();
        this.loadFromMigraineRepo();
    }

    protected abstract void prepareMorbidityIds();
    protected abstract void launchTreatmentResult(final List<Morbidity.Id> MORBIDITY_IDS);

    protected  void populateLayout(final LinearLayout LY,
                                   final Map<Morbidity.Id, Morbidity> MORBIDITIES)
    {
        final List<Morbidity> LIST_MORBIDITIES = new ArrayList<>( MORBIDITIES.values() );

        LIST_MORBIDITIES.sort( Comparator.comparing( m -> m.getId().getKey() ) );
        this.sortIdentifiableI18n( LIST_MORBIDITIES );

        for(Morbidity idObj: LIST_MORBIDITIES) {
            this.buildEntry( LY, idObj );
        }

        return;
    }

    protected void buildCheckboxDependencies()
    {
    }

    protected void loadFromMigraineRepo()
    {
    }

    protected void onStartMedicineDiscards()
    {
        final List<Morbidity.Id> MORBIDITY_IDS = new ArrayList<>();

        // Build the morbidity list
        for(final Morbidity.Id ID: this.entries.keySet()) {
            final CheckBox CHK = this.getCheckBoxFor( ID );

            if ( CHK.isChecked() ) {
                MORBIDITY_IDS.add( ID );
            }
        }

        this.launchTreatmentResult( MORBIDITY_IDS );
    }

    protected void buildEntry(final LinearLayout LY, final Morbidity MORBIDITY)
    {
        if ( MORBIDITY == null ) {
            throw new Error( "buildEntry(): missing morbidity" );
        }

        final LayoutInflater INFLATER = this.getLayoutInflater();
        String name = MORBIDITY.getId().getName().getForCurrentLanguage();

        // Prepare name (separate different lines by separator)
        int posSeparator = -1;
        for(int i = 0; i < name.length(); ++i) {
            char ch = name.charAt( i );

            if ( ch != ' '
                 && !Character.isLetter( ch ) )
            {
                posSeparator = i;
                break;
            }
        }

        if ( posSeparator > -1 ) {
            name = name.substring( 0, posSeparator ).trim()
                    + "\n" + name.substring( posSeparator + 1 ).trim();
        }

        // Prepare text view
        final View ENTRY = INFLATER.inflate( R.layout.morbidity_entry, null );
        final CheckBox CHK = ENTRY.findViewById( R.id.chkMorbidity );
        final ImageView BT_DETAILS = ENTRY.findViewById( R.id.btDetails );

        CHK.setText( name );
        BT_DETAILS.setOnClickListener( (v) -> this.launchMorbidityDetails( MORBIDITY ) );

        // Add to the layout
        this.entries.put( MORBIDITY.getId(), ENTRY );
        LY.addView( ENTRY );
    }

    protected void launchMorbidityDetails(Morbidity morbidity)
    {
        final Intent INTENT = new Intent( this, MorbidityActivity.class );

        MorbidityActivity.morbidity = morbidity;
        this.startActivity( INTENT );
    }

    protected CheckBox getCheckBoxFor(final Morbidity.Id ID)
    {
        final View ENTRY = this.entries.get( ID );

        if ( ENTRY == null ) {
            throw new IllegalArgumentException( "missing entry for id: " + ID );
        }

        return ENTRY.findViewById( R.id.chkMorbidity );
    }

    protected Map<Morbidity.Id, View> entries;
}
