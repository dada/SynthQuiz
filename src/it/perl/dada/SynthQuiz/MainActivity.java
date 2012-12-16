package it.perl.dada.SynthQuiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

	public final static int REQUEST_CODE_MINIGAME = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.LevelSelectorActivity");
            	startActivity(intent);            	
            }
        });

        Button stats = (Button) findViewById(R.id.stats);
        stats.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            	builder.setMessage("not implemented yet :-)");
            	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int button) {
            			dialog.dismiss();
            		}
            	});
            	AlertDialog dialog = builder.create();
            	dialog.show();
            }
        });

        Button minigames = (Button) findViewById(R.id.minigames);
        minigames.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.MinigameSelectorActivity");
            	startActivityForResult(intent, REQUEST_CODE_MINIGAME);   	
            	/*            	
            	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            	builder.setMessage("not implemented yet :-)");
            	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int button) {
            			dialog.dismiss();
            		}
            	});
            	AlertDialog dialog = builder.create();
            	dialog.show();
            	*/
            }
        });

        Button about = (Button) findViewById(R.id.about);
        about.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.AboutActivity");
            	startActivity(intent);   	
            	/*
            	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            	builder.setMessage("not implemented yet :-)");
            	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int button) {
            			dialog.dismiss();
            		}
            	});
            	AlertDialog dialog = builder.create();
            	dialog.show();
            	*/
            }
        });
    
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_MINIGAME) {
        	if(resultCode == RESULT_OK) {
        		Intent intent = new Intent();
            	intent.setClassName("it.perl.dada.SynthQuiz" ,"it.perl.dada.SynthQuiz.MinigameResultActivity");
				intent.putExtra(
					"how_many_questions",
					data.getIntExtra("how_many_questions", 10)
				);
				intent.putExtra(
					"how_many_wrong", 
					data.getIntExtra("how_many_wrong", 0)
				);
				intent.putExtra(
					"how_many_right", 
					data.getIntExtra("how_many_right", 0)
				);
            	startActivity(intent);
        	}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
