package com.example.isangeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class PlaySong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    TextView textView;
    ImageView pause, next, previous;
    MediaPlayer mediaPlayer;
    ArrayList<File> songs;
    int position;
    String songTitle;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        Toolbar yourToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(yourToolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        textView = findViewById(R.id.textView);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.prev);
        textView.setSelected(true);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        songs = (ArrayList) bundle.getParcelableArrayList("SongList");
        songTitle = intent.getStringExtra("CurrentSong");
        textView.setText(songTitle);
        position = intent.getIntExtra("Position", 0);

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position != 0) {
                    position -= 1;
                }
                else {
                    position = songs.size() - 1;
                }
                pause.setImageResource(R.drawable.pause);
                textView.setText(songs.get(position).getName().replace(".mp3", ""));
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                setUpdateSeek(seekBar);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                updateSeek.interrupt();
                if(position != songs.size() - 1) {
                    position += 1;
                }
                else {
                    position = 0;
                }
                pause.setImageResource(R.drawable.pause);
                textView.setText(songs.get(position).getName().replace(".mp3", ""));
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                setUpdateSeek(seekBar);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        setUpdateSeek(seekBar);
    }

    public void setUpdateSeek(SeekBar seekBar) {
        updateSeek = new Thread() {
            @Override
            public void run() {
                super.run();
                int currentPosition = 0;
                try {
                    while(currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                updateSeek.interrupt();
                                if(position != songs.size() - 1) {
                                    position += 1;
                                }
                                else {
                                    position = 0;
                                }
                                pause.setImageResource(R.drawable.pause);
                                textView.setText(songs.get(position).getName().replace(".mp3", ""));
                                Uri uri = Uri.parse(songs.get(position).toString());
                                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                                seekBar.setMax(mediaPlayer.getDuration());
                                mediaPlayer.start();
                                setUpdateSeek(seekBar);
                            }
                        });
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}