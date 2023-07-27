// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import android.util.Log;
import androidx.annotation.NonNull;

import com.devbaltasarq.cefaleapp.core.form.Value;
import com.devbaltasarq.cefaleapp.core.result.ResultStep;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;


public class FormJSONSaver {
    public FormJSONSaver(FormPlayer form)
    {
        this.FORM_PLAYER = form;
    }

    public @NonNull JSONObject toJSON()
    {
        final JSONObject TORET = new JSONObject();

        try {
            TORET.put( ETQ_ID, AppInfo.FULL_NAME );
            TORET.put( ETQ_SERIAL, Util.buildSerial() );
            TORET.put( ETQ_DIAGNOSTIC, this.buildDiagnostic() );
            TORET.put( ETQ_REPO, this.buildRepoInfo() );
        } catch(JSONException exc)
        {
            Log.e( LOG_ID, exc.getMessage() );
        }

        return TORET;
    }

    private JSONObject buildRepoInfo() throws JSONException
    {
        final Repo REPO = this.FORM_PLAYER.getRepo();
        final JSONObject TORET = new JSONObject();

        for(Repo.Id id: Repo.Id.values()) {
            final Value VALUE = REPO.getValue( id );

            if ( VALUE != null ) {
                TORET.put( id.toString().toLowerCase(), VALUE.get() );
            }
        }

        return TORET;
    }

    private JSONObject buildDiagnostic() throws JSONException
    {
        final JSONObject TORET = new JSONObject();

        for(final ResultStep RES_STEP: this.FORM_PLAYER.getResult().getSteps() ) {
            final JSONObject JSON_OPT = new JSONObject();

            if ( RES_STEP != null ) {
                JSON_OPT.put( ETQ_ACC_PROBABILITY, RES_STEP.getProbability() );
                JSON_OPT.put( ETQ_TEXT, RES_STEP.getValue().toString() );
                TORET.put( ETQ_OPT, JSON_OPT );
            }
        }

        TORET.put( ETQ_FINAL_PROBABILITY, this.FORM_PLAYER.getResult().getTotalProbability() );
        TORET.put( ETQ_DIAGNOSTIC, this.FORM_PLAYER.getResult().getDiagnostic() );

        return TORET;
    }

    public void saveToJSON(@NonNull Writer wr) throws IOException
    {
        wr.write( this.toJSON().toString() );
    }

    private final FormPlayer FORM_PLAYER;
    private final String LOG_ID = FormJSONSaver.class.getSimpleName();
    private final String ETQ_ID = "id";
    private final String ETQ_SERIAL = "serial";
    private final String ETQ_FINAL_PROBABILITY = "final_probability";
    private final String ETQ_OPT = "opt";
    private final String ETQ_TEXT = "txt";
    private final String ETQ_ACC_PROBABILITY = "acc_probability";
    private final String ETQ_REPO = "info_repo";
    private final String ETQ_DIAGNOSTIC = "diag";
}
