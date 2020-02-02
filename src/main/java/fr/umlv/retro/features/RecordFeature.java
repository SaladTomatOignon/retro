package fr.umlv.retro.features;

import java.util.stream.Collectors;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public class RecordFeature extends Feature {
	private final static String FEATURE_NAME = "RECORD";

	public RecordFeature() {
		super(FEATURE_NAME);
	}
	
	@Override
	public void transform(ClassNode cn) {
		cn.superName = Type.getType(java.lang.Object.class).getInternalName();
		
		cn.methods.removeIf(method -> method.name.equals("toString") ||
									  method.name.equals("hashCode") ||
								  	  method.name.equals("equals"));
	}

	@Override
	public void analyze(ClassNode cn) {
		if ( cn.superName.equals("java/lang/Record") ) {
			addFeatureInfos(new FeatureInfos(featureName(), cn.name, cn.sourceFile,
					"Record fields : " + cn.fields.stream().map(field -> '(' + field.desc + ')' + field.name).collect(Collectors.joining(", ")),
					null));
		}
	}
	
}
