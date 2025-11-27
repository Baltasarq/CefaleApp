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
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.ui.CefaleAppActivity;

import java.util.List;


public class PreventiveTreatmentResultActivity extends CefaleAppActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_preventive_treatment_result );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_treatment );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medical );
        }

        this.buildPreventiveMedicineList();
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    private void buildPreventiveMedicineList()
    {
        final LayoutInflater INFLATER = this.getLayoutInflater();
        final LinearLayout LY_PREVENTIVE = this.findViewById( R.id.lyPreventiveTreatment );

        this.sortIdentifiableI18n( medicineList );

        for(final Medicine MEDICINE: medicineList) {
            final View MED_VIEW = INFLATER.inflate( R.layout.medicine_entry, null );
            final TextView LBL_MEDICINE = MED_VIEW.findViewById( R.id.lblMedicine );

            LBL_MEDICINE.setText( MEDICINE.getId().getName().getForCurrentLanguage() );
            LBL_MEDICINE.setOnClickListener( (v) -> this.showMedicine( MEDICINE ) );
            LY_PREVENTIVE.addView( MED_VIEW );
        }

        return;
    }

    public static List<Medicine> medicineList;
}
