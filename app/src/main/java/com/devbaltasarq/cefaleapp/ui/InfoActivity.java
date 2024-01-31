// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;


public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_info );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_info );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medicine );
        }

        this.showFAQ();
    }

    private void showFAQ()
    {
        final LinearLayout LY_FAQ = this.findViewById( R.id.lyFAQContainer );
        final String[] FAQ_QUESTIONS = this.getResources().getStringArray( R.array.txt_faq_qs );
        final String[] FAQ_ANSWERS = this.getResources().getStringArray( R.array.txt_faq_as );
        final LayoutInflater INFLATER = this.getLayoutInflater();

        for(int i = 0; i < FAQ_QUESTIONS.length; ++i) {
            final LinearLayout FAQ_ENTRY = (LinearLayout) INFLATER
                                        .inflate( R.layout.faq_entry, null );
            final TextView LBL_QUESTION = FAQ_ENTRY.findViewById( R.id.lblFaqQuestion );
            final TextView LBL_ANSWER = FAQ_ENTRY.findViewById( R.id.lblFaqAnswer );

            LBL_QUESTION.setText( FAQ_QUESTIONS[ i ] );
            LBL_ANSWER.setText( FAQ_ANSWERS[ i ] );
            LY_FAQ.addView( FAQ_ENTRY );

            LBL_QUESTION.setOnClickListener( (v) -> {
                if ( LBL_ANSWER.getVisibility() == View.GONE ) {
                    LBL_ANSWER.setVisibility( View.VISIBLE );
                    LBL_QUESTION.setCompoundDrawablesWithIntrinsicBounds(
                            AppCompatResources.getDrawable( this,
                                    android.R.drawable.arrow_up_float ),
                            null,
                            null,
                            null );
                } else {
                    LBL_ANSWER.setVisibility( View.GONE );
                    LBL_QUESTION.setCompoundDrawablesWithIntrinsicBounds(
                            AppCompatResources.getDrawable( this,
                                    android.R.drawable.arrow_down_float ),
                            null,
                            null,
                            null );
                }
            });
        }

        return;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    private static SimpleExpandableListAdapter adapter = null;
}
