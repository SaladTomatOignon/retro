package fr.umlv.retro.util;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.tree.AbstractInsnNode.LABEL;
import static org.objectweb.asm.tree.AbstractInsnNode.LINE;
import static org.objectweb.asm.tree.AbstractInsnNode.METHOD_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.VAR_INSN;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ByteCode {
	public final static Predicate<AbstractInsnNode> isCloseMethod = instr -> {
		if ( instr.getType() == METHOD_INSN ) {
			MethodInsnNode methodInstr = (MethodInsnNode) instr;
			
			return methodInstr.name.equals("close");
		}
		 
		return false;
	};
	
	public final static Predicate<AbstractInsnNode> isAddSuppressedMethod = instr -> {
		if ( instr.getType() == METHOD_INSN ) {
			MethodInsnNode methodInstr = (MethodInsnNode) instr;
			
			return methodInstr.getOpcode() == INVOKEVIRTUAL &&
					methodInstr.owner.equals(Type.getType(java.lang.Throwable.class).getInternalName()) &&
					methodInstr.name.equals("addSuppressed");
		}
		 
		return false;
	};
	
	public static AbstractInsnNode findInstruction(Predicate<AbstractInsnNode> isInstrSatisfied, LabelNode start, LabelNode end) {
		Objects.requireNonNull(isInstrSatisfied);
		Objects.requireNonNull(start);
		Objects.requireNonNull(end);
		
		// On parcourt les instructions de start jusqu'à end.
		for (AbstractInsnNode currentInst = start; !Objects.isNull(currentInst) && !currentInst.equals(end); currentInst = currentInst.getNext()) {
			if ( isInstrSatisfied.test(currentInst) ) {
				return currentInst;
			}
		}
		
		return null;
	}
	
	public static LabelNode getNextLabel(LabelNode handler) {
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
	
	public static int getLineInst(AbstractInsnNode instr) {
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
	
	public static List<VarInsnNode> getLastLoadInstructions(AbstractInsnNode start, int n) {
		Objects.requireNonNull(start);
		if ( n < 0 ) {
			throw new IllegalArgumentException("Negative number of instructions");
		}
		
		List<VarInsnNode> lastNLoadInsts = new ArrayList<>(n);
		AbstractInsnNode currentInst = start.getPrevious();
		
		for (int i = 0; i < n; i++) {
			if ( !isLoadInstruction(currentInst) ) {
				break;
			}
			
			lastNLoadInsts.add((VarInsnNode) currentInst);
			currentInst = currentInst.getPrevious();
		}
		
		return lastNLoadInsts;
	}
	
	public static boolean isLoadInstruction(AbstractInsnNode instr) {
		Objects.requireNonNull(instr);
		
		return instr.getType() == VAR_INSN && Set.of(ILOAD, LLOAD, FLOAD, DLOAD, ALOAD).contains(instr.getOpcode());
	}
}
