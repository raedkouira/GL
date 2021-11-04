package fr.ensimag.deca.tree;

import java.io.PrintStream;
import org.apache.log4j.Logger;
import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.MethodTable;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.deca.tree.ListDeclMethod;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl28
 * @date 01/01/2020
 */
public class DeclClass extends AbstractDeclClass {
    private static final Logger LOG = Logger.getLogger(DeclClass.class);

	private AbstractIdentifier className;
	private AbstractIdentifier superClassName;
	private ListDeclField fields;
	private ListDeclMethod methods;
	public DeclClass(AbstractIdentifier className, AbstractIdentifier superClassName,
			ListDeclField fields, ListDeclMethod methods) {
		this.className = className;
		this.superClassName = superClassName;			
		this.fields = fields;
		this.methods = methods;
	}
	
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        this.className.decompile(s);
        if (this.superClassName != null) {
        	s.print(" extends ");
        	this.superClassName.decompile(s);
        }
        s.print(" {");
        s.println();
        s.indent();
        fields.decompile(s);
        s.println();
        methods.decompile(s);
        s.println();
        s.unindent();
        s.print("}");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
    	EnvironmentExp envTypes = compiler.getEnvTypes();
        
    	// get superclass definition
    	ClassDefinition superClassDef;
        if (superClassName.getName().toString().equals("Object")){ // if "extends Object"
            superClassDef = (ClassDefinition)envTypes.getDefinitionFromName("Object");
            superClassName.setLocation(superClassDef.getLocation());
        } else if (compiler.getEnvTypes().getDefinitionFromName(superClassName.getName().toString()) != null) {
        	throw new ContextualError("Cannot extends class " + superClassName.getName() + ", it is a predefined type",
        			superClassName.getLocation());
        } else if (envTypes.get(superClassName.getName()) == null) {
            throw new ContextualError("Superclass " + superClassName.getName() + " is not defined (1.3)",
    				superClassName.getLocation());
    	} else {
            superClassDef = (ClassDefinition)envTypes.get(superClassName.getName());
        }
        //decorate the superclass identifier
        superClassName.setType(superClassDef.getType());
        superClassName.setDefinition(superClassDef);
        
        // build the current class definition
        ClassType classType;
        if (compiler.getEnvTypes().getDefinitionFromName(className.getName().toString()) != null) {
        	throw new ContextualError("Cannot declare class " + className.getName() + ", it is a predefined type",
        			className.getLocation());
        }
    	try {
    		classType = new ClassType(className.getName(), this.getLocation(), superClassDef);
    		envTypes.declare(className.getName(), classType.getDefinition());
    	} catch (DoubleDefException e) {
    		throw new ContextualError("Class " + className.getName() + " is already defined (1.3)", this.getLocation());
    	}
    	// decorate the class identifier
        className.setType(classType);
        className.setDefinition(classType.getDefinition());
    }



    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
		ClassDefinition currentClass = (ClassDefinition)compiler.getEnvTypes().get(className.getName());
		ClassDefinition superClass= (ClassDefinition)currentClass.getSuperClass();
		// set the offset of methods and fields indexes
		// based on the superclass
		currentClass.setNumberOfFields(superClass.getNumberOfFields());
		currentClass.setNumberOfMethods(superClass.getNumberOfMethods());
    	fields.verifyListDeclField(compiler, currentClass);
    	methods.verifyListDeclMethod(compiler, currentClass);
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
    	ClassDefinition currentClass = (ClassDefinition)compiler.getEnvTypes().get(className.getName());
    	EnvironmentExp localEnv = new EnvironmentExp(currentClass.getMembers());
    	this.fields.verifyClassBodyListField(compiler, localEnv, currentClass);
    	this.methods.verifyClassBodyListMethod(compiler, localEnv, currentClass);
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	this.className.prettyPrint(s, prefix, false);
    	this.superClassName.prettyPrint(s, prefix, false);
    	this.fields.prettyPrint(s, prefix, false);
    	this.methods.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
    	className.iterChildren(f);
    	superClassName.iterChildren(f);
    	fields.iterChildren(f);
    	methods.iterChildren(f);
    }
    
    @Override
    public void createMethodTable(IMAProgram program) throws ContextualError{
    	
    	String classString = className.getName().getName();
    	if (classString.indexOf('$') >= 0) {
			throw new ContextualError("Assembly code does not support $ character in class name " + className.getName(), className.getLocation());
		}
    	    	
    	// adding class to the table of methods, generating an address for the origin of the table
		MethodTable.addClass(classString, 
				superClassName.getName().getName(), 
				className.getClassDefinition().getNumberOfMethods());
		
		
		// creating method table for the class
		for (DeclMethod declMethod : methods.getList()) {
			String methodString = declMethod.getMethodName().getName().getName();
			if (methodString.indexOf('$') >= 0) {
				throw new ContextualError("Assembly code does not support $ character in method name " + methodString, className.getLocation());
			}
			MethodTable.putMethod(classString, Label.getMethodStartLabel(classString, methodString), 
					declMethod.getMethodName().getMethodDefinition().getIndex()-1);
		}
		
		// generating code for the method table
		program.addInstruction(new LEA(MethodTable.getClassAddr(superClassName.getName().getName()), Register.R0));
		program.addInstruction(new STORE(Register.R0, MethodTable.getClassAddr(classString)));
		
		for (Label methodLabel : MethodTable.getMethods(classString)) {
			program.addInstruction(new LOAD(new LabelOperand(methodLabel), Register.R0));
			program.addInstruction(new STORE(Register.R0, RegisterManager.GLOBAL_REGISTER_MANAGER.getNewAddress()));
		}
		
	}
    
    protected void codeGenInit(IMAProgram program) {
    	RegisterManager registerManager = new RegisterManager(Register.LB);
    	
    	IMAProgram labelInstruction = new IMAProgram();
    	IMAProgram tstoInstruction = new IMAProgram();
    	IMAProgram saveRegisters = new IMAProgram();
    	IMAProgram classInit = new IMAProgram();
    	IMAProgram restoreRegisters = new IMAProgram();
    	
    	/* Coding init label */
    	labelInstruction.addLabel(Label.getInitLabel(className.getName().getName()));
    	
    	/* Coding initialisation */
    	fields.codeGenDefaultInit(classInit, registerManager);
    	
    	// super class initialisation
		if (!superClassName.getName().getName().equals("Object")) {
			classInit.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
			classInit.addInstruction(new PUSH(Register.R1));
			registerManager.incCurrentNumberOfMethodParams(1);
			classInit.addInstruction(new BSR(Label.getInitLabel(superClassName.getName().getName())));
			registerManager.incCurrentNumberOfMethodParams(2);
			registerManager.decCurrentNumberOfMethodParams(2);
			classInit.addInstruction(new SUBSP(1));
			registerManager.decCurrentNumberOfMethodParams(1);
		}
		
		fields.codeGenProperInit(classInit, registerManager);
		
		/* coding registers save */
		registerManager.saveGPRegisters(saveRegisters);
		
		/* coding registers restoration */
		registerManager.restoreGPRegisters(restoreRegisters);
		
		/* coding tsto instrunction */
		registerManager.codeTSTOandADDSP(tstoInstruction);
		
		/* class initialisation final structure */
		program.append(labelInstruction);
		program.append(tstoInstruction);
		program.append(saveRegisters);
		program.append(classInit);
		program.append(restoreRegisters);
		program.addInstruction(new RTS());
	}
    
    @Override
    protected void codeGenMethod(IMAProgram program) {
    	MethodTable.setCurrentClass(className.getName().getName());
    	
    	codeGenInit(program);
    	for (DeclMethod method : methods.getList()) {
			method.codeGen(program);
		}
    }
}
