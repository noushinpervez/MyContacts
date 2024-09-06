package edu.ewubd.mycontacts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private final Context context;
    private final ArrayList<Contact> contactsArrayList;
    private final LayoutInflater inflater;

    public ContactAdapter(@NonNull Context context, @NonNull ArrayList<Contact> items) {
        super(context, -1, items);
        this.context = context;
        this.contactsArrayList = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.row_contact_list, parent, false);

        Contact e = contactsArrayList.get(position);

        TextView name = rowView.findViewById(R.id.name);
        TextView email = rowView.findViewById(R.id.email);
        TextView homePhone = rowView.findViewById(R.id.homePhone);
        ImageView image = rowView.findViewById(R.id.image);

        name.setText(e.name);
        email.setText(e.email);
        homePhone.setText(e.homePhone);

        if (e.image != null && !e.image.isEmpty()) {
            byte[] bytes = Base64.decode(e.image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            image.setImageBitmap(bitmap);
        } else {
            image.setImageResource(R.drawable.ic_launcher_foreground);
            image.setBackgroundResource(R.drawable.circle);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ContactFormActivity.class);
                i.putExtra("id", e.id);
                context.startActivity(i);
            }
        });

        return rowView;
    }
}