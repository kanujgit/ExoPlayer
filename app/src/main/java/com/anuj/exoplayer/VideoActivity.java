package com.anuj.exoplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class VideoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<TaskModel> taskModels = new ArrayList<>();


    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/CTR/NoPadding";
    private static final String ENCRYPTED_FILE_NAME = "encrypted.mp4";

    private Cipher mCipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        recyclerView = (RecyclerView) findViewById(R.id.video_rec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        byte[] key = new byte[16];
        byte[] iv = new byte[16];
        //      secureRandom.nextBytes(key);
        //    secureRandom.nextBytes(iv);

        mSecretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
        mIvParameterSpec = new IvParameterSpec(iv);

        try {
            mCipher = Cipher.getInstance(AES_TRANSFORMATION);
            mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }


        VideoAdapter videoAdapter = new VideoAdapter(this, taskModels, mCipher,
                mSecretKeySpec, mIvParameterSpec, new VideoAdapter.onClick() {
            @Override
            public void encryptDownload(int position, File file) {
                encryptVideo(position, file);
            }

            @Override
            public void play(int position) {

            }
        });

        recyclerView.setAdapter(videoAdapter);
        createVideoList();
    }


    private void createVideoList() {

        taskModels.add(new TaskModel(0, "first", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"));
        taskModels.add(new TaskModel(1, "Second", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"));
        taskModels.add(new TaskModel(2, "third", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"));

    }

    public void encryptVideo(int position, File file) {
        Log.e("file_name", file.getName() + " //" + file.getAbsolutePath());
        if (hasFile(file)) {
            Log.d(getClass().getCanonicalName(), "encrypted file found, no need to recreate");
            Toast.makeText(this, "encrypted file found, no need to recreate", Toast.LENGTH_SHORT).show();
            return;
        } else {

            try {
                Cipher encryptionCipher = Cipher.getInstance(AES_TRANSFORMATION);
                encryptionCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
                // TODO:
                // you need to encrypt a video somehow with the same key and iv...  you can do that yourself and update
                // the ciphers, key and iv used in this demo, or to see it from top to bottom,
                // supply a url to a remote unencrypted file - this method will download and encrypt it
                // this first argument needs to be that url, not null or empty...
                // new DownloadAndEncryptFileTask(VideoActivity.this, taskModels.get(position).getUrl(), file, encryptionCipher).execute();
                new DownloadFilesTask(file, encryptionCipher).execute(taskModels.get(position).getUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean hasFile(File mEncryptedFile) {
        return mEncryptedFile != null
                && mEncryptedFile.exists()
                && mEncryptedFile.length() > 0;
    }


    private class DownloadFilesTask extends AsyncTask<String, Integer, Long> {

        ProgressDialog mProgressDialog;
        private PowerManager.WakeLock mWakeLock;


        private File mFile;
        private Cipher mCipher;

        public DownloadFilesTask(File mFile, Cipher mCipher) {
            this.mFile = mFile;
            this.mCipher = mCipher;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            super.onPreExecute();
            // Create progress dialog
            mProgressDialog = new ProgressDialog(VideoActivity.this);
            // Set your progress dialog Title
            mProgressDialog.setTitle("Downloading");
            // Set your progress dialog Message
            mProgressDialog.setMessage("Downloading, Please Wait!");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // Show progress dialog
            mProgressDialog.setCancelable(false);


            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) (VideoActivity.this).getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            //mWakeLock.acquire(10*60*1000L /*10 minutes*/);
            mWakeLock.acquire();
            mProgressDialog.show();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(values[0]);
        }

        protected Long doInBackground(String... Url) {

            long total = 0;
            try {
                URL url = new URL(Url[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // Detect the file lenghth
                int fileLength = connection.getContentLength();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException("server error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
                }

                InputStream inputStream = connection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(mFile);
                CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, mCipher);


                byte buffer[] = new byte[1024 * 1024];


                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {

                    if (isCancelled()) {
                        inputStream.close();
                        return null;
                    }
                    Log.d(getClass().getCanonicalName(), "reading from http...");

                    total += bytesRead;

                    Log.e("toal", total + "///" + fileLength);
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    cipherOutputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                cipherOutputStream.close();
                connection.disconnect();


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }


            return total;
        }

        protected void onPostExecute(Long result) {
            //   showDialog(("Downloaded " + result + " bytes"));
            mProgressDialog.dismiss();
            mWakeLock.release();

            if (result != null)
                Toast.makeText(VideoActivity.this, "File downloaded", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(VideoActivity.this, "Download error: " + result, Toast.LENGTH_LONG).show();

        }
    }
}
