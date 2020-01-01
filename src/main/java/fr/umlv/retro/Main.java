package fr.umlv.retro;

import java.io.IOException;
import java.io.InputStream;
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

/*         /!\ Main non définitif                  */

public class Main {
	private final static Map<String, Class<?>> featuresMap;
	static {
        Map<String, Class<?>> tmp = new HashMap<String, Class<?>>();
        tmp.put("try-with-resources", TryWithResourcesFeature.class);
        tmp.put("nestmates", NestMatesFeature.class);
        tmp.put("lambda", LambdaFeature.class);
        tmp.put("concatenation", ConcatenationFeature.class);
        tmp.put("record", RecordFeature.class);
        featuresMap = Collections.unmodifiableMap(tmp);
	}
	
	private static Object getFeature(String featureName) {
		Objects.requireNonNull(featureName);
		
		try {
			return featuresMap.get(featureName).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(featureName + " : unknown feature");
		}
	}
	
	private static boolean isDetector(Object feature) {
		return feature instanceof Detector; /*  <------  Pas de panique on va retirer ça.   */
	}
	
	private static void detectFeatures(ClassNode cn, FeaturesDetector detector) {
		Objects.requireNonNull(cn);
		Objects.requireNonNull(detector);
		
		detector.analyze();
		detector.getLogs().forEach(System.out::println);
		detector.clearLogs();
	}
	
	private static void showInfoFile(InputStream file, List<Detector> detectors) throws IOException {
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
	
	private static void showInfosFiles(List<InputStream> files, List<Object> features) throws IOException {
		Objects.requireNonNull(files);
		Objects.requireNonNull(features);
		
		features.forEach(feature -> {
			if ( !isDetector(feature)  ) {
				throw new IllegalArgumentException("This program is not able to detect feature '" + feature + "' for now");
			}
		});
		
		for (var file : files) {
			showInfoFile(file, features.stream().map(feature -> (Detector) feature).collect(Collectors.toList()));
		}
	}

	private static boolean isTransformer(Object feature) {
		return feature instanceof Transformer; /*  <------  Pas de panique on va retirer ça.   */
	}
	
	private static void transformFeatures(ClassNode cn, FeaturesTransformer transformer, int version) {
		Objects.requireNonNull(cn);
		Objects.requireNonNull(transformer);
		
		transformer.transform(version);
	}
	
	private static void retroFile(InputStream file, List<Transformer> transformers, int version) throws IOException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(transformers);
		
		ClassNode cn = new ClassNode(Opcodes.ASM7);
		ClassReader cr = new ClassReader(file);
		cr.accept(cn, 0);
		
		FeaturesTransformerBuilder ftb = new FeaturesTransformerBuilder(cn);
		transformers.forEach(detector -> ftb.append(detector));
		FeaturesTransformer ft = ftb.build();
		
		transformFeatures(cn, ft, version);
		// TODO Ecrire le fichier en sortie
	}

	private static void retroFiles(List<InputStream> files, List<Object> features, int version, boolean force) throws IOException {
		Objects.requireNonNull(files);
		Objects.requireNonNull(features);
		
		features.forEach(feature -> {
			if ( !isTransformer(feature)  ) {
				throw new IllegalArgumentException("This program is not able to transform feature '" + feature + "' for now");
			}
		});
		
		for (var file : files) {
			retroFile(file, features.stream().map(feature -> (Transformer) feature).collect(Collectors.toList()), version);
		}
	}

	public static void main(String[] args) throws IOException {
/*		var commandLine = ArgsParser.parse(args);
		var files = Files.getRessourceFiles(commandLine.getArgList());
		List<Object> features = new ArrayList<Object>();
		
		if ( commandLine.hasOption("feature") ) {
			features = Arrays.stream(commandLine.getOptionValues("feature")).map(Main::getFeature).collect(Collectors.toList());
		} else {
			features = featuresMap.keySet().stream().map(Main::getFeature).collect(Collectors.toList());
		}
		
		// On donne uniquement les infos.
		if ( commandLine.hasOption("info") ) {
			showInfosFiles(files, features);
		} else {
			retroFiles(files, features, Integer.parseInt(commandLine.getOptionValue("target")), commandLine.hasOption("force"));
		}
*/
		
		/* Partie ligne de commandes pas encore tout à fait opérationnelle :
		 * Seuls les .class dans le répertoire courant sont analysés et transformés. (les fichiers de sortie ne sont pas encore générés).
		 */
		var files = Files.getRessourceFiles(List.of("/"));
		List<Object> features = featuresMap.keySet().stream().map(Main::getFeature).collect(Collectors.toList());
		showInfosFiles(files, features);
		files.forEach(file -> {
			try {
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		files = Files.getRessourceFiles(List.of("/"));
		retroFiles(files, features, Opcodes.V1_5, true);
	}
}
