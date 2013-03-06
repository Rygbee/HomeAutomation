package net.sprakle.homeAutomation.speech.interpretation.module;

import java.util.ArrayList;

import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.speech.interpretation.Phrase;
import net.sprakle.homeAutomation.speech.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.speech.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ModuleManager {
	ArrayList<InterpretationModule> modules;

	Logger logger;

	public ModuleManager(Logger logger, Synthesis synth, ObjectDatabase od, Tagger tagger) {
		this.logger = logger;

		modules = ModuleFactory.getModules(logger, synth, od, tagger);
	}

	// checks each module for a claim on the given phrase
	public ClaimResponse submitForClaiming(Phrase phrase) {
		ClaimResponse response = new ClaimResponse();

		// list of all modules that claimed the phrase. Ideally only one module will be in this list
		ArrayList<InterpretationModule> claimers = new ArrayList<InterpretationModule>();

		for (InterpretationModule m : modules) {
			if (m.claim(phrase)) {
				claimers.add(m);
			}
		}

		// did we get too many claims?
		if (claimers.size() > 1) {
			response.toManyClaimed = true;
		} else {
			response.toManyClaimed = false;
		}

		// was there not enough claims? (0)
		if (claimers.size() <= 0) {
			response.notClaimed = true;
		} else {
			response.notClaimed = false;
		}

		// if everything worked out well
		if (!response.notClaimed && !response.toManyClaimed) {
			response.module = claimers.get(0);
		}

		return response;
	}

	// used to return data about claims
	public class ClaimResponse {
		public InterpretationModule module = null;
		public Boolean toManyClaimed = null;
		public Boolean notClaimed = null;
	}
}
