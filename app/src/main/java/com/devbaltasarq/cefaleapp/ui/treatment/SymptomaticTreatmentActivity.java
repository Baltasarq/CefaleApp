// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.transition.Transition;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineRepo;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;
import com.devbaltasarq.cefaleapp.core.treatment.advisor.SymptomaticTreatmentAdvisor;
import com.devbaltasarq.cefaleapp.ui.tests.MigraineTestActivity;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SymptomaticTreatmentActivity extends TreatmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView( R.layout.activity_symptomatic_treatment );

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

        final Morbidity.Id ID_PAIN_INTENSE = Morbidity.Id.get( "PAIN_INTENSE" );
        final Morbidity.Id ID_PAIN_LOW_FREQUENCY = Morbidity.Id.get( "PAIN_LOW_FREQUENCY" );

        this.painIntense = Objects.requireNonNull( MORBIDITIES.get( ID_PAIN_INTENSE ) );
        this.painLowFrequency = Objects.requireNonNull( MORBIDITIES.get( ID_PAIN_LOW_FREQUENCY ) );
    }

    @Override
    protected void populateLayout(final LinearLayout LY,
                                  final Map<Morbidity.Id, Morbidity> MORBIDITIES)
    {
        final Morbidity.Id ID_SULFAMID_ALLERGY = Morbidity.Id.get( "ALLERGY_SULFAMID" );
        final Morbidity.Id ID_ASPIRIN_ALLERGY = Morbidity.Id.get( "ALLERGY_ACETILSALICIDIC_ACID" );

        final Morbidity SULFAMID_ALLERGY = Objects.requireNonNull(
                                                MORBIDITIES.get( ID_SULFAMID_ALLERGY ));
        final Morbidity ASPIRIN_ALLERGY = Objects.requireNonNull(
                                                MORBIDITIES.get( ID_ASPIRIN_ALLERGY ));

        LY.removeAllViews();
        this.entries.clear();
        MORBIDITIES.clear();

        MORBIDITIES.put( this.painIntense.getId(), this.painIntense );
        MORBIDITIES.put( this.painLowFrequency.getId(), this.painLowFrequency );
        MORBIDITIES.put( ID_SULFAMID_ALLERGY, SULFAMID_ALLERGY );
        MORBIDITIES.put( ID_ASPIRIN_ALLERGY, ASPIRIN_ALLERGY );

        super.populateLayout( LY, MORBIDITIES );
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

    @Override
    protected void loadFromMigraineRepo()
    {
        final MigraineRepo REPO = MigraineTestActivity.player.getRepo();

        if ( !REPO.isEmpty() ) {
            final CheckBox CHK_PAIN_INTENSE = Objects.requireNonNull(
                    this.getCheckBoxFor( this.painIntense.getId() ));
            final CheckBox CHK_PAIN_LOW_FREQ = Objects.requireNonNull(
                    this.getCheckBoxFor( this.painLowFrequency.getId() ));

            CHK_PAIN_LOW_FREQ.setChecked(
                    REPO.getMixedFreq() == MigraineRepo.Frequency.ESPORADIC
                    || REPO.getMixedFreq() == MigraineRepo.Frequency.LOW );

            CHK_PAIN_INTENSE.setChecked( REPO.isPainIntense() );
        }

        return;
    }

    @Override
    protected void launchTreatmentResult(final List<Morbidity.Id> MORBIDITY_IDS)
    {
        final Intent INTENT = new Intent( this, SymptomaticTreatmentResultActivity.class );
        final SymptomaticTreatmentAdvisor ADVISOR = new SymptomaticTreatmentAdvisor( MORBIDITY_IDS );

        SymptomaticTreatmentResultActivity.treatmentStepList = ADVISOR.createResultList();
        this.startActivity( INTENT );
    }

    private Morbidity painIntense;
    private Morbidity painLowFrequency;
}
