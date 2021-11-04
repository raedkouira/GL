package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl28
 * @date 01/01/2020
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractOpArith.class);

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
    	Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
    	if (type1.isInt() && type2.isFloat()) {
    		ConvFloat leftConv = new ConvFloat(this.getLeftOperand());
    		leftConv.verifyExpr(compiler, localEnv, currentClass);
    		this.setLeftOperand(leftConv);
    		this.setType(type2);
    	} else if (type1.isFloat() && type2.isInt()) {
    		ConvFloat rightConv = new ConvFloat(this.getRightOperand());
    		rightConv.verifyExpr(compiler, localEnv, currentClass);
    		this.setRightOperand(rightConv);
    		this.setType(type1);
    	} else if ((type1.isInt() && type2.isInt()) ||
    			(type1.isFloat() && type2.isFloat())){
    		this.setType(type1);
    		// compatible types
    	} else {
    		throw new ContextualError("Arithmetic operation \"" + this.getOperatorName() +  "\" is not supported for types "
    				+ this.getLeftOperand().getType() + " and " + this.getRightOperand().getType() + " (3.33)",
    				this.getLocation());
    	}
    	return this.getType();
    }
}
