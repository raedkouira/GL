package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

public class ListDeclParam extends TreeList<DeclParam> {

	@Override
	public void decompile(IndentPrintStream s) {
		int listIndex = 0;
    	int size = this.getList().size();
        for (DeclParam p: this.getList()) {
        	s.print(p.decompile());
        	s.print(size - 1 == listIndex ? "" : ", ");
        	listIndex++;
        }
	}
	
	@Override
	public void iterChildren(TreeFunction f) {
		for (DeclParam p: getList()) {
			p.iterChildren(f);
		}
	}
	
	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		for (DeclParam p: getList()) {
			p.prettyPrintChildren(s, prefix);
		}
	}
	
	/**
	 * Verify {@link fr.ensimag.deca.context.Signature} of a method declaration
	 * 
	 * @param compiler
	 * @return
	 * 		the signature of the method
	 * @throws ContextualError
	 */
	public Signature verifyListDeclParam(DecacCompiler compiler) throws ContextualError {
		Signature sig = new Signature();
		Type paramType;
		for (DeclParam p: getList()) {
			paramType = p.verifyDeclParam(compiler);
			sig.add(paramType);
		}
		return sig;
	}
	
	/**
	 * Build the environment associated with the method
	 * 
	 * @param compiler
	 * @param envExpParam
	 * @throws ContextualError
	 */
	public void verifyClassBodyListDeclParam(DecacCompiler compiler,
			EnvironmentExp envExpParam) throws ContextualError {
		for (DeclParam p: getList()) {
			p.verifyClassBodyDeclParam(compiler, envExpParam);
		}
	}

}
