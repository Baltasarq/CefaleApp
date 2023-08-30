// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.FormPlayer;
import com.devbaltasarq.cefaleapp.core.FormXMLLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    public enum TextSize { SMALL, MEDIUM, LARGE }
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static String DATA_ASSET = "data.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_main );

        this.setButtonListeners();
        this.setNavViewOptionListener();
        this.initDefaults();
        this.loadData();
    }

    private void initDefaults()
    {
        EnquiryActivity.showNotesQuestion = false;
        this.textSize = TextSize.MEDIUM;
        this.textSize = TextSize.LARGE;
        /*        NAV_VIEW.getMenu().getItem( 2 ).setChecked( true );

        if ( diagonalInches <= 4 ) {
            this.textSize = TextSize.SMALL;
            NAV_VIEW.getMenu().getItem( 0 ).setChecked( true );
        }
        else
        if ( diagonalInches < 8 ) {
            this.textSize = TextSize.MEDIUM;
            NAV_VIEW.getMenu().getItem( 1 ).setChecked( true );
        }
*/
    }

    private void setNavViewOptionListener()
    {
        final NavigationView NAV_VIEW = this.findViewById( R.id.nav_view );
        final DrawerLayout LY_DRAWER = this.findViewById( R.id.lyDrawer );

        NAV_VIEW.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                final MainActivity SELF = MainActivity.this;

                if ( item.getItemId() == R.id.op_text_settings ) {
                    final Intent INTENT = new Intent( SELF, TextSettingsActivity.class );
                    SELF.startActivity( INTENT );
                }
                else
                if ( item.getItemId() == R.id.op_enquiry_settings ) {
                    //EnquiryActivity.showNotesQuestion = item.isChecked();
                } else {
                    throw new Error( "unknown option" );
                }

                LY_DRAWER.closeDrawers();
                return true;
            }
        });

        return;
    }

    private void setButtonListeners()
    {
        final ImageButton BT_MENU = this.findViewById( R.id.btMenu );
        final FloatingActionButton BT_START = this.findViewById( R.id.btStart );
        final DrawerLayout LY_DRAWER = this.findViewById( R.id.lyDrawer );

        BT_START.setOnClickListener( (v) -> {
            final Intent INTENT = new Intent(
                    MainActivity.this,
                    EnquiryActivity.class );

            EnquiryActivity.setTextAppeareanceFromTextSize( this.textSize );
            EnquiryActivity.player.reset();
            MainActivity.this.startActivity( INTENT );
        });

        BT_MENU.setOnClickListener( (v) -> {
            LY_DRAWER.openDrawer( GravityCompat.START, true );
        });

        return;
    }

    private void loadData()
    {
        final FloatingActionButton BT_START = this.findViewById( R.id.btStart );
        final LinearLayout LY_MAIN = this.findViewById( R.id.lyMain );

        try {
            EnquiryActivity.form = FormXMLLoader.loadFromFile( this.getAssets().open( DATA_ASSET ) );
            EnquiryActivity.player = new FormPlayer( EnquiryActivity.form );
        } catch(IOException exc) {
            final TextView LBL_MESSAGE = new TextView( this );

            LBL_MESSAGE.setText( "i/o error: " + exc.getMessage() );
            LY_MAIN.addView( LBL_MESSAGE );
            Log.e( LOG_TAG, "error loading asset data: " + exc.getMessage() );
            BT_START.setEnabled( false );
        }

        return;
    }

    private TextSize textSize;
}

