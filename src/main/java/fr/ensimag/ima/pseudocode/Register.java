package fr.ensimag.ima.pseudocode;


import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.AbstractLValue;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

/**
 * Register operand (including special registers like SP).
 * 
 * @author Ensimag
 * @date 01/01/2020
 */
public class Register extends DVal {
	
	private static final GPRegister[] R = initRegisters();
		
	private static int RMAX = 15;
	
	public static final int defaultRegisterIndex = 2;
	
	public static GPRegister getDefaultRegister() {
		return getR(defaultRegisterIndex);
	}
	
	public static int getRMAX() {
		return RMAX;
	}
	
	public static void setRMAX(int n) {
		Validate.isTrue(n > 1 && n < 16);
		RMAX = n;
	}
	
    private String name;
    protected Register(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Global Base register
     */
    public static final Register GB = new Register("GB");
    /**
     * Local Base register
     */
    public static final Register LB = new Register("LB");
    /**
     * Stack Pointer
     */
    public static final Register SP = new Register("SP");
    /**
     * General Purpose Registers. Array is private because Java arrays cannot be
     * made immutable, use getR(i) to access it.
     */
    /**
     * General Purpose Registers
     */
    public static GPRegister getR(int i) {
        return R[i];
    }
    /**
     * Convenience shortcut for R[0]
     */
    public static final GPRegister R0 = R[0];
    /**
     * Convenience shortcut for R[1]
     */
    public static final GPRegister R1 = R[1];
    
    public static final GPRegister R2 = R[2];
    
    static private GPRegister[] initRegisters() {
        GPRegister [] res = new GPRegister[16];
        for (int i = 0; i <= 15; i++) {
            res[i] = new GPRegister("R" + i, i);
        }
        return res;
    }
}
