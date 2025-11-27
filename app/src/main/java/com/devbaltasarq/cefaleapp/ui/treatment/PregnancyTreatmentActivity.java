// CefaleApp (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import androidx.appcompat.app.ActionBar;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.ui.CefaleAppActivity;


public class PregnancyTreatmentActivity extends CefaleAppActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView( R.layout.activity_pregnancy );

        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final WebView WView = this.findViewById( R.id.lbl_pregnancy_info );

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_pregnancy_treatment );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medicine );
        }

        WView.getSettings().setJavaScriptEnabled( true );
        this.loadL10nHTML( WView, "pregnancy_lactation-treatment.html" );
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }
}
