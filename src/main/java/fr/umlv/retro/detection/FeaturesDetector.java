package fr.umlv.retro.detection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.objectweb.asm.tree.ClassNode;

import fr.umlv.retro.features.FeatureInfos;
import fr.umlv.retro.features.FeatureRecognizer;

public class FeaturesDetector {
	private final ClassNode cn;
	private final Collection<? extends FeatureRecognizer> recognizers;
	private final List<String> logs;
	
	FeaturesDetector(ClassNode cn, Collection<? extends FeatureRecognizer> recognizers) {
		this.cn = Objects.requireNonNull(cn);
		this.recognizers = Objects.requireNonNull(recognizers);
		
		logs = new ArrayList<String>();
	}
	
	public void analyze() {
		recognizers.forEach(recognizer -> {
			recognizer.analyze(cn);
			recognizer.getRecognizedFeatures().forEach(this::logFeature);
			recognizer.clear();
		});
	}
	
	private void logFeature(FeatureInfos fi) {
		Objects.requireNonNull(fi);
		
		logs.add(fi.getName() + " at " + fi.getClasslocation() + 
				" (" + fi.getSourcelocation() + "): " +
				fi.getDetails());
	}
	
	public Stream<String> getLogs() {
		return logs.stream();
	}
	
	public void clearLogs() {
		logs.clear();
	}
}
