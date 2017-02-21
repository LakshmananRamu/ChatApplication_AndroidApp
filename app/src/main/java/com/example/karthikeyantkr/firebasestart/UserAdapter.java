package com.example.karthikeyantkr.firebasestart;

/**
 * Created by Lax on 11/18/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lax on 10/24/2016.
 */
public class UserAdapter extends ArrayAdapter<User> {
    Context mcontext;
    int mresource;
    ArrayList<User> newslist;


    public UserAdapter(Context context, int resource, ArrayList<User> objects) {

        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
        newslist = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mresource, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);

        TextView title = (TextView) convertView.findViewById(R.id.editText);
        TextView price = (TextView) convertView.findViewById(R.id.editText1);
        final User app = newslist.get(position);
        Picasso.with(mcontext).load(app.getProfilepic()).into(image);
        title.setText(app.getFname());
        price.setText(app.getLname());
Log.d("demo","adapeter"+app.getProfilepic()+app.getUid());
        notifyDataSetChanged();
convertView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        Intent intent=new Intent(mcontext,IndividualProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("demo","click"+app.getUid()+app.getProfilepic());
        intent.putExtra("user",app);
        intent.putExtra("id",app.getUid());
        intent.putExtra("pic",app.getProfilepic());
        mcontext.startActivity(intent);
    }
});
        return convertView;
    }
}