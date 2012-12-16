package it.perl.dada.SynthQuiz;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class SynthThumbAdapter extends BaseAdapter {

    private Context context;
    private List<Synth> synths;

    public SynthThumbAdapter(Context c, List<Synth> s) {
        context = c;
        synths = s;
    }

    public int getCount() {
    	if(synths != null) {
    		return synths.size();
        } else {
        	return 0;
        }
    }

    public Object getItem(int index) {
        return synths.get(index);
    }

    public long getItemId(int index) {
        return synths.get(index).getId();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int index, View convertView, ViewGroup parent) {
        View item;
        Synth synth = synths.get(index);
        
        if(convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = li.inflate(R.layout.view_thumb, parent, false);
        } else {
            item = convertView;
        }
        ImageView thumb = (ImageView) item.findViewById(R.id.thumb);
        ImageView difficulty = (ImageView) item.findViewById(R.id.difficulty);

        int r = context.getResources().getIdentifier(
        	String.format(
                "it.perl.dada.SynthQuiz:drawable/thumb_synth%04d", synth.getId()
            ),
            null, null
        );
        Log.d("SynthQuiz", "drawable for item " + index + " id=" + synths.get(index).getId() + " r=" + r);

        if(synth.getMakerGuessed() && synth.getModelGuessed()) {
        	Bitmap original = BitmapFactory.decodeResource(
        		SynthThumbAdapter.this.context.getResources(), r
        	);        	
        	Bitmap guessed = Bitmap.createBitmap(
        		original.getWidth(), original.getHeight(), original.getConfig()
        	);
        	Bitmap overlay = BitmapFactory.decodeResource(
        		SynthThumbAdapter.this.context.getResources(), R.drawable.guessed
        	);        	
            Canvas canvas = new Canvas(guessed);
            Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(original, 0, 0, paint);
            canvas.drawBitmap(overlay, 0, 0, paint);
        	thumb.setImageBitmap(guessed);
        } else {
        	thumb.setImageResource(r);
        }
        
        int d = context.getResources().getIdentifier(
        	String.format(
                "it.perl.dada.SynthQuiz:drawable/difficulty%02d", synths.get(index).getDifficulty()
            ),
            null, null
        );
        difficulty.setImageResource(d);
        
        return item;
    }
}