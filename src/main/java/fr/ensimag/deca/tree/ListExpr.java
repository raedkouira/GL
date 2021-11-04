package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl28
 * @date 01/01/2020
 */
public class ListExpr extends TreeList<AbstractExpr> {


    @Override
    public void decompile(IndentPrintStream s) {
    	int listIndex = 0;
    	int size = this.getList().size();
        for (AbstractExpr exp : this.getList()) {
        	s.print(exp.decompile());
        	s.print(size - 1 == listIndex ? "" : ", ");
        	listIndex++;
        }
    }
    
    /**
     * Verify {@link fr.ensimag.deca.context.Signature} of a methodCall
     * 
     * @param compiler
     * @param localEnv 
     * @param currentClass
     * @param sig2
     * 			in precondition, this is empty
     * 			in postcondition, this contains the type of the parameters
     * @throws ContextualError
     */
    public void verifySignature(DecacCompiler compiler, EnvironmentExp localEnv,
    		ClassDefinition currentClass, Signature sig2) throws ContextualError {
    	for (AbstractExpr exp: this.getList()) {
    		exp.setType(exp.verifyExpr(compiler, localEnv, currentClass));
    		sig2.add(exp.getType());
    	}
    }
}
