// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire;


import android.util.Log;
import androidx.annotation.NonNull;

import com.devbaltasarq.cefaleapp.core.AppInfo;
import com.devbaltasarq.cefaleapp.core.Util;
import com.devbaltasarq.cefaleapp.core.questionnaire.form.Value;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;


public class FormJSONSaver {
    public FormJSONSaver(MigraineRepo repo, Steps steps)
    {
        this.repo = repo;
        this.steps = steps;
    }

    public @NonNull JSONObject toJSON()
    {
        final JSONObject TORET = new JSONObject();

        try {
            TORET.put( ETQ_ID, AppInfo.FULL_NAME );
            TORET.put( ETQ_SERIAL, Util.buildSerial() );
            TORET.put( ETQ_HISTORY, this.buildHistory() );
            TORET.put( ETQ_REPO, this.buildRepoValues() );
        } catch(JSONException exc)
        {
            Log.e( LOG_ID, "FormJSONSaver.toJSON(): " + exc.getMessage() );
        }

        return TORET;
    }

    private JSONObject buildRepoValues() throws JSONException
    {
        final JSONObject TORET = new JSONObject();

        for(MigraineRepo.Id id: MigraineRepo.Id.values()) {
            final Value VALUE = this.repo.getValue( id );

            if ( VALUE != null ) {
                TORET.put( id.toString().toLowerCase(), VALUE.get() );
            }
        }

        return TORET;
    }

    private JSONArray buildHistory() throws JSONException
    {
        final JSONArray TORET = new JSONArray();

        for(final String RES_STEP: this.steps.getQuestionHistory() ) {
            TORET.put( RES_STEP );
        }

        return TORET;
    }

    public void saveToJSON(@NonNull Writer wr) throws IOException
    {
        wr.write( this.toJSON().toString() );
    }

    private final MigraineRepo repo;
    private final Steps steps;
    private final String LOG_ID = FormJSONSaver.class.getSimpleName();
    private final String ETQ_ID = "id";
    private final String ETQ_SERIAL = "serial";
    private final String ETQ_REPO = "repo";
    private final String ETQ_HISTORY = "history";
}
