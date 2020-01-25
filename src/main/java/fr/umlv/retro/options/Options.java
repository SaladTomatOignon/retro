package fr.umlv.retro.options;

import java.util.List;
import java.util.Objects;

public class Options {

	//methode addOption
	private final List<Option> options;

	public Options(List<Option> options) {
		this.options = options;
	}
	public void addOption(Option opt) {
		Objects.requireNonNull(opt);
		options.add(opt);
	}
}
