package com.devbaltasarq.cefaleapp.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.ui.MainActivity;

import java.util.List;


public class TextSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_text_settings);

        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final RadioGroup RG_TEXT_SIZE = this.findViewById( R.id.rgTextSizeOptions );

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_text_settings );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.cephalea );
        }

        this.setActiveTextSizeButton();

        RG_TEXT_SIZE.setOnCheckedChangeListener( (rg, i) -> {
            Integer[] vids = new Integer[]{ R.id.btTextSmall, R.id.btTextMedium, R.id.btTextLarge };
            int pos = List.of( vids ).indexOf( i );

            MainActivity.textSize = MainActivity.TextSize.values()[ pos ];
        });
    }

    private void setActiveTextSizeButton()
    {
        final RadioGroup RG_TEXT_SIZE = this.findViewById( R.id.rgTextSizeOptions );

        RG_TEXT_SIZE.clearCheck();

        if ( MainActivity.textSize == MainActivity.TextSize.SMALL ) {
            RG_TEXT_SIZE.check( R.id.btTextSmall );
        }
        else
        if ( MainActivity.textSize == MainActivity.TextSize.MEDIUM ) {
            RG_TEXT_SIZE.check( R.id.btTextMedium );
        }
        else
        if ( MainActivity.textSize == MainActivity.TextSize.LARGE ) {
            RG_TEXT_SIZE.check( R.id.btTextLarge );
        }

        return;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }
}
