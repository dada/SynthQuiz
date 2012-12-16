package it.perl.dada.SynthQuiz;

import java.io.IOException;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GuessActivity extends Activity {

	Synth synth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guess);

		synth = getIntent().getParcelableExtra("synth");

		if(synth.getMakerGuessed()) {
			EditText maker = (EditText) findViewById(R.id.maker);
			maker.setText(synth.getMaker());
			maker.setEnabled(false);
			maker.setKeyListener(null);
		}
		if(synth.getModelGuessed()) {
			EditText model = (EditText) findViewById(R.id.model);
			model.setText(synth.getModel());
			model.setEnabled(false);
			model.setKeyListener(null);
		}
		
		ImageView image = (ImageView) findViewById(R.id.synth);

		if(getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_XHIGH) {
	        int r = this.getResources().getIdentifier(
	        	String.format(
	                "it.perl.dada.SynthQuiz:raw/synth%04d", synth.getId()
	            ),
	            null, null
	        );
			image.setImageBitmap(
				BitmapFactory.decodeStream(
					getResources().openRawResource(r)
				)
			);			
		} else {
	        int r = this.getResources().getIdentifier(
	        	String.format(
	                "it.perl.dada.SynthQuiz:drawable/synth%04d", synth.getId()
	            ),
	            null, null
	        );
	        image.setImageResource(r);
		}
		
        image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.ZoomActivity");
            	intent.putExtra("synth", GuessActivity.this.synth);
            	startActivity(intent);				
			}
        	
        });
        
        Button check = (Button) findViewById(R.id.check);
        check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GuessActivity parent = GuessActivity.this;
				Synth synth = parent.synth;
				EditText maker = (EditText) parent.findViewById(R.id.maker);
				EditText model = (EditText) parent.findViewById(R.id.model);
				String check_maker = maker.getText().toString().trim().toLowerCase();
				String check_model = model.getText().toString().trim().toLowerCase();
			
				if(synth.validateMaker(check_maker)) {
					maker.setError(null);
					maker.clearFocus();
					maker.setText(synth.getMaker());
					maker.setEnabled(false);
					maker.setKeyListener(null);
					synth.setMakerGuessed(true);
				} else {
					ForegroundColorSpan error_span = new ForegroundColorSpan(R.color.red);
					SpannableStringBuilder error_text = new SpannableStringBuilder("WRONG");
					error_text.setSpan(error_span, 0, error_text.length(), 0);
					maker.setError(error_text);
					synth.setMakerGuessed(false);
				}

				if(synth.validateModel(check_model)) {
					model.setError(null);
					model.clearFocus();
					model.setText(synth.getModel());
					model.setEnabled(false);
					model.setKeyListener(null);
					synth.setModelGuessed(true);
				} else {
					ForegroundColorSpan error_span = new ForegroundColorSpan(R.color.red);
					SpannableStringBuilder error_text = new SpannableStringBuilder("WRONG");
					error_text.setSpan(error_span, 0, error_text.length(), 0);
					model.setError(error_text);
					synth.setModelGuessed(false);
				}

				UserDatabase user = new UserDatabase(getApplicationContext());
				try {
					user.getWritableDatabase();
				} catch (SQLException e) {
					throw new Error("Unable to open database");
				}
				user.storeGuess("default", synth);
				user.close();
				
				setViewIfGuessed();
			}
		});
        
        setViewIfGuessed();
    }

	private void setViewIfGuessed() {
        Button check = (Button) findViewById(R.id.check);
		TextView guessed = (TextView) findViewById(R.id.guessed);
        ImageView guessed_background = (ImageView) findViewById(R.id.guessed_background);
        LinearLayout links = (LinearLayout) findViewById(R.id.links);
        ImageView link_vse = (ImageView) findViewById(R.id.link_vse);
        ImageView link_wikipedia = (ImageView) findViewById(R.id.link_wikipedia);
        ImageView link_internet = (ImageView) findViewById(R.id.link_internet);
        if(synth.getMakerGuessed() && synth.getModelGuessed()) {
        	check.setEnabled(false);
        	check.setClickable(false);
        	check.setOnClickListener(null);
			UserDatabase user = new UserDatabase(getApplicationContext());
			try {
				user.getWritableDatabase();
			} catch (SQLException e) {
				throw new Error("Unable to open database");
			}
			int score = user.getSynthScore("default", synth);
			int full_score = user.calculateSynthScore("default", synth, 1, 1);
			float ratio = (float)score / (float)full_score;
			user.close();
			guessed_background.setVisibility(View.VISIBLE);
        	guessed.setVisibility(View.VISIBLE);
        	
        	if(score == full_score) {
        		guessed.setText("PERFECT (" + score + "/" + full_score + ")");
        		guessed_background.setImageResource(R.drawable.perfect);
        	} else {
        		if(ratio > 0.50) {
        			guessed.setText("good (" + score + "/" + full_score + ")");
            		guessed_background.setImageResource(R.drawable.good);
        		} else {
        			guessed.setText("meh (" + score + "/" + full_score + ")");
            		guessed_background.setImageResource(R.drawable.meh);
        		}
        	}
        	
        	links.setVisibility(View.VISIBLE);
        	if(synth.getLinkVse() != null && synth.getLinkVse().length() > 0) {
        		link_vse.setVisibility(View.VISIBLE);
        		link_vse.setOnClickListener(new OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				Intent intent = new Intent(
        					Intent.ACTION_VIEW, Uri.parse(
        						GuessActivity.this.synth.getLinkVse()
        					)
        				);
                        startActivity(intent);
        			}
                });
        	} else {
        		link_vse.setVisibility(View.GONE);
        	}
        	if(synth.getLinkWikipedia() != null && synth.getLinkWikipedia().length() > 0) {
        		link_wikipedia.setVisibility(View.VISIBLE);
        		if(link_vse.getVisibility() == View.GONE) {
        			RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) link_wikipedia.getLayoutParams();
        			p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        		}
        		link_wikipedia.setOnClickListener(new OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				Intent intent = new Intent(
        					Intent.ACTION_VIEW, Uri.parse(
        						GuessActivity.this.synth.getLinkWikipedia()
        					)
        				);
                        startActivity(intent);
        			}
                });
        	} else {
        		link_wikipedia.setVisibility(View.GONE);
        	}
        	if(synth.getLinkOther() != null && synth.getLinkOther().length() > 0) {
        		link_internet.setVisibility(View.VISIBLE);
        		link_internet.setOnClickListener(new OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				Intent intent = new Intent(
        					Intent.ACTION_VIEW, Uri.parse(
        						GuessActivity.this.synth.getLinkOther()
        					)
        				);
                        startActivity(intent);
        			}
                });
        	} else {
        		link_internet.setVisibility(View.GONE);
        	}
        } else {
        	guessed.setVisibility(View.GONE);
        	guessed_background.setVisibility(View.GONE);
        	links.setVisibility(View.GONE);
        }		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_level, menu);
		return true;
	}

}
