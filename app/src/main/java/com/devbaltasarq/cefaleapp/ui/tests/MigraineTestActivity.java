// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.tests;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.AppInfo;
import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.questionnaire.DropboxUsrClient;
import com.devbaltasarq.cefaleapp.core.questionnaire.Form;
import com.devbaltasarq.cefaleapp.core.questionnaire.FormJSONSaver;
import com.devbaltasarq.cefaleapp.core.questionnaire.FormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.MigraineFormPlayer;
import com.dropbox.core.DbxException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


public class MigraineTestActivity extends TestActivity {
    private final static String LOG_TAG = MigraineTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_migraine_enquiry );

        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final ImageView TEST_IMAGE = this.findViewById( R.id.imgTestIcon );

        // Migraine test
        TEST_IMAGE.setImageDrawable(
                AppCompatResources.getDrawable( this, R.drawable.test_migraine ) );

        // General
        this.setTitle( AppInfo.FULL_NAME );

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_enquiry );
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

    private File saveJSON()
    {
        final File INTERNAL_DIR = this.getCacheDir();
        final String FN = "enq-" + Util.buildSerial() + ".json";
        final File TORET = new File( INTERNAL_DIR, FN );

        try (FileOutputStream f = new FileOutputStream( TORET ) ) {
            final DataOutputStream STREAM = new DataOutputStream( f );
            final PrintWriter WR = new PrintWriter( STREAM );
            final MigraineFormPlayer PLAYER = (MigraineFormPlayer) this.getFormPlayer();

            new FormJSONSaver( PLAYER.getRepo(), PLAYER.getSteps() )
                    .saveToJSON( WR );
            WR.flush();
            WR.close();
            STREAM.close();
        } catch (IOException e) {
            final String ERR_MSG = this.getString( R.string.err_io );
            Log.e( this.getLogTag(), ERR_MSG );
            Toast.makeText( this, ERR_MSG, Toast.LENGTH_LONG ).show();
        }

        return TORET;
    }

    private void uploadToCloud(File fin)
    {
        final HandlerThread HANDLER_THREAD = new HandlerThread( "dropbox_backup" );
        final AppCompatActivity SELF = this;

        HANDLER_THREAD.start();

        final Handler HANDLER = new Handler( HANDLER_THREAD.getLooper() );
        HANDLER.post( () -> {
            try {
                drpbxClient.uploadFile( fin );
                SELF.runOnUiThread( () -> {
                    Log.i( this.getLogTag(), "Enquiry data uploaded" );
                });
            } catch(DbxException exc) {
                SELF.runOnUiThread( () -> {
                    Log.e( this.getLogTag(), "Error uploading: " + exc.getMessage() );
                });
            } finally {
                HANDLER.removeCallbacksAndMessages( null );
                HANDLER_THREAD.quit();
            }
        });

        return;
    }

    @Override
    public void showFormEnd()
    {
        // Prepare form end
        super.showFormEnd();

        // Details
        final LinearLayout LY_IMAGE = this.findViewById( R.id.lyImage );
        final TextView LBL_QUESTION = this.findViewById( R.id.lblQuestion );
        final MigraineFormPlayer PLAYER = (MigraineFormPlayer) this.getFormPlayer();
        final String FINAL_REPORT = PLAYER.getFinalReport();
        final StringBuilder END_TEXT = new StringBuilder();
        final ImageView BT_SHARE = this.buildButton( LY_IMAGE, android.R.drawable.ic_menu_share );

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

        this.uploadToCloud( this.saveJSON() );
        this.launchSpeak( LBL_QUESTION.getText().toString() );
    }


    public static Form migraineForm;
    public static MigraineFormPlayer player;
    public static MigraineFormPlayer.Settings playerSettings;
    public static DropboxUsrClient drpbxClient;
}
