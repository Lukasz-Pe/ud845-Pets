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
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if(name==null){
            throw new IllegalArgumentException("Pet requires a name!");
        }
        if(gender==null||!PetContract.PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Gender out of allowed values!");
        }
        if(weight<0||weight==null){
            throw new IllegalArgumentException("Weight has to be greater than 0");
        }
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
        if(contentValues.size()==0){
            return 0;
        }
        switch(sUriMatcher.match(uri)){
            case PETS:{
                return updatePet(uri, contentValues, selection, selectionArgs);
            }
            case PET_ID:{
                selection= PetContract.PetEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            }
            default:{
                throw new IllegalArgumentException("Unable to update unknown URI: " + uri);
            }
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)){
            String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if(name==null){
                throw new IllegalArgumentException("Pet requires a name!");
            }
        }
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)){
            Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
            if(gender==null||!PetContract.PetEntry.isValidGender(gender)){
                throw new IllegalArgumentException("Gender out of allowed values!");
            }
        }
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if(weight<0||weight==null){
                throw new IllegalArgumentException("Weight has to be greater than 0");
            }
        }
        SQLiteDatabase db = petdb.getWritableDatabase();

        return db.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = petdb.getWritableDatabase();

    switch (sUriMatcher.match(uri)) {
        case PETS:
            // Delete all rows that match the selection and selection args
            return db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
        case PET_ID:
             // Delete a single row given by the ID in the URI
            selection = PetContract.PetEntry._ID + "=?";
            selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            return db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
        default:
            throw new IllegalArgumentException("Deletion is not supported for " + uri);
    }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:{
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            }
            case PET_ID:{
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
            }
        }
    }
}
