package fr.umlv.retro.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;

import static org.objectweb.asm.tree.AbstractInsnNode.LINE;

abstract class AbstractFeature implements FeatureTransformer, FeatureRecognizer {
	private final String name;
	private final List<FeatureInfos> recognizedFeatures;
	
	AbstractFeature(String name) {
		this.name = Objects.requireNonNull(name);
		this.recognizedFeatures = new ArrayList<FeatureInfos>();
	}
	
	void addFeatureInfos(FeatureInfos fi) {
		recognizedFeatures.add(Objects.requireNonNull(fi));
	}
	
	int getLineInst(AbstractInsnNode instr) {
		Objects.requireNonNull(instr);
		
		// On remonte jusqu'au dernier LineNumberNode rencontré.
		while ( instr.getType() != LINE) {
			instr = instr.getPrevious();
			if ( Objects.isNull(instr) ) {
				return 0;
			}
		}
		
		// On est garantis que instr est de type LineNumberNode. */
		return ((LineNumberNode) instr).line;
	}
	
	@Override
	public String featureName() {
		return name;
	}
	
	@Override
	public Stream<FeatureInfos> getRecognizedFeatures() {
		return recognizedFeatures.stream();
	}
	
	@Override
	public void clear() {
		recognizedFeatures.clear();
	}
}
