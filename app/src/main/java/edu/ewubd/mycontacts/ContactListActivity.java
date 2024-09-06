package edu.ewubd.mycontacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    SharedPreferences sp;
    private ArrayList<Contact> contacts;
    private ListView lvContactList;
    private ContactAdapter csAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        sp = getSharedPreferences("user_info", MODE_PRIVATE);
        boolean remPass = sp.getBoolean("REM_PASS", false);

        lvContactList = findViewById(R.id.lvContactList);

        contacts = new ArrayList<>();
        csAdapter = new ContactAdapter(this, contacts);
        lvContactList.setAdapter(csAdapter);

        loadContact();

        findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decideNavigation(remPass);
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactListActivity.this, ContactFormActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContact();
    }

    private void loadContact() {
        String q = "SELECT * FROM contacts;";
        ContactDB db = new ContactDB(this);
        Cursor cur = db.selectContact(q);
        contacts.clear();

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(0);
                String name = cur.getString(1);
                String email = cur.getString(2);
                String homePhone = cur.getString(3);
                String officePhone = cur.getString(4);
                String image = cur.getString(5);

                Contact cs = new Contact(id, name, email, homePhone, officePhone, image);
                contacts.add(cs);
            }

            csAdapter.notifyDataSetInvalidated();
            csAdapter.notifyDataSetChanged();
        }
    }

    private void decideNavigation(boolean remPass) {
        if (!remPass) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setTitle("Log out");
            builder.setMessage("You will be logged out. Continue?");

            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor e = sp.edit();
                    e.remove("LOGGED_IN");
                    e.apply();
                    finish();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            finish();
        }
    }

    public void onDeleteButtonClick(View view) {
        int position = lvContactList.getPositionForView(view);
        deleteContact(position);
    }

    private void deleteContact(int position) {
        Contact deleteContact = contacts.get(position);
        String idToDelete = deleteContact.getId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("Delete Contact");
        builder.setMessage("Are you sure you want to delete this contact?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContactDB db = new ContactDB(ContactListActivity.this);
                db.deleteContact(idToDelete);

                contacts.remove(position);
                csAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}