package fr.umlv.retro.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CommandLine {
	private final Map<String, List<String>> optionValues;
	private final List<String> unrecognizedValues;

	public CommandLine() {
		this.unrecognizedValues = new ArrayList<String>();
		this.optionValues = new HashMap<String, List<String>>();
	}

	// methodes: hasOption, getArgList, getOptionValue, getOptionValues
	// hasArgs teste si on avaint une option dans la line de commande
	public boolean hasOption(String opt) {
		Objects.requireNonNull(opt);
		return optionValues.containsKey(opt);
	}

	// getArgList
	// renvoie les options qui ne sont pas reconnus
	public List<String> getArgList() {
		return Collections.unmodifiableList(unrecognizedValues);
	}

	// getOptionValue
	// prend une option en paramètre et renvoie la valeur de son option
	public String getOptionValue(String opt) {
		Objects.requireNonNull(opt);
		return optionValues.get(opt).get(0);
	}

	// getOptionValues
	// prend une option en paramètre et renvoie tous ses valeurs
	public List<String> getOptionValues(String opt) {
		Objects.requireNonNull(opt);
		return optionValues.get(opt);
	}

}
