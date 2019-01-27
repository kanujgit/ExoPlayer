package com.anuj.exoplayer;

import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.SimpleExoPlayer;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/CTR/NoPadding";

    private static final String ENCRYPTED_FILE_NAME = "encrypted.mp4";

    private Cipher mCipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;

    private File mEncryptedFile;

    //private SimpleExoPlayerView mSimpleExoPlayerView;
    SimpleExoPlayer mSimpleExoPlayerView;

  /*@Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video);

    mSimpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.simpleexoplayerview);

    mEncryptedFile = new File(getFilesDir(), ENCRYPTED_FILE_NAME);

    SecureRandom secureRandom = new SecureRandom();
    byte[] key = new byte[16];
    byte[] iv = new byte[16];
    secureRandom.nextBytes(key);
    secureRandom.nextBytes(iv);

    mSecretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
    mIvParameterSpec = new IvParameterSpec(iv);

    try {
      mCipher = Cipher.getInstance(AES_TRANSFORMATION);
      mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private boolean hasFile() {
    return mEncryptedFile != null
        && mEncryptedFile.exists()
        && mEncryptedFile.length() > 0;
  }

  public void encryptVideo(View view) {
    if (hasFile()) {
      Log.d(getClass().getCanonicalName(), "encrypted file found, no need to recreate");
      return;
    }
    try {
      Cipher encryptionCipher = Cipher.getInstance(AES_TRANSFORMATION);
      encryptionCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
      // TODO:
      // you need to encrypt a video somehow with the same key and iv...  you can do that yourself and update
      // the ciphers, key and iv used in this demo, or to see it from top to bottom,
      // supply a url to a remote unencrypted file - this method will download and encrypt it
      // this first argument needs to be that url, not null or empty...
      new DownloadAndEncryptFileTask(null, mEncryptedFile, encryptionCipher).execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void playVideo(View view) {
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
    TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
    LoadControl loadControl = new DefaultLoadControl();
    SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
    mSimpleExoPlayerView.setPlayer(player);
    DataSource.Factory dataSourceFactory = new EncryptedFileDataSourceFactory(mCipher, mSecretKeySpec, mIvParameterSpec, bandwidthMeter);
    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
    try {
      Uri uri = Uri.fromFile(mEncryptedFile);
      MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
      player.prepare(videoSource);
      player.setPlayWhenReady(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }*/

}
