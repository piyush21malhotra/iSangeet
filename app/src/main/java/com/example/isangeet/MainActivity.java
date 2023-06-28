package com.example.isangeet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView;
        listView = findViewById(R.id.listView);
        Toolbar yourToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(yourToolbar);


        Log.d("myTag", "onPermission");
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Runtime Permission Given", Toast.LENGTH_SHORT).show();
                        Log.d("myTag", "onPermissionGranted: ");
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String[] item = new String[mySongs.size()];
                        for(int i = 0; i<item.length; i++) {
                            item[i] = mySongs.get(i).getName().replace(".mp3", "");
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, item);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("SongList", mySongs);
                                intent.putExtra("CurrentSong", currentSong);
                                intent.putExtra("Position", position);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    public ArrayList<File> fetchSongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File [] songs = file.listFiles();

        if(songs != null) {
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory()) {
                    arrayList.addAll(fetchSongs(myFile));
                }
                else {
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")) {
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}