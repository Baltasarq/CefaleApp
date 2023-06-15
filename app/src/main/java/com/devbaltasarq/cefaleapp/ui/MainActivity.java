// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.AppInfo;
import com.devbaltasarq.cefaleapp.core.Form;
import com.devbaltasarq.cefaleapp.core.FormPlayer;
import com.devbaltasarq.cefaleapp.core.form.Option;
import com.devbaltasarq.cefaleapp.core.form.Question;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private final static String DATA_ASSET = "data.xml";
    private final static int BUTTON_ID_BASE = 999_999_000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        final LinearLayout LY_MAIN = this.findViewById( R.id.lyMain );

        try {
            this.form = Form.buildFromFile( this.getAssets().open( DATA_ASSET ) );
            this.player = new FormPlayer( this.form );
        } catch(IOException exc) {
            final TextView LBL_MESSAGE = new TextView( this );

            LBL_MESSAGE.setText( "i/o error: " + exc.getMessage() );
            LY_MAIN.addView( LBL_MESSAGE );
            Log.e( LOG_TAG, "error loading asset data: " + exc.getMessage() );
        }

        this.setTitle( AppInfo.FULL_NAME );
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if ( this.form != null ) {
            this.showCurrentQuestion();
        }
    }

    private void showFormEnd()
    {
        final FrameLayout FRM_END = this.findViewById( R.id.frmEnd );
        final LinearLayout LY_MAIN = this.findViewById( R.id.lyMain );
        final LinearLayout LY_TEXT = new LinearLayout( this );
        final LinearLayout LY_NEXT = new LinearLayout( this );

        // Reset views
        LY_MAIN.removeAllViews();
        FRM_END.removeAllViews();

        // Add info
        this.createText( LY_TEXT, "Final del cuestionario" );
        this.createText( LY_TEXT, this.player.getResult().getText() );
        this.createText( FRM_END, this.player.getResultsAsText() );

        // Margins and paddings
        LY_TEXT.setOrientation( LinearLayout.VERTICAL );
        LY_TEXT.setWeightSum( 80f );
        LY_TEXT.setPadding( 10, 10, 10, 10 );
        LY_TEXT.setGravity( View.TEXT_ALIGNMENT_GRAVITY );

        LY_NEXT.setPadding( 10, 10, 10, 10 );
        LY_NEXT.setGravity( View.TEXT_ALIGNMENT_GRAVITY );
        LY_NEXT.setOrientation( LinearLayout.VERTICAL );
        LY_NEXT.setWeightSum( 20f );

        // Next
        final ImageButton BUTTON_NEXT = this.createButton( LY_NEXT, R.drawable.reset );
        BUTTON_NEXT.setOnClickListener( (v) -> {
            final MainActivity SELF = MainActivity.this;
            SELF.player.reset();

            SELF.showCurrentQuestion();
        });

        LY_MAIN.addView( LY_TEXT );
        LY_MAIN.addView( LY_NEXT );
    }

    private void showCurrentQuestion()
    {
        final Question Q = this.player.getCurrentQuestion();
        final LinearLayout LY_MAIN = this.findViewById( R.id.lyMain );
        final FrameLayout FRM_END = this.findViewById( R.id.frmEnd );
        final LinearLayout LY_TEXT = new LinearLayout( this );
        final LinearLayout LY_OPTS = new LinearLayout( this );
        final LinearLayout LY_NEXT = new LinearLayout( this );
        final RadioGroup RADIO_GROUP = new RadioGroup( this );

        FRM_END.removeAllViews();
        LY_MAIN.removeAllViews();

        // Question's text
        this.createText( LY_TEXT, Q.getText() );

        // Image
        final ImageView IMAGE_VIEW = new ImageView( this );
        IMAGE_VIEW.setImageResource( R.drawable.man_woman );
        FRM_END.addView( IMAGE_VIEW );

        // Margins and paddings
        LY_TEXT.setOrientation( LinearLayout.VERTICAL );
        LY_TEXT.setWeightSum( 20f );
        LY_TEXT.setPadding( 10, 10, 10, 10 );
        LY_TEXT.setGravity( View.TEXT_ALIGNMENT_GRAVITY );
        LY_OPTS.setPadding( 10, 10, 10, 10 );
        LY_OPTS.setGravity( View.TEXT_ALIGNMENT_GRAVITY );
        LY_OPTS.setWeightSum( 60f );
        LY_OPTS.setOrientation( LinearLayout.VERTICAL );
        LY_NEXT.setPadding( 10, 10, 10, 10 );
        LY_NEXT.setGravity( View.TEXT_ALIGNMENT_GRAVITY );
        LY_NEXT.setOrientation( LinearLayout.VERTICAL );
        LY_NEXT.setWeightSum( 20f );

        // Options
        LY_OPTS.addView( RADIO_GROUP );
        final List<Option> OPTS = Q.getOptions();
        for(int i = 0; i < OPTS.size(); ++i) {
            final Option OPT = OPTS.get( i );

            this.createRadioButton(
                            BUTTON_ID_BASE + i, RADIO_GROUP, OPT.getText() );
        }

        // Next
        final ImageButton BUTTON_NEXT = this.createButton( LY_NEXT, R.drawable.right_arrow );
        BUTTON_NEXT.setOnClickListener( (v) -> {
            final MainActivity SELF = MainActivity.this;
            final int SELECTED_BUTTON_ID = RADIO_GROUP.getCheckedRadioButtonId();

            if ( SELECTED_BUTTON_ID >= 0
               || this.player.getCurrentQuestion().getNumOptions() == 0 )
            {
                if ( SELECTED_BUTTON_ID >= 0 ) {
                    final int OPT_ID = SELECTED_BUTTON_ID - BUTTON_ID_BASE;

                    this.player.setChosenOption( OPT_ID );
                }

                if ( this.player.isFinished() ) {
                    SELF.showFormEnd();
                } else {
                    SELF.showCurrentQuestion();
                }
            } else {
                Toast.makeText( this, R.string.msg_no_option_chosen, Toast.LENGTH_LONG ).show();
            }
        });

        LY_MAIN.addView( LY_TEXT );
        LY_MAIN.addView( LY_OPTS );
        LY_MAIN.addView( LY_NEXT );
    }

    private RadioButton createRadioButton(int id, final RadioGroup RADIO_GROUP, String text)
    {
        final RadioButton TORET = new RadioButton( this );

        TORET.setId( id );
        TORET.setPadding( 10, 10, 10, 10 );
        TORET.setGravity( View.TEXT_ALIGNMENT_GRAVITY );
        TextViewCompat.setTextAppearance( TORET, androidx.appcompat.R.style.TextAppearance_AppCompat_Large );
        TORET.setText( text );

        RADIO_GROUP.addView( TORET );
        return TORET;
    }

    private void createText(final ViewGroup CONTAINER, String text)
    {
        final TextView TORET = new TextView( this );

        TORET.setPadding( 10, 10, 10, 10 );
        TORET.setGravity( View.TEXT_ALIGNMENT_GRAVITY );
        TextViewCompat.setTextAppearance( TORET, androidx.appcompat.R.style.TextAppearance_AppCompat_Large );
        TORET.setText( text );

        CONTAINER.addView( TORET );
    }

    private ImageButton createButton(final ViewGroup CONTAINER, int resId)
    {
        final ImageButton TORET = new ImageButton( this );
        final TypedValue BCK = new TypedValue();
        int bckColor = Color.parseColor( "#ffffff" );

        // Get theme background
        this.getTheme().resolveAttribute( android.R.attr.windowBackground, BCK, true );
        bckColor = BCK.data;

        TORET.setBackgroundColor( bckColor );
        TORET.setContentDescription( "siguiente" );
        TORET.setImageResource( resId );
        TORET.setPadding( 10, 10, 10, 10 );
        CONTAINER.addView( TORET );
        return TORET;
    }

    private Form form;
    private FormPlayer player;
}
