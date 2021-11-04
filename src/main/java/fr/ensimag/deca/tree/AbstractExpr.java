	package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl28
 * @date 01/01/2020
 */
public abstract class AbstractExpr extends AbstractInst {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);

    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnvcodeGenPrint
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
    	Type type2 = this.verifyExpr(compiler, localEnv, currentClass);
    	if (type2.isInt() && expectedType.isFloat()) {
    		ConvFloat convFloat = new ConvFloat(this);
    		convFloat.verifyExpr(compiler, localEnv, currentClass);
    		return convFloat;
    	// if (rvalue type is a subclass of expectedtype)
    	// then it is assign-compatible
    	} else if (expectedType.isClass()) {
    		if (type2.isClassOrNull()) {
    			if (type2.isNull()) {}
    			else { // type.isClass()
    				ClassType cType2 = type2.asClassType("not a classs type", this.getLocation());
    				ClassType cExpectedType = expectedType.asClassType("not a class type", this.getLocation());
    				if (!cType2.isSubClassOf(cExpectedType)) {
    					throw new ContextualError("Class " + type2.getName() + " is not a subclass of "
    				+ expectedType.getName() + " (not assign_compatible)",
    							this.getLocation());
    				}
    			}
    		} else throw new ContextualError("Expected class " + expectedType.getName() +  ", got type " + type2.getName(),
    				this.getLocation());

    	} else if (!type2.sameType(expectedType)) {
    		throw new ContextualError("Expected type " + expectedType.getName() + ", got type "
    		+ type2.getName() + " (3.28)", this.getLocation());
    	}
    	return this;
    }
    
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
    	this.verifyExpr(compiler, localEnv, currentClass); // rule (3.20)
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	Type type = this.verifyExpr(compiler, localEnv, currentClass);
    	if (!type.isBoolean()) {
    		throw new ContextualError("Condition must be of type boolean , got type " + type.getName() + " (3.29)",
    				this.getLocation());
    	}
    	this.setType(compiler.getEnvTypes().getDefinitionFromName("boolean").getType());
    }

    /**
     * Generate code to print the expression
     *
     * @param program
     */
    protected void codeGenPrint(IMAProgram program, RegisterManager registerManager) {
    	program.addComment("print(ln) " + this.decompile());
    	codeGenInst(program, registerManager);
    	program.addInstruction(new LOAD(Register.getDefaultRegister(), Register.R1));
    	if (type.isFloat()) {
    		program.addInstruction(new WFLOAT());
		} else if (type.isInt() || type.isBoolean()) {
			program.addInstruction(new WINT());
		}
    }
    
    protected void codeGenPrintHex(IMAProgram program, RegisterManager registerManager) {
    	program.addComment("print(ln)x " + this.decompile());
    	codeGenInst(program, registerManager);
    	program.addInstruction(new LOAD(Register.getDefaultRegister(), Register.R1));
    	if (type.isInt()) {
			program.addInstruction(new FLOAT(Register.R1, Register.R1));
		}
    	program.addInstruction(new WFLOATX());
	}

    @Override
    protected void codeGenInst(IMAProgram program, RegisterManager registerManager) {
        codeExpr(program, Register.defaultRegisterIndex, registerManager);
    }
    
    
    protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
		throw new UnsupportedOperationException("not yet implemented");
	}
    
    protected void codeCMP(IMAProgram program, int n) {
    	if (getType().isFloat()) {
    		program.addInstruction(new CMP(new ImmediateFloat(0.0f), Register.getR(n)));
		} else if (getType().isClassOrNull()) {
			program.addInstruction(new CMP(new NullOperand(), Register.getR(n)));
		} else {
			program.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(n)));
		}
	}
    
    protected void codeAssign(IMAProgram program, int n, RegisterManager registerManager) {
		codeExpr(program, n, registerManager);
	}
    
    protected void codeBranch(IMAProgram program, boolean b, Label label) {
    	if (b) {
			program.addInstruction(new BNE(label));
		} else {
			program.addInstruction(new BEQ(label));
		}
	}
    
    protected void codeCondExpr(IMAProgram program, boolean b, Label label, int n, RegisterManager registerManager) {
    	codeExpr(program, n, registerManager);
    	codeCMP(program, n);
    	codeBranch(program, b, label);
    }
    
    protected void codeCond(IMAProgram program, boolean b, Label label, RegisterManager registerManager) {
    	codeCondExpr(program, b, label, 2, registerManager);
	}
    
    protected DVal dval() {
		return null;
	}
    

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
}
