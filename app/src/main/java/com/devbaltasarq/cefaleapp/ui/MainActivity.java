// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.questionnaire.DropboxUsrClient;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineFormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.FormXMLLoader;
import com.devbaltasarq.cefaleapp.ui.questionnaire.EnquiryActivity;
import com.devbaltasarq.cefaleapp.ui.questionnaire.QuestionSettingsActivity;
import com.devbaltasarq.cefaleapp.ui.questionnaire.TextSettingsActivity;
import com.devbaltasarq.cefaleapp.ui.treatment.TreatmentActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    public enum TextSize { SMALL, MEDIUM, LARGE }
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static String DATA_ASSET = "migraine_test.xml";

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
            final MainActivity SELF = MainActivity.this;
            Intent intent = null;

            if ( item.getItemId() == R.id.op_text_settings ) {
                intent = new Intent( SELF, TextSettingsActivity.class );
            }
            else
            if ( item.getItemId() == R.id.op_enquiry_settings ) {
                intent = new Intent( SELF, QuestionSettingsActivity.class );
            }
            else
            if ( item.getItemId() == R.id.op_start_enquiry ) {
                intent = new Intent( SELF, EnquiryActivity.class );
            }
            else
            if ( item.getItemId() == R.id.op_start_treatment ) {
                intent = new Intent( SELF, TreatmentActivity.class );
            } else {
                throw new Error( "unknown option" );
            }

            if ( intent != null ) {
                SELF.startActivity( intent );
            }

            LY_DRAWER.closeDrawers();
            return true;
        });

        return;
    }

    private void setButtonListeners()
    {
        final ImageButton BT_MENU = this.findViewById( R.id.btMenu );
        final FloatingActionButton BT_START = this.findViewById( R.id.btStartEnquiry);
        final DrawerLayout LY_DRAWER = this.findViewById( R.id.lyDrawer );

        BT_START.setOnClickListener( (v) -> {
            final Intent INTENT = new Intent(
                    MainActivity.this,
                    EnquiryActivity.class );

            EnquiryActivity.setTextAppeareanceFromTextSize( textSize );
            EnquiryActivity.player.reset();
            MigraineFormPlayer.settings = EnquiryActivity.playerSettings.clone();
            MainActivity.this.startActivity( INTENT );
        });

        BT_MENU.setOnClickListener( (v) -> {
            LY_DRAWER.openDrawer( GravityCompat.START, true );
        });

        return;
    }

    private void loadData()
    {
        final FloatingActionButton BT_START = this.findViewById( R.id.btStartEnquiry);
        final TextView TV_TITLE = this.findViewById( R.id.tvTitle );
        final TextView TV_INSTITUTION1 = this.findViewById( R.id.tvInstitution1 );
        final TextView TV_INSTITUTION2 = this.findViewById( R.id.tvInstitution2 );
        final ImageView IMG_INSTITUTION1 = this.findViewById( R.id.imgInstitution1 );
        final ImageView IMG_INSTITUTION2 = this.findViewById( R.id.imgInstitution2 );

        try {
            EnquiryActivity.form = FormXMLLoader.loadFromFile( this.getAssets().open( DATA_ASSET ) );
            EnquiryActivity.player = new MigraineFormPlayer( EnquiryActivity.form );
        } catch(IOException exc) {
            TV_INSTITUTION1.setVisibility( View.GONE );
            TV_INSTITUTION2.setVisibility( View.GONE );
            IMG_INSTITUTION1.setVisibility( View.GONE );
            IMG_INSTITUTION2.setVisibility( View.GONE );
            TV_TITLE.setText( "i/o error: " + exc.getMessage() );
            Log.e( LOG_TAG, "error loading asset data: " + exc.getMessage() );
            BT_START.setEnabled( false );
        }

        return;
    }

    public static TextSize textSize;
}
