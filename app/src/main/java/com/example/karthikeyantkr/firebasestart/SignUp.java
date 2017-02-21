package com.example.karthikeyantkr.firebasestart;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String fname,lname,email,profile,pwd;
    EditText fanme_edit,lname_edit,email_edit,pwd_edit,repeatpwd;
    ImageView profile_image;
    Button signup,cancel;
    FirebaseStorage storage;
    StorageReference storageRef;
    private DatabaseReference mDatabase,mData;
    Uri imageURI;
    String url,profile1;
    User edituser;
    Switch gender;
    String gendername="Female";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Sign UP");
         storage = FirebaseStorage.getInstance();
         storageRef = storage.getReferenceFromUrl("gs://homework7-e26ed.appspot.com");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        assignviews();
        gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    gendername="Male";
                }
                else
                {
                    gendername="Female";
                }
            }
        });
        if(getIntent().getExtras()!=null)
        {
getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
          String userid=  getIntent().getExtras().getString("userID");
edituser=getIntent().getExtras().getParcelable("value");
            mDatabase.child("user").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pwd_edit.setText(dataSnapshot.getValue(User.class).getPassword());
                    repeatpwd.setText(dataSnapshot.getValue(User.class).getPassword());
                    if(dataSnapshot.getValue(User.class).getProfilepic()==null)
                    {
                        Picasso.with(getApplicationContext()).load(R.drawable.profile).into(profile_image);
                   profile1=null;
                    }
                    else {
                        Picasso.with(getApplicationContext()).load(dataSnapshot.getValue(User.class).getProfilepic()).into(profile_image);
                    profile1=dataSnapshot.getValue(User.class).getProfilepic();
                    }
                        if(dataSnapshot.getValue(User.class).getGender()=="Male") {
                    gender.setChecked(true);
                }
                    else
                {
                    gender.setChecked(false);
                }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            fanme_edit.setText(edituser.fname);
            lname_edit.setText(edituser.lname);
            email_edit.setText(edituser.emailID);
            signup.setText("Edit Profile");

        }


       // mAuth.signOut();
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pictureIntent = new Intent(Intent.ACTION_PICK);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                Uri data = Uri.parse(pictureDirectoryPath);
                pictureIntent.setDataAndType(data, "image/*");
                startActivityForResult(pictureIntent,20);


            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupaction();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void signupaction() {

        if (email_edit.getText().toString().length() > 0 && fanme_edit.getText().toString().length() > 0 && lname_edit.getText().toString().length() > 0 && pwd_edit.getText().toString().length() > 5
               &&pwd_edit.getText().toString().equals(repeatpwd.getText().toString()) ) {
            mAuth.createUserWithEmailAndPassword(email_edit.getText().toString(), pwd_edit.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("demo", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                if(getIntent().getExtras()!=null)
                                {
                                    edituser.setFname(fanme_edit.getText().toString());
                                    edituser.setLname(lname_edit.getText().toString());
                                    edituser.setGender(gendername);
                                    edituser.setPassword(pwd_edit.getText().toString());
                                    mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).setValue(edituser);
                                    Intent intent=new Intent(SignUp.this,ProfileActivity.class);
                                    intent.putExtra("userID",mAuth.getCurrentUser().getUid());
                                    startActivity(intent);
                                }
                                Intent start = new Intent();
                                setResult(RESULT_CANCELED, start);
                                finish();
                            } else {

                               String userId =    task.getResult().getUser().getUid();
                                if(imageURI!=null) {
                                    String path = "images/" + userId + ".jpeg";
                                    StorageReference imageRef = storageRef.child(path);
                                    UploadTask upload = imageRef.putFile(imageURI);
                                    upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Uri download = taskSnapshot.getMetadata().getDownloadUrl();
                                            url = download.toString();

                                            User user = new User(fanme_edit.getText().toString(), lname_edit.getText().toString(), email_edit.getText().toString(), url, mAuth.getCurrentUser().getUid());

                                            user.setPassword(pwd_edit.getText().toString());

                                            mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).setValue(user);


                                            Intent intent = new Intent(SignUp.this, ProfileActivity.class);
                                            intent.putExtra("userID", mAuth.getCurrentUser().getUid());
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    //   Toast.makeText(getApplicationContext(), "User has been Created", Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    User user = new User(fanme_edit.getText().toString(), lname_edit.getText().toString(), email_edit.getText().toString(), null, mAuth.getCurrentUser().getUid());

                                    user.setPassword(pwd_edit.getText().toString());
user.setGender(gendername);
                                    mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).setValue(user);


                                    Intent intent = new Intent(SignUp.this, ProfileActivity.class);
                                    intent.putExtra("userID", mAuth.getCurrentUser().getUid());
                                    startActivity(intent);
                                    finish();
                                }
                            }


                        }
                    });
        } else {
            Toast.makeText(SignUp.this, "Error in Sign Up Details, Check all the fields are filled", Toast.LENGTH_LONG).show();
        }

    }


    private void assignviews() {
        fanme_edit= (EditText) findViewById(R.id.editTextFn);

        lname_edit= (EditText) findViewById(R.id.editTextLastName);

        email_edit= (EditText) findViewById(R.id.editTextEmail);

        pwd_edit= (EditText) findViewById(R.id.editTextPassword);
        signup= (Button) findViewById(R.id.buttonSubmit);
        cancel= (Button) findViewById(R.id.buttonCancelSignup);
        profile_image= (ImageView) findViewById(R.id.SignupImage);
        gender= (Switch) findViewById(R.id.switch1);
repeatpwd= (EditText) findViewById(R.id.editTextRepeatPassword);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 20) {
                imageURI = data.getData();
Picasso.with(getApplicationContext()).load(imageURI).into(profile_image);
            }
            }
    }
}
