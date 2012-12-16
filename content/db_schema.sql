CREATE TABLE "android_metadata" ("locale" TEXT DEFAULT 'en_US');

INSERT INTO android_metadata (locale) VALUES ('en_US');
	
CREATE TABLE makers (
    _id TEXT PRIMARY KEY,
    maker TEXT,
    re TEXT,
    difficulty INTEGER
);

CREATE TABLE synths (
    _id TEXT PRIMARY KEY,
    maker_id TEXT,
    model TEXT,
    re TEXT,
    difficulty INTEGER,
    year_produced_min INTEGER,
    year_produced_max INTEGER,
    polyphony INTEGER,
    characteristics TEXT,
    link_vse TEXT,
    link_wikipedia TEXT,
    link_other TEXT
);

CREATE TABLE quizzes (
    _id INTEGER PRIMARY KEY,
    level INTEGER,
    synth_id TEXT
);
