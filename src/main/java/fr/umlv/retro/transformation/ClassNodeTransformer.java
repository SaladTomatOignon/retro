package fr.umlv.retro.transformation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import fr.umlv.retro.features.FeatureTransformer;

import static org.objectweb.asm.Opcodes.ASM7;
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

public class ClassNodeTransformer {
	private final ClassNode cn;
	
	private ClassNodeTransformer(ClassNode cn) {
		this.cn = Objects.requireNonNull(cn);
	}
	
	public static ClassNodeTransformer createClassNodeTransformer(InputStream inputStream) throws IOException {
		Objects.requireNonNull(inputStream);
		
		ClassReader cr = new ClassReader(inputStream);
		ClassNode cn = new ClassNode(ASM7);
		
		cr.accept(cn, 0);
		
		return new ClassNodeTransformer(cn);
	}
	
	public void transform(FeatureTransformer transformer, int version) {
		Objects.requireNonNull(transformer);
		
		if ( List.of(V1_5, V1_6, V1_7, V1_8, V9, V10, V11, V12, V13, V14)
				.contains(version) ) {
			throw new IllegalArgumentException("Unsupported version");
		}
		
		cn.version = version;
		
		transformer.transformFields(cn.fields);
		transformer.transformMethods(cn.methods);
	}
}
