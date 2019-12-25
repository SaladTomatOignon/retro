package fr.umlv.retro.classNodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import fr.umlv.retro.transformers.Transformer;

import static org.objectweb.asm.Opcodes.ASM7;

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
	
	public void transform(Transformer transformer) {
		Objects.requireNonNull(transformer);
		
		cn.version = transformer.getVersion();
		
		transformer.transformFields(cn.fields);
		transformer.transformMethods(cn.methods);
	}
}
