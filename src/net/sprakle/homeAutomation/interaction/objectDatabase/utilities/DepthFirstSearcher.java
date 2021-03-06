package net.sprakle.homeAutomation.interaction.objectDatabase.utilities;

import java.util.ArrayList;
import java.util.Stack;

import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class DepthFirstSearcher {
	public static ArrayList<Component> depthFirstSearch(Logger logger, Component root, String query) {
		logger.log("Quering database via DFS for string '" + query + "'", LogSource.OD_DFS_INFO, 2);

		ArrayList<Component> foundComponents = new ArrayList<>();

		// list of already visited components
		ArrayList<Component> visited = new ArrayList<>();

		// stack of components for dfs
		Stack<Component> stack = new Stack<>();

		// get root component
		stack.push(root);

		// execute while still looking for correct object
		while (!stack.isEmpty()) {
			Component current = stack.peek();

			// if we haven't already, mark this on ad visited, and compare it to the string
			if (!visited.contains(current)) {

				// if this matches our query, add it to the list
				if (current.getIdentifier().equals(query)) {
					foundComponents.add(current);
				}

				visited.add(current);
			}

			// if the current component has children, add the first unvisited child to the stack
			if (current.hasChildren() && !allChildrenVisited(current, visited)) {

				for (Component child : current.getChildren()) {
					if (!visited.contains(child)) {
						stack.push(child);

						// don't go to the next child, as this is a depth first search
						break;
					}
				}
			} else {
				// if there are no children, pop this child off the stack, and repeat
				stack.pop();
			}
		}

		return foundComponents;
	}

	private static boolean allChildrenVisited(Component parent, ArrayList<Component> visited) {

		// assume true
		boolean allVisited = true;

		// break assumption if the child list doesn't contain a visited child
		for (Component child : parent.getChildren()) {
			if (!visited.contains(child)) {
				allVisited = false;
			}
		}
		return allVisited;
	}
}
