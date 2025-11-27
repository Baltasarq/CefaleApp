// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.ui;


import android.content.Intent;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.devbaltasarq.cefaleapp.R;
import com.devbaltasarq.cefaleapp.core.Identifiable;
import com.devbaltasarq.cefaleapp.core.Language;
import com.devbaltasarq.cefaleapp.core.treatment.Medicine;
import com.devbaltasarq.cefaleapp.ui.treatment.MedicineActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.List;
import java.util.Locale;


public class CefaleAppActivity extends AppCompatActivity {
    /** Shows the medicine activity with the given medicine loaded.
     * @param M the given medicine to show.
     */
    public void showMedicine(final Medicine M)
    {
        final Intent INTENT = new Intent( this, MedicineActivity.class );

        MedicineActivity.medicine = M;
        this.startActivity( INTENT );
    }

    public void loadL10nHTML(final WebView WV, final String ASSET_NAME )
    {
        String htmlBody;

        try (InputStream reader =
                            this.getResources().getAssets().open( ASSET_NAME ))
        {
            byte[] buffer = new byte[8192];
            int bytesRead;
            var output = new ByteArrayOutputStream();

            while ((bytesRead = reader.read(buffer)) != -1) {
                output.write( buffer, 0, bytesRead );
            }

            htmlBody = output.toString( StandardCharsets.UTF_8 );
        } catch(IOException exc)
        {
            htmlBody = "Loading " + ASSET_NAME + ": "
                    + this.getString( R.string.err_io );
            htmlBody += "&nbsp;&nbsp;&nbsp;&nbsp;" + exc.getClass().getSimpleName();
            htmlBody += "&nbsp;&nbsp;&nbsp;&nbsp;" + exc.getMessage();
        }

        htmlBody = htmlBody
                .replace( "$LANG",
                        Language.langFromDefaultLocale().toString() );

        // Set the HTML
        WV.loadData( htmlBody, "text/html", "UTF-8" );
    }

    /** Sorts identifiables (medicines, morbidities...) taking into account locales.
      * @param IDENTIFIABLES the medicine list to sort.
      */
    public void sortIdentifiableI18n(final List<? extends Identifiable> IDENTIFIABLES)
    {
        final Collator COLLATOR = Collator.getInstance( new Locale( "es","ES" ));

        IDENTIFIABLES.sort( (m1, m2) ->
                COLLATOR.compare(
                        m1.getId().getName().getForCurrentLanguage(),
                        m2.getId().getName().getForCurrentLanguage() ) );
    }
}
