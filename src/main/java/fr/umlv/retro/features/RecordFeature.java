package fr.umlv.retro.features;

import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class RecordFeature extends AbstractFeature {
	private final static String FEATURE_NAME = "RECORD";

	public RecordFeature() {
		super(FEATURE_NAME);
	}

	@Override
	public void transformFields(List<FieldNode> fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transformMethods(List<MethodNode> methods) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void analyze(ClassNode cn) {
		// TODO Auto-generated method stub
		
	}
	
}
