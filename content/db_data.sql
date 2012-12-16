-- #####################################################################
--  ____    __  ____    __  __  ______  _____   ______  
-- |    \  /  ||    \  |  |/ / |   ___||     | |   ___| 
-- |     \/   ||     \ |     \ |   ___||     \  `-.`-.  
-- |__/\__/|__||__|\__\|__|\__\|______||__|\__\|______| 
--
-- #####################################################################

DELETE FROM makers;

INSERT INTO makers (_id, maker, re, difficulty) VALUES (
	'moog', 'Moog', 'moog(\smusic|inc\.?|\smusic\sinc\.?)?', 1
);

-- #####################################################################
--  ______ __    _ ____   _    __    __   _  ______  
-- |   ___|\ \  //|    \ | | _|  |_ |  |_| ||   ___| 
 -- `-.`-.  \ \// |     \| ||_    _||   _  | `-.`-.  
-- |______| /__/  |__/\____|  |__|  |__| |_||______| 
-- 
-- #####################################################################

DELETE FROM synths;

INSERT INTO synths (maker_id, _id, model, re, difficulty, year_produced_min, year_produced_max, polyphony, characteristics, link_vse, link_wikipedia, link_other) VALUES (
    'moog', 'minimoog', 
    'Minimoog', 'minimoog', 1, 
    1970, 1982, 1, 'analog',
    'http://www.vintagesynth.com/moog/moog.php', 
    'http://en.wikipedia.org/wiki/Minimoog', 
    ''
);

-- #####################################################################
--  _____   __   _  ____  ______  ______  ______  ______  
-- /     \ |  | | ||    ||___   ||___   ||   ___||   ___| 
-- |     | |  |_| ||    | .-`.-`  .-`.-` |   ___| `-.`-.  
-- \___/\_\|______||____||______||______||______||______| 
--
-- #####################################################################

DELETE FROM quizzes;

INSERT INTO quizzes (_id, level, synth_id) VALUES (   1,  1, 'minimoog');

