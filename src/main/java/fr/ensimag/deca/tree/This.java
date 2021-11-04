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
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class This extends AbstractExpr {

	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {
		if (currentClass == null) {
			throw new ContextualError("\"This\" cannot be used in main (3.43)", this.getLocation());
		}
		this.setType(currentClass.getType());
		return currentClass.getType();
	}

	@Override
	public void decompile(IndentPrintStream s) {
		s.print("this");
	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		// leaf node -- nothing to do
	}

	@Override
	protected void iterChildren(TreeFunction f) {
		// leaf node -- nothing to do
	}
	
	@Override
	protected DVal dval() {
		return new RegisterOffset(-2, Register.LB);
	}
	
	
	@Override
	protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
		program.addInstruction(new LOAD(this.dval(), Register.getR(n)));
	}
}
