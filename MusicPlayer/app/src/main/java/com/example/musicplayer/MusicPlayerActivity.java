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
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekBar;

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
        String songName = intent.getStringExtra("song_name");
        position = bundle.getInt("position", 0);
        txtSongName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sName = mySongs.get(position).getName();
        txtSongName.setText(sName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeekBar = new Thread(() -> {
            int totalDuration = mediaPlayer.getDuration();
            int currentPosition = 0;
            while (currentPosition < totalDuration) {
                try {
                    Thread.sleep(500);
                    currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                } catch (InterruptedException | IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();

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

        txtStop.setText(convertTime(mediaPlayer.getDuration()));
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

        mediaPlayer.setOnCompletionListener(mp -> btnNext.performClick());

        btnNext.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = (position + 1) % mySongs.size();
            Uri uriNextSong = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uriNextSong);
            sName = mySongs.get(position).getName();
            txtSongName.setText(sName);
            mediaPlayer.start();
            btnPlay.setBackgroundResource(R.drawable.ic_pause_music);
        });

        btnPrevious.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
            Uri uriNextSong = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uriNextSong);
            sName = mySongs.get(position).getName();
            txtSongName.setText(sName);
            mediaPlayer.start();
            btnPlay.setBackgroundResource(R.drawable.ic_pause_music);
        });
    }

    private String convertTime(int duration) {
        int min = duration / 1000 / 60;
        int second = duration / 1000 % 60;
        return min + ":" + ((second < 0) ? "0" : "") + second;
    }

}