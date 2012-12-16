package it.perl.dada.SynthQuiz;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    
	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/it.perl.dada.SynthQuiz/databases/";
 
	private static String DB_NAME = "synths.sqlite";
	
	private SQLiteDatabase db;
 
	private final Context context;
 
	public Database(Context c) {
		super(c, DB_NAME, null, 1);
		this.context = c;
	}	
 
	public void createDataBase() throws IOException {
		// TODO: perche copiare? leggi direttamente da assets...
		// if(!checkDataBase()) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		// }
	}
 
	private boolean checkDataBase() {
		SQLiteDatabase check = null;
		try{
			String db_path = DB_PATH + DB_NAME;
			check = SQLiteDatabase.openDatabase(
				db_path, 
				null, 
				SQLiteDatabase.OPEN_READONLY
			);
		} catch(SQLiteException e) { }
 
		if(check != null) {
			check.close();
		}
 
		return check != null ? true : false;
	}
 
	private void copyDataBase() throws IOException {
		InputStream input = context.getAssets().open(DB_NAME); 
		String output_path = DB_PATH + DB_NAME;
 
		OutputStream output = new FileOutputStream(output_path);
 
		byte[] buffer = new byte[1024];
		int length;
		while ((length = input.read(buffer))>0){
			output.write(buffer, 0, length);
		}
 
		output.flush();
		output.close();
		input.close();
	}
 
	public void openDataBase() throws SQLException {
 		String myPath = DB_PATH + DB_NAME; 		
		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}
 
	@Override
	public synchronized void close() {
		if(db != null)
			db.close();
		super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) { }
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
 

	public List<Synth> getLevel(int level) {
		List<Synth> l = new ArrayList<Synth>();
		Cursor c = db.rawQuery(
			"SELECT q._id, " +
			"m.maker, m.re, m.difficulty, " +
			"s.model, s.re, s.difficulty, " +
			"s.link_vse, s.link_wikipedia, s.link_other " +
			"FROM quizzes q " +
			"JOIN synths s ON q.synth_id=s._id " +
			"JOIN makers m ON s.maker_id=m._id " +
			"WHERE q.level=" + level + " ORDER BY q._id",
			null
		);
		while (c.moveToNext()){
			Synth s = new Synth();
			s.setId(c.getInt(0));
			s.setMaker(c.getString(1), c.getString(2), c.getInt(3));
			s.setModel(c.getString(4), c.getString(5), c.getInt(6));
			s.setLinkVse(c.getString(7));
			s.setLinkWikipedia(c.getString(8));
			s.setLinkOther(c.getString(9));
			l.add(s);
		}
		return l;
	}

	public List<Integer> getLevels() {
		List<Integer> l = new ArrayList<Integer>();
		Cursor c = db.rawQuery(
			"SELECT DISTINCT level FROM quizzes ORDER BY level",
			null
		);
		while (c.moveToNext()){
			l.add(c.getInt(0));
		}
		return l;
	}	
 
	public List<Synth> getAllSynths() {
		List<Synth> l = new ArrayList<Synth>();
		Cursor c = db.rawQuery(
			"SELECT q._id, " +
			"m.maker, m.re, m.difficulty, " +
			"s.model, s.re, s.difficulty, " +
			"s.year_produced_min, s.year_produced_max, " +
			"s.polyphony, s.characteristics, " +
			"s.link_vse, s.link_wikipedia, s.link_other " +
			"FROM quizzes q " +
			"JOIN synths s ON q.synth_id=s._id " +
			"JOIN makers m ON s.maker_id=m._id",
			null
		);
		while (c.moveToNext()){
			Synth s = new Synth();
			s.setId(c.getInt(0));
			s.setMaker(c.getString(1), c.getString(2), c.getInt(3));
			s.setModel(c.getString(4), c.getString(5), c.getInt(6));
			s.setProductionDates(c.getInt(7), c.getInt(8));
			s.setPolyphony(c.getInt(9));
			s.setCharacteristics(c.getString(10));			
			s.setLinkVse(c.getString(11));
			s.setLinkWikipedia(c.getString(12));
			s.setLinkOther(c.getString(13));
			l.add(s);
		}
		return l;
	}
	
}