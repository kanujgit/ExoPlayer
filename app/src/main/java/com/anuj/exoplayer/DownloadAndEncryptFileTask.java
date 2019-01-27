package com.anuj.exoplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

/**
 * Created by Anuj on 27/01/19.
 */

public class DownloadAndEncryptFileTask extends AsyncTask<Void, Void, Void> {

    Context context;
    private String mUrl;
    private File mFile;
    private Cipher mCipher;
    ProgressDialog mProgressDialog;

    public DownloadAndEncryptFileTask(Context context,String url, File file, Cipher cipher) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("You need to supply a url to a clear MP4 file to download and encrypt, or modify the code to use a local encrypted mp4");
        }
        context =context;
        mUrl = url;
        mFile = file;
        mCipher = cipher;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Create progress dialog
        mProgressDialog = new ProgressDialog(context);
        // Set your progress dialog Title
        mProgressDialog.setTitle("Downloading");
        // Set your progress dialog Message
        mProgressDialog.setMessage("Downloading, Please Wait!");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // Show progress dialog
        mProgressDialog.show();
    }

    private void downloadAndEncrypt() throws Exception {

        URL url = new URL(mUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        int fileLength = connection.getContentLength();


        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("server error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
        }

        InputStream inputStream = connection.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(mFile);
        CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, mCipher);

        long total =0;
        byte buffer[] = new byte[1024 * 1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {

            Log.d(getClass().getCanonicalName(), "reading from http...");

            total += bytesRead;


            cipherOutputStream.write(buffer, 0, bytesRead);
        }


//
//        long total = 0;
//        int count;
//        while ((count = inputStream.read(buffer)) != -1) {
//            total += count;
//            // Publish the progress
//            //publishProgress((int) (total * 100 / fileLength));
//            //output.write(data, 0, count);
//        }



        inputStream.close();
        cipherOutputStream.close();
        connection.disconnect();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            downloadAndEncrypt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        mProgressDialog.dismiss();
        Log.d(getClass().getCanonicalName(), "done");
    }
}
