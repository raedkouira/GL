package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl28
 * @date 01/01/2020
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
    	Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
    	if (!(type1.isInt() && type2.isInt())) {
    		throw new ContextualError("Modulo operation is not supported for types "
    				+ type1 + " and " + type2 + " (3.51)",
    				this.getLocation());
    	}
    	this.setType(type1);
    	return this.getType();
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }

	@Override
	protected void mnemo(IMAProgram program, DVal op,
			GPRegister register) {
		program.addInstruction(new REM(op, register));
		program.addInstruction(new BOV(Label.DIVBYZERO));
	}

}
