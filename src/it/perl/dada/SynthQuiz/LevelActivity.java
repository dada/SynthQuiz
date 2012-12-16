package it.perl.dada.SynthQuiz;

import java.io.IOException;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class LevelActivity extends Activity {

	private int level;
	private List<Synth> synths;
	private int score;
	private int last_percent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level);

		last_percent = -1;
		level = getIntent().getIntExtra("level number", 1);
		
		Database db = new Database(getApplicationContext());
		try {
			db.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {
			db.openDataBase();
		} catch(SQLException sqle) {
			throw sqle;
		}
		synths = db.getLevel(level);
		db.close();
		Log.d("SynthQuiz", "db.getLevel returned " + synths.size() + " rows");
		
		getUserData();
		
		GridView grid = (GridView) findViewById(R.id.quizzes);
		grid.setAdapter(new SynthThumbAdapter(this, synths));
		
	    grid.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	GridView grid = (GridView) parent;
	        	SynthThumbAdapter synths = (SynthThumbAdapter) grid.getAdapter();
	        	Synth s = (Synth) synths.getItem(position);
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.GuessActivity");
            	intent.putExtra("synth", s);
            	startActivity(intent);
	        }
	    });

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_level, menu);
		return true;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		getUserData();		
		GridView grid = (GridView) findViewById(R.id.quizzes);
		grid.invalidateViews();

		int guessed = 0;
		int total = synths.size() * 2;
		int percent = 0;
		if(total > 0) {
			for(Synth s: synths) {
				if(s.getMakerGuessed()) guessed++;
				if(s.getModelGuessed()) guessed++;			
			}
			percent = guessed * 100 / total;
		}
		setTitle("Level " + level + " [" + percent + "%] Your score: " + score);
		
		if(last_percent != -1 && last_percent < 50 && percent >= 50) {
			Toast.makeText(this, "next level unlocked!", Toast.LENGTH_SHORT).show();
		}
		last_percent = percent;
	}
	
	private void getUserData() {
		UserDatabase user = new UserDatabase(getApplicationContext());
		try {
			user.getWritableDatabase();
		} catch (SQLException e) {
			throw new Error("Unable to open database");
		}
		user.addGuessesToLevel("default", synths);
		score = user.getScore("default");
		user.close();		
	}
	
}
