/*
 * Used to access external applications, usually OS dependent
 */

package net.sprakle.homeAutomation.externalSoftware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sprakle.homeAutomation.externalSoftware.GUI.ExternalSoftwareGUI;
import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineFactory;
import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ExternalSoftware {
	private final Logger logger;
	private final CommandLineInterface cli;

	// software can be disabled if the dependencies can not be met, for example if there is no internet connection
	private Map<SoftwareName, Boolean> activeSoftware;

	private final Map<SoftwareName, SoftwareInterface> software;

	public ExternalSoftware(Logger logger) {
		this.logger = logger;
		this.cli = CommandLineFactory.getCommandLine(logger);

		activeSoftware = new HashMap<>();
		software = new HashMap<>();

		ExternalSoftwareGUI gui = new ExternalSoftwareGUI(logger, SoftwareName.values());
		activeSoftware = gui.getActiveSoftware();
		gui.close();

		String active = "";
		for (Entry<SoftwareName, Boolean> e : activeSoftware.entrySet()) {
			if (e.getValue())
				active += e.getKey() + ", ";
			else
				logger.log(e.getKey() + " is disabled. Features depending on it may not work or provide incorrect functionality", LogSource.WARNING, LogSource.EXTERNAL_SOFTWARE, 1);
		}

		logger.log("Active external software: " + active, LogSource.EXTERNAL_SOFTWARE, 1);
	}

	/**
	 * Software can be initialised before it is needed
	 * 
	 * @param name
	 */
    void initSoftware(SoftwareName name) {
		boolean active = activeSoftware.get(name);

		SoftwareInterface sInterface = SoftwareFactory.getSoftware(logger, cli, name, active);
		software.put(name, sInterface);
	}

	public SoftwareInterface getSoftware(SoftwareName name) {
		SoftwareInterface sInterface = software.get(name);
		if (sInterface != null)
			return sInterface;

		// software has not been initialised yet
		initSoftware(name);
		return getSoftware(name);
	}
}
