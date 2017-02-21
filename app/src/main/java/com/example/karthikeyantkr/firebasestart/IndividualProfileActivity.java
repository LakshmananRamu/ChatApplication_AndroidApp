package com.example.karthikeyantkr.firebasestart;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class IndividualProfileActivity extends AppCompatActivity {
    ImageView profilepic,attach,msgpic;
    TextView fname,lname,gender;
    EditText message;
    Button send;
    User user,currentuser,touser;
    String userid,pic;
    Messages msg,msg1;
    private DatabaseReference mDatabase,mdata,mtouser;
    private FirebaseAuth mAuth;
    ArrayList<Messages> allmsg=new ArrayList<>();

    ArrayList<Messages> tomsg=new ArrayList<>();
    Uri imageURI;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_profile);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Profile");
        user=getIntent().getExtras().getParcelable("user");
        userid=getIntent().getExtras().getString("id");
        pic=getIntent().getExtras().getString("pic");
        Log.d("demo","userID "+userid+pic);
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("messages");
        mdata=FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
        mtouser=FirebaseDatabase.getInstance().getReference().child("user").child(userid);
        assignviews();
        setdata();
        mdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentuser= dataSnapshot.getValue(User.class);
                allmsg=currentuser.getMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mtouser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                touser=dataSnapshot.getValue(User.class);
                tomsg=touser.getMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg=new Messages(mAuth.getCurrentUser().getUid(),userid,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),message.getText().toString());
                msg.setRead("read");

                msg.setType("text");
                msg.setFromname(currentuser.fname+" "+currentuser.lname);
                msg.setToname(touser.fname+" "+touser.lname);
                allmsg.add(msg);
                msg1=new Messages(mAuth.getCurrentUser().getUid(),userid,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),message.getText().toString());

                msg1.setRead("unread");
                msg1.setType("text");


                msg1.setFromname(currentuser.fname+" "+currentuser.lname);
                msg1.setToname(touser.fname+" "+touser.lname);
                tomsg.add(msg1);
                currentuser.setMessages(allmsg);
                touser.setMessages(tomsg);
                mdata.setValue(currentuser);
                mtouser.setValue(touser);
                Intent intent=new Intent(IndividualProfileActivity.this,ChatActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Log.d("demo","click"+userid);

                intent.putExtra("id",mAuth.getCurrentUser().getUid());

                startActivity(intent);
            }
        });
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i=1;
                Intent pictureIntent = new Intent(Intent.ACTION_PICK);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                Uri data = Uri.parse(pictureDirectoryPath);
                pictureIntent.setDataAndType(data, "image/*");
                startActivityForResult(pictureIntent,20);
            }
        });
    }

    private void setdata() {
        fname.setText(user.fname);
        lname.setText(user.lname);
        Picasso.with(getApplicationContext()).load(pic).into(profilepic);
        gender.setText(user.gender);
    }

    private void assignviews() {
        profilepic= (ImageView) findViewById(R.id.imageView);
        fname= (TextView) findViewById(R.id.editText);
        lname= (TextView) findViewById(R.id.editText1);
        message= (EditText) findViewById(R.id.editText2);
        send= (Button) findViewById(R.id.button_send);
        gender= (TextView) findViewById(R.id.editText_gender);
        attach= (ImageView) findViewById(R.id.imageView_individual);
        msgpic= (ImageView) findViewById(R.id.imageView3);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_signout)
        {
            mAuth.signOut();
            Intent intent=new Intent(IndividualProfileActivity.this,MainActivity.class);

            startActivity(intent);
        }
        if(item.getItemId()==R.id.action_editProfile)
        {

            Intent intent=new Intent(IndividualProfileActivity.this,SignUp.class);
            intent.putExtra("value",user);
            intent.putExtra("userID",mAuth.getCurrentUser().getUid());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 20) {
                imageURI = data.getData();
                Picasso.with(getApplicationContext()).load(data.getAction()).into(msgpic);
                msg=new Messages(mAuth.getCurrentUser().getUid(),userid,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),null);
                msg.setRead("read");

                msg.setType("image");
                msg.setImagecontent(imageURI.toString());
                msg.setFromname(currentuser.fname+" "+currentuser.lname);
                msg.setToname(touser.fname+" "+touser.lname);
                allmsg.add(msg);
                msg1=new Messages(mAuth.getCurrentUser().getUid(),userid,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),null);

                msg1.setRead("unread");
                msg1.setType("image");
                msg1.setImagecontent(imageURI.toString());

                msg1.setFromname(currentuser.fname+" "+currentuser.lname);
                msg1.setToname(touser.fname+" "+touser.lname);
                tomsg.add(msg1);
                currentuser.setMessages(allmsg);
                touser.setMessages(tomsg);
                mdata.setValue(currentuser);
                mtouser.setValue(touser);
                Intent intent=new Intent(IndividualProfileActivity.this,ChatActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Log.d("demo","click"+userid);

                intent.putExtra("id",mAuth.getCurrentUser().getUid());

                startActivity(intent);
            }
        }
    }
}
