// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.questionnaire.DropboxUsrClient;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineFormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.FormXMLLoader;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.MedicinesXMLoader;
import com.devbaltasarq.cefaleapp.ui.questionnaire.EnquiryActivity;
import com.devbaltasarq.cefaleapp.ui.settings.QuestionSettingsActivity;
import com.devbaltasarq.cefaleapp.ui.settings.TextSettingsActivity;
import com.devbaltasarq.cefaleapp.ui.treatment.TreatmentActivity;
import com.devbaltasarq.cefaleapp.ui.treatment.VademecumActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    public enum TextSize { SMALL, MEDIUM, LARGE }
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static String FORM_DATA_ASSET = "migraine_test.xml";
    private final static String MEDICINES_DATA_ASSET = "medicines.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_main );

        this.initDefaults();
        this.prepareDropboxClient();
        this.setButtonListeners();
        this.setNavViewOptionListener();
        this.loadData();
    }

    private void initDefaults()
    {
        EnquiryActivity.playerSettings = new MigraineFormPlayer.Settings();
        textSize = TextSize.MEDIUM;
    }

    private void prepareDropboxClient()
    {
        EnquiryActivity.drpbxClient = new DropboxUsrClient( this );
    }

    private void setNavViewOptionListener()
    {
        final NavigationView NAV_VIEW = this.findViewById( R.id.nav_view );
        final DrawerLayout LY_DRAWER = this.findViewById( R.id.lyDrawer );

        NAV_VIEW.setNavigationItemSelectedListener( (MenuItem item) -> {
            LY_DRAWER.closeDrawers();

            if ( item.getItemId() == R.id.op_text_settings ) {
                this.onTextSettings();
            }
            else
            if ( item.getItemId() == R.id.op_enquiry_settings ) {
                this.onTestSettings();
            }
            else
            if ( item.getItemId() == R.id.op_start_enquiry ) {
                this.onEnquiry();
            }
            else
            if ( item.getItemId() == R.id.op_start_treatment ) {
                this.startActivity( TreatmentActivity.class );
            }
            else
            if ( item.getItemId() == R.id.op_start_vademecum ) {
                this.startActivity( VademecumActivity.class );
            }
            else
            if ( item.getItemId() == R.id.op_start_hit ) {
                this.onLaunchHitForm();
            }
            else
            if ( item.getItemId() == R.id.op_start_midas ) {
                this.onLaunchMidasForm();
            }
            else
            if ( item.getItemId() == R.id.op_start_info ) {
                this.startActivity( InfoActivity.class );
            }
            else
            if ( item.getItemId() == R.id.op_start_about ) {
                this.startActivity( AboutActivity.class );
            } else {
                throw new Error( "unknown option" );
            }

            return true;
        });

        return;
    }

    private void setButtonListeners()
    {
        final ImageButton BT_MENU = this.findViewById( R.id.btMenu );
        final FloatingActionButton BT_START = this.findViewById( R.id.btStartEnquiry);
        final DrawerLayout LY_DRAWER = this.findViewById( R.id.lyDrawer );
        final ImageView BT_ABOUT = this.findViewById( R.id.btAbout );
        final ImageView BT_INFO = this.findViewById( R.id.btInfo );
        final ImageView BT_VADEMECUM = this.findViewById( R.id.btVademecum );
        final ImageView BT_ENQUIRY = this.findViewById( R.id.btEnquiry );
        final ImageView BT_TREATMENT = this.findViewById( R.id.btTreatment );
        final ImageView BT_HIT = this.findViewById( R.id.btHIT );
        final ImageView BT_MIDAS = this.findViewById( R.id.btMIDAS );

        BT_MENU.setOnClickListener( v ->
            LY_DRAWER.openDrawer( GravityCompat.START, true )
        );

        BT_START.setOnClickListener( v -> this.onEnquiry() );
        BT_ENQUIRY.setOnClickListener( v -> this.onEnquiry() );
        BT_ABOUT.setOnClickListener( v -> this.startActivity( AboutActivity.class ));
        BT_INFO.setOnClickListener( v -> this.startActivity( InfoActivity.class ));
        BT_VADEMECUM.setOnClickListener( v-> this.startActivity( VademecumActivity.class ));
        BT_TREATMENT.setOnClickListener( v-> this.startActivity( TreatmentActivity.class ));
        BT_HIT.setOnClickListener( v-> this.onLaunchHitForm() );
        BT_MIDAS.setOnClickListener( v-> this.onLaunchMidasForm() );
    }

    private void onTextSettings()
    {
        final Intent INTENT = new Intent( this, TextSettingsActivity.class );
        this.startActivity( INTENT );
    }

    private void onTestSettings()
    {
        final Intent INTENT = new Intent( this, QuestionSettingsActivity.class );
        this.startActivity( INTENT );
    }

    private void onEnquiry()
    {
        final Intent INTENT = new Intent(
                MainActivity.this,
                EnquiryActivity.class );

        EnquiryActivity.setTextAppeareanceFromTextSize( textSize );
        EnquiryActivity.player.reset();
        MigraineFormPlayer.settings = EnquiryActivity.playerSettings.clone();
        this.startActivity( INTENT );
    }

    private void startActivity(Class<?> activityClass)
    {
        this.startActivity( new Intent( this, activityClass ) );
    }

    private void onLaunchHitForm()
    {

    }

    private void onLaunchMidasForm()
    {

    }

    private void loadData()
    {
        try {
            Medicine.setAllMedicines(
                MedicinesXMLoader.loadFromXML( this.getAssets().open( MEDICINES_DATA_ASSET ) ) );
            EnquiryActivity.form = FormXMLLoader.loadFromFile( this.getAssets().open( FORM_DATA_ASSET ) );
            EnquiryActivity.player = new MigraineFormPlayer( EnquiryActivity.form );
        } catch(IOException exc) {
            final TextView TV_STATUS = this.findViewById( R.id.tvErrorStatus );
            final LinearLayout LY_MAIN = this.findViewById( R.id.lyMain );

            LY_MAIN.setVisibility( View.GONE );
            TV_STATUS.setVisibility( View.VISIBLE );
            TV_STATUS.setText( "i/o error: " + exc.getMessage() );
            Log.e( LOG_TAG, "error loading asset data: " + exc.getMessage() );
        }

        return;
    }

    public static TextSize textSize;
}
