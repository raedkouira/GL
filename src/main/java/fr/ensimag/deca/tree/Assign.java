package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl28
 * @date 01/01/2020
 */
public class Assign extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(Assign.class);

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	// rule (3.32)
    	Type lValueType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
    	// verifyRValue returns **this** or **convfloat(this)** if needed
    	this.setRightOperand(this.getRightOperand().verifyRValue(compiler, localEnv, currentClass, lValueType));
    	this.setType(lValueType);
    	return this.getType();
    }


    @Override
    protected String getOperatorName() {
        return "=";
    }
     
    @Override
    protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
    	program.addComment(decompile());
    	if (getLeftOperand().daddr() == null) {
    		getLeftOperand().codeExpr(program, n, registerManager);
			if (n < Register.getRMAX()) {
				getRightOperand().codeAssign(program, n + 1, registerManager);
				program.addInstruction(new STORE(Register.getR(n+1), getLeftOperand().tempAddr(program, n, registerManager)));
			}
			else {
				program.addInstruction(new PUSH(Register.getR(n)));
				registerManager.incCurrentNumberOfTemps();
				getRightOperand().codeAssign(program, n, registerManager);
				program.addInstruction(new LOAD(Register.getR(n), Register.R0));
				program.addInstruction(new POP(Register.getR(n)));;
				registerManager.decCurrentNumberOfTemps();
				program.addInstruction(new STORE(Register.R0, getLeftOperand().tempAddr(program, n, registerManager)));
			}
		}
    	else {
    		getRightOperand().codeAssign(program, n, registerManager);
    		program.addInstruction(new STORE(Register.getR(n), getLeftOperand().daddr()));
		}
    }
    
}
