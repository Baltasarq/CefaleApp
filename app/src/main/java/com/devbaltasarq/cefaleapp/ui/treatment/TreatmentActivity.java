// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

        final LinearLayout LY_MORBIDITIES = this.findViewById( R.id.lyMorbidities );
        final LinearLayout LY_MEDICINE_GROUPS = this.findViewById( R.id.lyMedicineGroups );
        final LinearLayout LY_MEDICINES = this.findViewById( R.id.lyMedicines );
        final ImageButton BT_MORBIDITIES = this.findViewById( R.id.btMorbidities );
        final ImageButton BT_MEDICINE_GROUPS = this.findViewById( R.id.btMedicineGroups );
        final ImageButton BT_MEDICINES = this.findViewById( R.id.btMedicines );

        // Button listeners
        final View.OnClickListener ON_TAB_CLICK =
                (View v) -> TreatmentActivity.this.changePage( (ImageButton) v );

        BT_MORBIDITIES.setOnClickListener( ON_TAB_CLICK );
        BT_MEDICINE_GROUPS.setOnClickListener( ON_TAB_CLICK );
        BT_MEDICINES.setOnClickListener( ON_TAB_CLICK );

        // Populate with info
        this.populateLayout( LY_MORBIDITIES, Morbidity.collectAll() );
        this.populateLayout( LY_MEDICINE_GROUPS, MedicineGroup.collectAll().values() );
        this.populateLayout( LY_MEDICINES, Medicine.collectAll().values() );

        // Set current page
        this.changePage( BT_MORBIDITIES );
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

    private void changePage(final ImageButton BUTTON)
    {
        final LinearLayout LY_MORBIDITIES = this.findViewById( R.id.lyMorbidities );
        final LinearLayout LY_MEDICINE_GROUPS = this.findViewById( R.id.lyMedicineGroups );
        final LinearLayout LY_MEDICINES = this.findViewById( R.id.lyMedicines );
        final ImageButton BT_MORBIDITIES = this.findViewById( R.id.btMorbidities );
        final ImageButton BT_MEDICINE_GROUPS = this.findViewById( R.id.btMedicineGroups );
        final ImageButton BT_MEDICINES = this.findViewById( R.id.btMedicines );
        final Function<ImageButton, Integer> FN_VISIBLE =
                (bt) -> bt == BUTTON ? View.VISIBLE : View.GONE;
        final Function<ImageButton, Integer> FN_ALPHA =
                (bt) -> bt == BUTTON ? 75 : 255;

        // Layouts
        LY_MORBIDITIES.setVisibility( FN_VISIBLE.apply( BT_MORBIDITIES ) );
        LY_MEDICINE_GROUPS.setVisibility( FN_VISIBLE.apply( BT_MEDICINE_GROUPS ) );
        LY_MEDICINES.setVisibility( FN_VISIBLE.apply( BT_MEDICINES ) );

        // Buttons' background
        BT_MORBIDITIES.getDrawable().mutate().setAlpha( FN_ALPHA.apply( BT_MORBIDITIES ) );
        BT_MEDICINE_GROUPS.getDrawable().mutate().setAlpha( FN_ALPHA.apply( BT_MEDICINE_GROUPS ) );
        BT_MEDICINES.getDrawable().mutate().setAlpha( FN_ALPHA.apply( BT_MEDICINES ) );

        // Buttons enabled
        BT_MORBIDITIES.setEnabled( BUTTON != BT_MORBIDITIES );
        BT_MEDICINE_GROUPS.setEnabled( BUTTON != BT_MEDICINE_GROUPS );
        BT_MEDICINES.setEnabled( BUTTON != BT_MEDICINES );
    }
}
