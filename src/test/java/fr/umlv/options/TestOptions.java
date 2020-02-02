package fr.umlv.options;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fr.umlv.retro.options.ArgsParser;

class TestOptions {

	@Test
	void testGetOptionValue() {
		String ligne1 = "-t 9 --info";
		var args = ligne1.split(" ");
		var commandLine =ArgsParser.parse(args);
		assertEquals(commandLine.getOptionValue("target"), "9");
		
	}
	
	@Test
	void testGetOptionValues() {
		String ligne = "-t 9 --features lambda,nestmates,record,concatenation";
		var args = ligne.split(" ");
		var commandLine =ArgsParser.parse(args);
		assertEquals(commandLine.getOptionValues("features").get(0),"lambda" );
		assertEquals(commandLine.getOptionValues("features").get(1),"nestmates" );
		assertEquals(commandLine.getOptionValues("features").get(2),"record" );
		assertEquals(commandLine.getOptionValues("features").get(3),"concatenation" );
	}
	
	@Test
	void testHasOption() {
		String ligne1 = "--info --features lambda,nestmates";
		String ligne2 = "--features lambda,nestmates";
		var args1 = ligne1.split(" ");
		var args2 = ligne2.split(" ");
		var commandLine1 =ArgsParser.parse(args1);
		var commandLine2 =ArgsParser.parse(args2);
		assertEquals(commandLine1.hasOption("info"), "true" );
		assertEquals(commandLine2.hasOption("info"), "false" );
	}


	@Test
	void testGetArgList() {
		String ligne = "-info target bonjour --bonsoir --features lambda,nestmates,record,concatenation";
		var args = ligne.split(" ");
		var commandLine =ArgsParser.parse(args);
		assertEquals(commandLine.getArgList().get(0), "-info");
		assertEquals(commandLine.getArgList().get(1), "target");
		assertEquals(commandLine.getArgList().get(2), "bonjour");
		assertEquals(commandLine.getArgList().get(3), "--bonsoir");
		
		}


	}

	


