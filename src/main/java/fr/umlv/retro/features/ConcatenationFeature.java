package fr.umlv.retro.features;

import static org.objectweb.asm.tree.AbstractInsnNode.INVOKE_DYNAMIC_INSN;

import java.util.List;
import java.util.Objects;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ConcatenationFeature extends AbstractFeature {
	private final static String FEATURE_NAME = "CONCATENATION";
	
	public ConcatenationFeature() {
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
		cn.methods.forEach(method -> analyzeMethod(method, cn.name, cn.sourceFile));
	}

	private void analyzeMethod(MethodNode mn, String className, String sourceName) {
		Objects.requireNonNull(mn);
		Objects.requireNonNull(className);
		Objects.requireNonNull(sourceName);
		
		mn.instructions.forEach(instr -> {
			if ( isConcatInstruction(instr) ) {
				InvokeDynamicInsnNode idInstr = (InvokeDynamicInsnNode) instr;
				
				String details = "pattern " + convertUnicode(idInstr.bsmArgs[0].toString());
				
				addFeatureInfos(new FeatureInfos(featureName(), className + "." + mn.name + mn.desc, sourceName + ":" + getLineInst(idInstr), details));

			}
		});
	}
	
	private boolean isConcatInstruction(AbstractInsnNode instr) {
		Objects.requireNonNull(instr);
		
		if ( instr.getType() == INVOKE_DYNAMIC_INSN ) {
			InvokeDynamicInsnNode idInstr = (InvokeDynamicInsnNode) instr;
			
			return idInstr.bsm.getOwner().equals(Type.getType(java.lang.invoke.StringConcatFactory.class).getInternalName()) &&
					idInstr.bsm.getName().equals("makeConcatWithConstants");
		}
		
		return false;
	}
	
	private String convertUnicode(String unicodeString) {
		return unicodeString.replace("\u0001", "%1").replace("\u0002", "%2");
	}

}
