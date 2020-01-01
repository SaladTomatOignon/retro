package fr.umlv.retro.detection;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

public class FeaturesDetectorBuilder {
	private ClassNode cn;
	private final Set<Detector> recognizers;
	
	public FeaturesDetectorBuilder(ClassNode cn) {
		this.cn = Objects.requireNonNull(cn);
		this.recognizers = new HashSet<Detector>();
	}
	
	public FeaturesDetectorBuilder append(Detector recognizer) {
		recognizers.add(Objects.requireNonNull(recognizer));
		
		return this;
	}
	
	public FeaturesDetectorBuilder remove(Detector recognizer) {
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
