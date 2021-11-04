package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.DecacMain;
import fr.ensimag.deca.codegen.MethodTable;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class New extends AbstractExpr {
	private AbstractIdentifier newName;
	
	public New(AbstractIdentifier newName) {
		this.newName = newName;
	}
	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {
		Type nType = newName.verifyType(compiler);
		ClassType newType = nType.asClassType(nType + " is not a class type, cannot use New", newName.getLocation());
		this.setType(newType);
		newName.setDefinition(newType.getDefinition());
		return newType;
	}

	@Override
	public void decompile(IndentPrintStream s) {
		s.print("new ");
		newName.decompile(s);
		s.print("()");
	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		newName.prettyPrint(s, prefix, true);
	}

	@Override
	protected void iterChildren(TreeFunction f) {
		newName.iterChildren(f);
	}
	
	@Override
	protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
		registerManager.tryMaxRegisterIndex(n);
		int d = newName.getClassDefinition().getNumberOfFields() + 1;
		program.addInstruction(new NEW(new ImmediateInteger(d), Register.getR(n)));
		if (!DecacMain.COMPILER_OPTIONS.getNocheck()) {
			program.addInstruction(new BOV(Label.HEAPOVERFLOW));
		}
		DAddr addr = MethodTable.getClassAddr(newName.getName().getName());
		program.addInstruction(new LEA(addr, Register.R0));
		program.addInstruction(new STORE(Register.R0, new RegisterOffset(0, Register.getR(n))));
		program.addInstruction(new PUSH(Register.getR(n)));
		registerManager.incCurrentNumberOfMethodParams(1);
		program.addInstruction(new BSR(Label.getInitLabel(newName.getName().getName())));
		registerManager.incCurrentNumberOfMethodParams(2);
		registerManager.decCurrentNumberOfMethodParams(2);
		program.addInstruction(new POP(Register.getR(n)));
		registerManager.decCurrentNumberOfMethodParams(1);
	}

}
