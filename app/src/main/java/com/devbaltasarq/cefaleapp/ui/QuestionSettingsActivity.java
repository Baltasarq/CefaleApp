package com.devbaltasarq.cefaleapp.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.devbaltasarq.cefaleapp.R;


public class QuestionSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.question_settings_activity );

        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final CheckBox CHK_SHOW_NOTES = this.findViewById( R.id.chkShowNotesQuestion );

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_question_settings );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.cephalea );
        }

        this.setCheckedQuestions();

        CHK_SHOW_NOTES.setOnCheckedChangeListener( (bt, status) -> {
            EnquiryActivity.playerSettings.showNotesQuestion = status;
        });
    }

    private void setCheckedQuestions()
    {
        final CheckBox CHK_SHOW_NOTES = this.findViewById( R.id.chkShowNotesQuestion );

        CHK_SHOW_NOTES.setChecked( EnquiryActivity.playerSettings.showNotesQuestion );
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }
}