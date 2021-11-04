package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.SEQ;

/**
 *
 * @author gl28
 * @date 01/01/2020
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }
	
	@Override
	protected void codeBranch(IMAProgram program, boolean b, Label label) {
		if (b) {
			program.addInstruction(new BEQ(label));
		}
		else {
			program.addInstruction(new BNE(label));
		}
	}

	
	@Override
	protected void codeAssign(IMAProgram program, int n, RegisterManager registerManager) {
		codeExpr(program, n, registerManager);
		program.addInstruction(new SEQ(Register.getR(n)));		
	}
}
