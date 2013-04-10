// Initialises all modules. Remember to add new modules here

package net.sprakle.homeAutomation.interpretation.module;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;

import net.sprakle.homeAutomation.interpretation.module.modules.math.Math;
import net.sprakle.homeAutomation.interpretation.module.modules.media.Media;
import net.sprakle.homeAutomation.interpretation.module.modules.objectDatabaseCommand.ObjectDatabaseCommand;
import net.sprakle.homeAutomation.interpretation.module.modules.objectDatabaseRQD.ObjectDatabaseRQD;
import net.sprakle.homeAutomation.interpretation.module.modules.reloading.Reloading;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ModuleFactory {
	static HashMap<JCheckBox, InterpretationModule> getModules(Logger logger, Synthesis synth, ObjectDatabase od, Tagger tagger, ExternalSoftware exs) {
		HashMap<JCheckBox, InterpretationModule> modules = new HashMap<JCheckBox, InterpretationModule>();

		// temporarily hold modules for easy adding of each checkbox
		ArrayList<InterpretationModule> moduleArray = new ArrayList<InterpretationModule>();

		moduleArray.add(new ObjectDatabaseCommand(logger, synth, od));
		moduleArray.add(new ObjectDatabaseRQD(logger, synth, od));
		moduleArray.add(new Media(logger, exs));
		moduleArray.add(new Math(logger, synth));
		moduleArray.add(new Reloading(logger));

		// add module and checkbox
		for (InterpretationModule im : moduleArray) {
			String label = im.getName();
			JCheckBox checkbox = new JCheckBox(label);

			modules.put(checkbox, im);
		}

		return modules;
	}
}
