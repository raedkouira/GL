package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;

import fr.ensimag.ima.pseudocode.Register;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl28
 * @date 01/01/2020
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    public static final CompilerOptions COMPILER_OPTIONS= new CompilerOptions();
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        try {
        	COMPILER_OPTIONS.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            COMPILER_OPTIONS.displayUsage();
            System.exit(1);
        }
        if (COMPILER_OPTIONS.getPrintBanner()) {
        	// -b
            System.out.println("GL28");
        }
        if (COMPILER_OPTIONS.getNocheck()) {
        	// -n
        }
        if (COMPILER_OPTIONS.getRegisters() >= 3 && COMPILER_OPTIONS.getRegisters() <= 16) {
        	Register.setRMAX(COMPILER_OPTIONS.getRegisters() - 1);
        }
        if (COMPILER_OPTIONS.getDebug() != 0) {
        	// -d
        }
        if (COMPILER_OPTIONS.getSourceFiles().isEmpty()) {
            throw new IllegalArgumentException("Missing source file(s) as arguments of decac command");
        }
        if (COMPILER_OPTIONS.getParallel()) {
        	// -P
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
            throw new UnsupportedOperationException("Parallel build not yet implemented");
        } else {
            for (File source : COMPILER_OPTIONS.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(COMPILER_OPTIONS, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
