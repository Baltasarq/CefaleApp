// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui.tests;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.questionnaire.Form;
import com.devbaltasarq.cefaleapp.core.questionnaire.FormPlayer;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Option;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Question;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.ValueType;
import com.devbaltasarq.cefaleapp.ui.MainActivity;

import java.util.List;
import java.util.Locale;


public abstract class TestActivity extends AppCompatActivity {
    private final static int BUTTON_ID_BASE = 999_999_000;

    @Override
    public void onResume()
    {
        super.onResume();

        if ( this.isShowingEnd() ) {
            this.showFormEnd();
        }
        else
        if ( this.getFormPlayer() != null ) {
            this.showCurrentQuestion();
        }

        return;
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if ( this.ttEngine != null ) {
            this.ttEngine.stop();
        }

        return;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if ( this.ttEngine != null ) {
            this.ttEngine.stop();
            this.ttEngine.shutdown();
        }

        return;
    }

    public abstract FormPlayer getFormPlayer();
    public abstract Form getForm();
    public abstract String getLogTag();

    protected void init()
    {
        this.initProgress();;
        this.initNextButton();
        this.initTalk();

        this.showingEnd = false;
    }

    protected void initNextButton()
    {
        final ImageButton BUTTON_NEXT = this.findViewById( R.id.btNext );

        BUTTON_NEXT.setOnClickListener( (v) -> {
            final TestActivity SELF = TestActivity.this;

            if ( SELF.fetchAnswer() ) {
                if ( this.getFormPlayer().findNextQuestion() ) {
                    SELF.showCurrentQuestion();
                } else {
                    SELF.showFormEnd();
                }
            } else {
                Toast.makeText( this, R.string.msg_no_option_chosen, Toast.LENGTH_LONG ).show();
            }
        });
    }

    protected void initProgress()
    {
        final ProgressBar PROGRESS = this.findViewById( R.id.pbTestProgress);

        PROGRESS.setMin( 1 );
        PROGRESS.setMax( this.getForm().calcNumQuestions() );
    }

    protected void initTalk()
    {
        final ImageButton BT_TALK = this.findViewById( R.id.btTalk );

        // Speech engine
        BT_TALK.setOnClickListener( (v) -> this.onTalk() );

        this.ttEngine = new TextToSpeech(
            this.getApplicationContext(),
            (status) -> {
                if (status == TextToSpeech.SUCCESS ) {
                    this.ttEngine.setLanguage( Locale.forLanguageTag( "es-ES" ) );
                }
            });

        return;
    }

    protected boolean isShowingEnd()
    {
        return this.showingEnd;
    }

    protected void setShowingEnd()
    {
        this.setShowingEnd( true );
    }

    protected void setShowingEnd(boolean val)
    {
        this.showingEnd = val;
    }

    protected void showFormEnd()
    {
        final FrameLayout FRM_END = this.findViewById( R.id.frmEnd );
        final LinearLayout LY_ANSWER = this.findViewById( R.id.lyAnswer );
        final LinearLayout LY_NEXT = this.findViewById( R.id.lyNext );
        final LinearLayout LY_IMAGE = this.findViewById( R.id.lyImageTest);
        final RadioGroup RG_OPTS = this.findViewById( R.id.rgOptions );

        // Prepare
        LY_IMAGE.removeAllViews();

        FRM_END.setVisibility( View.VISIBLE );
        LY_ANSWER.setVisibility( View.GONE );
        LY_IMAGE.setVisibility( View.VISIBLE );
        LY_NEXT.setVisibility( View.GONE );
        RG_OPTS.setVisibility( View.GONE );

        RG_OPTS.clearCheck();
        RG_OPTS.removeAllViews();

        // Reset button
        final ImageButton BUTTON_RESET = this.buildButton( LY_IMAGE, R.drawable.reset );

        BUTTON_RESET.setOnClickListener( (v) -> {
            TestActivity.this.finish();
        });

        this.setShowingEnd();
    }

    protected void showCurrentQuestion()
    {
        final Question Q = this.getCurrentQuestion();
        final FrameLayout FRM_END = this.findViewById( R.id.frmEnd );
        final LinearLayout LY_NEXT = this.findViewById( R.id.lyNext );
        final TextView LBL_QUESTION = this.findViewById( R.id.lblQuestion );
        final ProgressBar PROGRESS = this.findViewById( R.id.pbTestProgress );

        // Prepare
        PROGRESS.setProgress( Q.getNum() );
        FRM_END.setVisibility( View.GONE );
        LY_NEXT.setVisibility( View.VISIBLE );

        // Question attributes
        this.setTextInTextView( LBL_QUESTION, Q.getText() );
        this.buildAnswerSupportFor( Q );
        this.buildImageFor( Q );
        this.showingEnd = false;

        this.launchSpeak( LBL_QUESTION.getText().toString() );
    }

    /** Triggered when the user asks for the phone to speak the question. */
    protected void onTalk()
    {
        final TextView LBL_QUESTION = this.findViewById( R.id.lblQuestion );

        this.speak( LBL_QUESTION.getText().toString() );
    }

    /** Actually speaks the text. Interrupts any other text being spoken.
      * @param txt the text to speak.
      */
    protected void speak(String txt)
    {
        this.ttEngine.speak(
                txt,
                TextToSpeech.QUEUE_FLUSH,
                null, null );

        return;
    }

    /** Launches a text to be spoken, but with a delay.
      * @param txt the text to be spoken.
      */
    protected void launchSpeak(String txt)
    {
        // Speech engine
        final Handler handler = new Handler( Looper.getMainLooper() );
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                TestActivity.this.speak( txt );
            }
        }, 1000 );
    }

    /** Shows HTML-styled text in a text view.
      * @param LBL_QUESTION the textview to show the text in.
      * @param txt the HTML-styled text.
      */
    protected void setTextInTextView(final TextView LBL_QUESTION, String txt)
    {
        TextViewCompat.setTextAppearance( LBL_QUESTION, textSize );
        LBL_QUESTION.setText( Util.richTextFromHtml( txt ) );
    }

    protected void buildAnswerSupportFor(final Question Q)
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
        }
        else
        if ( TYPE == ValueType.SCORE ) {
            this.buildRadioGroup( Q );
        } else {
            throw new Error( "no answer support for type: " +  TYPE );
        }

        return;
    }

    protected void buildRadioGroup(final Question Q)
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

    protected void buildRadioButton(int id, final RadioGroup RADIO_GROUP, String text)
    {
        final RadioButton TORET = new RadioButton( this );

        TORET.setId( id );
        TORET.setPadding( 10, 10, 10, 10 );
        TORET.setGravity( View.TEXT_ALIGNMENT_GRAVITY );
        TextViewCompat.setTextAppearance( TORET, textSize );
        TORET.setText( text );

        RADIO_GROUP.addView( TORET );
    }

    protected void buildImageFor(final Question Q)
    {
        final LinearLayout LY_IMAGE = this.findViewById( R.id.lyImageTest);

        if ( !Q.getPic().isEmpty() ) {
            final ImageView IMAGE_VIEW = this.findViewById( R.id.picImageTest );
            final String PIC_ID = Q.getPic();
            @SuppressLint("DiscouragedApi") final Drawable PIC = ResourcesCompat.getDrawable(
                    this.getResources(),
                    this.getResources().getIdentifier(
                            PIC_ID,
                            "drawable",
                            this.getPackageName() ),
                    null );
            IMAGE_VIEW.setImageDrawable( PIC );
            LY_IMAGE.setVisibility( View.VISIBLE );
        } else {
            LY_IMAGE.setVisibility( View.GONE );
        }

        return;
    }

    protected void buildNumInput()
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

    protected void buildTextArea()
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

    protected ImageButton buildButton(final ViewGroup CONTAINER, int resId)
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

    protected boolean fetchAnswer()
    {
        final Question Q = this.getFormPlayer().getCurrentQuestion();
        final ValueType TYPE = Q.getValueType();
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
        else
        if ( TYPE == ValueType.SCORE ) {
            value = this.fetchScoreAnswer();
        } else {
            throw new Error( "fetchAnswer(): unexpected type" );
        }

        return this.getFormPlayer().registerAnswer( value );
    }

    protected Value fetchBoolAnswer(final Question Q)
    {
        final RadioGroup RADIO_GROUP = this.findViewById( R.id.rgOptions );
        final int SELECTED_BUTTON_ID = RADIO_GROUP.getCheckedRadioButtonId();
        Value toret = null;

        if ( SELECTED_BUTTON_ID >= 0
          || this.getFormPlayer().getCurrentQuestion().getNumOptions() == 0 )
        {
            final Option OPT = Q.getOption( SELECTED_BUTTON_ID - BUTTON_ID_BASE );

            toret = new Value( OPT.getValue(), ValueType.BOOL );
        }

        return toret;
    }

    protected Value fetchStrAnswer()
    {
        final EditText TEXT_AREA = this.findViewById( R.id.edTextArea );
        final String STR = TEXT_AREA.getText().toString();
        Value toret = null;

        if ( !STR.isEmpty() ) {
            toret = new Value( STR, ValueType.STR );
        }

        return toret;
    }

    protected Value fetchIntAnswer()
    {
        final EditText NUM_INPUT = this.findViewById( R.id.edInputNum );
        final String STR = NUM_INPUT.getText().toString();
        Value toret = null;

        if ( !STR.isEmpty() ) {
            toret = new Value( STR, ValueType.INT );
        }

        return toret;
    }

    protected Value fetchScoreAnswer()
    {
        final Question Q = this.getCurrentQuestion();
        final RadioGroup RADIO_GROUP = this.findViewById( R.id.rgOptions );
        final int SELECTED_BUTTON_ID = RADIO_GROUP.getCheckedRadioButtonId();
        Value toret = null;

        if ( SELECTED_BUTTON_ID >= 0
          || this.getFormPlayer().getCurrentQuestion().getNumOptions() == 0 )
        {
            final Option OPT = Q.getOption( SELECTED_BUTTON_ID - BUTTON_ID_BASE );

            toret = new Value( OPT.getValue(), ValueType.INT );
        }

        return toret;
    }

    protected Question getCurrentQuestion()
    {
        final String MSG_ERROR = this.getString( R.string.err_unexpected );
        Question toret = null;

        // Chk player
        if ( this.getFormPlayer() == null ) {
            String txtError = MSG_ERROR + ": no player (??)";

            Toast.makeText( this,
                    MSG_ERROR,
                    Toast.LENGTH_LONG ).show();
            Log.e( this.getLogTag(), txtError  );
            throw new Error( txtError );
        } else {
            // Chk question
            toret = this.getFormPlayer().getCurrentQuestion();

            if ( toret == null ) {
                String txtError = MSG_ERROR + ": no current question (??)";

                Toast.makeText( this,
                        R.string.err_unexpected,
                        Toast.LENGTH_LONG ).show();
                Log.e( this.getLogTag(), txtError );
                throw new Error( txtError );
            }
        }

        return toret;
    }

    protected void hideKeyboard(final View VIEW)
    {
        final InputMethodManager IME = (InputMethodManager)
                this.getSystemService( Activity.INPUT_METHOD_SERVICE );

        IME.hideSoftInputFromWindow( VIEW.getWindowToken(), 0 );
    }

    public static void setTextAppeareanceFromTextSize(MainActivity.TextSize ts)
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

    public static int textSize;

    private boolean showingEnd;
    private TextToSpeech ttEngine;
}
