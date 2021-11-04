package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl28
 * @date 01/01/2020
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
    	// set type := float decoration on **operand**
    	this.setType(compiler.getEnvTypes().getDefinitionFromName("float").getType());
    	return this.getType();

    	}


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }
    
    
    @Override
	protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
    	getOperand().codeExpr(program, n, registerManager);
    	program.addInstruction(new FLOAT(Register.getR(n), Register.getR(n)));
    	program.addInstruction(new BOV(Label.IMPOSSIBLECONVFLOAT));
	}
}
