package fr.umlv.retro.features;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import fr.umlv.retro.util.ByteCode;

public class TryWithResourcesFeature extends AbstractFeature {
	private final static String FEATURE_NAME = "TRY_WITH_RESOURCES";
	private AbstractInsnNode lastCloseMethodVisited;

	public TryWithResourcesFeature() {
		super(FEATURE_NAME);
	}

	@Override
	public void transform(ClassNode cn) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void analyze(ClassNode cn) {
		Objects.requireNonNull(cn);
		
		clear();
		cn.methods.forEach(method -> analyzeMethod(method, cn.name, cn.sourceFile));
	}
	
	private void analyzeMethod(MethodNode mn, String className, String sourceName) {
		Objects.requireNonNull(mn);
		
		mn.tryCatchBlocks.forEach(tryCatch -> {
			if ( isTryCatchWithResources(mn.tryCatchBlocks, tryCatch) ) {
				addFeatureInfos(new FeatureInfos(featureName(),
						className + "." + mn.name + mn.desc,
						sourceName + ":" + getTryCatchLine(tryCatch),
						"try-with-ressources on " + ((MethodInsnNode)lastCloseMethodVisited).owner,
						null));
			}
		});
	}

	private boolean isTryCatchWithResources(List<TryCatchBlockNode> tryCatchBlocks, TryCatchBlockNode tryCatch) {
		Objects.requireNonNull(tryCatchBlocks);
		Objects.requireNonNull(tryCatch);
		
		var nestedTryCatch = getNestedTryCatch(tryCatchBlocks, tryCatch.handler);
		
		if ( nestedTryCatch.isPresent() ) { // On vérifie que le tryCatch contient un autre tryCatch imbriqué.
			if ( !Objects.isNull(lastCloseMethodVisited = ByteCode.findInstruction(ByteCode.isCloseMethod, tryCatch.end, tryCatch.handler)) ) { // On vérifie qu'il y a un close() dans le finally du tryCatch initial.
				if ( !Objects.isNull(ByteCode.findInstruction(ByteCode.isCloseMethod, nestedTryCatch.get().start, nestedTryCatch.get().end)) ) { // On vérifie qu'il y a un close() dans le try du tryCatch imbriqué.
					if ( !Objects.isNull(ByteCode.findInstruction(ByteCode.isAddSuppressedMethod, nestedTryCatch.get().handler, ByteCode.getNextLabel(nestedTryCatch.get().handler))) ) { // On vérifie qu'il y a un addSuppressed() dans le catch du tryCatch imbriqué.
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private Optional<TryCatchBlockNode> getNestedTryCatch(List<TryCatchBlockNode> tryCatchBlocks, LabelNode handler) {
		Objects.requireNonNull(tryCatchBlocks);
		Objects.requireNonNull(handler);
		
		LabelNode nextLabel = ByteCode.getNextLabel(handler);
		if ( Objects.isNull(nextLabel) ) {
			return Optional.empty();
		}
		
		return tryCatchBlocks.stream().filter(tcb -> tcb.start.equals(nextLabel)).findFirst();
	}
	
	private int getTryCatchLine(TryCatchBlockNode tryCatch) {
		Objects.requireNonNull(tryCatch);
		
		var lineInstr = tryCatch.start.getNext();
		
		return Objects.isNull(lineInstr) ? 0 : ((LineNumberNode) lineInstr).line - 1;
	}

}
