package com.experiment.sightwords.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.experiment.sightwords.util.Randomizer;

/**
 * Created by amciver on 2/8/15.
 */
public class SightWordsSQLiteHelper extends SQLiteOpenHelper {

    private static final String TABLE_WORDS = "words";
    private static final String KEY_ID = "id";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_WORD = "word";
    private static final String KEY_LAST_USED = "last_used";

    private static final String[] COLUMNS = {KEY_ID,KEY_LEVEL,KEY_WORD, KEY_LAST_USED};

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SightWordsDB";

    public SightWordsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORDS_TABLE = "CREATE TABLE IF NOT EXISTS words ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "level TEXT, "+
                "word TEXT, "+
                "last_used TEXT )";
        db.execSQL(CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS words");
        this.onCreate(db);
    }

    public void reset(){
        Log.d("SightWordsSQLiteHelper", "Resetting database");

        SQLiteDatabase db = null;
        try{
            db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS words");
            String CREATE_WORDS_TABLE = "CREATE TABLE words ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "level TEXT, "+
                    "word TEXT, "+
                    "last_used TEXT )";
            db.execSQL(CREATE_WORDS_TABLE);
        }
        catch (Exception e){
            Log.e("SightWordsSQLiteHelper", "Error resetting database" + e);
        }
        finally{
            if(db != null)
                try{db.close();}catch(Exception e){}
        }
    }

    public boolean addWord(String level, String word){
        Log.d("SightWordsSQLiteHelper", "Adding word [" + word + "]");

        SQLiteDatabase db = null;
        try{
            db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_LEVEL, level);
            values.put(KEY_WORD, word);

            db.insert(TABLE_WORDS, null, values); // key/value -> keys = column names/ values = column values
            db.close();

            return true;
        }
        catch (Exception e){
             Log.e("SightWordsSQLiteHelper", "Error adding word [" + word+"]" + e);

            return false;
        }
        finally{
            if(db != null)
                try{db.close();}catch(Exception e){}
        }
    }

    public String getWord(String level){

        Log.d("SightWordsSQLiteHelper", "getWord with level " + level + " called");

        String word = "";

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            String whereClause = KEY_LEVEL + " = ?";
            String[] whereArgs = new String[1];
            whereArgs[0] = level;

            db = this.getReadableDatabase();
            c = db.query(TABLE_WORDS, null, whereClause, whereArgs, null, null, null);

            if(c != null)  {
                int count = c.getCount();
                int index = Randomizer.getRandomNumber(count);

                Log.d("SightWordsSQLiteHelper", "Random index being used is [" + index + "] out of a total possible count of [" + count + "]");

                //move the curosr to the position of our indexing
                c.moveToPosition(index);
                word = c.getString(c.getColumnIndex(KEY_WORD));

                Log.d("SightWordsSQLiteHelper", "Word being set as " + word + " ");
            }
        }
        catch (Exception e){
            Log.e("SightWordsSQLiteHelper", "Error getting word for level [" + level+"]" + e);
        }
        finally{
            if(c != null)
                try{c.close();}catch(Exception e){}
            if(db != null)
                try{db.close();}catch(Exception e){}
        }

        return word;
    }
}
