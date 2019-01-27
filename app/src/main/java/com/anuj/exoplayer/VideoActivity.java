package com.anuj.exoplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import javax.crypto.Cipher;
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

        taskModels.add(new TaskModel(0,"first" ,"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"));
        taskModels.add(new TaskModel(1, "Second","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"));
        taskModels.add(new TaskModel(2, "third","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"));

    }

    public void encryptVideo(int position, File file) {
        Log.e("file_name",file.getName()+" //"+file.getAbsolutePath());
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
                new DownloadAndEncryptFileTask(taskModels.get(position).getUrl(), file, encryptionCipher).execute();
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
}
