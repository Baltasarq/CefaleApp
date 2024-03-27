// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.AppInfo;
import com.devbaltasarq.cefaleapp.core.questionnaire.DropboxUsrClient;
import com.devbaltasarq.cefaleapp.core.questionnaire.HITFormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.MIDASFormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineFormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.FormXMLLoader;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineClass;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineGroup;
import com.devbaltasarq.cefaleapp.core.treatment.TreatmentXMLoader;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;
import com.devbaltasarq.cefaleapp.ui.tests.MigraineTestActivity;
import com.devbaltasarq.cefaleapp.ui.settings.QuestionSettingsActivity;
import com.devbaltasarq.cefaleapp.ui.settings.TextSettingsActivity;
import com.devbaltasarq.cefaleapp.ui.tests.HITFormActivity;
import com.devbaltasarq.cefaleapp.ui.tests.MIDASFormActivity;
import com.devbaltasarq.cefaleapp.ui.tests.TestActivity;
import com.devbaltasarq.cefaleapp.ui.treatment.TreatmentActivity;
import com.devbaltasarq.cefaleapp.ui.treatment.VademecumActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    public enum TextSize { SMALL, MEDIUM, LARGE }
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static String MIGRAINE_FORM_DATA_ASSET = "migraine_test.xml";
    private final static String HIT_FORM_DATA_ASSET = "hit_test.xml";
    private final static String MIDAS_FORM_DATA_ASSET = "midas_test.xml";
    private final static String TREATMENT_DATA_ASSET = "migraine_treatment.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_main );

        this.initDefaults();
        this.prepareDropboxClient();
        this.setButtonListeners();
        this.setNavViewOptionListener();
        this.showFunctionality();
        this.loadData();
    }

    private void initDefaults()
    {
        MigraineTestActivity.playerSettings = new MigraineFormPlayer.Settings();
        textSize = TextSize.MEDIUM;
    }

    private void prepareDropboxClient()
    {
        MigraineTestActivity.drpbxClient = new DropboxUsrClient( this );
    }

    private void setNavViewOptionListener()
    {
        final NavigationView NAV_VIEW = this.findViewById( R.id.nav_view );
        final DrawerLayout LY_DRAWER = this.findViewById( R.id.lyDrawer );
        final TextView LBL_TITLE = this.findViewById( R.id.lblMainTitle );

        this.numTitleBarTaps = 0;
        LBL_TITLE.setOnClickListener( (v) -> {
            this.numTitleBarTaps += 1;

            if ( this.numTitleBarTaps >= 11 ) {
                this.showFunctionality( AppInfo.MODE != AppInfo.ModeType.PHYSICIAN );
                this.numTitleBarTaps = 0;
            }
        });

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
                MigraineTestActivity.class );

        TestActivity.setTextAppeareanceFromTextSize( textSize );
        MigraineTestActivity.player.reset();
        MigraineFormPlayer.settings = MigraineTestActivity.playerSettings.clone();
        this.startActivity( INTENT );
    }

    private void startActivity(Class<?> activityClass)
    {
        this.startActivity( new Intent( this, activityClass ) );
    }

    private void onLaunchHitForm()
    {
        this.startActivity( HITFormActivity.class );
    }

    private void onLaunchMidasForm()
    {
        this.startActivity( MIDASFormActivity.class );
    }

    private void loadData()
    {
        try {
            final TreatmentXMLoader LOADED = TreatmentXMLoader.loadFromXML(
                    this.getAssets().open( TREATMENT_DATA_ASSET ) );
            Medicine.setAllMedicines( LOADED.getMedicines() );
            MedicineGroup.setAllGroups( LOADED.getMedicineGroups() );
            MedicineClass.setAllClasses( LOADED.getMedicineClasses() );
            Morbidity.setAllMorbidities( LOADED.getMorbididties() );

            MigraineTestActivity.migraineForm = FormXMLLoader.loadFromFile(
                                    this.getAssets().open( MIGRAINE_FORM_DATA_ASSET ) );
            MigraineTestActivity.player = new MigraineFormPlayer( MigraineTestActivity.migraineForm );
            MIDASFormActivity.MIDASForm = FormXMLLoader.loadFromFile(
                                    this.getAssets().open( MIDAS_FORM_DATA_ASSET ) );
            MIDASFormActivity.player = new MIDASFormPlayer( MIDASFormActivity.MIDASForm );
            HITFormActivity.HITForm = FormXMLLoader.loadFromFile(
                                    this.getAssets().open( HIT_FORM_DATA_ASSET ) );
            HITFormActivity.player = new HITFormPlayer( HITFormActivity.HITForm );
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

    private void showFunctionality()
    {
        this.showFunctionality( AppInfo.MODE == AppInfo.ModeType.PHYSICIAN );
    }

    private void showFunctionality(boolean show)
    {
        final LinearLayout LY_TREATMENT = this.findViewById( R.id.lyTreatment );
        final LinearLayout LY_VADEMECUM = this.findViewById( R.id.lyVademecum );
        final NavigationView NAV_VIEW = this.findViewById( R.id.nav_view );
        final Menu NAV_MENU = NAV_VIEW.getMenu();
        final MenuItem OP_TREATMENT = NAV_MENU.findItem( R.id.op_start_treatment );
        final MenuItem OP_VADEMECUM = NAV_MENU.findItem( R.id.op_start_vademecum );

        if ( show ) {
            final AlertDialog.Builder DLG = new AlertDialog.Builder( this );
            DLG.setMessage( R.string.msg_app_physician_mode );
            DLG.setPositiveButton( "Ok", null );

            AppInfo.MODE = AppInfo.ModeType.PHYSICIAN;
            DLG.create().show();
        } else {
            AppInfo.MODE = AppInfo.ModeType.PATIENT;
        }

        LY_TREATMENT.setVisibility( show? View.VISIBLE: View.GONE );
        LY_VADEMECUM.setVisibility( show? View.VISIBLE: View.GONE );
        OP_TREATMENT.setVisible( show );
        OP_VADEMECUM.setVisible( show );
    }

    private int numTitleBarTaps;
    public static TextSize textSize;
}
