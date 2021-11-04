package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

public abstract class AbstractDeclParam extends Tree {
	
	/**
	 * Implements non-terminal "decl_param" of [SyntaxeContextuelle] in pass 3
	 * 
	 * @param compiler contains "env_types" attribute
	 * @return 
	 * 			the type of the parameter after contextual verification,
	 * 			used to build the signature of the method
	 * @throws ContextualError
	 */
	public abstract Type verifyDeclParam(DecacCompiler compiler) throws ContextualError;
	
	/**
	 * Implements non-terminal "decl_param" of [SyntaxeContextuelle] in pass 3
	 * 
	 * @param compiler contains "env_types" attribute
	 * @param envExpParam
	 * 				its parent corresponds to the class environment
	 * 				in precondition this environment is empty
	 * 				in postcondition it contains the parameters of the method
	 * 	
	 * @throws ContextualError
	 */
	public abstract void verifyClassBodyDeclParam(DecacCompiler compiler,
			EnvironmentExp envExpParam) throws ContextualError;

}
