package fr.umlv.retro.features;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Type.VOID_TYPE;
import static org.objectweb.asm.tree.AbstractInsnNode.INVOKE_DYNAMIC_INSN;
import static java.text.CharacterIterator.DONE;

import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import fr.umlv.retro.util.ByteCode;

public class ConcatenationFeature extends Feature {
	private final static String FEATURE_NAME = "CONCATENATION";
	
	public ConcatenationFeature() {
		super(FEATURE_NAME);
	}
	
	@Override
	public void transform(ClassNode cn) {
		Objects.requireNonNull(cn);
		
		cn.methods.forEach(method -> {
			analyzeMethod(method, cn.name, cn.sourceFile);
			transformMethod(method.instructions);
			clear();
		});
	}
	
	private void transformMethod(InsnList instructions) {
		Objects.requireNonNull(instructions);
		
		getRecognizedFeatures().forEach(concatFeature -> {
			transformConcatenation((InvokeDynamicInsnNode) concatFeature.getInstrMarker(), instructions);
		});
	}

	private void transformConcatenation(InvokeDynamicInsnNode makeConcatWithConstants, InsnList instructions) {
		Objects.requireNonNull(makeConcatWithConstants);
		Objects.requireNonNull(instructions);
		
		var vars = parseConcatStringArgument(makeConcatWithConstants.bsmArgs[0].toString());
		var varsType = Arrays.asList(Type.getArgumentTypes(makeConcatWithConstants.desc));
		var loadInstrs = ByteCode.getLastLoadInstructions(makeConcatWithConstants, varsType.size());
		Collections.reverse(loadInstrs);
		
		InsnList newInsnList = createConcatInstructions(vars, loadInstrs, varsType);
		
		AbstractInsnNode firstInstructionBefore = loadInstrs.get(0).getPrevious();
		loadInstrs.forEach(instr -> instructions.remove(instr));
		instructions.remove(makeConcatWithConstants);
		instructions.insert(firstInstructionBefore, newInsnList);
	}
	
	private InsnList createConcatInstructions(List<String> vars, List<VarInsnNode> loadInstrs, List<Type> varsType) {
		Objects.requireNonNull(vars);
		Objects.requireNonNull(loadInstrs);
		Objects.requireNonNull(varsType);
		
		InsnList stringBuilderInsts = new InsnList();
		initiateStringBuilder(stringBuilderInsts);
		appendVars(stringBuilderInsts, vars, loadInstrs, varsType);
		stringBuilderInsts.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(java.lang.StringBuilder.class).getInternalName(), "toString", Type.getMethodDescriptor(Type.getType(java.lang.String.class))));
		
		return stringBuilderInsts;
	}

	private void initiateStringBuilder(InsnList stringBuilderInsts) {
		Objects.requireNonNull(stringBuilderInsts);
		
		stringBuilderInsts.add(new TypeInsnNode(NEW, Type.getType(java.lang.StringBuilder.class).getInternalName()));
		stringBuilderInsts.add(new InsnNode(DUP));
		stringBuilderInsts.add(new MethodInsnNode(INVOKESPECIAL, Type.getType(java.lang.StringBuilder.class).getInternalName(), "<init>", Type.getMethodDescriptor(VOID_TYPE)));
	}
	
	private void appendVars(InsnList stringBuilderInsts, List<String> vars, List<VarInsnNode> loadInstrs, List<Type> varsType) {
		Objects.requireNonNull(stringBuilderInsts);
		Objects.requireNonNull(vars);
		Objects.requireNonNull(loadInstrs);
		Objects.requireNonNull(varsType);
		
		int i = 0;
		for (String var : vars) {
			if ( var.equals("\u0001") || var.equals("\u0002") ) {
				appendVarLoad(stringBuilderInsts, loadInstrs.get(i), varsType.get(i));
				i++;
			} else {
				appendStringLoad(stringBuilderInsts, var);
			}
		}
	}

	private void appendVarLoad(InsnList stringBuilderInsts, VarInsnNode varInsnNode, Type type) {
		Objects.requireNonNull(stringBuilderInsts);
		Objects.requireNonNull(varInsnNode);
		Objects.requireNonNull(type);
		
		stringBuilderInsts.add(new VarInsnNode(varInsnNode.getOpcode(), varInsnNode.var));
		stringBuilderInsts.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(java.lang.StringBuilder.class).getInternalName(), "append", Type.getMethodDescriptor(Type.getType(java.lang.StringBuilder.class), type)));
	}

	private void appendStringLoad(InsnList stringBuilderInsts, String var) {
		Objects.requireNonNull(stringBuilderInsts);
		Objects.requireNonNull(var);
		
		stringBuilderInsts.add(new LdcInsnNode(var));
		stringBuilderInsts.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(java.lang.StringBuilder.class).getInternalName(), "append", Type.getMethodDescriptor(Type.getType(java.lang.StringBuilder.class), Type.getType(java.lang.String.class))));
	}

	private List<String> parseConcatStringArgument(String concatString) {
		Objects.requireNonNull(concatString);
		List<String> parsedString = new ArrayList<>();
		
		StringCharacterIterator it = new StringCharacterIterator(concatString);
		while ( it.current() != DONE ) {
			if ( it.current() == '\u0001' || it.current() == '\u0001' ) {
				parsedString.add(String.valueOf(it.current()));
				it.next();
			} else {
				var sb = new StringBuilder();
				while ( it.current() != '\u0001' && it.current() != '\u0002' && it.current() != DONE ) {
					sb.append(it.current());
					it.next();
				}
				parsedString.add(sb.toString());
			}
		}
		
		return parsedString;
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
			if ( isConcatInstruction(instr) ) {
				InvokeDynamicInsnNode idInstr = (InvokeDynamicInsnNode) instr;
				
				String details = "pattern " + convertUnicode(idInstr.bsmArgs[0].toString());
				
				addFeatureInfos(new FeatureInfos(featureName(), className + "." + mn.name + mn.desc, sourceName + ":" + ByteCode.getLineInst(idInstr), details, idInstr));

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
