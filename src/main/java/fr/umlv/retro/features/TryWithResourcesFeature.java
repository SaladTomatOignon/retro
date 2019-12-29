package fr.umlv.retro.features;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import static org.objectweb.asm.tree.AbstractInsnNode.LABEL;
import static org.objectweb.asm.tree.AbstractInsnNode.METHOD_INSN;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;;

public class TryWithResourcesFeature extends AbstractFeature {
	private final static String FEATURE_NAME = "TRY_WITH_RESOURCES";
	private AbstractInsnNode lastCloseMethodVisited;
	
	private final static Predicate<AbstractInsnNode> isCloseMethod = instr -> {
		if ( instr.getType() == METHOD_INSN ) {
			MethodInsnNode methodInstr = (MethodInsnNode) instr;
			
			return methodInstr.name.equals("close");
		}
		 
		return false;
	};
	
	private final static Predicate<AbstractInsnNode> isAddSuppressedMethod = instr -> {
		if ( instr.getType() == METHOD_INSN ) {
			MethodInsnNode methodInstr = (MethodInsnNode) instr;
			
			return methodInstr.getOpcode() == INVOKEVIRTUAL &&
					methodInstr.owner.equals(Type.getType(java.lang.Throwable.class).getInternalName()) &&
					methodInstr.name.equals("addSuppressed");
		}
		 
		return false;
	};

	public TryWithResourcesFeature() {
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
		
		cn.methods.forEach(method -> analyzeMethod(method, cn.name, cn.sourceFile));
	}
	
	private void analyzeMethod(MethodNode mn, String className, String sourceName) {
		Objects.requireNonNull(mn);
		
		mn.tryCatchBlocks.forEach(tryCatch -> {
			if ( isTryCatchWithResources(mn.tryCatchBlocks, tryCatch) ) {
				addFeatureInfos(new FeatureInfos(featureName(),
						className + "." + mn.name + mn.desc,
						sourceName + ":" + getTryCatchLine(tryCatch),
						"try-with-ressources on " + ((MethodInsnNode)lastCloseMethodVisited).owner));
			}
		});
	}

	private boolean isTryCatchWithResources(List<TryCatchBlockNode> tryCatchBlocks, TryCatchBlockNode tryCatch) {
		Objects.requireNonNull(tryCatchBlocks);
		Objects.requireNonNull(tryCatch);
		
		var nestedTryCatch = getNestedTryCatch(tryCatchBlocks, tryCatch.handler);
		
		if ( nestedTryCatch.isPresent() ) { // On v�rifie que le tryCatch contient un autre tryCatch imbriqu�.
			if ( !Objects.isNull(lastCloseMethodVisited = findInstruction(isCloseMethod, tryCatch.end, tryCatch.handler)) ) { // On v�rifie qu'il y a un close() dans le finally du tryCatch initial.
				if ( !Objects.isNull(findInstruction(isCloseMethod, nestedTryCatch.get().start, nestedTryCatch.get().end)) ) { // On v�rifie qu'il y a un close() dans le try du tryCatch imbriqu�.
					if ( !Objects.isNull(findInstruction(isAddSuppressedMethod, nestedTryCatch.get().handler, getNextLabel(nestedTryCatch.get().handler))) ) { // On v�rifie qu'il y a un addSuppressed() dans le catch du tryCatch imbriqu�.
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private AbstractInsnNode findInstruction(Predicate<AbstractInsnNode> isInstrSatisfied, LabelNode start, LabelNode end) {
		Objects.requireNonNull(isInstrSatisfied);
		Objects.requireNonNull(start);
		Objects.requireNonNull(end);
		
		// On parcourt les instructions de start jusqu'� end.
		for (AbstractInsnNode currentInst = start; !Objects.isNull(currentInst) && !currentInst.equals(end); currentInst = currentInst.getNext()) {
			if ( isInstrSatisfied.test(currentInst) ) {
				return currentInst;
			}
		}
		
		return null;
	}
	
	private Optional<TryCatchBlockNode> getNestedTryCatch(List<TryCatchBlockNode> tryCatchBlocks, LabelNode handler) {
		Objects.requireNonNull(tryCatchBlocks);
		Objects.requireNonNull(handler);
		
		LabelNode nextLabel = getNextLabel(handler);
		if ( Objects.isNull(nextLabel) ) {
			return Optional.empty();
		}
		
		return tryCatchBlocks.stream().filter(tcb -> tcb.start.equals(nextLabel)).findFirst();
	}

	private LabelNode getNextLabel(LabelNode handler) {
		Objects.requireNonNull(handler);
		
		AbstractInsnNode currentInst = handler.getNext();
		
		while ( !Objects.isNull(currentInst) ) {
			if ( (currentInst.getType() == LABEL) ) {
				return (LabelNode) currentInst;
			}
			
			currentInst = currentInst.getNext();
		}
		
		return null;
	}
	
	private int getTryCatchLine(TryCatchBlockNode tryCatch) {
		Objects.requireNonNull(tryCatch);
		
		var lineInstr = tryCatch.start.getNext();
		
		return Objects.isNull(lineInstr) ? 0 : ((LineNumberNode) lineInstr).line - 1;
	}

}
