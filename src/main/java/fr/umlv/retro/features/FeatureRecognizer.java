package fr.umlv.retro.features;

import java.util.List;

import org.objectweb.asm.tree.MethodNode;

public interface FeatureRecognizer {
	String featureName();
	void analyze(MethodNode cn);
	List<FeatureInfos> getRecognizedFeatures();
	void clear();
}
