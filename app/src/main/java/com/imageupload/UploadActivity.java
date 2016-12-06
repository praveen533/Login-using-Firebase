package com.imageupload;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UploadActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView imageView;
    Button viewButton,signout_button;
    FirebaseAuth firebaseAuth;
    private Uri downloadUrl;
    private String profileImage,email;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        firebaseAuth=FirebaseAuth.getInstance();

        imageView=(ImageView)findViewById(R.id.circleView);
        viewButton = (Button) findViewById(R.id.viewbutton);
        signout_button=(Button)findViewById(R.id.signout);

        showProfileImage();

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showProfileImage();
            }
        });

        signout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();

                Intent i=new Intent(UploadActivity.this,LoginActivity.class);
                startActivity(i);

            }
        });

    }

    private void showProfileImage(){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userDatabase=database.getReference();

        userDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children= dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    RegistrationDB registrationDB = child.getValue(RegistrationDB.class);
                    profileImage = registrationDB.getProfileImage();
                    email = registrationDB.getEmailID();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w("loadPost:onCancelled", databaseError.toException());

            }
        });


        Toast.makeText(this,email,Toast.LENGTH_LONG).show();
        Picasso.with(UploadActivity.this).load(profileImage).into(imageView);
    }
}
