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

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.treatment.Identifiable;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.core.treatment.MedicineClass;
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

        final MedicineClass CLS_PREVENTIVE = MedicineClass.getAll().get( MedicineClass.Id.get( "PRVNT" ) );
        final MedicineClass CLS_SYMPTOMATIC = MedicineClass.getAll().get( MedicineClass.Id.get( "ANALG" ) );

        final TextView LBL_CLS_PREVENTIVE = this.findViewById( R.id.lblClsPreventive );
        final TextView LBL_CLS_SYMPTOMATIC = this.findViewById( R.id.lblClsSymptomatic );
        final LinearLayout LY_PREVENTIVE_GROUPS = this.findViewById( R.id.lyPreventiveGroups );
        final LinearLayout LY_SYMPTOMATIC_GROUPS = this.findViewById( R.id.lySymptomaticGroups );
        final LinearLayout LY_MEDICINES = this.findViewById( R.id.lyMedicines );
        final ImageButton BT_MEDICINE_GROUPS = this.findViewById( R.id.btMedicineGroups );
        final ImageButton BT_MEDICINES = this.findViewById( R.id.btMedicines );

        LBL_CLS_PREVENTIVE.setText( R.string.lbl_preventive_treatment );
        LBL_CLS_SYMPTOMATIC.setText( R.string.lbl_symptomatic_treatment);

        // Button listeners
        final View.OnClickListener ON_TAB_CLICK =
                (View v) -> VademecumActivity.this.changePage( (ImageButton) v );

        BT_MEDICINE_GROUPS.setOnClickListener( ON_TAB_CLICK );
        BT_MEDICINES.setOnClickListener( ON_TAB_CLICK );

        LBL_CLS_PREVENTIVE.setOnClickListener(
                                v -> switchShowClass(
                                            LBL_CLS_PREVENTIVE, LY_PREVENTIVE_GROUPS ) );
        LBL_CLS_SYMPTOMATIC.setOnClickListener(
                                v -> switchShowClass(
                                            LBL_CLS_SYMPTOMATIC, LY_SYMPTOMATIC_GROUPS ) );

        // Populate with info
        final List<MedicineGroup> PREV_GROUPS = CLS_PREVENTIVE.getGroups();
        final List<MedicineGroup> SYMP_GROUPS = CLS_SYMPTOMATIC.getGroups();
        this.populateLayout( LY_PREVENTIVE_GROUPS, PREV_GROUPS );
        this.populateLayout( LY_SYMPTOMATIC_GROUPS, SYMP_GROUPS );
        this.populateLayout( LY_MEDICINES, Medicine.getAll().values() );

        // Set current page
        this.changePage( BT_MEDICINE_GROUPS );
    }

    private void switchShowClass(
                    final TextView LBL_MED_CLS,
                    final LinearLayout LY_MED_CLS)
    {
        int contraryVisibility = View.VISIBLE;

        LBL_MED_CLS.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.arrow_up_float,
                0, 0, 0 );

        if ( LY_MED_CLS.getVisibility() == View.VISIBLE ) {
            contraryVisibility = View.GONE;
            LBL_MED_CLS.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.arrow_down_float,
                    0, 0, 0 );
        }

        LY_MED_CLS.setVisibility( contraryVisibility );
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

        final LayoutInflater INFLATER = this.getLayoutInflater();
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
                            android.R.drawable.arrow_up_float,
                            0, 0, 0 );

            if ( LY_SUB_ENTRIES.getChildCount() == 0 ) {
                for(final Medicine MEDICINE: MEDICINES) {
                    final View SUB_ENTRY_VIEW = INFLATER.inflate( R.layout.medicine_entry, null );
                    final TextView SUB_TV = SUB_ENTRY_VIEW.findViewById( R.id.lblMedicine );

                    SUB_TV.setText( MEDICINE.getId().getName() );
                    LY_SUB_ENTRIES.addView( SUB_ENTRY_VIEW );
                    SUB_TV.setOnClickListener( (v) -> this.showMedicine( MEDICINE ) );
                }
            }
        } else {
            LY_SUB_ENTRIES.setVisibility( View.GONE );
            TV.setCompoundDrawablesWithIntrinsicBounds(
                            android.R.drawable.arrow_down_float ,
                            0, 0, 0 );
        }

        return;
    }

    private void changePage(final ImageButton BUTTON)
    {
        final LinearLayout LY_CLASSES = this.findViewById( R.id.lyClasses );
        final LinearLayout LY_MEDICINES = this.findViewById( R.id.lyMedicines );
        final ImageButton BT_MEDICINE_GROUPS = this.findViewById( R.id.btMedicineGroups );
        final ImageButton BT_MEDICINES = this.findViewById( R.id.btMedicines );
        final Function<ImageButton, Integer> FN_VISIBLE =
                (bt) -> bt == BUTTON ? View.VISIBLE : View.GONE;
        final Function<ImageButton, Integer> FN_ALPHA =
                (bt) -> bt == BUTTON ? 75 : 255;

        // Layouts
        LY_CLASSES.setVisibility( FN_VISIBLE.apply( BT_MEDICINE_GROUPS ) );
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
