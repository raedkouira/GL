package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

public class DeclParam extends AbstractDeclParam {

	private AbstractIdentifier paramType;
	private AbstractIdentifier paramName;
	

	public AbstractIdentifier getParamName() {
		return paramName;
	}

	public AbstractIdentifier getParamType() {
		return paramType;
	}

	public DeclParam(AbstractIdentifier paramType, AbstractIdentifier paramName) {
		Validate.notNull(paramType);
		Validate.notNull(paramName);
		this.paramType = paramType;
		this.paramName = paramName;
	}
	
	public Type verifyDeclParam(DecacCompiler compiler) throws ContextualError {
		Type verifiedType = paramType.verifyType(compiler);
		return verifiedType;
	}
	
	@Override
	public void verifyClassBodyDeclParam(DecacCompiler compiler,
			EnvironmentExp envExpParam) throws ContextualError {
		ParamDefinition paramDef = new ParamDefinition(paramType.getType(), this.getLocation());
        paramName.setDefinition(paramDef);
		try {
			envExpParam.declare(paramName.getName(), paramName.getDefinition());
		} catch (DoubleDefException e) {
				throw new ContextualError("Two parameters share the same name " + paramName.getName(),
						paramName.getLocation());
		}
	}
		
	@Override
	public void decompile(IndentPrintStream s) {
		paramType.decompile(s);
		s.print(" ");
		paramName.decompile(s);
	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		paramType.prettyPrint(s, prefix, false);
		paramName.prettyPrint(s, prefix, true);
	}

	@Override
	protected void iterChildren(TreeFunction f) {
		paramType.iter(f);
		paramName.iter(f);
	}


}
