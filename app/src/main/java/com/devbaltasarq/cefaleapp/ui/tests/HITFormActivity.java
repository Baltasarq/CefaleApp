// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.tests;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.questionnaire.Form;
import com.devbaltasarq.cefaleapp.core.questionnaire.HITFormPlayer;


public class HITFormActivity extends TestActivity {
    private static final String LOG_TAG = HITFormActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_hitform );

        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final ImageView TEST_IMAGE = this.findViewById( R.id.imgTestIcon );

        // Migraine test
        TEST_IMAGE.setImageDrawable(
                AppCompatResources.getDrawable( this, R.drawable.test_hit ) );

        // General
        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_test_hit );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medicine );
        }

        this.init();
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public void init()
    {
        super.init();

        if ( player == null ) {
            player = new HITFormPlayer( HITForm );
        }

        player.reset();
    }

    @Override
    public HITFormPlayer getFormPlayer()
    {
        return player;
    }

    @Override
    public Form getForm()
    {
        return HITForm;
    }

    @Override
    public String getLogTag()
    {
        return LOG_TAG;
    }

    @Override
    public void showFormEnd()
    {
        // Prepare form end
        super.showFormEnd();

        // Details
        final TextView LBL_QUESTION = this.findViewById( R.id.lblQuestion );
        final StringBuilder END_TEXT = new StringBuilder();
        final LinearLayout LY_IMAGE = this.findViewById( R.id.lyImage );
        final ImageView BT_SHARE = this.buildButton( LY_IMAGE, android.R.drawable.ic_menu_share );
        final String FINAL_REPORT = player.getFinalReport();

        // Set info
        END_TEXT.append( "Final del cuestionario<br/><br/>" );
        END_TEXT.append( FINAL_REPORT );
        this.setTextInTextView( LBL_QUESTION, END_TEXT.toString() );

        BT_SHARE.setOnClickListener( (v) -> {
            final Intent SEND_INTENT = new Intent();
            SEND_INTENT.setAction( Intent.ACTION_SEND );
            SEND_INTENT.putExtra( Intent.EXTRA_TEXT, FINAL_REPORT );
            SEND_INTENT.setType( "text/html" );

            final Intent SHARE_INTENT = Intent.createChooser( SEND_INTENT, null );
            this.startActivity( SHARE_INTENT );
        });

        this.launchSpeak( LBL_QUESTION.getText().toString() );
    }

    public static Form HITForm;
    public static HITFormPlayer player;
}
