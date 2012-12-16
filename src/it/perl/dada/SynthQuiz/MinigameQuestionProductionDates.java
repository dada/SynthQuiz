package it.perl.dada.SynthQuiz;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

public class MinigameQuestionProductionDates extends MinigameQuestion {

	public MinigameQuestionProductionDates(Minigame g) {
		super(g);
	}
	
	public int getCategory() { return Minigame.CATEGORY_PRODUCTION_DATES; }
	
	public MinigameQuestion prepare() {
		
		int type = rnd.nextInt(9);
		
		switch(type) {
		case 0:
		case 1:
		case 2:
			prepareQuestionFirstProduced();
			break;
		case 3:
		case 4:
			prepareQuestionLastProduced();
			break;
		case 5:
		case 6:
		case 7:
			prepareQuestionStillProduced();
			break;
		case 8:
			prepareQuestionLongestProduction();
			break;
		}
		Log.d("SynthQuiz", String.format(
			"QUESTION %s: winner=%s (%d-%d) others=%s (%d-%d), %s (%d-%d), %s (%d-%d)",
			getType(),
			getSynth1().getModel(), getSynth1().getYearProducedMin(), getSynth1().getYearProducedMax(),
			getSynth2().getModel(), getSynth2().getYearProducedMin(), getSynth2().getYearProducedMax(),
			getSynth3().getModel(), getSynth3().getYearProducedMin(), getSynth3().getYearProducedMax(),
			getSynth4().getModel(), getSynth4().getYearProducedMin(), getSynth4().getYearProducedMax()
		));			
		return this;
	}
	
	private void prepareQuestionFirstProduced() {
		setType("FirstProduced");
		Synth s1 = getWinner(new ReasonablyOldFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new NewerThanFilter(s1));
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"the oldest",
			" (eg. was first produced)?"
		);
		setCorrectAnswer(0);		
	}

	private void prepareQuestionLastProduced() {
		setType("LastProduced");
		Synth s1 = getWinner(new ReasonablyNewFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new OlderThanFilter(s1));
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"the newest",
			" (eg. was produced most recently)?"
		);
		setCorrectAnswer(0);		
	}

	private void prepareQuestionStillProduced() {
		setType("StillProduced");
		Synth s1 = getWinner(new StillProducedFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new OutOfProductionFilter());
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"still in production",
			" (at the time of this game's release)?"
		);
		setCorrectAnswer(0);		
	}

	private void prepareQuestionLongestProduction() {
		setType("LongestProduction");
		Synth s1 = getWinner(new ReasonablyLongLifetimeFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new ShorterLifetimeThanFilter(s1));
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths has been ", 
			"in production the longest", 
			"?"
		);
		setCorrectAnswer(0);
	}

	protected class ReasonablyOldFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			int this_year = Calendar.getInstance().get(Calendar.YEAR);
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(this_year - s.getYearProducedMin() < 5) iter.remove();
			}
		}
	}
	
	protected class NewerThanFilter implements PoolFilter {
		private Synth comparison;
		public NewerThanFilter(Synth c) {
			comparison = c;
		}
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.getYearProducedMin() <= comparison.getYearProducedMin()) iter.remove();
			}
		}
	}

	protected class ReasonablyNewFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			int this_year = Calendar.getInstance().get(Calendar.YEAR);
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(this_year - s.getYearProducedMin() > 20) iter.remove();
			}
		}
	}

	protected class OlderThanFilter implements PoolFilter {
		private Synth comparison;
		public OlderThanFilter(Synth c) {
			comparison = c;
		}
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.getYearProducedMin() >= comparison.getYearProducedMin()) iter.remove();
			}
		}
	}

	protected class StillProducedFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.getYearProducedMax() > 0) iter.remove();
			}
		}
	}

	protected class OutOfProductionFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.getYearProducedMax() == 0) iter.remove();
			}
		}
	}

	protected class ReasonablyLongLifetimeFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.getLifeTime() < 3) iter.remove();
			}
		}
	}
	
	protected class ShorterLifetimeThanFilter implements PoolFilter {
		private Synth comparison;
		public ShorterLifetimeThanFilter(Synth c) {
			comparison = c;
		}
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.getLifeTime() >= comparison.getLifeTime()) iter.remove();
			}
		}
	}

}
