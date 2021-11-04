package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.InlinePortion;

public class MethodAsmBody extends AbstractMethodBody {
	private StringLiteral asm;
	
	public MethodAsmBody(StringLiteral asm) {
		this.asm = asm;
	}

	@Override
	public void verifyClassMethodBody(DecacCompiler compiler, EnvironmentExp envExpParam, ClassDefinition currentClass,
			Type returnType) throws ContextualError {
		asm.verifyExpr(compiler, envExpParam, currentClass);
		
	}
	
	
	@Override
	public void decompile(IndentPrintStream s) {
		s.print("asm(");
		s.print(asm.decompile());
		s.println(");");

	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		asm.prettyPrint(s, prefix, true);

	}

	@Override
	protected void iterChildren(TreeFunction f) {
		// leaf node -- nothing to do

	}

	
	@Override
	public void codeGen(IMAProgram program, RegisterManager registerManager) {
		program.add(new InlinePortion(asm.getValue()));
	}

}
