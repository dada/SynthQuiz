package it.perl.dada.SynthQuiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Minigame {

	public static final int CATEGORY_PRODUCTION_DATES = 0;
	public static final int CATEGORY_ANALOG_VS_DIGITAL = 1;
	public static final int CATEGORY_POLYPHONY = 2;	
	public static final int CATEGORY_MIXED = 3;
	
	private Random rnd;
	private int how_many_questions;
	private List<MinigameQuestion> questions;
	private int current_question;
	private List<Integer> images_to_question;

	private int how_many_wrong;
	private int how_many_right;

	private int category;
	
	private List<Synth> synths;
	
	public Minigame(int cat) {
		category = cat;
		rnd = new Random();
		questions = new ArrayList<MinigameQuestion>();
	}

	public void setSynths(List<Synth> s) {
		synths = s;
	}
	public List<Synth> getSynths() {
		return synths;
	}

	public void setHowManyQuestions(int n) {
		how_many_questions = n;
	}
	public int getHowManyQuestions() { 
		return how_many_questions; 
	}
	
	public void setCurrentQuestion(int n) {
		current_question = n;
	}
	public int getCurrentQuestion() {
		return current_question;
	}
	
	
	public void prepare() {
		questions.clear();
		for(int i = 0; i < how_many_questions; i++) {
				
			MinigameQuestion q;
			
			int c = category;
			if(category == CATEGORY_MIXED) {
				c = rnd.nextInt(CATEGORY_MIXED);
			}

			switch(c) {
			case CATEGORY_PRODUCTION_DATES:
				q = new MinigameQuestionProductionDates(this);
				questions.add(q.prepare());
				break;
			case CATEGORY_ANALOG_VS_DIGITAL:
				q = new MinigameQuestionAnalogVsDigital(this);
				questions.add(q.prepare());
				break;
			case CATEGORY_POLYPHONY:
				q = new MinigameQuestionPolyphony(this);
				questions.add(q.prepare());
				break;
			}			
		}
	}
	
	public Random getRandom() { return rnd; }
	public List<MinigameQuestion> getQuestions() { return questions; }
	
	public Synth pickSynth() {
		return synths.get(rnd.nextInt(synths.size()));
	}
	
}
