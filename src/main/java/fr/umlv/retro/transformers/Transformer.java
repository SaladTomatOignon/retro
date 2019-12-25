package fr.umlv.retro.transformers;

import java.util.List;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public interface Transformer {
	int getVersion();
	void transformFields(List<FieldNode> fields);
	void transformMethods(List<MethodNode> methods);
}
