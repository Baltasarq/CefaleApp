// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.tests;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.AppInfo;
import com.devbaltasarq.cefaleapp.core.questionnaire.Form;
import com.devbaltasarq.cefaleapp.core.questionnaire.FormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineFormPlayer;


public class MigraineTestActivity extends TestActivity {
    private final static String LOG_TAG = MigraineTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_migraineform );

        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final ImageView TEST_IMAGE = this.findViewById( R.id.imgTestIcon );

        // Migraine test
        TEST_IMAGE.setImageDrawable(
                AppCompatResources.getDrawable( this, R.drawable.test_migraine ) );

        // General
        this.setTitle( AppInfo.FULL_NAME );

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_test_migraine );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.cephalea );
        }

        this.init();
    }

    @Override
    public FormPlayer getFormPlayer()
    {
        return player;
    }

    @Override
    public String getLogTag()
    {
        return LOG_TAG;
    }

    @Override
    public Form getForm()
    {
        return migraineForm;
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
    public void showFormEnd()
    {
        // Prepare form end
        super.showFormEnd();

        // Details
        final LinearLayout LY_IMAGE = this.findViewById( R.id.lyImageTest );
        final TextView LBL_QUESTION = this.findViewById( R.id.lblQuestion );
        final ImageButton BT_TALK = this.findViewById( R.id.btTalk );
        final MigraineFormPlayer PLAYER = (MigraineFormPlayer) this.getFormPlayer();
        final String FINAL_REPORT = PLAYER.getFinalReport();
        final StringBuilder END_TEXT = new StringBuilder();
        final ImageView BT_SHARE = this.buildButton( LY_IMAGE, android.R.drawable.ic_menu_share );

        // Set info
        END_TEXT.append( "Final del cuestionario<br/><br/>" );
        END_TEXT.append( FINAL_REPORT );
        this.setTextInTextView( LBL_QUESTION, END_TEXT.toString() );

        BT_TALK.setVisibility( View.GONE );
        BT_SHARE.setOnClickListener( (v) -> {
            final Intent SEND_INTENT = new Intent( Intent.ACTION_SEND );
            SEND_INTENT.putExtra( Intent.EXTRA_TEXT, FINAL_REPORT );
            SEND_INTENT.setType( "text/html" );

            final Intent SHARE_INTENT = Intent.createChooser( SEND_INTENT, null );
            this.startActivity( SHARE_INTENT );
        });

        this.shutUp();
    }

    public static Form migraineForm;
    public static MigraineFormPlayer player;
    public static MigraineFormPlayer.Settings playerSettings;
}
