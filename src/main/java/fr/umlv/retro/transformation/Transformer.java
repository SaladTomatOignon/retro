package fr.umlv.retro.transformation;

import org.objectweb.asm.tree.ClassNode;

public interface Transformer {
	void transform(ClassNode cn);
}
