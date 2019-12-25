package fr.umlv.retro.transformers;

import java.util.List;

import static org.objectweb.asm.Opcodes.V1_5;
import static org.objectweb.asm.Opcodes.V1_6;
import static org.objectweb.asm.Opcodes.V1_7;
import static org.objectweb.asm.Opcodes.V1_8;
import static org.objectweb.asm.Opcodes.V9;
import static org.objectweb.asm.Opcodes.V10;
import static org.objectweb.asm.Opcodes.V11;
import static org.objectweb.asm.Opcodes.V12;
import static org.objectweb.asm.Opcodes.V13;
import static org.objectweb.asm.Opcodes.V14;

abstract class AbstractTransformer implements Transformer {
	private final int version;
	
	AbstractTransformer(int version) {
		if ( List.of(V1_5, V1_6, V1_7, V1_8, V9, V10, V11, V12, V13, V14)
				.contains(version) ) {
			throw new IllegalArgumentException("Unsupported version");
		}
		
		this.version = version;
	}
	
	public int getVersion() {
		return version;
	}
}
