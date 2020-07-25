package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.pets.data.PetDbHelper;

public class PetProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetDbHelper petdb;
    private static final int PETS = 100, PET_ID=101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    /**
     * Initialize the provider and the database helper object.
     */

    static{
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PetEntry.TABLE_NAME, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PetEntry.TABLE_NAME + "/#", PET_ID);
    }

    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        petdb=new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = petdb.getReadableDatabase();
        Cursor cursor;

        switch(sUriMatcher.match(uri)){
            case PETS:{
                cursor = database.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            case PET_ID:{
                selection= PetContract.PetEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            default:{
                throw new IllegalArgumentException("Unable to query unknown URI: " + uri);
            }
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (sUriMatcher.match(uri)){
            case PETS:{
                return insertPet(uri, contentValues);
            }
            default:{
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }
    }

    private Uri insertPet(Uri uri, ContentValues values){
        SQLiteDatabase db = petdb.getWritableDatabase();
        long id = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        if(id<0){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
