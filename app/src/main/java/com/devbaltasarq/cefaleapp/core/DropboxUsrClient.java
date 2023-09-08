// Cefaleapp (c) 2023 Baltasar MIT License <jbgarcia@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.devbaltasarq.cefaleapp.R;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/** This class automates access to the Dropbox API. */
public class DropboxUsrClient {
    public static final String TAG = DropboxUsrClient.class.getSimpleName();
    private enum ListMode{ NO_DIRS, INCLUDE_DIRS }

    /** Creates a new Dropbox client for the user of the given email.
      * @param OWNER The activity this client is going to be used in.
      */
    public DropboxUsrClient(final AppCompatActivity OWNER)
    {
        final String APP_PACKAGE = OWNER.getPackageName();
        final DbxRequestConfig CONFIG = DbxRequestConfig.newBuilder( APP_PACKAGE ).build();
        final DbxCredential CREDENTIALS = new DbxCredential(
                "",
                0L,
                OWNER.getString( R.string.dropbox_token ),
                OWNER.getString( R.string.dropbox_key ),
                OWNER.getString( R.string.dropbox_secret ) );

        this.DBOX_CLIENT = new DbxClientV2(CONFIG, CREDENTIALS );
    }

    /** Gets all the files in a given remote directory.
     * @param dir The remote directory to list files of.
     * @return An array of String[] with the name (without paths) of found files.
     * @throws DbxException if comms with remote go wrong.
     */
    private String[] listFilesInDir(String dir, ListMode listMode) throws DbxException
    {
        final List<String> TORET = new ArrayList<>();
        ListFolderResult folderResult = DBOX_CLIENT.files().listFolder( dir );

        do {
            for(Metadata metadata: folderResult.getEntries()) {
                if ( metadata instanceof FileMetadata
                  || ( listMode == ListMode.INCLUDE_DIRS
                    && metadata instanceof FolderMetadata ) )
                {
                    TORET.add( metadata.getName() );
                }
            }

            folderResult = DBOX_CLIENT.files().listFolderContinue( folderResult.getCursor() );
        } while( folderResult.getHasMore() );

        return TORET.toArray( new String[ 0 ] );
    }

    /** Downloads a given file into the app's temp files.
      * @param fileName the name of the file, and only its name (in order to save in local).
      * @param FILE_PATH the path of the file, which should exist.
      * @throws DbxException when dropbox comms go wrong.
      */
    private File downloadFileTo(String fileName, String FILE_PATH) throws DbxException
    {
        DbxDownloader<FileMetadata> downloader = DBOX_CLIENT.files().download( FILE_PATH );
        File f;
        OutputStream out = null;

        try {
            // Download file to a temporary
            f = File.createTempFile( TAG, fileName );
            out = new FileOutputStream( f );

            downloader.download( out );
        } catch (IOException exc) {
            throw new DbxException( exc.getMessage() );
        } finally {
            if ( out != null ) {
                try {
                    out.close();
                } catch(IOException e) {
                    Log.e( TAG, "error closing stream downloading: " + e.getMessage() );
                }
            }
        }

        return f;
    }

    /** Uploads a file.
     * @param fin The file to upload.
     * @throws DbxException when something goes wrong uploading.
     */
    public void uploadFile(File fin) throws DbxException
    {
        this.uploadFile( fin,
                    ROOT_DIR
                          + DROPBOX_DIR_SEPARATOR
                          + fin.getName() );
    }

    /** Uploads a file.
     * @param fin The file to upload.
     * @param toPath the absolute path of the file in the cloud.
     * @throws DbxException when something goes wrong uploading.
     */
    public void uploadFile(File fin, String toPath) throws DbxException
    {
        //this.refreshToken();

        // Upload file to Dropbox
        try (InputStream in = new FileInputStream( fin ) ) {
            this.DBOX_CLIENT.files()
                    .uploadBuilder( toPath )
                    .withMode( WriteMode.OVERWRITE )
                    .uploadAndFinish( in );
        } catch(IOException exc)
        {
            throw new DbxException( "uploading: " + exc.getMessage() );
        }
    }

    private DbxClientV2 DBOX_CLIENT;
    private static final String ENQUIRIES_DIR = "enquiries";
    private static final String DROPBOX_DIR_SEPARATOR = "/";
    private final static String ROOT_DIR = DROPBOX_DIR_SEPARATOR + ENQUIRIES_DIR;
}
