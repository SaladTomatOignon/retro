package fr.umlv.retro.options;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Options implements Iterable<Option> {

	private final Set<Option> options;

	public Options() {
		this.options = new HashSet<Option>();
	}
	
	public void addOption(Option opt) {
		Objects.requireNonNull(opt);
		options.add(opt);
	}
	
	
	public Option getOptionByName(String name) {
		Objects.requireNonNull(name);
		
		if ( name.startsWith("--") ) {
			for (Option opt : options) {
				if ( opt.getLongOptName().equals(name.substring(2)) ) {
					return opt;
				}
			}
		} else if ( name.startsWith("-") ) {
			for (Option opt : options) {
				if ( opt.getOptName().equals(name.substring(1)) ) {
					return opt;
				}
			}
		}
		
		return null;
	}

	@Override
	public Iterator<Option> iterator() {
		return options.iterator();
	}
	
	public Stream<Option> stream() {
		return options.stream();
	}
}
