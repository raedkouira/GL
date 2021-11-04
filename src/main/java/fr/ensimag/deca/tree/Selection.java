package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class Selection extends AbstractLValue {
	private AbstractExpr objectName;
	private AbstractIdentifier fieldName;
	
	public Selection(AbstractExpr objectName, AbstractIdentifier fieldName) {
		Validate.notNull(objectName);
		Validate.notNull(fieldName);
		this.objectName = objectName;
		this.fieldName = fieldName; 
	}

	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {
		Type cType = objectName.verifyExpr(compiler, localEnv, currentClass);
		ClassType classType = cType.asClassType("Expression at " + objectName.getLocation() + "is not a class object, cannot use \".\"", objectName.getLocation());
		if (classType.getDefinition().getMembers().getAny(fieldName.getName()) == null) {
			throw new ContextualError("Field " + fieldName.getName() + " is not defined in class " + classType.getName(),
					fieldName.getLocation());
		}
		Definition field = classType.getDefinition().getMembers().getAny(fieldName.getName());
		FieldDefinition fieldDef = field.asFieldDefinition("not a field definition", fieldName.getLocation());
		if (fieldDef.getVisibility() == Visibility.PROTECTED && (currentClass == null ||
				!currentClass.equals(classType.getDefinition()))) {
			throw new ContextualError("Cannot access protected field " + fieldName.getName(),
					fieldName.getLocation());
		}
		fieldName.setDefinition(fieldDef);
		this.setType(fieldDef.getType());
		return fieldDef.getType();
	}

	@Override
	public void decompile(IndentPrintStream s) {
		s.print(objectName.decompile());
		s.print(".");
		s.print(fieldName.decompile() + " ");

	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		objectName.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, true);

	}

	@Override
	protected void iterChildren(TreeFunction f) {
		objectName.iterChildren(f);
		fieldName.iterChildren(f);
	}
	
	@Override
	protected DAddr daddr() {
		return null;
	}
	
	@Override
	protected DAddr tempAddr(IMAProgram program, int n, RegisterManager registerManager) {
		objectName.codeExpr(program, n, registerManager);
		program.addInstruction(new CMP(new NullOperand(), Register.getR(n)));
		program.addInstruction(new BEQ(Label.NULLOBJECT));
		registerManager.tryMaxRegisterIndex(n);
		return new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.getR(n));
	}
	
	
	@Override
	protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
		objectName.codeExpr(program, n, registerManager);
		program.addInstruction(new CMP(new NullOperand(), Register.getR(n)));
		program.addInstruction(new BEQ(Label.NULLOBJECT));
		program.addInstruction(new LOAD(new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.getR(n)), Register.getR(n)));
	}

}
