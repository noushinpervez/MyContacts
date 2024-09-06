package edu.ewubd.mycontacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDB extends SQLiteOpenHelper {

    public ContactDB(Context context) {
        super(context, "ContactDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");

        String sql = "CREATE TABLE contacts  (" + "id TEXT PRIMARY KEY," + "name TEXT," + "email TEXT," + "homePhone TEXT," + "officePhone TEXT," + "image TEXT" + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Write code to modify database schema here");
    }

    public void insertContact(String id, String name, String email, String homePhone, String officePhone, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();

        cols.put("id", id);
        cols.put("name", name);
        cols.put("email", email);
        cols.put("homePhone", homePhone);
        cols.put("officePhone", officePhone);
        cols.put("image", image);

        db.insert("contacts", null, cols);
        db.close();
    }

    public void updateContact(String id, String name, String email, String homePhone, String officePhone, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();

        cols.put("id", id);
        cols.put("name", name);
        cols.put("email", email);
        cols.put("homePhone", homePhone);
        cols.put("officePhone", officePhone);
        cols.put("image", image);

        db.update("contacts", cols, "id=?", new String[]{id});
        db.close();
    }

    public void deleteContact(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("contacts", "id=?", new String[]{id});
        db.close();
    }

    public Cursor selectContact(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = null;

        try {
            cur = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cur;
    }

    public Contact getContactById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Contact contacts = null;

        Cursor cursor = db.query("contacts", null, "id = ?", new String[]{id}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String homePhone = cursor.getString(cursor.getColumnIndex("homePhone"));
            String officePhone = cursor.getString(cursor.getColumnIndex("officePhone"));
            String image = cursor.getString(cursor.getColumnIndex("image"));

            contacts = new Contact(id, name, email, homePhone, officePhone, image);
        }

        if (cursor != null) {
            cursor.close();
        }

        return contacts;
    }
}