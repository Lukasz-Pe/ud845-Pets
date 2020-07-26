package com.example.android.pets;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.example.android.pets.data.PetContract.*;

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor c) {
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView breed = (TextView) view.findViewById(R.id.breed);
        TextView gender = (TextView) view.findViewById(R.id.gender);
        TextView weight = (TextView) view.findViewById(R.id.weight);

        String nameText = c.getString(c.getColumnIndexOrThrow(PetEntry.COLUMN_PET_NAME));
        String breedText = c.getString(c.getColumnIndexOrThrow(PetEntry.COLUMN_PET_BREED));
        String weightValue = c.getString(c.getColumnIndexOrThrow(PetEntry.COLUMN_PET_WEIGHT));
        String genderText = "Not available";
        switch(c.getInt(c.getColumnIndex(PetEntry.COLUMN_PET_GENDER))){
            case PetEntry.GENDER_UNKNOWN:{
                genderText = context.getResources().getString(R.string.gender_unknown);
                break;
            }
            case PetEntry.GENDER_MALE:{
                genderText = context.getResources().getString(R.string.gender_male);
                break;
            }
            case PetEntry.GENDER_FEMALE:{
                genderText = context.getResources().getString(R.string.gender_female);
                break;
            }
            default:{
                genderText = "Undefined";
            }
        }

        name.setText(nameText);
        breed.setText(breedText);
        gender.setText(genderText);
        weight.setText(weightValue);
    }
}
