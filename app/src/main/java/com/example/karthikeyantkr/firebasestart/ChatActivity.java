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
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class ChatActivity extends AppCompatActivity {
String id;


    User user,currentuser,touser;
    ListView chats;
    EditText content;
    Button send;
    Messages msg,msg1;
    private DatabaseReference mDatabase,mdata,mtouser;
    private FirebaseAuth mAuth;
    ArrayList<Messages> dummy=new ArrayList<>();
    ImageView attach;
    Uri imageURI;
    ArrayList<Messages> tomsg=new ArrayList<>();
    ArrayList<Messages> allmsg=new ArrayList<Messages>();
    String userId,url;
    FirebaseStorage storage;
    StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Chat");
       id= getIntent().getExtras().getString("id");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://homework7-e26ed.appspot.com");
        content= (EditText) findViewById(R.id.editText_msg);
        send= (Button) findViewById(R.id.button_send);
        attach= (ImageView) findViewById(R.id.imageView_attach);
        chats= (ListView) findViewById(R.id.listView_chat);
        mAuth = FirebaseAuth.getInstance();


        mdata=FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
        mtouser=FirebaseDatabase.getInstance().getReference().child("user").child(id);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);

dummy=user.getMessages();
                for(Messages m :user.getMessages() )
                {
                    if(m.fromuser.equals(id)||m.touser.equals(id))
                    {
                        allmsg.add(m);
                    }
                }
                ChatAdapter adapter = new ChatAdapter(getApplicationContext(), R.layout.chatlistview,allmsg);
                chats.setAdapter(adapter);
                adapter.setNotifyOnChange(true);
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
        msg=new Messages(mAuth.getCurrentUser().getUid(),id,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),content.getText().toString());
        msg.setRead("read");
        msg.setType("text");
        msg.setFromname(user.fname+" "+user.lname);
        msg.setToname(touser.fname+" "+touser.lname);
        allmsg.add(msg);
        dummy.add(msg);
        msg1=new Messages(mAuth.getCurrentUser().getUid(),id,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),content.getText().toString());

        msg1.setRead("unread");
        msg1.setType("text");
        msg1.setFromname(user.fname+" "+user.lname);
        msg1.setToname(touser.fname+" "+touser.lname);
        tomsg.add(msg1);
        user.setMessages(dummy);
        touser.setMessages(tomsg);
        mdata.setValue(user);
        mtouser.setValue(touser);
        Log.d("demo","test3 "+allmsg);
        Collections.sort(allmsg, new Comparator<Messages>() {
            @Override
            public int compare(Messages fruit2, Messages fruit1)
            {
                return Double.compare(Double.parseDouble(fruit2.date.substring(0,8)+fruit2.date.substring(9,15)),Double.parseDouble(fruit1.date.substring(0,8)+fruit1.date.substring(9,15)));

            }
        });

        ChatAdapter adapter = new ChatAdapter(getApplicationContext(), R.layout.chatlistview,allmsg);
        chats.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

    }
});
        attach.setOnClickListener(new View.OnClickListener() {
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
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 20) {
                imageURI = data.getData();
                userId=mAuth.getCurrentUser().getUid();
                String path = "images/" + userId +new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".jpeg";
                StorageReference imageRef = storageRef.child(path);
                UploadTask upload = imageRef.putFile(imageURI);
                upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri download = taskSnapshot.getMetadata().getDownloadUrl();
                        url = download.toString();

                        msg=new Messages(mAuth.getCurrentUser().getUid(),id,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),null);
                        msg.setRead("read");
                        msg.setImagecontent(url);
                        msg.setType("image");

                        msg.setFromname(user.fname+" "+user.lname);
                        msg.setToname(touser.fname+" "+touser.lname);
                        allmsg.add(msg);
                        msg1=new Messages(mAuth.getCurrentUser().getUid(),id,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),null);

                        msg1.setRead("unread");
                        msg1.setImagecontent(url);
                        msg1.setType("image");

                        msg1.setFromname(user.fname+" "+user.lname);
                        msg1.setToname(touser.fname+" "+touser.lname);
                        tomsg.add(msg1);
                        user.setMessages(allmsg);
                        touser.setMessages(tomsg);
                        mdata.setValue(user);
                        mtouser.setValue(touser);
                        Log.d("demo","test3 "+allmsg);
                        Collections.sort(allmsg, new Comparator<Messages>() {
                            @Override
                            public int compare(Messages fruit2, Messages fruit1)
                            {
                                return Double.compare(Double.parseDouble(fruit2.date.substring(0,8)+fruit2.date.substring(9,15)),Double.parseDouble(fruit1.date.substring(0,8)+fruit1.date.substring(9,15)));

                            }
                        });for(Messages m:allmsg) {
                            Log.d("demo", "test4 " + m.toString());
                        }
                            ChatAdapter adapter = new ChatAdapter(getApplicationContext(), R.layout.chatlistview,allmsg);
                        chats.setAdapter(adapter);
                        adapter.setNotifyOnChange(true);
                    }
                });
                //Picasso.with(getApplicationContext()).load(data.getAction()).into(profile_image);
            }
        }
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
            Intent intent=new Intent(ChatActivity.this,MainActivity.class);

            startActivity(intent);
        }
        if(item.getItemId()==R.id.action_editProfile)
        {

            Intent intent=new Intent(ChatActivity.this,SignUp.class);
            intent.putExtra("value",user);
            intent.putExtra("userID",mAuth.getCurrentUser().getUid());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }
}
