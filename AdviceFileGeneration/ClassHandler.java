import java.io.IOException;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.NEWARRAY;


public class ClassHandler {
	
	private JavaClass bcelClass;
	private ConstantPool cPool;
	
	private static String resourceToDescriptor(String resourceName) {
		boolean finished = false;
		resourceName = resourceName.trim();
		resourceName = resourceName.replace('.', '/');
		String prefix = "";
		while (!finished) {
			String t = resourceName.substring(resourceName.length()-2, resourceName.length());
			if (t.equals("[]")) {
				prefix = prefix + "[";
				resourceName = resourceName.substring(0, resourceName.length()-2);
			}
			else {
				finished = true;
			}
		}
		if (resourceName.equals("byte")) {
			return (prefix + "B");
		}
		else if (resourceName.equals("char")) {
			return (prefix + "C");
		}
		else if (resourceName.equals("double")) {
			return (prefix + "D");
		}
		else if (resourceName.equals("float")) {
			return (prefix + "F");
		}
		else if (resourceName.equals("int")) {
			return (prefix + "I");
		}
		else if (resourceName.equals("long")) {
			return (prefix + "J");
		}
		else if (resourceName.equals("short")) {
			return (prefix + "S");
		}
		else if (resourceName.equals("boolean")) {
			return (prefix + "Z");
		}
		else if (resourceName.equals("void")) {
			return (prefix + "V");
		}
		else {
			return (prefix + "L" + resourceName + ";");
		}
	}

	public ClassHandler(String pathToClass) {
		try {
			bcelClass = new ClassParser(pathToClass).parse();
		} catch (ClassFormatException e) {
			e.printStackTrace();
			System.out.println("# ClassFormatException for " + pathToClass);
			return;
		} catch (IOException e) {
			System.out.println("# IOException for " + pathToClass);
			e.printStackTrace();
			return;
		}
		catch (Exception e) {
			System.out.println("# Exception for " + pathToClass);
			e.printStackTrace();
			return;
		}
		cPool = bcelClass.getConstantPool();
	}
	
	public static final int NEW_OPCODE = (new NEW(1)).getOpcode();
	public static final int NEWARRAY_OPCODE = (new NEWARRAY(BasicType.BOOLEAN)).getOpcode();
	public static final int ANEWARRAY_OPCODE = (new ANEWARRAY(1)).getOpcode();
	public static final int MULTIANEWARRAY_OPCODE = (new MULTIANEWARRAY(1, (short) 1)).getOpcode();
	
	public void methodsHandler() {
		if (bcelClass == null || cPool == null) {
			return;
		}
		//System.out.println("# New class: " + bcelClass.getClassName());
		Method[] methods = bcelClass.getMethods();
		for (int m = 0; m < methods.length; m++) {
			Method method = methods[m];
			//System.out.println("# New method: " + method.getName());
			methodHandler(method);
		}
	}
	
	private void methodHandler(Method m) {
		if (m.getCode() == null) {
			return;
		}
		Instruction[] instructions = new InstructionList(m.getCode().getCode()).getInstructions();
		int bcodeIdx = 0;
		for (int i = 0; i < instructions.length; i++) {
			Instruction instruction = instructions[i];
			if (instruction.getOpcode() == NEW_OPCODE) {
				int constantPoolIdx = new Integer(instruction.toString().split(" ")[1]);
				if (Main.sharedTypes.contains(resourceToDescriptor(cPool.constantToString(cPool.getConstant(constantPoolIdx))))) {
					//System.out.println("# Type: " + resourceToDescriptor(cPool.constantToString(cPool.getConstant(constantPoolIdx))));
					System.out.println(resourceToDescriptor(bcelClass.getClassName()) + ":" + m.getName() + m.getSignature() + ":" + bcodeIdx + ":3");
				}
			}
			else if (instruction.getOpcode() == NEWARRAY_OPCODE) {
				if (Main.sharedTypes.contains(resourceToDescriptor(instruction.toString().split(" ")[1] + "[]"))) {
					//System.out.println("# Type: " + resourceToDescriptor(instruction.toString().split(" ")[1] + "[]"));
					System.out.println(resourceToDescriptor(bcelClass.getClassName()) + ":" + m.getName() + m.getSignature() + ":" + bcodeIdx + ":3");
				}
			}
			else if (instruction.getOpcode() == ANEWARRAY_OPCODE) {
				int constantPoolIdx = new Integer(instruction.toString().split(" ")[1]);
				if (Main.sharedTypes.contains(resourceToDescriptor(cPool.constantToString(cPool.getConstant(constantPoolIdx)) + "[]"))) {
					//System.out.println("# Type: " + resourceToDescriptor(cPool.constantToString(cPool.getConstant(constantPoolIdx)) + "[]"));
					System.out.println(resourceToDescriptor(bcelClass.getClassName()) + ":" + m.getName() + m.getSignature() + ":" + bcodeIdx + ":3");
				}
			}
			bcodeIdx += instruction.getLength();
		}
	}
}
