package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;

/**
 * List of declarations (e.g. int x; float y,z).
 * 
 * @author gl28
 * @date 01/01/2020
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclVar dv : this.getList()) {
        	s.println(dv.decompile());
        }
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the "env_exp_r" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */    
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	// rule (3.16)
    	for (AbstractDeclVar a: this.getList()) {
    		a.verifyDeclVar(compiler, localEnv, currentClass);
    	}
    }
    
    protected void codeGenDecl(IMAProgram program, RegisterManager registerManager) {
    	for (AbstractDeclVar declVar : getList()) {
    		String declComment = declVar.decompile();
    		program.addComment(declComment);
    		declVar.codeGenDeclVar(program, registerManager);
    	}
	}
}
