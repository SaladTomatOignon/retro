package fr.umlv.retro.detection;

import java.util.stream.Stream;

import org.objectweb.asm.tree.ClassNode;

import fr.umlv.retro.features.FeatureInfos;

public interface Detector {
	String featureName();
	void analyze(ClassNode cn);
	Stream<FeatureInfos> getRecognizedFeatures();
	void clear();
}
