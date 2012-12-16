package it.perl.dada.SynthQuiz;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class Synth implements Parcelable {

	private int id;
	private String maker;
	private String model;
	private String maker_re;
	private String model_re;
	private int maker_difficulty;
	private int model_difficulty;
	private String link_vse;
	private String link_wikipedia;
	private String link_other;
	private boolean maker_guessed;
	private boolean model_guessed;
	private int year_produced_min = 0;
	private int year_produced_max = 0;
	private int polyphony = 0;
	private String characteristics;
	
	public Synth() {
		id = 0;
		maker_guessed = false;
		model_guessed = false;
	}
		
	public void setId(int i) {
		id = i;
	}
	public int getId() {
		return id;
	}
	
	public void setMaker(String name, String re, int difficulty) {
		maker = name;
		maker_re = re;
		maker_difficulty = difficulty;
	}
	public String getMaker() {
		return maker;
	}
	
	public void setModel(String name, String re, int difficulty) {
		model = name;
		model_re = re;
		model_difficulty = difficulty;
	}
	public String getModel() {
		return model;
	}
	
	public int getMakerDifficulty() {
		return maker_difficulty;		
	}
	public int getModelDifficulty() {
		return model_difficulty;
	}
	public int getDifficulty() {
		return maker_difficulty + model_difficulty;
	}

	public void setProductionDates(int min, int max) {
		year_produced_min = min;
		year_produced_max = max;		
	}
	public int getYearProducedMin() {
		return year_produced_min;
	}
	public int getYearProducedMax() {
		return year_produced_max;
	}
	public int getLifeTime() {
		int min = year_produced_min;
		int max = year_produced_max;
		if(max == 0) max = Calendar.getInstance().get(Calendar.YEAR);
		return max - min + 1;
	}
	
	public void setLinkVse(String link) { link_vse = link; }
	public String getLinkVse() { return link_vse; }
	public void setLinkWikipedia(String link) { link_wikipedia = link; }
	public String getLinkWikipedia() { return link_wikipedia; }
	public void setLinkOther(String link) { link_other = link; }
	public String getLinkOther() { return link_other; }

	public void setPolyphony(int p) { polyphony = p; }
	public int getPolyphony() { return polyphony; }
	
	public void setCharacteristics(String c) { characteristics = c; }
	public String getCharacteristics() { return characteristics; }
	
	public boolean hasCharacteristic(String c) {
		return characteristics.matches(".*\\b" + c + "\\b.*");
	}
	
	public boolean validateMaker(String check) {
		if(check.matches(maker_re)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean validateModel(String check) {
		if(check.matches(model_re)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setMakerGuessed(boolean b) {
		maker_guessed = b;
	}
	public boolean getMakerGuessed() {
		return maker_guessed;
	}

	public void setModelGuessed(boolean b) {
		model_guessed = b;
	}
	public boolean getModelGuessed() {
		return model_guessed;
	}
	
	public void setGuessed(boolean maker, boolean model) {
		maker_guessed = maker;
		model_guessed = model;
	}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(maker);
        dest.writeString(model);
        dest.writeString(maker_re);
        dest.writeString(model_re);
        dest.writeInt(maker_difficulty);
        dest.writeInt(model_difficulty);
        dest.writeBooleanArray(new boolean[] {
        	maker_guessed, model_guessed
        });
        dest.writeString(link_vse);
        dest.writeString(link_wikipedia);
        dest.writeString(link_other);
        dest.writeInt(polyphony);
        dest.writeString(characteristics);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        maker = in.readString();
        model = in.readString();
        maker_re = in.readString();
        model_re = in.readString();
        maker_difficulty = in.readInt();
        model_difficulty = in.readInt();
        boolean[] guesses = new boolean[2];
        in.readBooleanArray(guesses);
        maker_guessed = guesses[0];
        model_guessed = guesses[1];        
        link_vse = in.readString();
        link_wikipedia = in.readString();
        link_other = in.readString();
        polyphony = in.readInt();
        characteristics = in.readString();
    }

    @Override
    public int describeContents() {
    	return 0;
    }
    
    public static final Parcelable.Creator<Synth> CREATOR = new Parcelable.Creator<Synth>() {
        public Synth createFromParcel(Parcel in) {
            Synth s = new Synth();
            s.readFromParcel(in);
            return s;
        }

        public Synth[] newArray(int size) {
            return new Synth[size];
        }
    };
}
