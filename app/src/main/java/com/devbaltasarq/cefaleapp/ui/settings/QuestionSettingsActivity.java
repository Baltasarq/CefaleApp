// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.ui.settings;


import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.ui.tests.MigraineTestActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class QuestionSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_question_settings );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_question_settings );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.cephalea );
        }

        // Relationships between checkboxes and configuration
        this.buildChkMappings();

        // Apply the current configuration to the view
        this.setCheckedQuestions();

        // For each view, set the same listener
        for(final View V: this.mapViewRdWt.keySet()) {
            final CheckBox CHK = (CheckBox) V;

            CHK.setOnCheckedChangeListener( this::onChk );
        }

        return;
    }

    private void buildChkMappings()
    {
        final CheckBox CHK_SHOW_NOTES = this.findViewById( R.id.chkShowNotesQuestion );
        final CheckBox CHK_SHOW_AGE = this.findViewById( R.id.chkShowAgeQuestion );
        final CheckBox CHK_SHOW_SIZE = this.findViewById( R.id.chkShowSizeQuestions );
        final CheckBox CHK_SHOW_PRESSURE = this.findViewById( R.id.chkShowPressureQuestions );

        this.mapViewRdWt = new HashMap<>( 5 );

        // Show notes question
        this.mapViewRdWt.put( CHK_SHOW_NOTES,
                new Pair<>( () ->  MigraineTestActivity.playerSettings.showNotesQuestion,
                            status -> MigraineTestActivity.playerSettings.showNotesQuestion = status ) );

        // Show age question
        this.mapViewRdWt.put( CHK_SHOW_AGE,
                new Pair<>( () ->  MigraineTestActivity.playerSettings.showAgeQuestion,
                            status -> MigraineTestActivity.playerSettings.showAgeQuestion = status ) );

        // Show size questions
        this.mapViewRdWt.put( CHK_SHOW_SIZE,
                new Pair<>( () ->  MigraineTestActivity.playerSettings.showSizeQuestions,
                            status -> MigraineTestActivity.playerSettings.showSizeQuestions = status ) );

        // Show pressure questions
        this.mapViewRdWt.put( CHK_SHOW_PRESSURE,
                new Pair<>( () ->  MigraineTestActivity.playerSettings.showPressureQuestions,
                        status -> MigraineTestActivity.playerSettings.showPressureQuestions = status ) );
    }

    private void onChk(Button b, boolean status)
    {
        Objects.requireNonNull( this.mapViewRdWt.get( b ) ).second.accept( status );
    }

    private void setCheckedQuestions()
    {
        for(final Map.Entry<Button, Pair<Supplier<Boolean>, Consumer<Boolean>>> ENTRY:
                this.mapViewRdWt.entrySet())
        {
            final CheckBox CHK = (CheckBox) ENTRY.getKey();

            CHK.setChecked( ENTRY.getValue().first.get() );
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    private Map<Button, Pair<Supplier<Boolean>, Consumer<Boolean>>> mapViewRdWt = null;
}
