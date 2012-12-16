package it.perl.dada.SynthQuiz;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LevelThumbAdapter extends BaseAdapter {

    private Context context;
    private List<String> levels;

    public LevelThumbAdapter(Context c, List<String> l) {
        context = c;
        levels = l;
    }

    public int getCount() {
    	if(levels != null) {
    		return levels.size();
        } else {
        	return 0;
        }
    }

    public Object getItem(int index) {
        return levels.get(index);
    }

    public long getItemId(int index) {
    	String[] l = levels.get(index).split("-");    	
        return Long.parseLong(l[0], 10);
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int index) {
        String[] l = levels.get(index).split("-");
        int is_locked = Integer.parseInt(l[3]);
        return is_locked == 1 ? false : true;
    }

    public View getView(int index, View convertView, ViewGroup parent) {
        View item;
        String[] l = levels.get(index).split("-");
        int level = Integer.parseInt(l[0]);
        int percentage = Integer.parseInt(l[1]);
        int score = Integer.parseInt(l[2]);
        int is_locked = Integer.parseInt(l[3]);
        if(convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = li.inflate(R.layout.view_level, parent, false);
        } else {
            item = convertView;
        }

        ImageView knob = (ImageView) item.findViewById(R.id.knob);
        TextView level_number = (TextView) item.findViewById(R.id.level_number);
        TextView percent_done = (TextView) item.findViewById(R.id.percent_done);
        TextView level_score = (TextView) item.findViewById(R.id.level_score);

        if(level == 0) {
        	knob.setVisibility(View.GONE);
        	level_number.setText("More levels");
        	percent_done.setTextColor(parent.getResources().getColor(R.color.ivory));
        	percent_done.setText("coming soon...");
        	percent_done.setTypeface(Typeface.DEFAULT);
        	percent_done.setVisibility(View.VISIBLE);
        	level_score.setVisibility(View.INVISIBLE);
        } else {
	        knob.setVisibility(View.VISIBLE);
	        level_number.setText("Level " + level);
	        if(is_locked == 1) {
	        	knob.setImageResource(R.drawable.locked);
	        	level_score.setVisibility(View.INVISIBLE);
	        	percent_done.setTextColor(parent.getResources().getColor(R.color.red));
	        	percent_done.setTypeface(Typeface.DEFAULT_BOLD);
	        	percent_done.setText("LOCKED");
	        	percent_done.setVisibility(View.VISIBLE);
	        } else {
	        	percent_done.setTextColor(parent.getResources().getColor(R.color.ivory));
	        	percent_done.setText(Integer.toString(percentage) + "%");
	        	percent_done.setTypeface(Typeface.DEFAULT);
	        	percent_done.setVisibility(View.VISIBLE);
	
	        	if(percentage < 0) percentage = 0;
	        	if(percentage > 99) percentage = 99;
	        	int rest = percentage % 5;
	        	int percentage5;
	        	if(rest < 3) {
	        		percentage5 = percentage - rest;
	        	} else {
	        		percentage5 = percentage + 5 - rest;
	        	}
	        	int r = context.getResources().getIdentifier(
	        		String.format(
	        			"it.perl.dada.SynthQuiz:drawable/knob%03d", percentage5
	        		),
	        		null, null
	        	);
	        	knob.setImageResource(r);
	        	level_score.setVisibility(View.VISIBLE);
	        	level_score.setText("Score: " + score);
	        }        
        }
	    return item;
    }
}