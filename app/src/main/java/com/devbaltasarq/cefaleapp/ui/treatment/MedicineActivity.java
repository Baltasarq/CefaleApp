// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.Util;
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
        final TextView LBL_ADVERSE_EFFECTS = this.findViewById( R.id.lblMedicineAdverseEffects );
        final TextView LBL_POSOLOGY = this.findViewById( R.id.lblMedicinePosology );
        final TextView LBL_MEDICINE_GROUP = this.findViewById( R.id.lblMedicineMedicineGroup );
        final FloatingActionButton BT_WEB = this.findViewById( R.id.btMedicineWeb );
        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final String COMPLETE_GROUP_NAME = "<b>"
                                    + medicine.getGroup().getClsId().getName()
                                    + "</b>.<br/>"
                                    + this.getString( R.string.lbl_medicine_group )
                                    + ": <i>"
                                    + medicine.getGroupId().getName().toLowerCase()
                                    + "</i>.";

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( this.getString( R.string.lbl_medicine )
                                + ": "
                                + medicine.getId().getName() );
        }

        // Set info
        LBL_POSOLOGY.setText( medicine.getPosology() );
        LBL_ADVERSE_EFFECTS.setText( medicine.getAdverseEffects() );
        LBL_MEDICINE_GROUP.setText( Util.richTextFromHtml( COMPLETE_GROUP_NAME ) );
        BT_WEB.setOnClickListener( v -> openVademecum( medicine.getUrl() ) );
    }

    private void openVademecum(String url)
    {
        this.startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( url ) ) );
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
