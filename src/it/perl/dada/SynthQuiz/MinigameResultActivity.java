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

public class MinigameResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_minigame_result);

		TextView right = (TextView) findViewById(R.id.right);
		TextView wrong = (TextView) findViewById(R.id.wrong);
		TextView judgemental = (TextView) findViewById(R.id.judgemental);		
		ImageView results_background = (ImageView) findViewById(R.id.results_bg);
		
		int how_many_questions = getIntent().getIntExtra("how_many_questions", 10);
		int how_many_wrong = getIntent().getIntExtra("how_many_wrong", 10);
		int how_many_right = getIntent().getIntExtra("how_many_right", 0);
		
		right.setText("Correct answers: " + how_many_right);
		wrong.setText("Wrong answers: " + how_many_wrong);
		
		float ratio =  (float)how_many_right / (float)how_many_questions;
		if(how_many_right == how_many_questions) {
    		judgemental.setText("PERFECT!!!");
    		results_background.setImageResource(R.drawable.perfect);
    	} else {
    		if(ratio > 0.50) {
    			results_background.setImageResource(R.drawable.good);
    			if(ratio > 0.80)
    				judgemental.setText("Well done!");
    			else
    				judgemental.setText("Not bad");
    		} else {
        		results_background.setImageResource(R.drawable.meh);
        		if(ratio > 0.20)
        			judgemental.setText("You can do better");
        		else
        			judgemental.setText("You suck at this :-)");
    		}
    	}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_minigame_production_date,
				menu);
		return true;
	}
}
