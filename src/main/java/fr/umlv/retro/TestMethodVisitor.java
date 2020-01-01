package fr.umlv.retro;

import org.objectweb.asm.*;

public class TestMethodVisitor extends MethodVisitor {

	public TestMethodVisitor(int api) {
		super(api);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		System.out.println(line);
		super.visitLineNumber(line, start);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
		super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
		System.out.println("Opcode : " + opcode + " Owner : " + owner + " Name : " + name + " Descriptor : "
				+ descriptor + " IsInterface : " + isInterface);
	}

	@Override
	public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle,
			Object... bootstrapMethodArguments) {
		System.out.println("Name : " + name + " Descriptor : + " + descriptor + " bootsrapMethodHandle : ");
		for (var elem : bootstrapMethodArguments) {
			System.out.println(elem);
		}
		super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
	}

}
