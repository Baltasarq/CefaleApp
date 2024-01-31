// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.treatment.Dosage;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MedicineActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_medicine );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_medicine );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medicine );
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.showMedicine();
    }

    private void showMedicine()
    {
        final TextView LBL_MINIMUM_DOSAGE = this.findViewById( R.id.lblMedicineMinimumDosage );
        final TextView LBL_RECOMMENDED_DOSAGE = this.findViewById( R.id.lblMedicineRecommendedDosage );
        final TextView LBL_MAXIMUM_DOSAGE = this.findViewById( R.id.lblMedicineMaximumDosage );
        final TextView LBL_ADVERSE_EFFECTS = this.findViewById( R.id.lblMedicineAdverseEffects );
        final TextView LBL_MEDICINE_GROUP = this.findViewById( R.id.lblMedicineMedicineGroup );
        final LinearLayout LY_MIN_DOSAGE = this.findViewById( R.id.lyMedicineMinimumDosage );
        final LinearLayout LY_REC_DOSAGE = this.findViewById( R.id.lyMedicineRecommendedDosage );
        final LinearLayout LY_MAX_DOSAGE = this.findViewById( R.id.lyMedicineMaximumDosage );
        final FloatingActionButton BT_WEB = this.findViewById( R.id.btMedicineWeb );
        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final Dosage MIN_DOSAGE = medicine.getMinDosage();
        final Dosage MAX_DOSAGE = medicine.getMaxDosage();
        final Dosage REC_DOSAGE = medicine.getRecommendedDosage();
        final String LBL_PILLS = this.getString( R.string.lbl_pills );
        final int MIN_DOSAGE_VISIBLE = MIN_DOSAGE.isValid() ? View.VISIBLE : View.GONE;
        final int MAX_DOSAGE_VISIBLE = MAX_DOSAGE.isValid() ? View.VISIBLE : View.GONE;
        final int REC_DOSAGE_VISIBLE = REC_DOSAGE.isValid() ? View.VISIBLE : View.GONE;

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( this.getString( R.string.lbl_medicine )
                                + ": "
                                + medicine.getId().toString() );
        }

        // Set info
        if ( MIN_DOSAGE_VISIBLE == View.VISIBLE ) {
            LBL_MINIMUM_DOSAGE.setText(
                    medicine.getMinDosage().getFormatted( LBL_PILLS ));
        }

        if ( REC_DOSAGE_VISIBLE == View.VISIBLE ) {
            LBL_RECOMMENDED_DOSAGE.setText(
                    medicine.getRecommendedDosage().getFormatted( LBL_PILLS ));
        }

        if ( MAX_DOSAGE_VISIBLE == View.VISIBLE ) {
            LBL_MAXIMUM_DOSAGE.setText(
                    medicine.getMaxDosage().getFormatted( LBL_PILLS ));
        }

        LBL_ADVERSE_EFFECTS.setText( medicine.getAdverseEffects() );
        LBL_MEDICINE_GROUP.setText( medicine.getGroup().getId().getName() );
        BT_WEB.setOnClickListener( v -> openVademecum( medicine.getUrl() ) );

        // Set visibility
        LY_MIN_DOSAGE.setVisibility( MIN_DOSAGE_VISIBLE );
        LY_MAX_DOSAGE.setVisibility( MAX_DOSAGE_VISIBLE );
        LY_REC_DOSAGE.setVisibility( REC_DOSAGE_VISIBLE );
    }

    private void openVademecum(String url)
    {
        final Intent BROWSER_INTENT = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
        this.startActivity( BROWSER_INTENT );
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    public static Medicine medicine;
}
