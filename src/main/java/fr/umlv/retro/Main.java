package fr.umlv.retro;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import fr.umlv.retro.detection.FeaturesDetector;
import fr.umlv.retro.detection.FeaturesDetectorBuilder;
import fr.umlv.retro.features.*;
import fr.umlv.retro.options.ArgsParser;

public class Main {
	
	private static void analyserFeatures(ClassNode cn) {
		FeaturesDetector fd = new FeaturesDetectorBuilder(cn)
									.append(new TryWithResourcesFeature())
									.append(new NestMatesFeature())
									.append(new LambdaFeature())
									.append(new ConcatenationFeature())
									.append(new RecordFeature())
									.build();
		fd.analyze();
		fd.getLogs().forEach(System.out::println);
		fd.clearLogs();
	}
	
	private static void run(CommandLine cl, InputStream ressources) throws IOException {
		ClassNode cn = new ClassNode(Opcodes.ASM7);
		ClassReader cr = new ClassReader(ressources);
		cr.accept(cn, 0);
		
		analyserFeatures(cn);
		
/*		if ( cl.hasOption("info") ) {
			analyserFeatures(cn);
		} else {
			var version = cl.getOptionValue("target");
			// TODO
		}
*/
	}

	public static void main(String[] args) throws IOException {
		var commandLine = ArgsParser.parse(args);
		var directory = "src/main/resources/";
		URLClassLoader loader = new URLClassLoader(new URL[] { new URL("file:" + directory) });
		
		try (Stream<Path> walk = Files.walk(Paths.get(directory))) {
			walk.map(f -> f.getFileName().toString()).filter(f -> f.endsWith(".class")).forEach(f -> {
				var ressources = loader.getResourceAsStream(f);
				try {
					run(commandLine, ressources);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			loader.close();
		}
	}
}
