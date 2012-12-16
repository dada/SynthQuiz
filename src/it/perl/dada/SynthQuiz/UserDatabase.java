package it.perl.dada.SynthQuiz;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDatabase extends SQLiteOpenHelper {
    
	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/it.perl.dada.SynthQuiz/databases/";
 
	private static String DB_NAME = "user.sqlite";
	
	private static String DB_CREATE_PROFILES = 
		"CREATE TABLE profiles (" +
	    "    _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    "    name TEXT NOT NULL," +
	    "    score INTEGER" +
	    "); ";
	private static String DB_CREATE_GUESSES = 
		"CREATE TABLE guesses (" +
		"    _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"    profile TEXT, " +
		"    quiz_id INTEGER, " +
		"    maker_guessed INTEGER, " +
		"    maker_tries INTEGER, " +
		"    model_guessed INTEGER, " +
		"    model_tries INTEGER" + 
		"); "
	;
	
	private SQLiteDatabase db;
 
	private final Context context;
 
	public UserDatabase(Context c) {
		super(c, DB_NAME, null, 1);
		this.context = c;
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_PROFILES);
		ContentValues values = new ContentValues();
		values.put("name", "default");
		values.put("score", 0);
		db.insert("profiles", null, values);
		db.execSQL(DB_CREATE_GUESSES);
	}
 	
	@Override
	public SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase sqlite = super.getWritableDatabase();
		db = sqlite;
		return sqlite;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
 
	public int getLevelCompletionPercentage(String profile, List<Synth> level) {
		int guessed = 0;
		int total = level.size() * 2;
		if(total == 0) { return 0; }
		StringBuilder placeholders = new StringBuilder();
		String[] ids = new String[level.size()]; 
		for(int i = 0; i < level.size(); i++) {
			if(placeholders.length() > 0) placeholders.append(", ");
			placeholders.append("?");
			ids[i] = Integer.toString(level.get(i).getId());
		}
		String query = "SELECT maker_guessed, model_guessed FROM guesses WHERE quiz_id IN (" + placeholders.toString() + ")";

		Cursor c = db.rawQuery(query, ids);
		while(c.moveToNext()) {			
			int maker_guessed = c.getInt(0);
			int model_guessed = c.getInt(1);
			if(maker_guessed > 0) guessed++;
			if(model_guessed> 0) guessed++;
		}
		return guessed * 100 / total;
	}

	private Cursor queryLevel(String profile, List<Synth> level) {
		StringBuilder placeholders = new StringBuilder();
		String[] params = new String[level.size()+1];
		params[0] = profile;
		for(int i = 0; i < level.size(); i++) {
			if(placeholders.length() > 0) placeholders.append(", ");
			placeholders.append("?");
			params[i+1] = Integer.toString(level.get(i).getId());
		}
		String query = 
			"SELECT quiz_id, maker_guessed, model_guessed, maker_tries, model_tries " +
			"FROM guesses WHERE profile=? AND quiz_id IN (" + placeholders.toString() + ")";
		return db.rawQuery(query, params);
	}
	
	public void addGuessesToLevel(String profile, List<Synth> level) {	
		Cursor c = queryLevel(profile, level);
		while(c.moveToNext()) {
			int synth_id = c.getInt(0);
			int maker_guessed = c.getInt(1);
			int model_guessed = c.getInt(2);
			for(Synth synth: level) {
				if(synth.getId() == synth_id) {
					synth.setMakerGuessed(maker_guessed > 0 ? true : false);
					synth.setModelGuessed(model_guessed > 0 ? true : false);
					break;
				}			
			}
		}
		c.close();		
	}
	
	public void storeGuess(String profile, Synth synth) {		
		int maker_tries = 0;
		int model_tries = 0;
		int row_id = 0;
		Cursor c = db.rawQuery("SELECT _id, maker_tries, model_tries FROM guesses WHERE quiz_id=" + synth.getId(), null);		
		while(c.moveToNext()) {
			row_id = c.getInt(0);
			maker_tries += c.getInt(1);
			model_tries += c.getInt(2);
		}
		c.close();

		ContentValues values = new ContentValues();
		values.put("quiz_id", synth.getId());
		values.put("profile", profile);
		values.put("maker_guessed", synth.getMakerGuessed() ? 1 : 0);
		values.put("maker_tries", maker_tries+1);		
		values.put("model_guessed", synth.getModelGuessed() ? 1 : 0);
		values.put("model_tries", model_tries+1);

		if(row_id != 0) {
			db.update("guesses", values, "_id=?", new String[]{ Integer.toString(row_id) });
		} else {
			db.insert("guesses", null, values);
		}
		
		if(synth.getMakerGuessed() && synth.getModelGuessed()) {
			// score!
			int score = calculateSynthScore(profile, synth, maker_tries+1, model_tries+1);
			Log.d("SynthQuiz", "SCORE +" + score);
			db.execSQL("UPDATE profiles SET score=score+" + score + " WHERE name='" + profile + "';");
		}		
	}
	
	public int getScore(String profile) {
		int score = 0;
		Cursor c = db.rawQuery("SELECT score FROM profiles WHERE name=?;", new String[]{ profile });
		if(c.moveToFirst()) {
			score = c.getInt(0);
		}
		return score;
	}
 
	public int calculateSynthScore(String profile, Synth synth, int maker_tries, int model_tries) {
		if(synth.getMakerGuessed() && synth.getModelGuessed()) {
			int score_maker = 
				synth.getMakerDifficulty() * (synth.getMakerDifficulty()+1-(maker_tries-1));			
			int score_model =
				synth.getModelDifficulty() * (synth.getModelDifficulty()+1-(model_tries-1));			
			int score = score_maker + score_model;
			if(score < 0) score = 0;
			return score;
		} else {
			return 0;
		}
	}

	public int getSynthScore(String profile, Synth synth) {
		List<Synth> l = new ArrayList<Synth>();
		l.add(synth);
		return getLevelScore(profile, l);
	}
	
	public int getLevelScore(String profile, List<Synth> level) {
		int score = 0;
		Cursor c = queryLevel(profile, level);
		while(c.moveToNext()) {
			int synth_id = c.getInt(0);
			int maker_guessed = c.getInt(1);
			int model_guessed = c.getInt(2);
			int maker_tries = c.getInt(3);
			int model_tries = c.getInt(4);
			for(Synth synth: level) {
				if(synth.getId() == synth_id) {
					synth.setMakerGuessed(maker_guessed > 0 ? true : false);
					synth.setModelGuessed(model_guessed > 0 ? true : false);
					score += calculateSynthScore(profile, synth, maker_tries, model_tries);					
					break;
				}
			}
		}
		c.close();
		return score;
	}
	
}