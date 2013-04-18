package net.sprakle.homeAutomation.behaviour;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sprakle.homeAutomation.behaviour.actions.Action;
import net.sprakle.homeAutomation.behaviour.actions.ActionFactory;
import net.sprakle.homeAutomation.behaviour.triggers.Trigger;
import net.sprakle.homeAutomation.behaviour.triggers.TriggerFactory;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.utilities.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public final class Behaviour {

	private Logger logger;

	private List<Trigger> triggers;
	private List<Action> actions;

	private String name;
	private String description;

	Behaviour(Logger logger, Element behaviourElement, ObjectDatabase od, ExternalSoftware exs) {
		this.logger = logger;

		name = behaviourElement.attributeValue("name");
		description = behaviourElement.elementText("description");

		if (name == null) {
			String path = behaviourElement.getUniquePath();
			logger.log("Behaviour does not have a name: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}

		if (description == null) {
			String path = behaviourElement.getUniquePath();
			logger.log("Behaviour does not have a description: " + path, LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}

		Element triggerElements = behaviourElement.element("triggers");
		Element actionElements = behaviourElement.element("actions");

		triggers = makeTriggers(triggerElements, od);
		actions = makeActions(actionElements, exs);
	}

	boolean check() {
		for (Trigger t : triggers)
			if (t.check())
				return true;

		return false;
	}

	void execute() {
		for (Action a : actions)
			a.execute();
	}

	String getName() {
		return name;
	}

	String getDescription() {
		return description;
	}

	private List<Trigger> makeTriggers(Element triggerElements, ObjectDatabase od) {
		List<Trigger> triggers = new ArrayList<Trigger>();

		for (Iterator<?> i = triggerElements.elementIterator("trigger"); i.hasNext();) {
			Element triggerElement = (Element) i.next();

			Trigger trigger = TriggerFactory.makeTrigger(logger, triggerElement, od);

			if (trigger == null)
				logger.log("Unable to make trigger from behaviours file: " + triggerElement.getUniquePath(), LogSource.ERROR, LogSource.BEHAVIOUR, 1);

			triggers.add(trigger);
		}

		return triggers;
	}

	private List<Action> makeActions(Element actionElements, ExternalSoftware exs) {
		List<Action> actions = new ArrayList<Action>();

		for (Iterator<?> i = actionElements.elementIterator("action"); i.hasNext();) {
			Element actionElement = (Element) i.next();

			Action action = ActionFactory.makeAction(logger, actionElement, exs);

			if (action == null)
				logger.log("Unable to make action from behaviours file: " + actionElement.getUniquePath(), LogSource.ERROR, LogSource.BEHAVIOUR, 1);

			actions.add(action);
		}

		return actions;
	}
}