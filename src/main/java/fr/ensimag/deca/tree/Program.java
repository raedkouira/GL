package fr.ensimag.deca.tree;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.MethodTable;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl28
 * @date 01/01/2020
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
    	// rule (3.1)
    	// the EnvironmentType attribute of compiler is updated
    	// after the first pass (2.1)
        classes.verifyListClass(compiler);
        // second pass
        classes.verifyListClassMembers(compiler);
        // third pass
        classes.verifyListClassBody(compiler);
        main.verifyMain(compiler);
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) throws ContextualError{
    	
    	IMAProgram methodTableCode = new IMAProgram();
    	IMAProgram mainCode = new IMAProgram();
    	IMAProgram errorsCode = new IMAProgram();
    	IMAProgram classMethodCode = new IMAProgram();
    	IMAProgram tstoCode = new IMAProgram();
    	
    	createMethodTable(methodTableCode);
    	codeGenObject(classMethodCode);
    	classes.codeGenMethod(classMethodCode);
    	main.codeGenMain(mainCode);
    	codeGenErrors(errorsCode);
    	
        RegisterManager.GLOBAL_REGISTER_MANAGER.codeTSTOandADDSP(tstoCode);
        
        /* structuring program */
        compiler.append(tstoCode);
    	compiler.append(methodTableCode);
    	compiler.append(mainCode);
    	compiler.addInstruction(new HALT());;
    	compiler.append(errorsCode);
    	compiler.append(classMethodCode);
    }
    
    protected void createMethodTable(IMAProgram program) throws ContextualError{
    	
    	// creating the method table for the super-class Object
    	MethodTable.addClass("Object", 1);
    	Label objectEqualsLabel = Label.getMethodStartLabel("Object", "equals");
    	MethodTable.putMethod("Object", objectEqualsLabel, 0);
    	
    	// generating code for the method table of the super class Object
    	program.addInstruction(new LOAD(new NullOperand(), Register.R0));
    	program.addInstruction(new STORE(Register.R0, MethodTable.getClassAddr("Object")));
    	program.addInstruction(new LOAD(new LabelOperand(objectEqualsLabel), Register.R0));
    	program.addInstruction(new STORE(Register.R0, RegisterManager.GLOBAL_REGISTER_MANAGER.getNewAddress()));
    	
    	// creating the method table for each class, and generating code for the method table
    	classes.createMethodTable(program);
	}
    
    
    protected void codeGenObject(IMAProgram program) {
    	program.addLabel(Label.getInitLabel("Object"));
    	program.addInstruction(new RTS());
    	// codeGen for object.equals
    	program.addLabel(Label.getMethodStartLabel("Object", "equals"));
    	program.addInstruction(new TSTO(1));
    	program.addInstruction(new BOV(Label.STACKOVERFLOW));
    	program.addInstruction(new PUSH(Register.R2));
		program.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R2));
		program.addInstruction(new CMP(new RegisterOffset(-3, Register.LB), Register.R2));
		program.addInstruction(new SEQ(Register.R1));
		program.addLabel(Label.getMethodEndLabel("Object", "equals"));
		program.addInstruction(new POP(Register.R2));
		program.addInstruction(new RTS());
	}
    
    
    protected void codeGenErrors(IMAProgram program) {
    	program.addLabel(Label.STACKOVERFLOW);
    	program.addInstruction(new WSTR(new ImmediateString("Error: Stack overflow")));
    	program.addInstruction(new WNL());
    	program.addInstruction(new ERROR());
    	program.addLabel(Label.HEAPOVERFLOW);
    	program.addInstruction(new WSTR(new ImmediateString("Error: Impossible allocation : heap overflow")));
    	program.addInstruction(new WNL());
    	program.addInstruction(new ERROR());
    	program.addLabel(Label.DIVBYZERO);
    	program.addInstruction(new WSTR(new ImmediateString("Error: Division by zero")));
    	program.addInstruction(new WNL());
    	program.addInstruction(new ERROR());
    	program.addLabel(Label.OVERFLOW);
    	program.addInstruction(new WSTR(new ImmediateString("Error: Overflow during arithmetic operation")));
    	program.addInstruction(new WNL());
    	program.addInstruction(new ERROR());
    	program.addLabel(Label.INVALIDINPUT);
    	program.addInstruction(new WSTR(new ImmediateString("Error: Invalid input")));
    	program.addInstruction(new WNL());
    	program.addInstruction(new ERROR());
    	program.addLabel(Label.NULLOBJECT);
    	program.addInstruction(new WSTR(new ImmediateString("Error: Cannot acces null object")));
    	program.addInstruction(new WNL());
    	program.addInstruction(new ERROR());
    	program.addLabel(Label.IMPOSSIBLECONVFLOAT);
    	program.addInstruction(new WSTR(new ImmediateString("Error: Impossible converion to float")));
    	program.addInstruction(new WNL());
    	program.addInstruction(new ERROR());
	}
    

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
