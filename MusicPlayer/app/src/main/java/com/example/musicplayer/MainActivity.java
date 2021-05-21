package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] songItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list_view_song);

        runtime();
    }

    public void runtime() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> findSongs(File file) {
        ArrayList<File> songList = new ArrayList<>();

        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    songList.addAll(findSongs(singleFile));
                } else if (singleFile.getName().endsWith(".mp3")){
                    songList.add(singleFile);
                }
            }
        }
        else {
            Toast.makeText(this, "Cannot retrieve files", Toast.LENGTH_SHORT).show();
        }
        return songList;
    }

    /**
     * getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
     * @return storage>emulated>0>Android>data>com.example.musicplayer>files>Download
     */
    public void displaySongs() {
        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        File directory = new File(path);
        final ArrayList<File> songs = findSongs(directory);
        if (songs.isEmpty()) {
            Toast.makeText(this, "Can not retrieve files", Toast.LENGTH_SHORT).show();
        }
        else {
            songItems = new String[songs.size()];
            for (int i = 0; i < songs.size(); i++) {
                songItems[i] = songs.get(i).getName().replace("mp3", "");
            }
//            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songItems);
//            listView.setAdapter(myAdapter);
            CustomAdapter myAdapter = new CustomAdapter();
            listView.setAdapter(myAdapter);

           listView.setOnItemClickListener((parent, view, position, id) -> {
               String songName = (String)listView.getItemAtPosition(position);
               startActivity(new Intent(getApplicationContext(), MusicPlayerActivity.class)
               .putExtra("songs", songs)
               .putExtra("song_name", songName)
               .putExtra("position", position));
           });
        }
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return songItems.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textSong = myView.findViewById(R.id.song_name);
            textSong.setSelected(true);
            textSong.setText(songItems[position]);

            return myView;
        }
    }
}