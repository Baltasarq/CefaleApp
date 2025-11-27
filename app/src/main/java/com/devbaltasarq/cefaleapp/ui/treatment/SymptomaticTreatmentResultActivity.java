// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.Language;
import com.devbaltasarq.cefaleapp.core.RichText;
import com.devbaltasarq.cefaleapp.core.Identifiable;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.advisor.TreatmentStep;
import com.devbaltasarq.cefaleapp.ui.CefaleAppActivity;

import java.util.List;


public class SymptomaticTreatmentResultActivity extends CefaleAppActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView( R.layout.activity_symptomatic_treatment_result );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_treatment );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medical );
        }

        this.buildSymptomaticTreatmentSteps();
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

    private void buildSymptomaticTreatmentSteps()
    {
        final LinearLayout LY_SYMPTOMATIC = this.findViewById( R.id.lySymptomaticTreatment);
        final LayoutInflater INFLATER = this.getLayoutInflater();
        final Language LANG = Language.langFromDefaultLocale();

        int stepNum = 1;
        for(final TreatmentStep STEP: treatmentStepList) {
            final View STEP_VIEW = INFLATER.inflate( R.layout.treatment_step_entry, null );
            final LinearLayout LY_MEDICINES = STEP_VIEW.findViewById( R.id.lyMedicines );
            final TextView LBL_DESC = STEP_VIEW.findViewById( R.id.lblDesc );
            final List<Medicine> MEDICINES = Identifiable.objListFromIdList( Medicine.getAll(), STEP.getMedicineIds() );
            final String NUM = "<p><b>" + stepNum + "</b>.&nbsp;";

            LBL_DESC.setText( new RichText( NUM + STEP.getDesc().get( LANG )
                                                + "</p>" ).get() );
            ++stepNum;

            for(final Medicine MEDICINE: MEDICINES) {
                final View MEDICINE_ENTRY = INFLATER.inflate( R.layout.medicine_entry, null );
                final TextView LBL_MEDICINE = MEDICINE_ENTRY.findViewById( R.id.lblMedicine );

                LBL_MEDICINE.setText( MEDICINE.getId().getName().getForCurrentLanguage() );
                MEDICINE_ENTRY.setOnClickListener( (v) -> this.showMedicine( MEDICINE ) );
                LY_MEDICINES.addView( MEDICINE_ENTRY );
            }

            LY_SYMPTOMATIC.addView( STEP_VIEW );
        }

        return;
    }

    public static List<TreatmentStep> treatmentStepList;
}
