package fr.umlv.retro.options;

import java.util.Objects;

public class HelpFormatter {

	public void printHelp(String cmdLineSyntax, String header, Options options, String footer) {
		Objects.requireNonNull(cmdLineSyntax);
		Objects.requireNonNull(header);
		Objects.requireNonNull(options);
		Objects.requireNonNull(footer);
		
		System.out.println("Usage : " + cmdLineSyntax);
		System.out.println(header + "\n");
		
		for (Option opt : options) {
			System.out.println("-" + opt.getOptName() + ", --" + opt.getLongOptName() + "\t\t" + opt.getDescription());
		}
		
		System.out.println("\n" + footer);
	}

}
