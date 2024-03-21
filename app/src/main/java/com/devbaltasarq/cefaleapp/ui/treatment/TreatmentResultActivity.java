// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineGroup;


public class TreatmentResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_treatment_result );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_treatment );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medical );
        }

        final LayoutInflater INFLATER = this.getLayoutInflater();
        final LinearLayout LY_PREVENTIVE = this.findViewById( R.id.lyPreventiveTreatment );
        final LinearLayout LY_SYMPTOMATIC = this.findViewById( R.id.lySymptomaticTreatment);

        Util.sortIdentifiableI18n( medicineList );

        for(final Medicine MEDICINE: medicineList) {
            final View MED_VIEW = INFLATER.inflate( R.layout.medicine_group_entry, null );
            final TextView LBL_MEDICINE = MED_VIEW.findViewById( R.id.lblMedicine );
            final MedicineGroup.Id SYMPTOMATIC_GROUP_ID = MedicineGroup.Id.get( 'I' );

            LBL_MEDICINE.setText( MEDICINE.getId().getName() );
            LBL_MEDICINE.setOnClickListener( (v) -> Util.showMedicine( this, MEDICINE ) );

            if ( MEDICINE.getGroupId().equals( SYMPTOMATIC_GROUP_ID ) ) {
                LY_SYMPTOMATIC.addView( MED_VIEW );
            } else {
                LY_PREVENTIVE.addView( MED_VIEW );
            }
        }

        return;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    public static List<Medicine> medicineList;
}
