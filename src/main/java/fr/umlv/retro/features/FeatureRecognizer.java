package fr.umlv.retro.features;

import java.util.stream.Stream;

import org.objectweb.asm.tree.ClassNode;

public interface FeatureRecognizer {
	String featureName();
	void analyze(ClassNode cn);
	Stream<FeatureInfos> getRecognizedFeatures();
	void clear();
}
