package fr.umlv.retro.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandLine {
	private final Map<String, List<String>> optionValues;
	private final List<String> unrecognizedArgs;

	public CommandLine() {
		this.unrecognizedArgs = new ArrayList<String>();
		this.optionValues = new HashMap<String, List<String>>();
	}

	// methodes: hasOption, getArgList, getOptionValue, getOptionValues
	// hasArgs teste si on avaint une option dans la line de commande
	public boolean hasOption(String opt) {
		Objects.requireNonNull(opt);
		return optionValues.containsKey(opt);
	}

	// getArgList
	// renvoie les options qui ne sont pas reconnues
	public List<String> getArgList() {
		return Collections.unmodifiableList(unrecognizedArgs);
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
	
	public void addUnrecognizedValue(String arg) {
		Objects.requireNonNull(arg);
		unrecognizedArgs.add(arg);
	}
	
	public void addOptionValue(String optName, List<String> args) {
		Objects.requireNonNull(optName);
		Objects.requireNonNull(args);
		
		optionValues.put(optName, args);
	}
	
	// Renvoie le nom des options reconnues
	private Collection<String> getOptions() {
		return optionValues.keySet();
	}
	
	public boolean isValid(Options options) {
		Objects.requireNonNull(options);
		
		// On v�rifie si toutes le CommandLine contient toutes les options obligatoires dans 'options'.
		return getOptions().containsAll(options.stream().filter(Option::isRequired).map(Option::getLongOptName).collect(Collectors.toList()));
	}

}
