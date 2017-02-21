package com.example.karthikeyantkr.firebasestart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lax on 11/22/2016.
 */
public class ChatAdapter extends ArrayAdapter<Messages> {
    private DatabaseReference mDatabase,mData;
    Context mcontext;
    int mresource;
    private FirebaseAuth mAuth;
    ArrayList<Messages> newslist;
User user;
    public ChatAdapter(Context context, int resource, ArrayList<Messages> objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
        newslist = objects;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mresource, parent, false);
        }
        mAuth=FirebaseAuth.getInstance();
        final String id=mAuth.getCurrentUser().getUid();
        final String otherid;

        final String userid="";

        final TextView title = (TextView) convertView.findViewById(R.id.textView_content);

        final TextView price = (TextView) convertView.findViewById(R.id.textView_name);
        final TextView date = (TextView) convertView.findViewById(R.id.textView_date);
        final Messages app = newslist.get(position);
        title.setTextColor(Color.BLACK);
        price.setTextColor(Color.BLACK);
        date.setTextColor(Color.BLACK);
        Log.d("demo","demooooo "+app.type);
        ImageView attached=(ImageView) convertView.findViewById(R.id.imageView_attached);
        if(app.type!=null&&app.type.equals("image"))
        {
attached.setVisibility(View.VISIBLE);
        Picasso.with(mcontext).load(app.imagecontent).into(attached);
            title.setVisibility(View.INVISIBLE);
        }
        else {
            attached.setVisibility(View.INVISIBLE);

            title.setVisibility(View.VISIBLE);
            title.setText(app.content);
        }
            date.setText(app.date.substring(0,4)+"/"+app.date.substring(4,6)+"/"+app.date.substring(6,8)+" "+app.date.substring(9,11)+":"+app.date.substring(11,13)+":"+app.date.substring(13,15));
        if(id.equals(app.fromuser)) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(app.touser);
otherid=app.touser;
        }
        else
        {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(app.fromuser);
otherid=app.fromuser;
        }
        Log.d("demo","inside adapter "+app.read);
        convertView.setBackgroundColor(Color.WHITE);
        if(app.read.equals("unread"))
        {
            Log.d("demo","clicked");

            convertView.setBackgroundColor(Color.BLUE);
        }
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(id.equals(app.fromuser)) {
                    price.setText("me");

                }
                else
                {
                    price.setText(user.fname+" "+user.lname);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mData=FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 user=dataSnapshot.getValue(User.class);
                ArrayList<Messages> msgs=new ArrayList<>();
                ArrayList<Messages> newmsgs=new ArrayList<>();
                for(Messages m:user.getMessages()) {
                    if(m.fromuser.equals(id)||m.touser.equals(id))
                        msgs.add(m);
                }
                for(Messages m :msgs)
                {
                    if(m.fromuser.equals(id)||m.touser.equals(id)) {
                        m.setRead("read");
                    }
                    newmsgs.add(m);
                }
                user.setMessages(newmsgs);
                mData=FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
                mData.setValue(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        convertView.findViewById(R.id.imageView_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newslist.remove(position);
                ArrayList<Messages> restmsg=new ArrayList<Messages>();
                for(Messages m :user.getMessages())
                {
                    if(!(m.fromuser.equals(otherid))&&!(m.touser.equals(otherid)))
                    {
                        restmsg.add(m);
                    }
                }
                for(Messages m:newslist)
                {
                    restmsg.add(m);
                }
                user.setMessages(restmsg);

                mData.setValue(user);
                notifyDataSetChanged();
            }
        });
        //Picasso.with(mcontext).load(app.getProfilepic()).into(image);
        notifyDataSetChanged();

        return convertView;
    }
}
