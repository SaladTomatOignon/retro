package fr.umlv.retro.features;

import java.util.List;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class ConcatenationFeature implements FeatureTransformer, FeatureRecognizer {
	
	@Override
	public void transformFields(List<FieldNode> fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transformMethods(List<MethodNode> methods) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String featureName() {
		return "CONCATENATION";
	}

	@Override
	public void analyze(MethodNode cn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<FeatureInfos> getRecognizedFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
