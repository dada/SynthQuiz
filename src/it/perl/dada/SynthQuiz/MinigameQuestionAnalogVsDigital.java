package it.perl.dada.SynthQuiz;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

public class MinigameQuestionAnalogVsDigital extends MinigameQuestion {

	public MinigameQuestionAnalogVsDigital(Minigame g) {
		super(g);
	}
	
	public int getCategory() { return Minigame.CATEGORY_ANALOG_VS_DIGITAL; }
	
	public MinigameQuestion prepare() {
		
		int type = rnd.nextInt(9);
		
		switch(type) {
		case 0:
		case 1:
			prepareQuestionAnalog();
			break;
		case 2:
		case 3:
			prepareQuestionNotAnalog();
			break;
		case 4:
		case 5:
			prepareQuestionDigital();
			break;
		case 6:
		case 7:
			prepareQuestionNotDigital();
			break;
		case 8:
			prepareQuestionHybrid();
			break;
		}
		Log.d("SynthQuiz", String.format(
			"QUESTION %s: winner=%s (%s) others=%s (%s), %s (%s), %s (%s)",
			getType(),
			getSynth1().getModel(), getSynth1().getCharacteristics(),
			getSynth2().getModel(), getSynth2().getCharacteristics(),
			getSynth3().getModel(), getSynth3().getCharacteristics(),
			getSynth4().getModel(), getSynth4().getCharacteristics()
		));		
		return this;
	}
	
	private void prepareQuestionAnalog() {
		setType("Analog");
		Synth s1 = getWinner(new AnalogFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new NotAnalogFilter());	
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"analog",
			"?"
		);
		setCorrectAnswer(0);		
	}

	private void prepareQuestionNotAnalog() {
		setType("NotAnalog");
		Synth s1 = getWinner(new NotAnalogFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new AnalogFilter());
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"NOT analog",
			"?"
		);
		setCorrectAnswer(0);		
	}

	private void prepareQuestionDigital() {
		setType("Digital");
		Synth s1 = getWinner(new DigitalFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new NotDigitalFilter());	
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"digital",
			"?"
		);
		setCorrectAnswer(0);		
	}

	private void prepareQuestionNotDigital() {
		setType("NotDigital");
		Synth s1 = getWinner(new NotDigitalFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new DigitalFilter());	
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"NOT digital",
			"?"
		);
		setCorrectAnswer(0);		
	}

	private void prepareQuestionHybrid() {
		setType("Hybrid");
		Synth s1 = getWinner(new HybridFilter());
		List<Synth> pool = getSynthsWithout(s1);
		filterPool(pool, new NotHybridFilter());	
		Collections.shuffle(pool);
		Synth s2 = pool.get(0);
		Synth s3 = pool.get(1);
		Synth s4 = pool.get(2);
		setSynths(s1, s2, s3, s4);
		setText(
			"Which one of these synths is ",
			"hybrid (analog/digital)",
			"?"
		);
		setCorrectAnswer(0);
	}

	protected class NotAnalogFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.hasCharacteristic("analog")) iter.remove();
			}			
		}
	}

	protected class AnalogFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(!s.hasCharacteristic("analog")) iter.remove();
			}
		}
	}

	protected class NotDigitalFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.hasCharacteristic("digital")) iter.remove();
			}
		}
	}

	protected class DigitalFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(!s.hasCharacteristic("digital")) iter.remove();
			}
		}
	}

	protected class HybridFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(!s.hasCharacteristic("hybrid")) iter.remove();
			}
		}
	}

	protected class NotHybridFilter implements PoolFilter {
		public void execute(List<Synth> pool) {
			Iterator<Synth> iter = pool.iterator();
			while(iter.hasNext()) {
				Synth s = iter.next();
				if(s.hasCharacteristic("hybrid")) iter.remove();
			}
		}
	}

	
}
