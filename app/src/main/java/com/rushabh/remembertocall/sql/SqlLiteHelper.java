package com.rushabh.remembertocall.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rushabh.remembertocall.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rushabh on 18/12/15.
 */
public class SqlLiteHelper extends SQLiteOpenHelper{

    public static final String TABLE = "contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_LAST_CALL_DATE = "last_call_date";
    public static final String COLUMN_LAST_CALL_DURATION = "last_call_duration";

    private static final String DATABASE_NAME = "Contact.db";
    private static final int DATABASE_VERSION = 2;

    public SqlLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table IF NOT EXISTS "
            + TABLE + "("
            + COLUMN_ID + " integer primary key , "
            + COLUMN_DISPLAY_NAME + " text not null, "
            + COLUMN_LAST_CALL_DATE + " integer not null,"
            + COLUMN_LAST_CALL_DURATION + " integer not null"
            + ");";



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // Adding new contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, contact.getID()); // Contact id
        values.put(COLUMN_DISPLAY_NAME, contact.getDisplayName()); // Contact name
        values.put(COLUMN_LAST_CALL_DATE, contact.getDaySinceLastCall()); // Contact last call date
        values.put(COLUMN_LAST_CALL_DURATION, contact.getLastCallDuration()); // Contact last call duration

        // Inserting Row
        db.insert(TABLE, null, values);
        //db.close(); // Closing database connection
    }

    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setDisplayName(cursor.getString(1));
                contact.setLastCallDuration(cursor.getInt(3));
                contact.setDaySinceLastCall(cursor.getLong(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, contact.getID()); // Contact id
        values.put(COLUMN_DISPLAY_NAME, contact.getDisplayName()); // Contact name
        values.put(COLUMN_LAST_CALL_DATE, contact.getDaySinceLastCall()); // Contact last call date
        values.put(COLUMN_LAST_CALL_DURATION, contact.getLastCallDuration()); // Contact last call duration

        // updating row
        return db.update(TABLE, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, COLUMN_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        //db.close();
    }
}
