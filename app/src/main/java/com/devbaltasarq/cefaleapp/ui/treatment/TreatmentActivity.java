// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.questionnaire.Steps;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;
import com.devbaltasarq.cefaleapp.core.treatment.TreatmentAdvisor;
import com.devbaltasarq.cefaleapp.ui.tests.MigraineTestActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class TreatmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_treatment );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_treatment );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.cephalea );
        }

        // Listeners
        final FloatingActionButton FAB_START = this.findViewById( R.id.btStartMedicineDiscards );

        FAB_START.setOnClickListener( (v) -> this.onStartMedicineDiscards() );

        // Populate with info
        final LinearLayout LY_MORBIDITIES = this.findViewById( R.id.lyMorbidities );

        this.entries = new HashMap<>();
        this.prepareMorbidityIds();
        this.populateLayout( LY_MORBIDITIES, Morbidity.getAll() );
        this.buildDependencies();
        this.loadFromMigraineRepo();
    }

    private void prepareMorbidityIds()
    {
        final Map<Morbidity.Id, Morbidity> MORBIDITIES = Morbidity.getAll();

        final Morbidity.Id ID_HIPOTENSION = Morbidity.Id.get( "HIPOTENSION" );
        final Morbidity.Id ID_HYPERTENSION = Morbidity.Id.get( "HYPERTENSION" );
        final Morbidity.Id ID_OBESITY = Morbidity.Id.get( "OBESITY" );
        final Morbidity.Id ID_ANOREXIA = Morbidity.Id.get( "ANOREXIA" );

        this.hypotension = Objects.requireNonNull( MORBIDITIES.get( ID_HIPOTENSION ) );
        this.hypertension = Objects.requireNonNull( MORBIDITIES.get( ID_HYPERTENSION ) );
        this.obesity = Objects.requireNonNull( MORBIDITIES.get( ID_OBESITY ) );
        this.anorexia = Objects.requireNonNull( MORBIDITIES.get( ID_ANOREXIA ) );
    }

    private void populateLayout(
            final LinearLayout LY,
            final Map<Morbidity.Id, Morbidity> MORBIDITIES)
    {
        LY.removeAllViews();
        this.entries.clear();

        // Put the opposite ones before
        this.buildEntry( LY, this.hypotension);
        this.buildEntry( LY, this.hypertension );
        this.buildEntry( LY, this.anorexia );
        this.buildEntry( LY, this.obesity );

        MORBIDITIES.remove( this.hypotension.getId() );
        MORBIDITIES.remove( this.hypertension.getId() );
        MORBIDITIES.remove( this.anorexia.getId() );
        MORBIDITIES.remove( this.obesity.getId() );

        final List<Morbidity> LIST_MORBIDITIES = new ArrayList<>( MORBIDITIES.values() );

        LIST_MORBIDITIES.sort( Comparator.comparing( m -> m.getId().getKey() ) );
        Util.sortIdentifiableI18n( LIST_MORBIDITIES );
        for(Morbidity idObj: LIST_MORBIDITIES) {
            this.buildEntry( LY, idObj );
        }

        return;
    }

    private void buildEntry(final LinearLayout LY, final Morbidity MORBIDITY)
    {
        if ( MORBIDITY == null ) {
            throw new Error( "buildEntry(): missing morbidity" );
        }

        final LayoutInflater INFLATER = this.getLayoutInflater();
        String name = MORBIDITY.getId().getName();

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

    private void launchMorbidityDetails(Morbidity morbidity)
    {
        final Intent INTENT = new Intent( this, MorbidityActivity.class );

        MorbidityActivity.morbidity = morbidity;
        this.startActivity( INTENT );
    }

    private CheckBox getCheckBoxFor(final Morbidity.Id ID)
    {
        final View ENTRY = this.entries.get( ID );

        if ( ENTRY == null ) {
            throw new IllegalArgumentException( "missing entry for id: " + ID );
        }

        return ENTRY.findViewById( R.id.chkMorbidity );
    }

    private void buildDependencies()
    {
        final CheckBox CHK_HYPER = this.getCheckBoxFor( this.hypertension.getId() );
        final CheckBox CHK_HYPO = this.getCheckBoxFor( this.hypotension.getId() );
        final CheckBox CHK_OBESITY = this.getCheckBoxFor( this.obesity.getId() );
        final CheckBox CHK_ANOREXIA = this.getCheckBoxFor( this.anorexia.getId() );

        if ( CHK_HYPER == null ) {
            throw new Error( "missing hypertension checkbox" );
        }

        if ( CHK_HYPO == null ) {
            throw new Error( "missing hypotension checkbox" );
        }

        if ( CHK_OBESITY == null ) {
            throw new Error( "missing obesity checkbox" );
        }

        if ( CHK_ANOREXIA == null ) {
            throw new Error( "missing anorexia checkbox" );
        }

        CHK_HYPER.setOnCheckedChangeListener( (view, checked) -> {
            CHK_HYPO.setEnabled( !checked );

            if ( checked ) {
                CHK_HYPO.setChecked( false );
            }
        });

        CHK_HYPO.setOnCheckedChangeListener( (view, checked) -> {
            CHK_HYPER.setEnabled( !checked );

            if ( checked ) {
                CHK_HYPER.setChecked( false );
            }
        });

        CHK_ANOREXIA.setOnCheckedChangeListener( (view, checked) -> {
            CHK_OBESITY.setEnabled( !checked );

            if ( checked ) {
                CHK_OBESITY.setChecked( false );
            }
        });

        CHK_OBESITY.setOnCheckedChangeListener( (view, checked) -> {
            CHK_ANOREXIA.setEnabled( !checked );

            if ( checked ) {
                CHK_ANOREXIA.setChecked( false );
            }
        });

        return;
    }

    private void loadFromMigraineRepo()
    {
        final Steps STEPS = MigraineTestActivity.player.getSteps();

        if ( !STEPS.isEmpty() ) {
            final CheckBox CHK_HYPER = this.getCheckBoxFor( this.hypertension.getId() );
            final CheckBox CHK_HYPO = this.getCheckBoxFor( this.hypotension.getId() );
            final CheckBox CHK_OBESITY = this.getCheckBoxFor( this.obesity.getId() );
            final CheckBox CHK_ANOREXIA = this.getCheckBoxFor( this.anorexia.getId() );
            final CheckBox CHK_DEPRESSION = this.getCheckBoxFor( Morbidity.Id.get( "DEPRESSION" ) );

            CHK_HYPER.setChecked( STEPS.hasHyperTension() );
            CHK_HYPO.setChecked( STEPS.hasHypoTension() );
            CHK_OBESITY.setChecked( STEPS.isObese() );
            CHK_ANOREXIA.setChecked( STEPS.isAnorexic() );
            CHK_DEPRESSION.setChecked( STEPS.isDepressed() );
        }

        return;
    }

    private void onStartMedicineDiscards()
    {
        final List<Morbidity.Id> MORBIDITY_IDS = new ArrayList<>();

        // Build the modbidity list
        for(final Morbidity.Id ID: this.entries.keySet()) {
            final CheckBox CHK = this.getCheckBoxFor( ID );

            if ( CHK.isChecked() ) {
                MORBIDITY_IDS.add( ID );
            }
        }

        // Launch activity
        final Intent INTENT = new Intent( this, TreatmentResultActivity.class );
        final TreatmentAdvisor ADVISOR = new TreatmentAdvisor( MORBIDITY_IDS );

        TreatmentResultActivity.medicineList = ADVISOR.determineMedicines();
        this.startActivity( INTENT );
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    private Morbidity anorexia;
    private Morbidity obesity;
    private Morbidity hypotension;
    private Morbidity hypertension;
    private Map<Morbidity.Id, View> entries;
}
