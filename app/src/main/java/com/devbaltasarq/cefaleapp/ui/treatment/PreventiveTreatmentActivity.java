// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import androidx.appcompat.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineRepo;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;
import com.devbaltasarq.cefaleapp.core.treatment.TreatmentAdvisor;
import com.devbaltasarq.cefaleapp.core.treatment.advisor.PreventiveTreatmentAdvisor;
import com.devbaltasarq.cefaleapp.ui.tests.MigraineTestActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class PreventiveTreatmentActivity extends TreatmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_preventive_treatment);

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_treatment );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.cephalea );
        }

        this.init();
    }

    @Override
    protected void prepareMorbidityIds()
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

    @Override
    protected void populateLayout(
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

        // Remove the uninteresting ones for preventive treatment
        MORBIDITIES.remove( Morbidity.Id.get( "PAIN_INTENSE" ) );
        MORBIDITIES.remove( Morbidity.Id.get( "PAIN_MODERATE" ) );
        MORBIDITIES.remove( Morbidity.Id.get( "ALLERGY_SULFAMID" ) );
        MORBIDITIES.remove( Morbidity.Id.get( "ALLERGY_ACETILSALICIDIC_ACID" ) );

        super.populateLayout( LY, MORBIDITIES );
    }

    @Override
    protected void buildCheckboxDependencies()
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

    @Override
    protected void loadFromMigraineRepo()
    {
        final MigraineRepo REPO = MigraineTestActivity.player.getRepo();

        if ( !REPO.isEmpty() ) {
            final CheckBox CHK_HYPER = Objects.requireNonNull(
                                        this.getCheckBoxFor( this.hypertension.getId() ));
            final CheckBox CHK_HYPO = Objects.requireNonNull(
                                        this.getCheckBoxFor( this.hypotension.getId() ));
            final CheckBox CHK_OBESITY = Objects.requireNonNull(
                                        this.getCheckBoxFor( this.obesity.getId() ));
            final CheckBox CHK_ANOREXIA = Objects.requireNonNull(
                                        this.getCheckBoxFor( this.anorexia.getId() ));
            final CheckBox CHK_DEPRESSION = Objects.requireNonNull(
                                        this.getCheckBoxFor( Morbidity.Id.get( "DEPRESSION" ) ));

            CHK_HYPER.setChecked( REPO.hasHyperTension() );
            CHK_HYPO.setChecked( REPO.hasHypoTension() );
            CHK_OBESITY.setChecked( REPO.isObese() );
            CHK_ANOREXIA.setChecked( REPO.isAnorexic() );
            CHK_DEPRESSION.setChecked( REPO.isDepressed() );
        }

        return;
    }

    @Override
    protected void launchTreatmentResult(final List<Morbidity.Id> MORBIDITY_IDS)
    {
        final Intent INTENT = new Intent( this, PreventiveTreatmentResultActivity.class );
        final TreatmentAdvisor ADVISOR = new PreventiveTreatmentAdvisor( MORBIDITY_IDS );

        PreventiveTreatmentResultActivity.medicineList = (List<Medicine>) ADVISOR.createResultList();
        this.startActivity( INTENT );
    }

    @Override
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
}
