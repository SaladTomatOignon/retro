package fr.umlv.retro.detection;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

import fr.umlv.retro.features.FeatureRecognizer;

public class FeaturesDetectorBuilder {
	private ClassNode cn;
	private final Set<FeatureRecognizer> recognizers;
	
	public FeaturesDetectorBuilder(ClassNode cn) {
		this.cn = Objects.requireNonNull(cn);
		this.recognizers = new HashSet<FeatureRecognizer>();
	}
	
	public FeaturesDetectorBuilder append(FeatureRecognizer recognizer) {
		recognizers.add(Objects.requireNonNull(recognizer));
		
		return this;
	}
	
	public FeaturesDetectorBuilder remove(FeatureRecognizer recognizer) {
		recognizers.remove(Objects.requireNonNull(recognizer));
		
		return this;
	}
	
	public FeaturesDetectorBuilder withNewClassNode(ClassNode cn) {
		this.cn = Objects.requireNonNull(cn);
		
		return this;
	}
	
	public FeaturesDetector build() {
		return new FeaturesDetector(cn, recognizers);
	}
}
