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
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.Language;
import com.devbaltasarq.cefaleapp.core.faq.Faq;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


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
        final Language LANG = Language.langFromDefaultLocale();
        final LinearLayout LY_FAQ = this.findViewById( R.id.lyFAQContainer );
        final List<Faq> FAQ_ENTRIES= new ArrayList<>( Faq.getAll().values() );
        final LayoutInflater INFLATER = this.getLayoutInflater();

        FAQ_ENTRIES.sort( Comparator.comparing( Faq::getId ) );

        for(final Faq FAQ: FAQ_ENTRIES) {
            final LinearLayout FAQ_VIEW = (LinearLayout) INFLATER
                                        .inflate( R.layout.faq_entry, null );
            final TextView LBL_QUESTION = FAQ_VIEW.findViewById( R.id.lblFaqQuestion );
            final TextView LBL_ANSWER = FAQ_VIEW.findViewById( R.id.lblFaqAnswer );

            LBL_QUESTION.setText( FAQ.getQuestion().get( LANG ) );
            LBL_ANSWER.setText( FAQ.getAnswer().get( LANG ) );
            LY_FAQ.addView( FAQ_VIEW );

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
}
