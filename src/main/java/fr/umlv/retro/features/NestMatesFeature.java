package fr.umlv.retro.features;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.objectweb.asm.tree.ClassNode;

public class NestMatesFeature extends AbstractFeature {
	private final static String FEATURE_NAME = "NESTMATES";

	public NestMatesFeature() {
		super(FEATURE_NAME);
	}
	
	@Override
	public void transform(ClassNode cn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void analyze(ClassNode cn) {
		Objects.requireNonNull(cn);
		String nm, nh;
		
		clear();
		
		if ( !(nm = lookForNestMates(cn.nestMembers)).isEmpty() ) {
			addFeatureInfos(new FeatureInfos(featureName(), cn.name, cn.sourceFile, nm.replace("#", cn.name), null));
		}
			
		if ( !(nh = lookForNestHost(cn.nestHostClass)).isEmpty() ) {
			addFeatureInfos(new FeatureInfos(featureName(), cn.name, cn.sourceFile, nh, null));
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
