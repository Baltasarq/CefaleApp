// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.devbaltasarq.cefaleapp.core.FormJSONSaver;
import com.devbaltasarq.cefaleapp.core.FormPlayer;
import com.devbaltasarq.cefaleapp.core.Repo;
import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.form.Option;
import com.devbaltasarq.cefaleapp.core.form.Question;
import com.devbaltasarq.cefaleapp.core.form.Value;
import com.devbaltasarq.cefaleapp.core.form.ValueType;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;


public class EnquiryActivity extends AppCompatActivity {
    private final static String LOG_TAG = EnquiryActivity.class.getSimpleName();
    private final static int BUTTON_ID_BASE = 999_999_000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_enquiry );

        final ActionBar ACTION_BAR = this.getSupportActionBar();
        final ImageButton BT_TALK = this.findViewById( R.id.btTalk );

        BT_TALK.setOnClickListener( (v) -> this.onTalk() );

        this.setTitle( AppInfo.FULL_NAME );
        this.ttEngine = new TextToSpeech(
                            this.getApplicationContext(),
                            (status) -> {
                                if (status == TextToSpeech.SUCCESS ) {
                                    this.ttEngine.setLanguage( Locale.forLanguageTag( "es-ES" ) );
                                }
                            });

        if ( ACTION_BAR != null ) {
            ACTION_BAR.hide();
        }

        this.buildNextButton();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if ( player != null ) {
            this.showCurrentQuestion();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if ( this.ttEngine != null ) {
            this.ttEngine.stop();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if ( this.ttEngine != null ) {
            this.ttEngine.stop();
            this.ttEngine.shutdown();
        }
    }

    /** Triggered when the user asks for the phone to speak the question. */
    private void onTalk()
    {
        if ( !this.ttEngine.isSpeaking() ) {
            final TextView LBL_QUESTION = this.findViewById( R.id.lblQuestion );

            this.ttEngine.speak(
                    LBL_QUESTION.getText(),
                    TextToSpeech.QUEUE_FLUSH,
                    null, null );
        }

        return;
    }

    private void showFormEnd()
    {
        final FrameLayout FRM_END = this.findViewById( R.id.frmEnd );
        final LinearLayout LY_ANSWER = this.findViewById( R.id.lyAnswer );
        final LinearLayout LY_NEXT = this.findViewById( R.id.lyNext );
        final LinearLayout LY_IMAGE = this.findViewById( R.id.lyImage );
        final TextView LBL_QUESTION = this.findViewById( R.id.lblQuestion );
        final RadioGroup RG_OPTS = this.findViewById( R.id.rgOptions );
        final StringBuilder END_TEXT = new StringBuilder();

        // Prepare
        LY_IMAGE.removeAllViews();

        FRM_END.setVisibility( View.VISIBLE );
        LY_ANSWER.setVisibility( View.GONE );
        LY_IMAGE.setVisibility( View.VISIBLE );
        LY_NEXT.setVisibility( View.GONE );
        RG_OPTS.setVisibility( View.GONE );

        RG_OPTS.clearCheck();
        RG_OPTS.removeAllViews();

        // Set info
        END_TEXT.append( "Final del cuestionario\n\n" );
        END_TEXT.append( player.getFinalReport() );

        LBL_QUESTION.setText( END_TEXT.toString() );

        // Store JSON
        this.saveJSON();

        // Reset button
        final ImageButton BUTTON_RESET = this.buildButton( LY_IMAGE, R.drawable.reset );
        BUTTON_RESET.setOnClickListener( (v) -> {
            EnquiryActivity.this.finish();
        });
    }

    private void saveJSON()
    {
        final String FN = "data-" + Util.buildSerial() + ".json";

        try (FileOutputStream f = this.openFileOutput( FN, Context.MODE_PRIVATE ) ) {
            DataOutputStream STREAM = new DataOutputStream( f );
            final PrintWriter WR = new PrintWriter( STREAM );
            new FormJSONSaver( player ).saveToJSON( WR );
            WR.flush();
            WR.close();
            STREAM.close();
        } catch (IOException e) {
            final String ERR_MSG = this.getString( R.string.err_io );
            Log.e( LOG_TAG, ERR_MSG );
            Toast.makeText( this, ERR_MSG, Toast.LENGTH_LONG ).show();
        }

        return;
    }

    private void showCurrentQuestion()
    {
        final Question Q = this.getCurrentQuestion();
        final FrameLayout FRM_END = this.findViewById( R.id.frmEnd );
        final LinearLayout LY_NEXT = this.findViewById( R.id.lyNext );
        final TextView LBL_QUESTION = this.findViewById( R.id.lblQuestion );

        // Prepare
        FRM_END.setVisibility( View.GONE );
        LY_NEXT.setVisibility( View.VISIBLE );

        // Question attributes
        TextViewCompat.setTextAppearance( LBL_QUESTION, textSize );
        LBL_QUESTION.setText( Q.getText() );
        this.buildAnswerSupportFor( Q );
        this.buildImageFor( Q );
    }

    private void buildAnswerSupportFor(final Question Q)
    {
        final ValueType TYPE = Q.getValueType();

        if ( TYPE == ValueType.BOOL ) {
            this.buildRadioGroup( Q );
        }
        else
        if ( TYPE == ValueType.STR ) {
            this.buildTextArea();
        }
        else
        if ( TYPE == ValueType.INT ) {
            this.buildNumInput();
        } else {
            throw new Error( "no answer support for type: " +  TYPE );
        }

        return;
    }

    private void buildRadioGroup(final Question Q)
    {
        final RadioGroup RADIO_GROUP = this.findViewById( R.id.rgOptions );
        final EditText TEXT_AREA = this.findViewById( R.id.edTextArea );
        final EditText NUM_INPUT = this.findViewById( R.id.edInputNum );

        // Prepare visibility
        RADIO_GROUP.setVisibility( View.VISIBLE );
        TEXT_AREA.setVisibility( View.GONE );
        NUM_INPUT.setVisibility( View.GONE );

        // Load radio group with options
        RADIO_GROUP.removeAllViews();
        RADIO_GROUP.clearCheck();

        final List<Option> OPTS = Q.getOptions();
        for(int i = 0; i < OPTS.size(); ++i) {
            final Option OPT = OPTS.get( i );

            this.buildRadioButton(
                    BUTTON_ID_BASE + i, RADIO_GROUP, OPT.getText() );
        }

        this.hideKeyboard( RADIO_GROUP );
    }

    private void hideKeyboard(final View VIEW)
    {
        final InputMethodManager IME = (InputMethodManager)
                this.getSystemService( Activity.INPUT_METHOD_SERVICE );

        IME.hideSoftInputFromWindow( VIEW.getWindowToken(), 0 );
    }

    private void buildRadioButton(int id, final RadioGroup RADIO_GROUP, String text)
    {
        final RadioButton TORET = new RadioButton( this );

        TORET.setId( id );
        TORET.setPadding( 10, 10, 10, 10 );
        TORET.setGravity( View.TEXT_ALIGNMENT_GRAVITY );
        TextViewCompat.setTextAppearance( TORET, textSize );
        TORET.setText( text );

        RADIO_GROUP.addView( TORET );
    }

    private void buildImageFor(final Question Q)
    {
        final LinearLayout LY_IMAGE = this.findViewById( R.id.lyImage );

        if ( !Q.getPic().isEmpty() ) {
            LY_IMAGE.setVisibility( View.VISIBLE );
            LY_IMAGE.setPadding( 10, 10, 10, 10 );
            LY_IMAGE.setLayoutParams(
                    new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f )
            );

            final ImageView IMAGE_VIEW = new ImageView( this );
            final String PIC_ID = Q.getPic();
            @SuppressLint("DiscouragedApi") final Drawable PIC = ResourcesCompat.getDrawable(
                                                    this.getResources(),
                                                    this.getResources().getIdentifier(
                                                                            PIC_ID,
                                                                            "drawable",
                                                                            this.getPackageName() ),
                                                    null );
            IMAGE_VIEW.setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f ) );
            IMAGE_VIEW.setImageDrawable( PIC );
            LY_IMAGE.addView( IMAGE_VIEW );
        } else {
            LY_IMAGE.setVisibility( View.GONE );
        }

        return;
    }

    private void buildNumInput()
    {
        final RadioGroup RADIO_GROUP = this.findViewById( R.id.rgOptions );
        final EditText TEXT_AREA = this.findViewById( R.id.edTextArea );
        final EditText NUM_INPUT = this.findViewById( R.id.edInputNum );

        NUM_INPUT.getText().clear();

        NUM_INPUT.setVisibility( View.VISIBLE );
        TEXT_AREA.setVisibility( View.GONE );
        RADIO_GROUP.setVisibility( View.GONE );

        NUM_INPUT.requestFocus();
    }

    private void buildTextArea()
    {
        final RadioGroup RADIO_GROUP = this.findViewById( R.id.rgOptions );
        final EditText TEXT_AREA = this.findViewById( R.id.edTextArea );
        final EditText NUM_INPUT = this.findViewById( R.id.edInputNum );

        TEXT_AREA.getText().clear();
        TextViewCompat.setTextAppearance( TEXT_AREA, textSize );

        NUM_INPUT.setVisibility( View.GONE );
        TEXT_AREA.setVisibility( View.VISIBLE );
        RADIO_GROUP.setVisibility( View.GONE );

        TEXT_AREA.requestFocus();
    }

    private void buildNextButton()
    {
        final ImageButton BUTTON_NEXT = this.findViewById( R.id.btNext );

        BUTTON_NEXT.setOnClickListener( (v) -> {
            final EnquiryActivity SELF = EnquiryActivity.this;

            if ( SELF.fetchAnswerFor( this.getCurrentQuestion() ) ) {
                if ( player.gotoNextQuestion() ) {
                    SELF.showCurrentQuestion();
                } else {
                    SELF.showFormEnd();
                }
            } else {
                Toast.makeText( this, R.string.msg_no_option_chosen, Toast.LENGTH_LONG ).show();
            }
        });
    }

    private ImageButton buildButton(final ViewGroup CONTAINER, int resId)
    {
        final ImageButton TORET = new ImageButton( this );
        final TypedValue BCK = new TypedValue();
        int bckColor = Color.parseColor( "#ffffff" );

        // Get theme background
        this.getTheme().resolveAttribute( android.R.attr.windowBackground, BCK, true );
        bckColor = BCK.data;

        TORET.setBackgroundColor( bckColor );
        TORET.setContentDescription( this.getString( R.string.lbl_next ) );
        TORET.setImageResource( resId );
        TORET.setPadding( 10, 10, 10, 10 );
        CONTAINER.addView( TORET );
        return TORET;
    }

    private boolean fetchAnswerFor(final Question Q)
    {
        final ValueType TYPE = Q.getValueType();
        final Repo.Id ID = Repo.Id.parse( Q.getDataFromId() );
        Value value = null;

        if ( TYPE == ValueType.BOOL ) {
            value = this.fetchBoolAnswer( Q );
        }
        else
        if ( TYPE == ValueType.INT ) {
            value = this.fetchIntAnswer();
        }
        else
        if ( TYPE == ValueType.STR ) {
            value = this.fetchStrAnswer();
        }

        boolean toret = ( value != null );

        if ( toret ) {
            // Store the answer
            player.getRepo().setValue( ID, value );
        }

        return toret;
    }

    private Value fetchBoolAnswer(final Question Q)
    {
        final RadioGroup RADIO_GROUP = this.findViewById( R.id.rgOptions );
        final int SELECTED_BUTTON_ID = RADIO_GROUP.getCheckedRadioButtonId();
        Value toret = null;

        if ( SELECTED_BUTTON_ID >= 0
          || player.getCurrentQuestion().getNumOptions() == 0 )
        {
            final Option OPT = Q.getOption( SELECTED_BUTTON_ID - BUTTON_ID_BASE );

            toret = new Value( OPT.getValue(), ValueType.BOOL );
        }

        return toret;
    }

    private Value fetchStrAnswer()
    {
        final EditText TEXT_AREA = this.findViewById( R.id.edTextArea );
        final String STR = TEXT_AREA.getText().toString();
        Value toret = null;

        if ( !STR.isEmpty() ) {
            toret = new Value( STR, ValueType.STR );
        }

        return toret;
    }

    private Value fetchIntAnswer()
    {
        final EditText NUM_INPUT = this.findViewById( R.id.edInputNum );
        final String STR = NUM_INPUT.getText().toString();
        Value toret = null;

        if ( !STR.isEmpty() ) {
            toret = new Value( STR, ValueType.INT );
        }

        return toret;
    }

    private Question getCurrentQuestion()
    {
        final String MSG_ERROR = this.getString( R.string.err_unexpected );
        Question toret = null;

        // Chk player
        if ( player == null ) {
            String txtError = MSG_ERROR + ": no player (??)";

            Toast.makeText( this,
                                MSG_ERROR,
                                Toast.LENGTH_LONG ).show();
            Log.e( LOG_TAG, txtError  );
            throw new Error( txtError );
        } else {
            // Chk question
            toret = player.getCurrentQuestion();

            if ( toret == null ) {
                String txtError = MSG_ERROR + ": no current question (??)";

                Toast.makeText( this,
                        R.string.err_unexpected,
                        Toast.LENGTH_LONG ).show();
                Log.e( LOG_TAG, txtError );
                throw new Error( txtError );
            }
        }

        return toret;
    }

    static void setTextAppeareanceFromTextSize(MainActivity.TextSize ts)
    {
        textSize = androidx.appcompat.R.style.TextAppearance_AppCompat_Small;

        if ( ts == MainActivity.TextSize.MEDIUM ) {
            textSize = androidx.appcompat.R.style.TextAppearance_AppCompat_Medium;
        }
        else
        if ( ts == MainActivity.TextSize.LARGE ) {
            textSize = androidx.appcompat.R.style.TextAppearance_AppCompat_Large;
        }

        return;
    }

    static Form form;
    static FormPlayer player;
    private TextToSpeech ttEngine;
    static int textSize;
    static boolean showNotesQuestion;
}
