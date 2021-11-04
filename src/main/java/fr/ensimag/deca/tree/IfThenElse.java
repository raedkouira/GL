package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl28
 * @date 01/01/2020
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
    	// rule (3.22)
    	condition.verifyCondition(compiler, localEnv, currentClass);
    	thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    	elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    protected void codeGenInst(IMAProgram program, RegisterManager registerManager) {
    	
    	program.addComment("if instruction");
    	
    	Label elseLabel = Label.newElseIfLabel();
    	Label endIfLabel = Label.newEndIfLabel();
    	condition.codeCond(program, false, elseLabel, registerManager);
    	thenBranch.codeGenListInst(program, registerManager);
    	program.addInstruction(new BRA(endIfLabel));
    	program.addLabel(elseLabel);
    	elseBranch.codeGenListInst(program, registerManager);
    	program.addLabel(endIfLabel);
    }

    @Override
    public void decompile(IndentPrintStream s) {
    	s.print("if (");
    	condition.decompile(s);
    	s.println(") {");
    	s.indent();
    	thenBranch.decompile(s);
    	s.unindent();
    	s.println("} else {");
    	s.indent();
    	elseBranch.decompile(s);
    	s.unindent();
    	s.print("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
    
}
