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

/**
 * Created by Lax on 11/21/2016.
 */
public class MessageAdapter extends ArrayAdapter<Messages> {
    private DatabaseReference mDatabase,mdata;
    Context mcontext;
    int mresource;
    private FirebaseAuth mAuth;
    ArrayList<Messages> newslist,msglist=new ArrayList<>();
    int i=0;
    ArrayList<String> mread,mname;
    public MessageAdapter(Context context, int resource, ArrayList<Messages> objects,ArrayList<String> read,ArrayList<String> name) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
        newslist = objects;
        mread=read;
    mname=name;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mresource, parent, false);
        }
        convertView.setBackgroundColor(Color.WHITE);
        Log.d("demo","dummmmmy "+mread.get(position));

        i=0;
        msglist=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();
        final String id=mAuth.getCurrentUser().getUid();
        final ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        final String userid;

        final TextView title = (TextView) convertView.findViewById(R.id.editText);
        final TextView price = (TextView) convertView.findViewById(R.id.editText1);
        final TextView date = (TextView) convertView.findViewById(R.id.editText2);
        final Messages app = newslist.get(position);
        title.setTextColor(Color.BLACK);
        price.setTextColor(Color.BLACK);
        date.setTextColor(Color.BLACK);
      // title.setText(mname.get(position));
        price.setText(app.getContent());
        date.setText(app.date.substring(0,4)+"/"+app.date.substring(4,6)+"/"+app.date.substring(6,8));

if(id.equals(app.fromuser)) {
   title.setText(app.toname);
    userid=app.touser;
}
        else
{
  title.setText(app.fromname);
    userid=app.fromuser;
}

       /* mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                title.setText(user.getFname()+" "+user.getLname());

                Picasso.with(mcontext).load(user.getProfilepic()).into(image);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        //Picasso.with(mcontext).load(app.getProfilepic()).into(image);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(mcontext,ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Log.d("demo","click"+userid);

                intent.putExtra("id",userid);

                mcontext.startActivity(intent);
            }
        });
        if(mread.get(position).equals("unread"))
        {
            price.setText("unread messages");
            convertView.setBackgroundColor(Color.GREEN);
        }
        notifyDataSetChanged();
        return convertView;
    }
}
