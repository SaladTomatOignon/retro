package fr.umlv.retro.features;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodNode;

import fr.umlv.retro.util.ByteCode;

import static org.objectweb.asm.tree.AbstractInsnNode.INVOKE_DYNAMIC_INSN;

public class LambdaFeature extends AbstractFeature {
	private final static String FEATURE_NAME = "LAMBDA";

	public LambdaFeature() {
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
		Objects.requireNonNull(className);
		Objects.requireNonNull(sourceName);
		
		mn.instructions.forEach(instr -> {
			if ( isLambdaInstruction(instr) ) {
				InvokeDynamicInsnNode idInstr = (InvokeDynamicInsnNode) instr;
				
				String details = "lambda " + Type.getReturnType(idInstr.desc).getInternalName() + " capture " +
								Arrays.stream(Type.getArgumentTypes(idInstr.desc)).map(x -> x.toString()).collect(Collectors.joining(", ", "[", "]")) +
								" calling " + idInstr.bsmArgs[1].toString().split(" ")[0];
				
				addFeatureInfos(new FeatureInfos(featureName(), className + "." + mn.name + mn.desc, sourceName + ":" + ByteCode.getLineInst(idInstr), details, idInstr));
			}
		});
	}
	
	private boolean isLambdaInstruction(AbstractInsnNode instr) {
		Objects.requireNonNull(instr);
		
		if ( instr.getType() == INVOKE_DYNAMIC_INSN ) {
			InvokeDynamicInsnNode idInstr = (InvokeDynamicInsnNode) instr;
			
			return idInstr.bsm.getOwner().equals(Type.getType(java.lang.invoke.LambdaMetafactory.class).getInternalName()) &&
					idInstr.bsm.getName().equals("metafactory");
		}
		
		return false;
	}

}
