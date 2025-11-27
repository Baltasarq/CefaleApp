// CefaleApp (c) 20023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.AppInfo;


public class AboutActivity extends CefaleAppActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_about );

        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final ImageView BT_ABOUT = this.findViewById( R.id.btAboutAbout );
        final ImageView BT_INFO = this.findViewById( R.id.btAboutInfo );
        final ImageView BT_DETAIL = this.findViewById( R.id.btAboutDetailsInfo );
        final WebView LBL_APP_INFO = this.findViewById( R.id.lbl_app_info );
        final TextView TV_VERSION = this.findViewById( R.id.tvVersion );

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle(
                    this.getString( R.string.lbl_about )
                    + " " + AppInfo.NAME );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medicine );
        }

        // Prepare
        LBL_APP_INFO.getSettings().setJavaScriptEnabled( true );
        this.loadL10nHTML( LBL_APP_INFO, "about.html" );
        TV_VERSION.setText( AppInfo.FULL_NAME );
        BT_ABOUT.setOnClickListener( this::buttonPressed );
        BT_INFO.setOnClickListener( this::buttonPressed );
        BT_DETAIL.setOnClickListener( this::buttonPressed );
        this.buttonPressed( BT_DETAIL );
    }

    private void buttonPressed(View v)
    {
        final ImageView BT_ABOUT = this.findViewById( R.id.btAboutAbout );
        final ImageView BT_INFO = this.findViewById( R.id.btAboutInfo );
        final ImageView BT_DETAIL = this.findViewById( R.id.btAboutDetailsInfo );
        final LinearLayout LY_ABOUT = this.findViewById( R.id.lyAboutAbout );
        final WebView LY_INFO = this.findViewById( R.id.lbl_app_info );
        final TableLayout LY_TABLE = this.findViewById( R.id.tblAppInfo );

        if ( v == BT_INFO ) {
            BT_INFO.setAlpha( 0.25f );
            BT_ABOUT.setAlpha( 1f );
            BT_DETAIL.setAlpha( 1f );
            LY_ABOUT.setVisibility( View.GONE );
            LY_INFO.setVisibility( View.VISIBLE );
            LY_TABLE.setVisibility( View.GONE );
        }
        else
        if ( v == BT_ABOUT ) {
            BT_INFO.setAlpha( 1f );
            BT_ABOUT.setAlpha( 0.25f );
            BT_DETAIL.setAlpha( 1f );
            LY_ABOUT.setVisibility( View.VISIBLE );
            LY_INFO.setVisibility( View.GONE );
            LY_TABLE.setVisibility( View.GONE );
        } else {
            BT_INFO.setAlpha( 1f );
            BT_ABOUT.setAlpha( 1f );
            BT_DETAIL.setAlpha( 0.25f );
            LY_ABOUT.setVisibility( View.GONE );
            LY_INFO.setVisibility( View.GONE );
            LY_TABLE.setVisibility( View.VISIBLE );
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
}
