package it.perl.dada.SynthQuiz;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.util.Log;

public class MinigameQuestion {
	
	protected Minigame game;
	
	private Synth[] synths;
	private int correct;
	private String text_before;
	private String text_bold;
	private String text_after;
	
	private int category;
	protected String question_type;
	
	protected Random rnd;
	
	public MinigameQuestion(Minigame g) {
		game = g;
		synths = new Synth[4];
		rnd = game.getRandom();
	}

	public MinigameQuestion() {
		synths = new Synth[4];
		rnd = new Random();
	}

	protected void setSynths(Synth s1, Synth s2, Synth s3, Synth s4) {
		synths[0] = s1;
		synths[1] = s2;
		synths[2] = s3;
		synths[3] = s4;
	}
	
	public void setCorrectAnswer(int i) {
		correct = i;
	}

	public void setText(String t) {
		text_before = t;
		text_bold = "";
		text_after = "";
	}
	public void setText(String t1, String t2, String t3) {
		text_before = t1;
		text_bold = t2;
		text_after = t3;
	}
	
	public Synth getSynth1() { return synths[0]; }
	public Synth getSynth2() { return synths[1]; }
	public Synth getSynth3() { return synths[2]; }
	public Synth getSynth4() { return synths[3]; }
	public Synth getSynth(int index) { return synths[index]; }
	
	public String getText() { return text_before + text_bold + text_after; }
	public String getTextBefore() { return text_before; }
	public String getTextBold() { return text_bold; }
	public String getTextAfter() { return text_after; }
		
	public void setType(String t) { question_type = t; }
	public String getType() { return question_type; }
	
	
	public List<Synth> getSynthsAtRandom() {
		List<Synth> s = new ArrayList<Synth>();
		s.add(synths[0]);
		s.add(synths[1]);
		s.add(synths[2]);
		s.add(synths[3]);
		Collections.shuffle(s);
		return s;
	}
	
	public boolean checkAnswer(Synth s) {
		int a = 666;
		for(int i = 0; i < 4; i++) {
			if(s.hashCode() == synths[i].hashCode()) a = i;
		}
		return a == correct;
	}
	
	public boolean checkAnswer(int i) {
		return i == correct;
	}
	
	protected boolean isAlreadyUsed(int id) {
		for(MinigameQuestion q: game.getQuestions()) {
			if(q.getSynth1().getId() == id) return true;
		}
		return false;
	}

	protected boolean isAlreadyUsed(int id, int cat) {
		for(MinigameQuestion q: game.getQuestions()) {
			if(q.getSynth1().getId() == id && q.getCategory() == cat) return true;
		}
		return false;
	}

	private int getCategory() {
		return Minigame.CATEGORY_MIXED;
	}
	
	protected MinigameQuestion prepare() {
		Log.e("SynthQuiz", "everything is wrong!!!");
		return this;
	}
	
	protected void filterPool(List<Synth> pool, PoolFilter filter) {
		Log.d("SynthQuiz", "filterPool called with " + pool.size());		
		try {
			filter.execute(pool);
		} catch(Exception e) {
			Log.e("SynthQuiz", "oops, could not filter synth list: " + e.toString());
		}
		Log.d("SynthQuiz", "filterPool returning " + pool.size());		
	}
	
	protected List<Synth> getSynthsWithout(Synth to_remove) {
		List<Synth> pool = new ArrayList<Synth>();
		pool.addAll(game.getSynths());
		removeSynth(pool, to_remove);
		return pool;
	}
	
	protected Synth getWinner(PoolFilter filter) {
		List<Synth> pool = new ArrayList<Synth>();
		pool.addAll(game.getSynths());
		for(MinigameQuestion q: game.getQuestions()) {
			if(q.getCategory() == getCategory()) {
				removeSynth(pool, q.getSynth1());
			}
		}
		filter.execute(pool);
		// no synths to choose, try again without excluding the already used ones
		if(pool.size() == 0) {
			pool.addAll(game.getSynths());
			filter.execute(pool);
		}
		Collections.shuffle(pool);
		return pool.get(0);
	}
	
	protected void removeSynth(List<Synth> pool, Synth to_remove) {
		Iterator<Synth> iter = pool.iterator();
		while(iter.hasNext()) {
			Synth s = iter.next();
			if(s.getId() == to_remove.getId()) iter.remove();
		}
	}
	
	protected interface PoolFilter {
		public void execute(List<Synth> pool);
	}
}
