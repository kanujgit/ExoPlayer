package com.anuj.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.io.File;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.RrbViewHolder> {

    Context context;
    ArrayList<TaskModel> dummyModels;
  //  File mEncryptedFile;
    //private var player: SimpleExoPlayer? = null
    SimpleExoPlayer exoPlayer = null;

    Cipher mCipher;
    SecretKeySpec mSecretKeySpec;
    IvParameterSpec mIvParameterSpec;


    interface onClick {
        public void encryptDownload(int position, File file);

        public void play(int position);
    }

    onClick onClick;


    public VideoAdapter(Context context, ArrayList<TaskModel> rrbModals, Cipher mCipher,
                        SecretKeySpec mSecretKeySpec, IvParameterSpec mIvParameterSpec,
                        onClick onClick) {
        this.context = context;
        this.dummyModels = rrbModals;
        this.onClick = onClick;
        this.mCipher = mCipher;
        this.mSecretKeySpec = mSecretKeySpec;
        this.mIvParameterSpec = mIvParameterSpec;


    }

    View view;

    @NonNull
    @Override
    public RrbViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.video_item, viewGroup, false);
        return new RrbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RrbViewHolder holder, final int i) {

        final  TaskModel taskModel = dummyModels.get(i);


        holder.encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File mEncryptedFile;   mEncryptedFile = new File(context.getFilesDir(), taskModel.getTitle() + ".mp4");
                onClick.encryptDownload(i, mEncryptedFile);
            }
        });

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File mEncryptedFile = new File(context.getFilesDir(), taskModel.getTitle() + ".mp4");
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
                LoadControl loadControl = new DefaultLoadControl();
                exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                holder.player.setPlayer(exoPlayer);

                DataSource.Factory dataSourceFactory = new EncryptedFileDataSourceFactory(mCipher, mSecretKeySpec,
                        mIvParameterSpec, bandwidthMeter);

                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                try {
                    Uri uri = Uri.fromFile(mEncryptedFile);
                    //MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
                    MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                    exoPlayer.prepare(videoSource);
                    exoPlayer.setPlayWhenReady(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dummyModels.size();
    }

    public class RrbViewHolder extends RecyclerView.ViewHolder {


        //PlayerView playerView;
        Button play, encrypt;
        public PlayerView player;

        public RrbViewHolder(@NonNull View itemView) {
            super(itemView);


            player = itemView.findViewById(R.id.video_view);
            play = (Button) itemView.findViewById(R.id.play);
            encrypt = (Button) itemView.findViewById(R.id.encrypt);

        }
    }
}
