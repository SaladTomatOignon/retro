package fr.umlv.retro.features;

import java.util.List;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public interface FeatureTransformer {
	void transformFields(List<FieldNode> fields);
	void transformMethods(List<MethodNode> methods);
}
