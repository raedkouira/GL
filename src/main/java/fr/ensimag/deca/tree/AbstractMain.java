package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.IMAProgram;

/**
 * Main block of a Deca program.
 *
 * @author gl28
 * @date 01/01/2020
 */
public abstract class AbstractMain extends Tree {

    protected abstract void codeGenMain(IMAProgram program);


    /**
     * Implements non-terminal "main" of [SyntaxeContextuelle] in pass 3 
     */
    protected abstract void verifyMain(DecacCompiler compiler) throws ContextualError;
}
