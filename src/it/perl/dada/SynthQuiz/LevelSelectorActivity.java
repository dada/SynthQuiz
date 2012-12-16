package it.perl.dada.SynthQuiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LevelSelectorActivity extends Activity {

	private List<String> items;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level_selector);

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

		UserDatabase user = new UserDatabase(getApplicationContext());
		try {
			user.getWritableDatabase();
		} catch (SQLException e) {
			throw new Error("Unable to open database");
		}
		
		int last_percentage = -1;
		List<Integer> levels = db.getLevels();
		items = new ArrayList<String>();
		for(int i = 0; i < levels.size(); i++) {
			int l = levels.get(i);
			Log.d("SynthQuiz", "got level " + l);
			List<Synth> level = db.getLevel(l);
			int percentage = user.getLevelCompletionPercentage("default", level);
			int score = user.getLevelScore("default", level);
			int locked = 0;
			if(last_percentage != -1 && last_percentage < 50) {
				locked = 1;
			}
			items.add(l + "-" + percentage + "-" + score + "-" + locked);
			Log.d("SynthQuiz", "onCreate: items[" + i + "]=" + items.get(i));
			last_percentage = percentage;
		}
		
		items.add("0-0-0-0"); // more levels
		
		db.close();			
		user.close();	
				
		GridView grid = (GridView) findViewById(R.id.levels);
		grid.setAdapter(new LevelThumbAdapter(this, items));
		
	    grid.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	GridView grid = (GridView) parent;
	        	String item = (String) items.get(position);
	        	Log.d("SynthQuiz", "item from levels grid: " + item);
	            String[] l = item.split("-");
	            int level = Integer.parseInt(l[0]);
	        	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.LevelActivity");
            	intent.putExtra("level number", level);
            	startActivity(intent);
	        }
	    });
		
	    /*
		
		TextView percent_done = (TextView) findViewById(R.id.percent_done);
		percent_done.setText(Integer.toString(percentage) + "%");
		
		ImageView knob = (ImageView) findViewById(R.id.knob);
		if(percentage < 0) percentage = 0;
		if(percentage > 99) percentage = 99;
        int r = getResources().getIdentifier(
        	String.format(
                "it.perl.dada.SynthQuiz:drawable/knob%03d", percentage
            ),
            null, null
        );
		knob.setImageResource(r);
		
        Button level1= (Button) findViewById(R.id.level1);     
        level1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.LevelActivity");
            	intent.putExtra("level number", 1);
            	startActivity(intent);            	
            }
        });        
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_level_selector, menu);
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
		if(items != null) {
			refreshUserData();		
			GridView grid = (GridView) findViewById(R.id.levels);
			grid.invalidateViews();
		}
	}

	
	private void refreshUserData() {
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

		UserDatabase user = new UserDatabase(getApplicationContext());
		try {
			user.getWritableDatabase();
		} catch (SQLException e) {
			throw new Error("Unable to open database");
		}
		
		int last_percentage = -1;
		for(int i = 0; i < items.size(); i++) {
	        String[] l = items.get(i).split("-");
	        int level_number = Integer.parseInt(l[0]);
			
			List<Synth> level = db.getLevel(level_number);
			int percentage = user.getLevelCompletionPercentage("default", level);
			int level_score = user.getLevelScore("default", level);
			int locked = 0;
			if(last_percentage != -1 && last_percentage < 50) {
				locked = 1;
			}
			items.set(i, level_number + "-" + percentage + "-" + level_score + "-" + locked);
			Log.d("SynthQuiz", "refreshUserData: items[" + i + "]=" + items.get(i));
			last_percentage = percentage;
		}

		db.close();			
		user.close();	
	}
	
}
