package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl28
 * @date 01/01/2020
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractOpBool.class);

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
    	Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
    	if (!type1.isBoolean()) {
    		throw new ContextualError("Invalid type for boolean operation " + this.getOperatorName() +
    				": left operand is of type " + type1 + " instead of boolean (3.33)",
    				this.getLeftOperand().getLocation());
    	} else if (!type2.isBoolean()) {
    		throw new ContextualError("Invalid type for boolean operation " + this.getOperatorName() +
    				": right operand is of type " + type2 + " instead of boolean (3.33)",
    				this.getRightOperand().getLocation());
    	}
    	this.setType(this.getLeftOperand().getType());
    	return this.getType();
    }
    
}
