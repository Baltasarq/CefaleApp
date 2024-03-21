// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.treatment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.treatment.Identifiable;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;


public class VademecumActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_vademecum );

        final ActionBar ACTION_BAR = this.getSupportActionBar();

        if ( ACTION_BAR != null ) {
            ACTION_BAR.setTitle( R.string.lbl_vademecum );
            ACTION_BAR.setDisplayHomeAsUpEnabled( true );
            ACTION_BAR.setLogo( R.drawable.medicine );
        }

        final LinearLayout LY_MEDICINE_GROUPS = this.findViewById( R.id.lyMedicineGroups );
        final LinearLayout LY_MEDICINES = this.findViewById( R.id.lyMedicines );
        final ImageButton BT_MEDICINE_GROUPS = this.findViewById( R.id.btMedicineGroups );
        final ImageButton BT_MEDICINES = this.findViewById( R.id.btMedicines );

        // Button listeners
        final View.OnClickListener ON_TAB_CLICK =
                (View v) -> VademecumActivity.this.changePage( (ImageButton) v );

        BT_MEDICINE_GROUPS.setOnClickListener( ON_TAB_CLICK );
        BT_MEDICINES.setOnClickListener( ON_TAB_CLICK );

        // Populate with info
        this.populateLayout( LY_MEDICINE_GROUPS, MedicineGroup.getAll().values() );
        this.populateLayout( LY_MEDICINES, Medicine.getAll().values() );

        // Set current page
        this.changePage( BT_MEDICINE_GROUPS );
    }

    private<T extends Identifiable> void populateLayout(
            final LinearLayout LY,
            final Collection<T> C_ID_OBJS)
    {
        final List<T> L_OBJS = new ArrayList<>( C_ID_OBJS );

        L_OBJS.sort( Comparator.comparing( e -> e.getId().getName() ));
        LY.removeAllViews();

        for(Identifiable idObj: L_OBJS) {
            this.buildEntry( LY, idObj );
        }
    }

    private void buildEntry(final LinearLayout LY, final Identifiable ID_OBJ)
    {
        final LayoutInflater INFLATER = this.getLayoutInflater();
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

        final View ENTRY_VIEW = INFLATER.inflate(
                                        R.layout.vademecum_entry,
                                    null );
        final TextView TV = ENTRY_VIEW.findViewById( R.id.lblEntry );

        if ( ID_OBJ instanceof Medicine ) {
            TV.setCompoundDrawables( null, null, null, null );
        } else {
            TV.setTextColor( Color.parseColor( "#000000" ) );
        }

        // Add to the general layout
        TV.setText( name );
        ENTRY_VIEW.setOnClickListener( (v) -> this.show( ENTRY_VIEW, ID_OBJ ) );
        LY.addView( ENTRY_VIEW );
    }

    private void show(final View ENTRY_VIEW, final Identifiable ID_OBJ)
    {
        if ( ID_OBJ instanceof MedicineGroup MEDICINE_GROUP ) {
            this.showMedicineGroup( ENTRY_VIEW, MEDICINE_GROUP );
        }
        else
        if ( ID_OBJ instanceof Medicine MEDICINE ) {
            this.showMedicine( MEDICINE );
        } else {
            throw new Error( "VademecumAct.show(): no type recognized" );
        }

        return;
    }

    private void showMedicine(final Medicine MEDICINE)
    {
        final Intent INTENT = new Intent( this, MedicineActivity.class );

        MedicineActivity.medicine = MEDICINE;
        this.startActivity( INTENT );
    }

    private void showMedicineGroup(final View ENTRY_VIEW, final MedicineGroup GROUP)
    {
        final TextView TV = ENTRY_VIEW.findViewById( R.id.lblEntry );
        final LinearLayout LY_SUB_ENTRIES = ENTRY_VIEW.findViewById( R.id.lySubEntries );
        final LayoutInflater INFLATER = this.getLayoutInflater();
        final List<Medicine> MEDICINES = GROUP.getMedicines();

        if ( LY_SUB_ENTRIES.getVisibility() != View.VISIBLE ) {
            LY_SUB_ENTRIES.setVisibility( View.VISIBLE );
            TV.setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable( this,
                            android.R.drawable.arrow_up_float ),
                    null, null, null );

            if ( LY_SUB_ENTRIES.getChildCount() == 0 ) {
                for(final Medicine MEDICINE: MEDICINES) {
                    final View SUB_ENTRY_VIEW = INFLATER.inflate( R.layout.medicine_group_entry, null );
                    final TextView SUB_TV = SUB_ENTRY_VIEW.findViewById( R.id.lblMedicine );

                    SUB_TV.setText( MEDICINE.getId().getName() );
                    LY_SUB_ENTRIES.addView( SUB_ENTRY_VIEW );
                    SUB_TV.setOnClickListener( (v) -> this.showMedicine( MEDICINE ) );
                }
            }
        } else {
            LY_SUB_ENTRIES.setVisibility( View.GONE );
            TV.setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable( this,
                            android.R.drawable.arrow_down_float ),
                    null, null, null );
        }

        return;
    }

    private void changePage(final ImageButton BUTTON)
    {
        final LinearLayout LY_MEDICINE_GROUPS = this.findViewById( R.id.lyMedicineGroups );
        final LinearLayout LY_MEDICINES = this.findViewById( R.id.lyMedicines );
        final ImageButton BT_MEDICINE_GROUPS = this.findViewById( R.id.btMedicineGroups );
        final ImageButton BT_MEDICINES = this.findViewById( R.id.btMedicines );
        final Function<ImageButton, Integer> FN_VISIBLE =
                (bt) -> bt == BUTTON ? View.VISIBLE : View.GONE;
        final Function<ImageButton, Integer> FN_ALPHA =
                (bt) -> bt == BUTTON ? 75 : 255;

        // Layouts
        LY_MEDICINE_GROUPS.setVisibility( FN_VISIBLE.apply( BT_MEDICINE_GROUPS ) );
        LY_MEDICINES.setVisibility( FN_VISIBLE.apply( BT_MEDICINES ) );

        // Buttons' background
        BT_MEDICINE_GROUPS.getDrawable().mutate().setAlpha( FN_ALPHA.apply( BT_MEDICINE_GROUPS ) );
        BT_MEDICINES.getDrawable().mutate().setAlpha( FN_ALPHA.apply( BT_MEDICINES ) );

        // Buttons enabled
        BT_MEDICINE_GROUPS.setEnabled( BUTTON != BT_MEDICINE_GROUPS );
        BT_MEDICINES.setEnabled( BUTTON != BT_MEDICINES );
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
