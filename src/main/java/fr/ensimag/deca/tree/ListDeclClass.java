package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;

/**
 *
 * @author gl28
 * @date 01/01/2020
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
    	for (AbstractDeclClass c: getList()) {
    		c.verifyClass(compiler);
    	}
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
    	for (AbstractDeclClass c: getList()) {
    		c.verifyClassMembers(compiler);
    	}
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
    	for (AbstractDeclClass c: getList()) {
    		c.verifyClassBody(compiler);
    	}
    }

    public void codeGenMethod(IMAProgram program) {
		for (AbstractDeclClass declClass : getList()) {
			declClass.codeGenMethod(program);
		}
	}
    
    public void createMethodTable(IMAProgram program) throws ContextualError{
		for (AbstractDeclClass declClass : getList()) {
			declClass.createMethodTable(program);
		}
	}
}
