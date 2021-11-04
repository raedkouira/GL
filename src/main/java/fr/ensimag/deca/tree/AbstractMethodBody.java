package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.IMAProgram;

public abstract class AbstractMethodBody extends Tree {

	public abstract void verifyClassMethodBody(DecacCompiler compiler,
			EnvironmentExp envExpParam, ClassDefinition currentClass,
			Type returnType) throws ContextualError;


	public abstract void codeGen(IMAProgram program, RegisterManager registerManager);
}
