// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.devbaltasarq.cefaleapp.R;


public class TreatmentDeliverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_treatment_deliver );

        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final Button BT_PREVENTIVE = this.findViewById( R.id.btPreventiveTreatment );
        final Button BT_SYMPTOMATIC = this.findViewById( R.id.btSymptomaticTreatment );

        // Back
        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_treatment );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medical );
        }

        BT_PREVENTIVE.setOnClickListener( (v) -> this.launchPreventiveTreatment() );
        BT_SYMPTOMATIC.setOnClickListener( (v) -> this.lanchSymptomaticTreatment() );
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    private void launchPreventiveTreatment()
    {
        this.startActivity(
                new Intent( this, PreventiveTreatmentActivity.class ) );
    }

    private void lanchSymptomaticTreatment()
    {
        this.startActivity(
                new Intent( this, SymptomaticTreatmentActivity.class ) );
    }
}
