package fr.umlv.retro.transformation;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.V10;
import static org.objectweb.asm.Opcodes.V11;
import static org.objectweb.asm.Opcodes.V12;
import static org.objectweb.asm.Opcodes.V13;
import static org.objectweb.asm.Opcodes.V14;
import static org.objectweb.asm.Opcodes.V1_5;
import static org.objectweb.asm.Opcodes.V1_6;
import static org.objectweb.asm.Opcodes.V1_7;
import static org.objectweb.asm.Opcodes.V1_8;
import static org.objectweb.asm.Opcodes.V9;

public class FeaturesTransformer {
	private final ClassNode cn;
	private final Collection<? extends Transformer> transformers;
	
	FeaturesTransformer(ClassNode cn, Collection<? extends Transformer> transformers) {
		this.cn = Objects.requireNonNull(cn);
		this.transformers = Objects.requireNonNull(transformers);
	}
	
	public void transform(int version) {
		if ( !Set.of(V1_5, V1_6, V1_7, V1_8, V9, V10, V11, V12, V13, V14).contains(version) ) {
			throw new IllegalArgumentException("Unsupported version");
		}
		
		cn.version = version;
		
		transformers.forEach(transformer -> {
			transformer.transform(cn);
		});
	}
}
