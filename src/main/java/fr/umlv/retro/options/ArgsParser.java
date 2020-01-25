package fr.umlv.retro.options;

import java.util.Objects;

public class ArgsParser {
	
	/* Parse les options de 'args' et renvoie le CommandLine associé. */
	public static CommandLine parse(String[] args) {
		Objects.requireNonNull(args);
		
		Options options = getOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cl = null;
		
		try {
			cl = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println("Error while parsing arguments\n" + e.getMessage());
			System.exit(1);
		}
		
		return cl;
	}
	
	public static void displayHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("retro", "Retro tool : Convert class and jar files to an older version.\n ", ArgsParser.getOptions(), "\nAuthors : MILED Amany, WADAN Samy", true);
	}
	
	private static Options getOptions() {
		Options options = new Options();
		
		options.addOption(optionTarget());
		options.addOption(optionForce());
		options.addOption(optionInfo());
		options.addOption(optionFeatures());
		options.addOption(optionHelp());
		
		return options;
	}
	
	private static Option optionHelp() {
		return new Option("h", "help", false, "Print this message");
	}

	private static Option optionFeatures() {
		return Option.builder("l")
				.longOpt("features")
				.hasArgs()
				.desc("List of features")
				.valueSeparator(',')
				.build();
	}

	private static Option optionInfo() {
		return new Option("i", "info", false, "Display features only");
	}

	private static Option optionForce() {
		return new Option("f", "force", false, "Force the implementation");
	}

	private static Option optionTarget() {
		Option opt = new Option("t", "target", true, "Target version");
		opt.setRequired();
		
		return opt;
	}
}
