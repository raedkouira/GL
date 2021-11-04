package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * @author gl28
 * @date 01/01/2020
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	this.getOperand().verifyExpr(compiler, localEnv, currentClass);
    	if (!(this.getOperand().getType().isInt() ||
    			this.getOperand().getType().isFloat())) {
    		throw new ContextualError("Invalid type for operand \"-\": " + this.getOperand().getType() + " instead of int or float",
    				this.getOperand().getLocation());
    	}
    	this.setType(this.getOperand().getType());
    	return this.getType();
    }
    
    @Override
    protected String getOperatorName() {
        return "-";
    }
    
    @Override
	protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
    	getOperand().codeExpr(program, n, registerManager);
    	program.addInstruction(new OPP(Register.getR(n), Register.getR(n)));
	}
}
