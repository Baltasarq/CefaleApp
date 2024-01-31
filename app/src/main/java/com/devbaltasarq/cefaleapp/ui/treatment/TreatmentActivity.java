// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.treatment.Identifiable;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineGroup;
import com.devbaltasarq.cefaleapp.core.treatment.Morbidity;

import java.util.Collection;
import java.util.function.Function;


public class TreatmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_treatment );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_treatment );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.cephalea );
        }

        final LinearLayout LY_MORBIDITIES = this.findViewById( R.id.lyMorbidities );

        // Populate with info
        this.populateLayout( LY_MORBIDITIES, Morbidity.collectAll() );
    }

    private<T extends Identifiable> void populateLayout(
            final LinearLayout LY,
            final Collection<T> L_ID_OBJS)
    {
        LY.removeAllViews();

        for(Identifiable idObj: L_ID_OBJS) {
            this.buildEntry( LY, idObj );
        }
    }

    private void buildEntry(final LinearLayout LY, final Identifiable ID_OBJ)
    {
        final AlertDialog.Builder DLG_INFO = new AlertDialog.Builder( this );
        final TextView TV = new TextView( this );
        final View SEPARATOR = new View( this );
        String name = ID_OBJ.getId().getName();

        // Prepare name (separate different lines by separator)
        int posSeparator = -1;
        for(int i = 0; i < name.length(); ++i) {
            char ch = name.charAt( i );

            if ( ch != ' '
              && !Character.isLetter( ch ) )
            {
                posSeparator = i;
                break;
            }
        }

        if ( posSeparator > -1 ) {
            name = name.substring( 0, posSeparator ).trim()
                    + "\n" + name.substring( posSeparator + 1 ).trim();
        }

        // Prepare text view
        TV.setTextAppearance( android.R.style.TextAppearance_Large );
        TV.setText( name );
        TV.setOnClickListener( (v) -> DLG_INFO.create().show() );

        // Prepare separator
        SEPARATOR.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1 ) );
        SEPARATOR.setMinimumHeight( 1 );
        SEPARATOR.setBackgroundResource( android.R.color.darker_gray );

        // Prepare the dialog for the text view's info
        DLG_INFO.setTitle( name );
        DLG_INFO.setMessage( ID_OBJ.toString() );

        // Add to the layout
        LY.addView( TV );
        LY.addView( SEPARATOR );
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == android.R.id.home ) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }
}
