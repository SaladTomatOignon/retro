package fr.umlv.retro.transformation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

public class FeaturesTransformerBuilder {
	private ClassNode cn;
	private final Set<Transformer> transformers;
	
	public FeaturesTransformerBuilder(ClassNode cn) {
		this.cn = Objects.requireNonNull(cn);
		this.transformers = new HashSet<Transformer>();
	}
	
	public FeaturesTransformerBuilder append(Transformer transformer) {
		transformers.add(Objects.requireNonNull(transformer));
		
		return this;
	}
	
	public FeaturesTransformerBuilder remove(Transformer transformer) {
		transformers.remove(Objects.requireNonNull(transformer));
		
		return this;
	}
	
	public FeaturesTransformerBuilder withNewClassNode(ClassNode cn) {
		this.cn = Objects.requireNonNull(cn);
		
		return this;
	}
	
	public FeaturesTransformer build() {
		return new FeaturesTransformer(cn, transformers);
	}
}
