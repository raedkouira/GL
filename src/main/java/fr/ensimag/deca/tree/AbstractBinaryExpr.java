package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Binary expressions.
 *
 * @author gl28
 * @date 01/01/2020
 */
public abstract class AbstractBinaryExpr extends AbstractExpr {

    public AbstractExpr getLeftOperand() {
        return leftOperand;
    }

    public AbstractExpr getRightOperand() {
        return rightOperand;
    }

    protected void setLeftOperand(AbstractExpr leftOperand) {
        Validate.notNull(leftOperand);
        this.leftOperand = leftOperand;
    }

    protected void setRightOperand(AbstractExpr rightOperand) {
        Validate.notNull(rightOperand);
        this.rightOperand = rightOperand;
    }

    private AbstractExpr leftOperand;
    private AbstractExpr rightOperand;

    public AbstractBinaryExpr(AbstractExpr leftOperand,
            AbstractExpr rightOperand) {
        Validate.notNull(leftOperand, "left operand cannot be null");
        Validate.notNull(rightOperand, "right operand cannot be null");
        Validate.isTrue(leftOperand != rightOperand, "Sharing subtrees is forbidden");
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
    }

    abstract protected String getOperatorName();

    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);
    }

    
    protected void mnemo(IMAProgram program, DVal op, 
    		GPRegister register) {
    	throw new UnsupportedOperationException("not yet implemented");
    }
    
    @Override
	protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
    	leftOperand.codeExpr(program, n, registerManager);
    	DVal rightDVal = rightOperand.dval();
    	if (rightDVal != null) {
    		mnemo(program, rightOperand.dval(), Register.getR(n));
		}
    	else {
			if (n < Register.getRMAX()) {
				rightOperand.codeExpr(program, n + 1, registerManager);
				mnemo(program, Register.getR(n + 1), Register.getR(n));
			}
			else {
				program.addInstruction(new PUSH(Register.getR(n)));
				registerManager.incCurrentNumberOfTemps();
				rightOperand.codeExpr(program, n, registerManager);
				program.addInstruction(new LOAD(Register.getR(n), Register.R0));
				program.addInstruction(new POP(Register.getR(n)));;
				registerManager.decCurrentNumberOfTemps();
				mnemo(program, Register.R0, Register.getR(n));
			}
		}
	}
    	
    
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, true);
    }

}
