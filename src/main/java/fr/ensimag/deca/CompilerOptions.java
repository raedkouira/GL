package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl28
 * @date 01/01/2020
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public boolean getParse() {
		return parse;
	}

	public boolean getVerification() {
		return verification;
	}

	public boolean getNocheck() {
		return nocheck;
	}

	public int getRegisters() {
		return registers;
	}

	private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parse = false;
    private boolean verification = false;
    private boolean nocheck = false;
    private int registers = -1;
    
    private List<File> sourceFiles = new ArrayList<File>();


    public void parseArgs(String[] args) throws CLIException {
    	int argCounter = 0;
        while (argCounter < args.length) {
        	switch (args[argCounter]) {
        	case "-b":
        		printBanner = true;
        		break;
        	case "-p":
        		parse = true;
        		break;
        	case "-v":
        		verification = true;
        		break;
        	case "-n":
        		nocheck = true;
        		break;
        	case "-r":
        		registers = Integer.parseInt(args[++argCounter]);
        		break;
        	case "-d":
        		debug++;
        		break;
        	case "-P":
        		parallel = true;
        		break;
        	default:
        		if (args[argCounter].endsWith(".deca")) {
        			sourceFiles.add(new File(args[argCounter]));
        		} else {
        			throw new CLIException("invalid argument" + args[argCounter]);
        		}
        	}
        	argCounter++;
        }
        
        if (parse && verification) {
        	throw new CLIException("-v & -p incompatible, please remove one");
        }
        
        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

    }

    protected void displayUsage() {
    	// pass
    }
}
