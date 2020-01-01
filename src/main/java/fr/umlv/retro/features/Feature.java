package fr.umlv.retro.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.objectweb.asm.tree.ClassNode;

import fr.umlv.retro.detection.Detector;
import fr.umlv.retro.transformation.Transformer;

public class Feature implements Transformer, Detector {
	private final String name;
	private final List<FeatureInfos> recognizedFeatures;
	
	Feature(String name) {
		this.name = Objects.requireNonNull(name);
		this.recognizedFeatures = new ArrayList<FeatureInfos>();
	}
	
	void addFeatureInfos(FeatureInfos fi) {
		recognizedFeatures.add(Objects.requireNonNull(fi));
	}
	
	@Override
	public String featureName() {
		return name;
	}
	
	@Override
	public Stream<FeatureInfos> getRecognizedFeatures() {
		return recognizedFeatures.stream();
	}
	
	@Override
	public void clear() {
		recognizedFeatures.clear();
	}

	@Override
	public void analyze(ClassNode cn) {
		// Do nothing
	}

	@Override
	public void transform(ClassNode cn) {
		// Do nothing
	}
}
