package com.imageupload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageView;
    Button uploadButton,viewButton;
    //ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        firebaseAuth=FirebaseAuth.getInstance();

        imageView = (ImageView) findViewById(R.id.view);
        uploadButton = (Button) findViewById(R.id.upload);
        viewButton = (Button) findViewById(R.id.viewbutton);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user != null){
                    saveToFirebase();
                }else{
                    Intent intent=new Intent(UploadActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadImage();
            }
        });

    }

    private void saveToFirebase(){

        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();

            // Log.d(TAG, String.valueOf(bitmap));
            //imageView.setImageBitmap(bitmap);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference=storage.getReferenceFromUrl("gs://image-df9b6.appspot.com");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();

            StorageReference reference=storageReference.child("images/"+bytes);

            UploadTask uploadTask = reference.putBytes(bytes,metadata);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getApplicationContext(),"Upload Failed",Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(getApplicationContext(),"Upload Successful",Toast.LENGTH_LONG).show();
                }
            });
        }

    }
/*
    protected void onProgressUpdate(Integer... progress) {
        // Making progress bar visible
        progressBar.setVisibility(View.VISIBLE);

        // updating progress bar value
        progressBar.setProgress(progress[0]);

        // updating percentage value
        progresstext.setText("Uploading Image " + String.valueOf(progress[0]) + "% Done");
    }*/

    private void downloadImage(){

        //imageView.setImageBitmap(bitmap);
    }
}
