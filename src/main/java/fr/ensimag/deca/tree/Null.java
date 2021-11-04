package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class Null extends AbstractExpr {

	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {
		this.setType(compiler.getEnvTypes().getDefinitionFromName("null").getType());
		return this.getType();
	}

	@Override
	public void decompile(IndentPrintStream s) {
		s.print("null");

	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		// nothing to do -- leaf node

	}

	@Override
	protected void iterChildren(TreeFunction f) {
		// nothing to do -- leaf node
	}
	
	@Override
	protected DVal dval() {
		return new NullOperand();
	}
	
	@Override
	protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
		program.addInstruction(new LOAD(dval(), Register.getR(n)));
	}

}
