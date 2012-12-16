package it.perl.dada.SynthQuiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MinigameActivity extends Activity {

	private List<Synth> synths;

	private Random rnd;
	private int how_many_questions;
	private int current_question;
	private List<Integer> images_to_question;

	private Minigame game;
	private int category;
	
	private int how_many_wrong;
	private int how_many_right;
	
	private OnClickListener check_answer = new OnClickListener() {			
		@Override
		public void onClick(View v) {
			MinigameActivity.this.checkAnswer(v);
		}
	};	

	private Timer timer;
	private TimerTask timer_task;
	private Runnable next_question = new Runnable() {
	    public void run() {
	    	timer_task.cancel();
	    	timer.purge();
			MinigameActivity parent = MinigameActivity.this;
			if(parent.current_question < 9) {
				parent.current_question++;
				parent.displayQuestion(parent.current_question);
			} else {
				Intent data = new Intent();
				data.putExtra("how_many_wrong", how_many_wrong);
				data.putExtra("how_many_right", how_many_right);
				setResult(RESULT_OK, data);
				parent.finish();
			}
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_minigame);

		ImageView answer_a = (ImageView) findViewById(R.id.answer_a);
		ImageView answer_b = (ImageView) findViewById(R.id.answer_b);
		ImageView answer_c = (ImageView) findViewById(R.id.answer_c);
		ImageView answer_d = (ImageView) findViewById(R.id.answer_d);

		answer_a.setOnClickListener(check_answer);
		answer_b.setOnClickListener(check_answer);
		answer_c.setOnClickListener(check_answer);
		answer_d.setOnClickListener(check_answer);
		
		images_to_question = new ArrayList<Integer>();
		timer = new Timer();
		
		category = getIntent().getIntExtra("category", Minigame.CATEGORY_MIXED);
		
		prepareGame();
		
		current_question = 0;
		displayQuestion(current_question);
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_minigame_production_date,
				menu);
		return true;
	}

	private void prepareGame() {
		
		rnd = new Random();
		
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
		synths = db.getAllSynths();
		db.close();

		how_many_questions = 10;
		how_many_right = 0;
		how_many_wrong = 0;
		
		game = new Minigame(category);
		game.setHowManyQuestions(how_many_questions);
		game.setSynths(synths);
		game.prepare();

	}

	private void displayQuestion(int i) {
		displayQuestion(game.getQuestions().get(i));
	}
	
	private void displayQuestion(MinigameQuestion q) {
		TextView quiz_text = (TextView) findViewById(R.id.quiz_text);
		ImageView answer_a = (ImageView) findViewById(R.id.answer_a);
		ImageView answer_b = (ImageView) findViewById(R.id.answer_b);
		ImageView answer_c = (ImageView) findViewById(R.id.answer_c);
		ImageView answer_d = (ImageView) findViewById(R.id.answer_d);

		images_to_question.clear();
		images_to_question.add(0);
		images_to_question.add(1);
		images_to_question.add(2);
		images_to_question.add(3);

		Collections.shuffle(images_to_question);
		
		setSynthDrawable(answer_a, q.getSynth(images_to_question.get(0)));
		setSynthDrawable(answer_b, q.getSynth(images_to_question.get(1)));
		setSynthDrawable(answer_c, q.getSynth(images_to_question.get(2)));
		setSynthDrawable(answer_d, q.getSynth(images_to_question.get(3)));

		String t1 = q.getTextBefore();
		String t2 = q.getTextBold();
		String t3 = q.getTextAfter();
		SpannableString span = new SpannableString(t1 + t2 + t3);
		span.setSpan(
			new StyleSpan(Typeface.BOLD), 
			t1.length(), t1.length() + t2.length(), 0
		);
		quiz_text.setText(span);

		setTitle(
			"Question " + (current_question+1) + "/" + how_many_questions +
			" [right: " + how_many_right + " / wrong: " + how_many_wrong + "]"
		);
		
	}
	
	private int getCorrectAnswerViewId() {
		if(images_to_question.get(0) == 0) return R.id.answer_a;
		if(images_to_question.get(1) == 0) return R.id.answer_b;
		if(images_to_question.get(2) == 0) return R.id.answer_c;
		if(images_to_question.get(3) == 0) return R.id.answer_d;
		return R.id.answer_a; // WTF?
	}
	
	private void setSynthDrawable(ImageView image, Synth synth) {
		// here we want a drawable that is as big as the overlay
		// with the synth image centered vertically in it
		int r;
		if(getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_XHIGH) {
	        r = this.getResources().getIdentifier(
	        	String.format(
	                "it.perl.dada.SynthQuiz:raw/synth%04d", synth.getId()
	            ),
	            null, null
	        );
		} else {
	        r = this.getResources().getIdentifier(
	        	String.format(
	                "it.perl.dada.SynthQuiz:drawable/synth%04d", synth.getId()
	            ),
	            null, null
	        );
		}		
    	Bitmap overlay = BitmapFactory.decodeResource(
    		getResources(), R.drawable.minigame_overlay
    	);
    	Bitmap synth_image = BitmapFactory.decodeStream(
			getResources().openRawResource(r)
		);
    	Bitmap centered = Bitmap.createBitmap(
    		overlay.getWidth(), overlay.getHeight(), overlay.getConfig()
    	);
        Canvas canvas = new Canvas(centered);
        int y = (overlay.getHeight() - synth_image.getHeight()) / 2;
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(synth_image, 0, y, paint);    	
		image.setImageBitmap(centered);
	}

	private void applyOverlay(int view_id, int overlay_id) {
		ImageView view = (ImageView) findViewById(view_id);
		Drawable drawable = view.getDrawable();
		Bitmap original;
		if(drawable instanceof BitmapDrawable) {
			original = ((BitmapDrawable)drawable).getBitmap();
		} else {
			original = Bitmap.createBitmap(
				drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				Config.ARGB_8888
			);
		}
    	Bitmap overlay = BitmapFactory.decodeResource(getResources(), overlay_id);
    	Bitmap overlayed = Bitmap.createBitmap(
    		original.getWidth(), original.getHeight(), original.getConfig()
    	);
        Canvas canvas = new Canvas(overlayed);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(original, 0, 0, paint);
        canvas.drawBitmap(overlay, 0, 0, paint);
    	view.setImageBitmap(overlayed);		
	}
	
	private void checkAnswer(View v) {
		int view_id = v.getId();
		boolean is_right = false;
		
		switch(view_id) {
		case R.id.answer_a: 			
			is_right = images_to_question.get(0) == 0;
			break;
		case R.id.answer_b:
			is_right = images_to_question.get(1) == 0;
			break;
		case R.id.answer_c: 			
			is_right = images_to_question.get(2) == 0;
			break;
		case R.id.answer_d:
			is_right = images_to_question.get(3) == 0;
			break;		
		default:
			Log.e("SynthQuiz", "got bogus view id: " + view_id);
		}
		if(is_right) {
			how_many_right++;
			applyOverlay(view_id, R.drawable.minigame_overlay_right);
		} else {
			how_many_wrong++;
			applyOverlay(view_id, R.drawable.minigame_overlay_wrong);
			applyOverlay(getCorrectAnswerViewId(), R.drawable.minigame_overlay_right);
		}
		
		// Toast.makeText(this, is_right ? "RIGHT" : "WRONG", Toast.LENGTH_SHORT).show();
		timer_task = new TimerTask() {
			@Override
			public void run() {
				MinigameActivity.this.runOnUiThread(next_question);				
			}
		};
		timer.schedule(timer_task, 3000);
	}
	
	public void onBackPressed() {
		int questions_left = how_many_questions - current_question;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(
    		"You still have " + questions_left + 
    		" question" + (questions_left > 1 ? "s" : "") +
    		" left...  do you want to abandon?"
    	);
    	builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int button) {
    			dialog.dismiss();
    		}
    	});
    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int button) {
				setResult(RESULT_CANCELED); 
    			MinigameActivity.this.finish();
    		}
    	});
    	AlertDialog dialog = builder.create();
    	dialog.show();
	}
}
