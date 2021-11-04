package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.DecacMain;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.QUO;

/**
 *
 * @author gl28
 * @date 01/01/2020
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
	protected void mnemo(IMAProgram program, DVal op,
			GPRegister register) {
    	if (getLeftOperand().getType().isInt()) {
    		program.addInstruction(new QUO(op, register));
		}
    	else if (getLeftOperand().getType().isFloat() || getRightOperand().getType().isFloat()) {
    		program.addInstruction(new DIV(op, register));
        	if (!DecacMain.COMPILER_OPTIONS.getNocheck() && (getLeftOperand().getType().isFloat() || getRightOperand().getType().isFloat())) {
        		program.addInstruction(new BOV(Label.OVERFLOW));
    		}
		}
    	program.addInstruction(new BOV(Label.DIVBYZERO));
    }
    
}
