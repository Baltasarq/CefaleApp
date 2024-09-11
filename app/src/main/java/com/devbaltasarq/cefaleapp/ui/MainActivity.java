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
import com.devbaltasarq.cefaleapp.core.faq.Faq;
import com.devbaltasarq.cefaleapp.core.faq.FaqXMLLoader;
import com.devbaltasarq.cefaleapp.core.links.WebUrl;
import com.devbaltasarq.cefaleapp.core.links.WebUrlXMLLoader;
import com.devbaltasarq.cefaleapp.core.questionnaire.HITFormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.MIDASFormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineFormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.FormXMLLoader;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineClass;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineGroup;
import com.devbaltasarq.cefaleapp.core.treatment.TreatmentXMLoader;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;
import com.devbaltasarq.cefaleapp.core.treatment.advisor.TreatmentMessage;
import com.devbaltasarq.cefaleapp.ui.tests.MigraineTestActivity;
import com.devbaltasarq.cefaleapp.ui.settings.QuestionSettingsActivity;
import com.devbaltasarq.cefaleapp.ui.settings.TextSettingsActivity;
import com.devbaltasarq.cefaleapp.ui.tests.HITFormActivity;
import com.devbaltasarq.cefaleapp.ui.tests.MIDASFormActivity;
import com.devbaltasarq.cefaleapp.ui.tests.TestActivity;
import com.devbaltasarq.cefaleapp.ui.treatment.TreatmentDeliverActivity;
import com.devbaltasarq.cefaleapp.ui.treatment.VademecumActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    public enum TextSize { SMALL, MEDIUM, LARGE }
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private final static String MIGRAINE_FORM_DATA_ASSET = "migraine_test.xml";
    private final static String HIT_FORM_DATA_ASSET = "hit_test.xml";
    private final static String MIDAS_FORM_DATA_ASSET = "midas_test.xml";
    private final static String TREATMENT_DATA_ASSET = "migraine_treatment.xml";
    private final static String FAQ_DATA_ASSET = "faq.xml";
    private final static String LINKS_DATA_ASSET = "links.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_main );

        this.initDefaults();
        this.setButtonListeners();
        this.setNavViewOptionListener();
        this.loadData();
    }

    private void initDefaults()
    {
        MigraineTestActivity.playerSettings = new MigraineFormPlayer.Settings();
        textSize = TextSize.MEDIUM;
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
                this.gotoActivity( TreatmentDeliverActivity.class );
            }
            else
            if ( item.getItemId() == R.id.op_start_vademecum ) {
                this.gotoActivity( VademecumActivity.class );
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
                this.gotoActivity( InfoActivity.class );
            }
            else
            if ( item.getItemId() == R.id.op_start_about ) {
                this.gotoActivity( AboutActivity.class );
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
        final DrawerLayout LY_DRAWER = this.findViewById( R.id.lyDrawer );
        final ImageView BT_ABOUT = this.findViewById( R.id.btAbout );
        final ImageView BT_INFO = this.findViewById( R.id.btInfo );
        final ImageView BT_LINKS = this.findViewById( R.id.btLinks );
        final ImageView BT_VADEMECUM = this.findViewById( R.id.btVademecum );
        final ImageView BT_ENQUIRY = this.findViewById( R.id.btTestMigraine);
        final ImageView BT_TREATMENT = this.findViewById( R.id.btTreatment );
        final ImageView BT_HIT = this.findViewById( R.id.btHIT );
        final ImageView BT_MIDAS = this.findViewById( R.id.btMIDAS );

        BT_MENU.setOnClickListener( v ->
            LY_DRAWER.openDrawer( GravityCompat.START, true )
        );

        BT_ENQUIRY.setOnClickListener( v -> this.onEnquiry() );
        BT_ABOUT.setOnClickListener( v -> this.gotoActivity( AboutActivity.class ));
        BT_INFO.setOnClickListener( v -> this.gotoActivity( InfoActivity.class ));
        BT_LINKS.setOnClickListener( v -> this.gotoActivity( LinksActivity.class ));
        BT_VADEMECUM.setOnClickListener( v-> this.gotoActivity( VademecumActivity.class ));
        BT_TREATMENT.setOnClickListener( v-> this.gotoActivity( TreatmentDeliverActivity.class ));
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
                MigraineTestActivity.class );

        TestActivity.setTextAppeareanceFromTextSize( textSize );
        MigraineTestActivity.player.reset();
        MigraineFormPlayer.settings = MigraineTestActivity.playerSettings.clone();
        this.startActivity( INTENT );
    }

    private void gotoActivity(Class<?> activityClass)
    {
        this.startActivity( new Intent( this, activityClass ) );
    }

    private void onLaunchHitForm()
    {
        this.gotoActivity( HITFormActivity.class );
    }

    private void onLaunchMidasForm()
    {
        this.gotoActivity( MIDASFormActivity.class );
    }

    private void loadData()
    {
        try {
            // Load all treatment-related data
            final TreatmentXMLoader TR_LOADER = TreatmentXMLoader.loadFromXML(
                    this.getAssets().open( TREATMENT_DATA_ASSET ) );
            Medicine.setAll( TR_LOADER.getMedicines() );
            MedicineGroup.setAll( TR_LOADER.getMedicineGroups() );
            MedicineClass.setAll( TR_LOADER.getMedicineClasses() );
            Morbidity.setAll( TR_LOADER.getMorbididties() );
            TreatmentMessage.setAll( TR_LOADER.getTreatmentMessages() );

            // Load all tests
            MigraineTestActivity.migraineForm = FormXMLLoader.loadFrom(
                                    this.getAssets().open( MIGRAINE_FORM_DATA_ASSET ) );
            MigraineTestActivity.player = new MigraineFormPlayer( MigraineTestActivity.migraineForm );
            MIDASFormActivity.MIDASForm = FormXMLLoader.loadFrom(
                                    this.getAssets().open( MIDAS_FORM_DATA_ASSET ) );
            MIDASFormActivity.player = new MIDASFormPlayer( MIDASFormActivity.MIDASForm );
            HITFormActivity.HITForm = FormXMLLoader.loadFrom(
                                    this.getAssets().open( HIT_FORM_DATA_ASSET ) );
            HITFormActivity.player = new HITFormPlayer( HITFormActivity.HITForm );

            // Load FAQ entries
            Faq.setAll( FaqXMLLoader.loadFrom( this.getAssets().open( FAQ_DATA_ASSET ) ) );

            // Load links
            WebUrl.setAll( WebUrlXMLLoader.loadFrom( this.getAssets().open( LINKS_DATA_ASSET ) ) );
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
