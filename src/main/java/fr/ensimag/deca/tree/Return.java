package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.MethodTable;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class Return extends AbstractInst {
	
	AbstractExpr rvalue;
	
	public AbstractExpr getRvalue() {
		return this.rvalue;
	}
	
	public Return(AbstractExpr rvalue) {
		Validate.notNull(rvalue);
		this.rvalue = rvalue;
	}

	@Override
	protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass,
			Type returnType) throws ContextualError {
        rvalue = rvalue.verifyRValue(compiler, localEnv, currentClass, returnType);
        /*
		Type rType = rvalue.getType();
		if (rType != null && !rType.sameType(returnType)) {
			throw new ContextualError("Expected return of type " + returnType.getName() + ", got " + rType.getName(),
					rvalue.getLocation());
		}
		*/
		if (returnType.isVoid()) { // security - could not find a test to trigger this error
			throw new ContextualError("Return value cannot be of type void (3.24)",
					this.getLocation());
		}
	}

	@Override
	protected void codeGenInst(IMAProgram program, RegisterManager registerManager) {
		program.addComment(this.decompile());
		rvalue.codeExpr(program, Register.defaultRegisterIndex, registerManager);
		program.addInstruction(new LOAD(Register.getDefaultRegister(), Register.R1));
		program.addInstruction(new BRA(Label.getMethodEndLabel(MethodTable.getCurrentClass(), MethodTable.getCurrentMethod())));
	}

	@Override
	public void decompile(IndentPrintStream s) {
        s.print("return ");
        getRvalue().decompile(s);
        s.print(";");
	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		rvalue.prettyPrint(s, prefix, true);
	}

	@Override
	protected void iterChildren(TreeFunction f) {
		rvalue.iter(f);
	}

}
