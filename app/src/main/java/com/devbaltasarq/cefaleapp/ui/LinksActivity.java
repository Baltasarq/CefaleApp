// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.Language;
import com.devbaltasarq.cefaleapp.core.links.WebUrl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class LinksActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_links );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_links );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.ic_internet );
        }

        this.showLinks();
    }

    private void showLinks()
    {
        final LinearLayout LY_ENTRIES = this.findViewById( R.id.lyLinkEntries );
        final LayoutInflater INFLATER = this.getLayoutInflater();
        final Language LANG = Language.langFromDefaultLocale();
        final List<WebUrl> LINKS = new ArrayList<>( WebUrl.getAll( LANG ).values() );

        LINKS.sort( Comparator.comparing( WebUrl::getSource ) );

        for(final WebUrl LINK: LINKS) {
            final View ENTRY_VIEW = INFLATER.inflate( R.layout.link_entry, null );
            final ImageView BT_LINKS = ENTRY_VIEW.findViewById( R.id.btGoLink );
            final TextView LBL_SRC = ENTRY_VIEW.findViewById( R.id.lblLinkSrc );
            final TextView LBL_DESC = ENTRY_VIEW.findViewById( R.id.lblLinkDesc );

            LBL_SRC.setText( LINK.getSource() );
            LBL_DESC.setText( LINK.getDesc() );

            BT_LINKS.setOnClickListener( v -> this.go( LINK.getUrl() ) );
            LY_ENTRIES.addView( ENTRY_VIEW );
        }

        return;
    }

    private void go(String url)
    {
        final Intent INTENT = new Intent( Intent.ACTION_VIEW );

        INTENT.setData( Uri.parse( url ) );
        this.startActivity( INTENT );
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