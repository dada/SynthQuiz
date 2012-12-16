package it.perl.dada.SynthQuiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

public class MinigameQuestionPolyphony extends MinigameQuestion {

	public MinigameQuestionPolyphony(Minigame g) {
		super(g);
	}
	
	public int getCategory() { return Minigame.CATEGORY_POLYPHONY; }
	
	public MinigameQuestion prepare() {
		
		int type = rnd.nextInt(11);
		
		switch(type) {
		case 0:
		case 1:
		case 2:
		case 3:
			prepareQuestionMono();
			break;
		case 4:
		case 5:
		case 6:
		case 7:
			prepareQuestionPoly();
			break;
		case 8:
		case 9:
			prepareQuestionHighestPolyphony();
			break;
		case 10:
			prepareQuestionLowestPolyphony();
			break;
		}
		Log.d("SynthQuiz", String.format(
			"QUESTION %s: winner=%s (%d) others=%s (%d), %s (%d), %s (%d)",
			getType(),
			getSynth1().getModel(), getSynth1().getPolyphony(),
			getSynth2().getModel(), getSynth2().getPolyphony(),
			getSynth3().getModel(), getSynth3().getPolyphony(),
			getSynth4().getModel(), getSynth4().getPolyphony()
		));		
		return this;
	}
	
	private void prepareQuestionMono() {
		setType("Mono");
		Synth s1 = getWinner(new MonoFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new PolyFilter());	
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"monophonic",
			"?"
		);
		setCorrectAnswer(0);		
	}

	private void prepareQuestionPoly() {
		setType("Poly");
		Synth s1 = getWinner(new PolyFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new MonoFilter());	
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"polyphonic",
			"?"
		);
		setCorrectAnswer(0);		
	}

	private void prepareQuestionHighestPolyphony() {
		setType("HighestPolyphony");
		Synth s1 = getWinner(new ReasonablyHighPolyphonyFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new LowerPolyphonyThanFilter(s1));	
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths has ",
			"the highest polyphony",
			"?"
		);
		setCorrectAnswer(0);		
	}

	
	private void prepareQuestionLowestPolyphony() {
		setType("LowestPolyphony");
		Synth s1 = getWinner(new ReasonablyLowPolyphonyFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new HigherPolyphonyThanFilter(s1));
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths has ",
			"the lowest polyphony",
			" (eg. the smallest number of voices)?"
		);
		setCorrectAnswer(0);		
	}


	protected class MonoFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.hasCharacteristic("drum")) iter.remove();
				else if(s.getPolyphony() > 1) iter.remove();
			}			
		}
	}

	protected class PolyFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.hasCharacteristic("drum")) iter.remove();
				else if(s.getPolyphony() == 1) iter.remove();
			}
		}
	}

	protected class HigherPolyphonyThanFilter implements PoolFilter {
		private Synth comparison;
		public HigherPolyphonyThanFilter(Synth c) {
			comparison = c;
		}
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.hasCharacteristic("drum")) iter.remove();
				else if(s.getPolyphony() <= comparison.getPolyphony()) iter.remove();
			}
		}
	}

	protected class LowerPolyphonyThanFilter implements PoolFilter {
		private Synth comparison;
		public LowerPolyphonyThanFilter(Synth c) {
			comparison = c;
		}
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.hasCharacteristic("drum")) iter.remove();
				else if(s.getPolyphony() >= comparison.getPolyphony()) iter.remove();
			}
		}
	}

	protected class ReasonablyHighPolyphonyFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.hasCharacteristic("drum")) iter.remove();
				else if(s.getPolyphony() < 4) iter.remove();
			}
		}
	}

	protected class ReasonablyLowPolyphonyFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.hasCharacteristic("drum")) iter.remove();
				else if(s.getPolyphony() > 4) iter.remove();
			}
		}
	}
	
}
