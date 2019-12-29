package fr.umlv.retro.features;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class NestMatesFeature extends AbstractFeature {
	private final static String FEATURE_NAME = "NESTMATES";

	public NestMatesFeature() {
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
		Objects.requireNonNull(cn);
		String nm, nh;
		
		if ( !(nm = lookForNestMates(cn.nestMembers)).isEmpty() ) {
			addFeatureInfos(new FeatureInfos(featureName(), cn.name, cn.sourceFile, nm.replace("#", cn.name)));
		}
			
		if ( !(nh = lookForNestHost(cn.nestHostClass)).isEmpty() ) {
			addFeatureInfos(new FeatureInfos(featureName(), cn.name, cn.sourceFile, nh));
		}
	}
	
	private String lookForNestMates(List<String> nestMates) {
		if ( !Objects.isNull(nestMates) ) {
			return "nest host " + "#" + " members " + nestMates.stream().collect(Collectors.joining(", ", "[", "]"));
		} else {
			return "";
		}
	}
	
	private String lookForNestHost(String nestHost) {
		if ( !Objects.isNull(nestHost) ) {
			return "nestmate of " + nestHost;
		} else {
			return "";
		}
	}

}
