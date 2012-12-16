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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MinigameSelectorActivity extends Activity {

	public final static int REQUEST_CODE_MINIGAME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_minigame_selector);

		Button production_dates = (Button) findViewById(R.id.production_dates);
		Button analog_vs_digital = (Button) findViewById(R.id.analog_vs_digital);
		Button polyphony = (Button) findViewById(R.id.polyphony);
		Button mixed = (Button) findViewById(R.id.mixed);
		
		production_dates.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.MinigameActivity");
            	intent.putExtra("category", Minigame.CATEGORY_PRODUCTION_DATES);
            	startActivityForResult(intent, REQUEST_CODE_MINIGAME);   	
            }
		});
		analog_vs_digital.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.MinigameActivity");
            	intent.putExtra("category", Minigame.CATEGORY_ANALOG_VS_DIGITAL);
            	startActivityForResult(intent, REQUEST_CODE_MINIGAME);   	
            }
		});
		polyphony.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.MinigameActivity");            	
            	intent.putExtra("category", Minigame.CATEGORY_POLYPHONY);
            	startActivityForResult(intent, REQUEST_CODE_MINIGAME);   	
            }
		});
		mixed.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.MinigameActivity");
            	intent.putExtra("category", Minigame.CATEGORY_MIXED);
            	startActivityForResult(intent, REQUEST_CODE_MINIGAME);   	
            }
		});
		
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_MINIGAME) {
        	if(resultCode == RESULT_OK) {
        		Intent new_data = new Intent();
				new_data.putExtra(
					"how_many_questions",
					data.getIntExtra("how_many_questions", 10)
				);
				new_data.putExtra(
					"how_many_wrong", 
					data.getIntExtra("how_many_wrong", 0)
				);
				new_data.putExtra(
					"how_many_right", 
					data.getIntExtra("how_many_right", 0)
				);
				setResult(RESULT_OK, new_data);
        	} else {
        		setResult(resultCode);
        	}
        }
        finish();
    }

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_minigame_production_date,
				menu);
		return true;
	}
}
