package it.perl.dada.SynthQuiz;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ZoomActivity extends Activity {

	int old_dpi;
	
	// source: http://stackoverflow.com/questions/10630373/android-image-view-pinch-zooming
	
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	float[] matrixValues = new float[9];
	
	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	String savedItemClicked;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoom);
		
		ImageView image = (ImageView) findViewById(R.id.synth);

		Synth synth = getIntent().getParcelableExtra("synth");
		/*
        int r = this.getResources().getIdentifier(
        	String.format(
                "it.perl.dada.SynthQuiz:drawable/hires_synth%04d", synth.getId()
            ),
            null, null
        );
        image.setImageResource(r);
	    */

		/*
		try {
			InputStream jpeg = getAssets().open(String.format("synth%04d.jpg", synth.getId()));
			Bitmap bm = BitmapFactory.decodeStream(jpeg);
			image.setImageBitmap(bm);
		} catch(IOException e) {
			finish();
		}
		*/
		/*		
		old_dpi = getResources().getDisplayMetrics().densityDpi;
		if(old_dpi != DisplayMetrics.DENSITY_XHIGH) {
			DisplayMetrics new_dpi = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(new_dpi);
			new_dpi.densityDpi = DisplayMetrics.DENSITY_XHIGH;
			Configuration cfg = getResources().getConfiguration();
			getResources().updateConfiguration(cfg, new_dpi);
		}
        int r = getResources().getIdentifier(
        	String.format(
                "it.perl.dada.SynthQuiz:drawable/synth%04d", synth.getId()
            ),
            null, null
        );
        
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inDensity = DisplayMetrics.DENSITY_XHIGH;
		Bitmap bm = BitmapFactory.decodeResource(getResources(), r, opts);
		image.setImageBitmap(bm);
		*/

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
		
		image.setOnTouchListener(new OnTouchListener() {
			
			@Override	
			public boolean onTouch(View v, MotionEvent event) {
		
			    ImageView view = (ImageView) v;
			    dumpEvent(event);
		
			    // Handle touch events here...
			    switch (event.getAction() & MotionEvent.ACTION_MASK) {
			    case MotionEvent.ACTION_DOWN:
			        savedMatrix.set(matrix);
			        start.set(event.getX(), event.getY());
			        Log.d("SynthQuiz", "mode=DRAG");
			        mode = DRAG;
			        break;
			    case MotionEvent.ACTION_POINTER_DOWN:
			        oldDist = spacing(event);
			        Log.d("SynthQuiz", "oldDist=" + oldDist);
			        if (oldDist > 10f) {
			            savedMatrix.set(matrix);
			            midPoint(mid, event);
			            mode = ZOOM;
			            Log.d("SynthQuiz", "mode=ZOOM");
			        }
			        break;
			    case MotionEvent.ACTION_UP:
			    case MotionEvent.ACTION_POINTER_UP:
			        mode = NONE;
			        Log.d("SynthQuiz", "mode=NONE");
			        break;
			    case MotionEvent.ACTION_MOVE:
			        if (mode == DRAG) {
			            matrix.set(savedMatrix);
			            matrix.postTranslate(event.getX() - start.x, event.getY()
			                    - start.y);
			        } else if (mode == ZOOM) {
			            float newDist = spacing(event);
			            Log.d("SynthQuiz", "newDist=" + newDist);
			            if (newDist > 10f) {
			                matrix.set(savedMatrix);
			                float scale = newDist / oldDist;
			                matrix.getValues(matrixValues);
			                float currentScale = matrixValues[Matrix.MSCALE_X];
			                if(scale * currentScale < 1.0f) {
			                	scale = 1.0f / currentScale;
			                }
			                matrix.postScale(scale, scale, mid.x, mid.y);
			            }
			        }
			        break;
			    }
		
			    view.setImageMatrix(matrix);
			    return true;
			}
		
			private void dumpEvent(MotionEvent event) {
			    String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
			            "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
			    StringBuilder sb = new StringBuilder();
			    int action = event.getAction();
			    int actionCode = action & MotionEvent.ACTION_MASK;
			    sb.append("event ACTION_").append(names[actionCode]);
			    if (actionCode == MotionEvent.ACTION_POINTER_DOWN
			            || actionCode == MotionEvent.ACTION_POINTER_UP) {
			        sb.append("(pid ").append(
			                action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			        sb.append(")");
			    }
			    sb.append("[");
			    for (int i = 0; i < event.getPointerCount(); i++) {
			        sb.append("#").append(i);
			        sb.append("(pid ").append(event.getPointerId(i));
			        sb.append(")=").append((int) event.getX(i));
			        sb.append(",").append((int) event.getY(i));
			        if (i + 1 < event.getPointerCount())
			            sb.append(";");
			    }
			    sb.append("]");
			    Log.d("SynthQuiz", sb.toString());
			}
		
			/** Determine the space between the first two fingers */
			private float spacing(MotionEvent event) {
			    float x = event.getX(0) - event.getX(1);
			    float y = event.getY(0) - event.getY(1);
			    return FloatMath.sqrt(x * x + y * y);
			}
		
			/** Calculate the mid point of the first two fingers */
			private void midPoint(PointF point, MotionEvent event) {
			    float x = event.getX(0) + event.getX(1);
			    float y = event.getY(0) + event.getY(1);
			    point.set(x / 2, y / 2);
			}

		});

	}

	/*
	@Override
	public void onPause() {
		DisplayMetrics dpi = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dpi);
		dpi.densityDpi = old_dpi;
		Configuration cfg = getResources().getConfiguration();
		getResources().updateConfiguration(cfg, dpi);
		super.onPause();
	}
	*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_zoom, menu);
		return true;
	}

}
