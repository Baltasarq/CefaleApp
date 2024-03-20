// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;

import java.util.List;


public class MorbidityActivity extends AppCompatActivity {
    private static final String LOG_TAG = MorbidityActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_morbidity );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        // Back
        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_treatment );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.cephalea );
        }

        this.initMorbidityInfo();
    }

    private void initMorbidityInfo()
    {
        final TextView LBL_DETAILS = this.findViewById( R.id.lblDesc );
        final LinearLayout LY_COMPATIBLE_MEDICINES = this.findViewById( R.id.lyCompatibleMedicines);
        final LinearLayout LY_INCOMPATIBLE_MEDICINES = this.findViewById( R.id.lyIncompatibleMedicines);
        final TextView LBL_COMPATIBLE_MEDICINES = this.findViewById( R.id.lblCompatibleMedicines );
        final TextView LBL_INCOMPATIBLE_MEDICINES = this.findViewById( R.id.lblIncompatibleMedicines );
        final List<Medicine.Id> MID_COMPATIBLE = morbidity.getAllAdvisedMedicines();
        final List<Medicine.Id> MID_INCOMPATIBLE = morbidity.getAllIncompatibleMedicines();

        this.setTitle( morbidity.getId().getName() );
        LBL_DETAILS.setText( Util.formatText( morbidity.getDesc() ) );

        // Build compatible medicines
        if ( !MID_COMPATIBLE.isEmpty() ) {
            for(Medicine.Id mid: MID_COMPATIBLE) {
                LY_COMPATIBLE_MEDICINES.addView( buildMedicineEntry( mid ) );
            }

            LBL_COMPATIBLE_MEDICINES.setVisibility( View.VISIBLE );
        } else {
            LBL_COMPATIBLE_MEDICINES.setVisibility( View.GONE );
        }

        // Build incompatible medicines
        if ( !MID_INCOMPATIBLE.isEmpty() ) {
            for(Medicine.Id mid: MID_INCOMPATIBLE) {
                LY_INCOMPATIBLE_MEDICINES.addView( buildMedicineEntry( mid ) );
            }

            LBL_INCOMPATIBLE_MEDICINES.setVisibility( View.VISIBLE );
        } else {
            LBL_INCOMPATIBLE_MEDICINES.setVisibility( View.GONE );
        }

        return;
    }

    private View buildMedicineEntry(final Medicine.Id MID)
    {
        final Medicine MEDICINE = Medicine.getAll().get( MID );
        final LayoutInflater INFLATER = this.getLayoutInflater();
        final View MEDICINE_ENTRY = INFLATER.inflate( R.layout.medicine_group_entry, null );
        final TextView LBL_MEDICINE = MEDICINE_ENTRY.findViewById( R.id.lblMedicine );

        if ( MEDICINE == null ) {
            throw new Error( "missing medicine: " + MID.toString() );
        }

        LBL_MEDICINE.setText( MEDICINE.getId().getName() );
        LBL_MEDICINE.setOnClickListener( (v) -> this.showMedicine( MEDICINE ) );
        return MEDICINE_ENTRY;
    }

    private void showMedicine(Medicine m)
    {
        final Intent INTENT = new Intent( this, MedicineActivity.class );

        MedicineActivity.medicine = m;
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

    public static Morbidity morbidity;
}
