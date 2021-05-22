package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {
    Button btnPlay, btnNext, btnPrevious;
    TextView txtSongName, txtStart, txtStop;
    SeekBar seekBar;

    String sName;
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        btnPlay = findViewById(R.id.btn_play_song);
        btnNext = findViewById(R.id.btn_next_song);
        btnPrevious = findViewById(R.id.btn_previous_song);
        txtSongName = findViewById(R.id.txt_song_name);
        txtStart = findViewById(R.id.txt_start_song);
        txtStop = findViewById(R.id.txt_stop_song);
        seekBar = findViewById(R.id.seek_bar);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        position = bundle.getInt("position", 0);
        txtSongName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sName = mySongs.get(position).getName();
        txtSongName.setText(sName);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeekBar();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        setSongStop(mediaPlayer.getDuration());
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = convertTime(mediaPlayer.getCurrentPosition());
                txtStart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                btnPlay.setBackgroundResource(R.drawable.ic_play_music);
                mediaPlayer.pause();
            }
            else {
                btnPlay.setBackgroundResource(R.drawable.ic_pause_music);
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(mp -> btnNext.performClick());//play next song when the current one is finished

        btnNext.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = (position + 1) % mySongs.size();
            Uri uriNextSong = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uriNextSong);
            sName = mySongs.get(position).getName();
            txtSongName.setText(sName);
            setSongStop(mediaPlayer.getDuration());
            mediaPlayer.start();
            btnPlay.setBackgroundResource(R.drawable.ic_pause_music);
            mediaPlayer.setOnCompletionListener(mp -> btnNext.performClick());//play next song when the current one is finished
        });

        btnPrevious.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
            Uri uriNextSong = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uriNextSong);
            sName = mySongs.get(position).getName();
            txtSongName.setText(sName);
            setSongStop(mediaPlayer.getDuration());
            mediaPlayer.start();
            btnPlay.setBackgroundResource(R.drawable.ic_pause_music);
            mediaPlayer.setOnCompletionListener(mp -> btnNext.performClick());//play next song when the current one is finished
        });
    }

    /**
     *
     * @param duration in milliseconds
     * @return the duration in minutes and seconds
     */
    private String convertTime (int duration) {
        int min = duration / 1000 / 60;
        int second = duration / 1000 % 60;
        return min + ":" + ((second < 10) ? "0" : "") + second;
    }

    //update the seek bar when the media is playing
    private void updateSeekBar () {
        Handler handler = new Handler();
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void setSongStop(int duration) {
        String stopTime = convertTime(duration);
        txtStop.setText(stopTime);
    }
}