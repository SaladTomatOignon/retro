package fr.umlv.retro;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
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
import fr.umlv.retro.transformation.FeaturesTransformer;
import fr.umlv.retro.transformation.FeaturesTransformerBuilder;
import fr.umlv.retro.transformation.Transformer;
import fr.umlv.retro.util.Files;

public class Main {
	private final static Map<String, Class<? extends Feature>> featuresMap;
	static {
        Map<String, Class<? extends Feature>> tmp = new HashMap<String, Class<? extends Feature>>();
        tmp.put("try-with-resources", TryWithResourcesFeature.class);
        tmp.put("nestmates", NestMatesFeature.class);
        tmp.put("lambda", LambdaFeature.class);
        tmp.put("concatenation", ConcatenationFeature.class);
        tmp.put("record", RecordFeature.class);
        featuresMap = Collections.unmodifiableMap(tmp);
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
	
	private static void showInfoFile(InputStream file, List<? extends Detector> detectors) throws IOException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(detectors);
		
		ClassNode cn = new ClassNode(Opcodes.ASM7);
		ClassReader cr = new ClassReader(file);
		cr.accept(cn, 0);
		
		FeaturesDetectorBuilder fdb = new FeaturesDetectorBuilder(cn);
		detectors.forEach(detector -> fdb.append(detector));
		FeaturesDetector fd = fdb.build();
		
		detectFeatures(cn, fd);
	}
	
	private static void showInfosFiles(Collection<InputStream> files, List<Feature> features) throws IOException {
		Objects.requireNonNull(files);
		Objects.requireNonNull(features);
		
		for (var file : files) {
			showInfoFile(file, features);
		}
	}
	
	private static void transformFeatures(ClassNode cn, FeaturesTransformer transformer, int version, boolean force) {
		Objects.requireNonNull(cn);
		Objects.requireNonNull(transformer);
		
		transformer.transform(version, force);
	}
	
	private static void retroFile(Path filePath, InputStream fileResource, List<? extends Transformer> transformers, int version, boolean force) throws IOException {
		Objects.requireNonNull(filePath);
		Objects.requireNonNull(fileResource);
		Objects.requireNonNull(transformers);
		
		ClassNode cn = new ClassNode(Opcodes.ASM7);
		ClassReader cr = new ClassReader(fileResource);
		cr.accept(cn, 0);
		
		FeaturesTransformerBuilder ftb = new FeaturesTransformerBuilder(cn);
		transformers.forEach(detector -> ftb.append(detector));
		FeaturesTransformer ft = ftb.build();
		
		transformFeatures(cn, ft, version, force);
		Files.generateOutputFile(cn, filePath);
	}

	private static void retroFiles(Map<Path, InputStream> files, List<Feature> features, int version, boolean force) throws IOException {
		Objects.requireNonNull(files);
		Objects.requireNonNull(features);
		
		for (var file : files.entrySet()) {
			retroFile(file.getKey(), file.getValue(), features, version, force);
		}
	}

	public static void main(String[] args) throws IOException {
/*		var commandLine = ArgsParser.parse(args);
		var files = Files.getRessourceFiles(commandLine.getArgList());
		List<Feature> features = new ArrayList<Feature>();
		
		// On r�cup�re les features donn�es en arguments.
		if ( commandLine.hasOption("feature") ) {
			features = Arrays.stream(commandLine.getOptionValues("feature")).map(Main::getFeature).collect(Collectors.toList());
		} else {
			features = featuresMap.keySet().stream().map(Main::getFeature).collect(Collectors.toList());
		}
		
		// On donne uniquement les infos.
		if ( commandLine.hasOption("info") ) {
			showInfosFiles(files.values(), features);
		} else {
			// Sinon on transforme.
			try {
				retroFiles(files, features, Integer.parseInt(commandLine.getOptionValue("target")), commandLine.hasOption("force"));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("target version must be a number");
			}
		}
*/
	
		
		
		
		/* Désolé, pour l'instant le parseur de ligne de commande n'est pas encore fait,
		 * Merci de renseigner manuellement les valeurs.
		 */
		
		var files = Files.getRessourceFiles(List.of("/")); // Répertoire où chercher les .class (par défaut repertoire courant)
		List<Feature> features = Arrays.stream(new String[] {"try-with-resources", "nestmates", "lambda", "concatenation", "record"}).map(Main::getFeature).collect(Collectors.toList()); // Liste des features � retro
		
		boolean donner_uniquement_les_infos = true; // Correspond à l'option -infos
		
		if ( donner_uniquement_les_infos ) {
			showInfosFiles(files.values(), features); // Detecter et afficher les features.
		} else {
			retroFiles(files, features, Opcodes.V1_5, true); // Transformer les features.
		}
	}
}
