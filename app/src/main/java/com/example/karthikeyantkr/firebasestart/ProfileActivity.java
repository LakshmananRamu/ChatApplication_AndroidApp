package com.example.karthikeyantkr.firebasestart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,mdata;
    String userid;
    User user;
    ImageView profile,seemsg;
    TextView fname,lname,gender;
    ArrayList<User> userlist=new ArrayList<User>();
    Button listusers;
    ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Profile");
        assignviews();
        /*if(getIntent().getExtras().getString("sign")!=null)
        {

        }*/

        mAuth = FirebaseAuth.getInstance();
        userid=getIntent().getExtras().getString("userID");
            mDatabase= FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              user=   dataSnapshot.getValue(User.class);
                if(user.profilepic==null)
                {
                    Picasso.with(getApplicationContext()).load(R.drawable.profile).into(profile);
                }
                else {
                    Picasso.with(getApplicationContext()).load(user.profilepic).into(profile);
                }
                    fname.setText(user.fname);
                lname.setText(user.lname);
                gender.setText(user.gender);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mdata=FirebaseDatabase.getInstance().getReference().child("user");
        mdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    userlist.add(snapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAdapter adapter = new UserAdapter(getApplicationContext(), R.layout.profilelistview,userlist);
                listview.setAdapter(adapter);
                adapter.setNotifyOnChange(true);
            }
        });
seemsg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(ProfileActivity.this,MessageActivity.class);
        startActivity(intent);
    }
});
    }

    private void assignviews() {
        profile= (ImageView) findViewById(R.id.imageView);
        fname= (TextView) findViewById(R.id.editText);
        lname= (TextView) findViewById(R.id.editText1);
        listusers= (Button) findViewById(R.id.button_listusers);
        listview= (ListView) findViewById(R.id.listView);
        seemsg= (ImageView) findViewById(R.id.imageView_message);
        gender= (TextView) findViewById(R.id.editText_gender);
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
            Intent intent=new Intent(ProfileActivity.this,MainActivity.class);

            startActivity(intent);
        }
        if(item.getItemId()==R.id.action_editProfile)
        {

            Intent intent=new Intent(ProfileActivity.this,SignUp.class);
intent.putExtra("value",user);
            intent.putExtra("userID",mAuth.getCurrentUser().getUid());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }
}
