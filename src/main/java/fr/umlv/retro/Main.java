package fr.umlv.retro;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import fr.umlv.retro.detection.Detector;
import fr.umlv.retro.detection.FeaturesDetector;
import fr.umlv.retro.detection.FeaturesDetectorBuilder;
import fr.umlv.retro.features.*;
import fr.umlv.retro.options.ArgsParser;
import fr.umlv.retro.transformation.FeaturesTransformer;
import fr.umlv.retro.transformation.FeaturesTransformerBuilder;
import fr.umlv.retro.transformation.Transformer;
import fr.umlv.retro.util.Files;

import static org.objectweb.asm.Opcodes.V1_1;
import static org.objectweb.asm.Opcodes.V1_2;
import static org.objectweb.asm.Opcodes.V1_3;
import static org.objectweb.asm.Opcodes.V1_4;
import static org.objectweb.asm.Opcodes.V1_5;
import static org.objectweb.asm.Opcodes.V1_6;
import static org.objectweb.asm.Opcodes.V1_7;
import static org.objectweb.asm.Opcodes.V1_8;
import static org.objectweb.asm.Opcodes.V9;
import static org.objectweb.asm.Opcodes.V10;
import static org.objectweb.asm.Opcodes.V11;
import static org.objectweb.asm.Opcodes.V12;
import static org.objectweb.asm.Opcodes.V13;
import static org.objectweb.asm.Opcodes.V14;

public class Main {
	private final static Map<String, Class<? extends Feature>> featuresMap;
	private final static Map<Integer, Integer> versionNumber;
	static {
        Map<String, Class<? extends Feature>> tmp = new HashMap<String, Class<? extends Feature>>();
        tmp.put("try-with-resources", TryWithResourcesFeature.class);
        tmp.put("nestmates", NestMatesFeature.class);
        tmp.put("lambda", LambdaFeature.class);
        tmp.put("concatenation", ConcatenationFeature.class);
        tmp.put("record", RecordFeature.class);
		featuresMap = Collections.unmodifiableMap(tmp);
	}
	
	static {
        Map<Integer, Integer> tmp = new HashMap<Integer, Integer>();
        tmp.put(1, V1_1);	tmp.put(2, V1_2);
        tmp.put(3, V1_3);	tmp.put(4, V1_4);
        tmp.put(5, V1_5);	tmp.put(6, V1_6);
        tmp.put(7, V1_7);	tmp.put(8, V1_8);
        tmp.put(9, V9);		tmp.put(10, V10);
        tmp.put(11, V11);	tmp.put(12, V12);
        tmp.put(13, V13);	tmp.put(14, V14);
        versionNumber = Collections.unmodifiableMap(tmp);
	}
	
	private static Feature getFeature(String featureName) {
		Objects.requireNonNull(featureName);
		
		try {
			return featuresMap.get(featureName).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(featureName + " : unknown feature");
		}
	}
	
	private static void detectFeatures(ClassNode cn, FeaturesDetector detector) {
		Objects.requireNonNull(cn);
		Objects.requireNonNull(detector);
		
		detector.analyze();
		detector.getLogs().forEach(System.out::println);
		detector.clearLogs();
	}
	
	private static void showInfoFile(byte[] dataFile, List<? extends Detector> detectors) throws IOException {
		Objects.requireNonNull(dataFile);
		Objects.requireNonNull(detectors);
		
		ClassNode cn = new ClassNode(Opcodes.ASM7);
		ClassReader cr = new ClassReader(dataFile);
		cr.accept(cn, 0);
		
		FeaturesDetectorBuilder fdb = new FeaturesDetectorBuilder(cn);
		detectors.forEach(detector -> fdb.append(detector));
		FeaturesDetector fd = fdb.build();
		
		detectFeatures(cn, fd);
	}
	
	private static void showInfosFiles(Collection<byte[]> dataFiles, List<Feature> features) throws IOException {
		Objects.requireNonNull(dataFiles);
		Objects.requireNonNull(features);
		
		for (var dataFile : dataFiles) {
			showInfoFile(dataFile, features);
		}
	}
	
	private static void transformFeatures(ClassNode cn, FeaturesTransformer transformer, int version, boolean force) {
		Objects.requireNonNull(cn);
		Objects.requireNonNull(transformer);
		
		transformer.transform(version, force);
	}
	
	private static void retroFile(Path filePath, byte[] dataFile, List<? extends Transformer> transformers, int version, boolean force) throws IOException {
		Objects.requireNonNull(filePath);
		Objects.requireNonNull(dataFile);
		Objects.requireNonNull(transformers);
		
		ClassNode cn = new ClassNode(Opcodes.ASM7);
		ClassReader cr = new ClassReader(dataFile);
		cr.accept(cn, 0);
		
		FeaturesTransformerBuilder ftb = new FeaturesTransformerBuilder(cn);
		transformers.forEach(detector -> ftb.append(detector));
		FeaturesTransformer ft = ftb.build();
		
		transformFeatures(cn, ft, version, force);
		Files.generateOutputFile(cn, filePath);
	}

	private static void retroFiles(Map<Path, byte[]> dataFiles, List<Feature> features, int version, boolean force) throws IOException {
		Objects.requireNonNull(dataFiles);
		Objects.requireNonNull(features);
		
		for (var dataFile : dataFiles.entrySet()) {
			retroFile(dataFile.getKey(), dataFile.getValue(), features, version, force);
		}
	}

	public static void main(String[] args) throws IOException {
		var commandLine = ArgsParser.parse(args);
		var files = Files.getResourceFiles(commandLine.getArgList());
		List<Feature> features = new ArrayList<Feature>();
		
		// On récupère les features données en arguments.
		if ( commandLine.hasOption("feature") ) {
			features = Arrays.stream(commandLine.getOptionValues("feature")).map(Main::getFeature).collect(Collectors.toList());
		} else {
			features = featuresMap.keySet().stream().map(Main::getFeature).collect(Collectors.toList());
		}
		
		if ( commandLine.hasOption("help") ) {
			ArgsParser.displayHelp();
		} else if ( commandLine.hasOption("info") ) {
			// On donne uniquement les infos.
			showInfosFiles(files.values(), features);
		} else {
			// Sinon on transforme.
			try {
				retroFiles(files, features, versionNumber.getOrDefault(Integer.parseInt(commandLine.getOptionValue("target")), Integer.parseInt(commandLine.getOptionValue("target"))), commandLine.hasOption("force"));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("target version must be a number");
			}
		}

	}
}
