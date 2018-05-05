package ru.j2kv.filter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {
    static final int PHOTO_REQ = 1;
    static final int SELECT_REQ = 2;
    Uri photoUri;
    String photoPath;
    final int REQUEST_PERMISSION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabPhoto = findViewById(R.id.fab_photo);
        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        FloatingActionButton fabSelect = findViewById(R.id.fab_select);
        fabSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_REQ: {
                    photoUri = data.getData();
                    break;
                }
                case PHOTO_REQ: {

                    break;
                }
            }
            Intent editPhotoIntent = new Intent(this, EditActivity.class);
            editPhotoIntent.putExtra("photoUri", photoUri);
            startActivity(editPhotoIntent);
        }

        if (requestCode == PHOTO_REQ && resultCode == RESULT_OK) {

        }
    }



    private void takePhoto() {
        File photo = null;
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photo = File.createTempFile(String.valueOf(new Random().nextInt()), ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                photoPath = photo.getAbsolutePath();
            } catch (IOException ex) {
                Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            if (photo != null) {
                photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photo);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePhotoIntent, PHOTO_REQ);
            }
        }
    }


    private void selectPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_REQ);
    }
}
