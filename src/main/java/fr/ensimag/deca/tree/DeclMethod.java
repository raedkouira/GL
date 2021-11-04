package fr.ensimag.deca.tree;

import java.io.PrintStream;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.MethodTable;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.ListDeclParam;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

public class DeclMethod extends Tree {
	private AbstractIdentifier type;
	private AbstractIdentifier methodName;
	private ListDeclParam listDeclParam;
	private AbstractMethodBody methodBody;
	
	public DeclMethod(AbstractIdentifier type, AbstractIdentifier methodName,
			ListDeclParam listDeclParam, AbstractMethodBody methodBody) {
		this.type = type;
		this.methodName = methodName;
		this.listDeclParam = listDeclParam;
		this.methodBody = methodBody;
	}
	
	public void verifyDeclMethod(DecacCompiler compiler,
			ClassDefinition currentClass) throws ContextualError {
    	// return type decoration 
		EnvironmentExp envTypes = compiler.getEnvTypes();
    	Definition def = envTypes.getDefinitionFromName(type.getName().toString()); // predefined types
    	Type verifiedType;
    	if (def == null) def = envTypes.get(type.getName()); // classes type
    	if (def == null) {
    		throw new ContextualError("Type " + type.getName() + " is not defined (0.2)",
    				this.getLocation());
    	} else {
        	verifiedType = def.getType();
        	type.setDefinition(def);
    	}
    	// determine whether field is already defined in this class - in one of its parents - or not at all
		Signature sig = listDeclParam.verifyListDeclParam(compiler);
		MethodDefinition incMethodDef;
		
		if (currentClass.getMembers().get(methodName.getName()) != null) {
			throw new ContextualError("Field or method " + methodName.getName() + " is already defined in class " + currentClass.toString(),
					methodName.getLocation());
		} else if (currentClass.getMembers().getAny(methodName.getName()) != null) {
			Definition previousDef = currentClass.getSuperClass().getMembers().getAny(methodName.getName());
			MethodDefinition previousMethodDef = previousDef.asMethodDefinition("Method " + methodName.getName()
			+ " should redefine another method", methodName.getLocation());
			if (!sig.equals(previousMethodDef.getSignature())) {
                throw new ContextualError("Wrong signature for redifinition of method " + methodName.getName(),
                        methodName.getLocation());
			} else { // check for assign_compatible return types
				if (previousMethodDef.getType().isClass()) {
					ClassType previousCType = previousMethodDef.getType().asClassType("not a class type", this.getLocation());
					if (verifiedType.isNull() || (verifiedType.isClass() &&
							verifiedType.asClassType("not a class type", this.getLocation()).isSubClassOf(previousCType))) {
						incMethodDef = new MethodDefinition(verifiedType,
				        		this.getLocation(), sig, previousMethodDef.getIndex());
					} else throw new ContextualError("Wrong return type for method redefinition: class " + verifiedType.getName() + " is not"
							+ " a subclass of class " + previousCType.getName(),
							this.getLocation());
				} else if ((previousMethodDef.getType().isFloat() && verifiedType.isInt()) ||
						(previousMethodDef.getType().sameType(verifiedType))) {
					incMethodDef = new MethodDefinition(verifiedType,
		        		this.getLocation(), sig, previousMethodDef.getIndex());
				} else throw new ContextualError("wrong return type for method " + methodName.getName() + " redefinition",
						methodName.getLocation());
			}

		} else {
			currentClass.incNumberOfMethods();
			incMethodDef = new MethodDefinition(verifiedType, methodName.getLocation(),
					sig, currentClass.getNumberOfMethods());
		}
		// decorate method
		methodName.setDefinition(incMethodDef);
		try {
			currentClass.getMembers().declare(methodName.getName(), methodName.getDefinition());
		} catch (DoubleDefException e) {
			// this never happens since we verified previously
			e.printStackTrace();
		}
    	
    	
    }
	
	public void verifyClassBodyMethod(DecacCompiler compiler, 
			EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
		EnvironmentExp envExpParam = new EnvironmentExp(localEnv);
		listDeclParam.verifyClassBodyListDeclParam(compiler,
				envExpParam);
		methodBody.verifyClassMethodBody(compiler, envExpParam, currentClass, type.getDefinition().getType());
	}
	
	public AbstractIdentifier getMethodName() {
		return methodName;
	}
	
	
	@Override
	public void decompile(IndentPrintStream s) {
		type.decompile(s);
		s.print(" ");
		methodName.decompile(s);
		s.print("(");
		listDeclParam.decompile(s);
		s.println(")");
		methodBody.decompile(s);
	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		type.prettyPrint(s, prefix, false);
		methodName.prettyPrint(s, prefix, false);
		listDeclParam.prettyPrint(s, prefix, false);
		methodBody.prettyPrint(s, prefix, true);
	}

	@Override
	protected void iterChildren(TreeFunction f) {
		type.iter(f);
		methodName.iter(f);
		listDeclParam.iterChildren(f);
	}
	
	public void codeGen(IMAProgram program) {
		MethodTable.setCurrentMethod(methodName.getName().getName());
		String methodString = MethodTable.getCurrentMethod();
		String classString = MethodTable.getCurrentClass();
		
		RegisterManager registerManager = new RegisterManager(Register.LB);
		
		int argIndex = 3;
		for (DeclParam arg : listDeclParam.getList()) {
			arg.getParamName().getExpDefinition().setOperand(new RegisterOffset(-argIndex, Register.LB));
			argIndex++;
		}
		
		IMAProgram startLabelCode = new IMAProgram();
		IMAProgram tstoCode = new IMAProgram();
		IMAProgram saveRegisterCode = new IMAProgram();
		IMAProgram bodyCode = new IMAProgram(); 
		IMAProgram returnErrorCode = new IMAProgram();
		IMAProgram endLabelCode = new IMAProgram();
		IMAProgram restoreRegistersCode = new IMAProgram();
		
		startLabelCode.addLabel(Label.getMethodStartLabel(classString, methodString));
		endLabelCode.addLabel(Label.getMethodEndLabel(classString, methodString));
		
		methodBody.codeGen(bodyCode, registerManager);
		
		if (!type.getDefinition().getType().isVoid()) {
			returnErrorCode.addInstruction(new WSTR(new ImmediateString("Error : exiting function " + 
							classString + "." + methodString + " without return instruction")));
			returnErrorCode.addInstruction(new WNL());
			returnErrorCode.addInstruction(new ERROR());
		}
		
		registerManager.saveGPRegisters(saveRegisterCode);
		registerManager.restoreGPRegisters(restoreRegistersCode);
		
		registerManager.codeTSTOandADDSP(tstoCode);
		
		
		program.append(startLabelCode);
		program.append(tstoCode);
		program.append(saveRegisterCode);
		program.append(bodyCode);
		program.append(returnErrorCode);
		program.append(endLabelCode);
		program.append(restoreRegistersCode);
		program.addInstruction(new RTS());
	}
}
