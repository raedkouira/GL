package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.BLE;
import fr.ensimag.ima.pseudocode.instructions.SLE;

/**
 *
 * @author gl28
 * @date 01/01/2020
 */
public class LowerOrEqual extends AbstractOpIneq {
    public LowerOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<=";
    }

	
	@Override
	protected void codeBranch(IMAProgram program, boolean b, Label label) {
		if (b) {
			program.addInstruction(new BLE(label));
		} else {
			program.addInstruction(new BGT(label));
		}
	}
	
	@Override
	protected void codeAssign(IMAProgram program, int n, RegisterManager registerManager) {
		codeExpr(program, n, registerManager);
		program.addInstruction(new SLE(Register.getR(n)));		
	}
}
