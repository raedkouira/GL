package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Method call
 *
 * @author gl28
 * @date 11/01/2020
 */
public class MethodCall extends AbstractExpr {

    public AbstractExpr getTreeExpr() {
        return treeExpr;
    }

    public AbstractIdentifier getMethodName() {
        return methodName;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    private AbstractExpr treeExpr;
    private AbstractIdentifier methodName;
    private ListExpr arguments;

    public MethodCall(AbstractExpr treeExpr, AbstractIdentifier methodName, ListExpr arguments) {
        Validate.notNull(methodName);
        Validate.notNull(arguments);
        this.treeExpr = treeExpr;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	
    	ClassType objectType = treeExpr.verifyExpr(compiler, localEnv, currentClass).asClassType("Expression at " + treeExpr.getLocation()
    		 + " is not a class object, cannot do a method call", treeExpr.getLocation());
    	Definition def = objectType.getDefinition().getMembers().getAny(methodName.getName());
    	objectType.getDefinition().getMembers().toString();
    	if (def == null) {
    		throw new ContextualError("Method " + methodName.getName() + " is not defined in class " + objectType.getName(),
    				methodName.getLocation());
    	}
    	MethodDefinition methodDef = def.asMethodDefinition(methodName.getName() + " is not a method, cannot do a method call", this.getLocation());
    	// verify that the object's class is the same as the method's class
    	Signature sig = methodDef.getSignature();
    	Signature sig2 = new Signature();
    	arguments.verifySignature(compiler, localEnv, currentClass, sig2);
    	// verify that signatures match
    	if (!sig.equals(sig2)) {
    		if (sig.size() == sig2.size()) {
    			for (int i = 0; i < sig.size(); i++) {
    				Type t = sig.paramNumber(i);
    				Type t2 = sig2.paramNumber(i);
    				if ((t.sameType(t2)) || (t.isFloat() && t2.isInt()) || // assign_compatible
    						(( t.isClass() && t2.isNull())) ||
    						(( t.isClass() && t2.isClass() && t2.asClassType("wont happen",
    								this.getLocation()).isSubClassOf(t.asClassType("wont happen",
    										this.getLocation()))))) { // assign_compatible
    					
    				} else throw new ContextualError("Wrong signature for call of method " + methodName.getName() + " does not match its definition (3.71)"
    	    				+ ", expected " + sig.toString() + ", got " + sig2.toString(), methodName.getLocation());
    			}
    		}
    	}
    	// returnType of the method called
    	this.setType(methodDef.getType());
    	methodName.setDefinition(methodDef);
    	return methodDef.getType();
    }


    @Override
    public void decompile(IndentPrintStream s){
        // A FAIRE : gérer plus proprement ça, dans les autres classes (de base) c'est pas comme ça
    	treeExpr.decompile(s);
    	methodName.decompile(s);
    	arguments.decompile(s);
    }

    @Override
    String prettyPrintNode() {
        return "MethodCall";
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        treeExpr.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        arguments.prettyPrint(s, prefix, true);
    }
    
    @Override
    protected void codeExpr(IMAProgram program, int n, RegisterManager registerManager) {
    	registerManager.tryMaxRegisterIndex(n);
    	treeExpr.codeExpr(program, n, registerManager);
    	
    	// reserving place for method args
    	program.addInstruction(new ADDSP(arguments.size() + 1));
    	registerManager.incCurrentNumberOfMethodParams(arguments.size() + 1);
    	
    	// adding implicit argument to stack
    	program.addInstruction(new STORE(Register.getR(n), new RegisterOffset(0, Register.SP)));
    	
    	// adding args to stack
    	int argIndex = 1;
    	for (AbstractExpr arg : arguments.getList()) {
			arg.codeExpr(program, n, registerManager);
			program.addInstruction(new STORE(Register.getR(n), new RegisterOffset(-argIndex, Register.SP)));
			argIndex ++;
		}
    	
    	// testing for null deref
    	program.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.getR(n)));
    	program.addInstruction(new CMP(new NullOperand(), Register.getR(n)));
    	program.addInstruction(new BEQ(Label.NULLOBJECT));
    	
    	// calling method
    	program.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(n)), Register.getR(n)));
    	program.addInstruction(new BSR(
    			new RegisterOffset(methodName.getMethodDefinition().getIndex(), Register.getR(n))));
    	registerManager.incCurrentNumberOfMethodParams(2);
    	registerManager.decCurrentNumberOfMethodParams(2);
    	
    	program.addInstruction(new SUBSP(arguments.size() + 1));
    	registerManager.decCurrentNumberOfMethodParams(arguments.size() + 1);
    	
    	program.addInstruction(new LOAD(Register.R1, Register.getR(n)));
    }

}
