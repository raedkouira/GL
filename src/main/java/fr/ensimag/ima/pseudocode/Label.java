package fr.ensimag.ima.pseudocode;

import java.util.HashMap;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.tools.SymbolTable.Symbol;

/**
 * Representation of a label in IMA code. The same structure is used for label
 * declaration (e.g. foo: instruction) or use (e.g. BRA foo).
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class Label extends Operand {
	
	public static final Label STACKOVERFLOW = new Label("stack_overflow");
	public static final Label HEAPOVERFLOW = new Label("heap_overflow");
	public static final Label DIVBYZERO = new Label("div_by_zero");
	public static final Label INVALIDINPUT = new Label("invalid_input");
	public static final Label OVERFLOW = new Label("overflow");
	public static final Label NULLOBJECT = new Label("null_object");	
	public static final Label IMPOSSIBLECONVFLOAT = new Label("impossible_conv_float");

	
	private static int endAndLabelCounter = 0;
	private static int endOrLabelCounter = 0;
	private static int endIfLabelCounter = 0;
	private static int elseIfLabelCounter = 0;
	private static int beginWhileLabelCounter = 0;
	private static int whileCondLabelCounter = 0;
	
	
	public static Label getInitLabel(String className) {
		return new Label("init." + className);
	}
	
	public static Label getMethodStartLabel(String className, String methodName) {
		return new Label("code." + className + "." + methodName);
	}
	
	public static Label getMethodEndLabel(String className, String methodName) {
		return new Label("end." + className + "." + methodName);
	}
	
	
	public static Label newEndAndLabel() {
		int temp = endAndLabelCounter;
		endAndLabelCounter ++;
		return new Label("end_and_" + temp);
	}
	
	public static Label newEndOrLabel() {
		int temp = endOrLabelCounter;
		endOrLabelCounter ++;
		return new Label("end_or_" + temp);
	}
	
	public static Label newEndIfLabel() {
		int temp = endIfLabelCounter;
		endIfLabelCounter ++;
		return new Label("end_if_" + temp);
	}
	
	public static Label newElseIfLabel() {
		int temp = elseIfLabelCounter;
		elseIfLabelCounter ++;
		return new Label("else_" + temp);
	}
	
	public static Label newBeginWhileLabel() {
		int temp = beginWhileLabelCounter;
		beginWhileLabelCounter ++;
		return new Label("begin_while_" + temp);
	}
	
	public static Label newWhileCondLabel() {
		int temp = whileCondLabelCounter;
		whileCondLabelCounter ++;
		return new Label("while_cond_" + temp);
	}

    @Override
    public String toString() {
        return name;
    }

    public Label(String name) {
        super();
        Validate.isTrue(name.length() <= 1024, "Label name too long, not supported by IMA");
        Validate.isTrue(name.matches("^[a-zA-Z][a-zA-Z0-9_.]*$"), "Invalid label name " + name);
        this.name = name;
    }
    private String name;
}
