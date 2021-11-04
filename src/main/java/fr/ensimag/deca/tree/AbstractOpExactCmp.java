package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 *
 * @author gl28
 * @date 01/01/2020
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {

    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
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
    	} else if (type1.isFloat() && type2.isInt()) {
    		ConvFloat rightConv = new ConvFloat(this.getRightOperand());
            rightConv.verifyExpr(compiler, localEnv, currentClass);
    		this.setRightOperand(rightConv);
    	} else if ((type1.isInt() && type2.isInt()) ||
    			(type1.isFloat() && type2.isFloat()) ||
    			(type1.isBoolean() && type2.isBoolean()) ||
    			(type1.isClass() && type2.isNull()) ||
    			(type1.isNull() && type2.isClass())){
    		// compatible types for exact comparison operation
    	} else {
    		throw new ContextualError("Exact comparison operation \"" + this.getOperatorName() +  "\" is not supported for types "
    				+ this.getLeftOperand().getType() + " and " + this.getRightOperand().getType() + " (3.33)",
    				this.getLocation());
    	}
    	this.setType(compiler.getEnvTypes().getDefinitionFromName("boolean").getType());
    	return this.getType();
    }

}
