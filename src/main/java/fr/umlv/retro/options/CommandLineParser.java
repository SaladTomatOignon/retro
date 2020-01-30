package fr.umlv.retro.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class CommandLineParser {
	// constructeur vide
	// parse renvoie un commandline et prend en argment un options et tableau de
	// string
	// reconnaitre les arguments en fonction des options
	public CommandLineParser() {

	}

	public CommandLine parse(Options opts, String[] args) {
		Objects.requireNonNull(opts);
		Objects.requireNonNull(args);
		
		CommandLine cl = new CommandLine();
		var it = Arrays.asList(args).iterator();
		
		while ( it.hasNext() ) {
			String arg = it.next();
			
			if ( arg.startsWith("-") ) {
				addOption(cl, arg, it, opts);
			} else {
				cl.addUnrecognizedValue(arg);
			}
		}
		
		return cl;
	}

	private void addOption(CommandLine cl, String arg, Iterator<String> it, Options opts) {
		Objects.requireNonNull(cl);
		Objects.requireNonNull(arg);
		Objects.requireNonNull(it);
		Objects.requireNonNull(opts);
		
		Option opt = opts.getOptionByName(arg);
		
		if ( Objects.isNull(opt) ) {
			cl.addUnrecognizedValue(arg);
		} else {
			List<String> optValues = getValuesList(opt, it);
			cl.addOptionValue(opt.getLongOptName(), optValues);
		}
	}

	private List<String> getValuesList(Option opt, Iterator<String> it) {
		Objects.requireNonNull(opt);
		Objects.requireNonNull(it);
		
		List<String> lst = new ArrayList<String>();
		
		if ( !opt.hasArg() ) {
			lst.add(null);
		} else if ( opt.hasArgs() ) {
			String[] args = it.next().split(String.valueOf(opt.separator()));
			lst.addAll(Arrays.asList(args));
		} else {
			lst.add(it.next());
		}
		
		return lst;
	}
}
