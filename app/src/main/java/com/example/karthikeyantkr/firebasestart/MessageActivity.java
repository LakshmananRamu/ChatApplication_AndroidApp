package com.example.karthikeyantkr.firebasestart;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
     DatabaseReference mDatabase,mdata;
    User user;
    ArrayList<Messages> msgs=new ArrayList<>();
    String id;
    ArrayList<String> read=new ArrayList<>();
    ArrayList<String> users=new ArrayList<String>();

    ArrayList<User> allusers=new ArrayList<>();
    ListView msglist;
     ArrayList<String> name=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Inbox");
        mAuth= FirebaseAuth.getInstance();
        id=mAuth.getCurrentUser().getUid();
        final ArrayList<Messages> usermsgs=new ArrayList<>();
        Log.d("demo","testtt "+id);
        msglist= (ListView) findViewById(R.id.listView_message);

     mDatabase=FirebaseDatabase.getInstance().getReference().child("user").child(id);
        mdata=FirebaseDatabase.getInstance().getReference().child("user");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            user=dataSnapshot.getValue(User.class);
            msgs=user.getMessages();
            Collections.sort(msgs, new Comparator<Messages>() {
                @Override
                public int compare(Messages fruit2, Messages fruit1)
                {
                    return Double.compare(Double.parseDouble(fruit1.date.substring(0,8)+fruit1.date.substring(9,15)),Double.parseDouble(fruit2.date.substring(0,8)+fruit2.date.substring(9,15)));

                }
            });
            Log.d("demo","msgs "+msgs.toString());

             for(Messages msg:msgs)
            {
                if(id.equals(msg.fromuser))
                {
                    if(!(users.contains(msg.touser))) {
                        users.add(msg.touser);
                        usermsgs.add(msg);

                    }
                    }
                else
                {
                    if(!(users.contains(msg.fromuser))) {
                        users.add(msg.fromuser);
                        usermsgs.add(msg);

                    }
                    }
            }
            for(Messages m :usermsgs)
            {
                if(m.getRead().equals("unread"))
                {
                    read.add("unread");
                }
                else
                {
                    read.add("read");
                }
            }

           // adapter.setNotifyOnChange(true);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });








        mdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    allusers.add(snapshot.getValue(User.class));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

Log.d("demo","tesssssst"+usermsgs.toString()+" "+allusers.toString());
for(Messages m:usermsgs)
{
    for(User u:allusers)
    {
        if(!(m.fromuser.equals(mAuth.getCurrentUser().getUid())))
        {
         if(m.touser.equals(u.getUid()))
         {
             name.add(u.fname+" "+u.lname);
         }
        }
        else
        {
            if(m.fromuser.equals(u.getUid()))
            {
                name.add(u.fname+" "+u.lname);
            }
        }
    }
}


        MessageAdapter adapter = new MessageAdapter(getApplicationContext(), R.layout.messagelistview,usermsgs,read,name);
        msglist.setAdapter(adapter);

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
            Intent intent=new Intent(MessageActivity.this,MainActivity.class);

            startActivity(intent);
        }
        if(item.getItemId()==R.id.action_editProfile)
        {

            Intent intent=new Intent(MessageActivity.this,SignUp.class);
            intent.putExtra("value",user);
            intent.putExtra("userID",mAuth.getCurrentUser().getUid());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }
}
