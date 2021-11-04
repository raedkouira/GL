package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Syntax error for a integer that is too big.
 *
 * @author gl28
 * @date 18/01/2020
 */
public class IntegerTooBig extends DecaRecognitionException {

    private static final long serialVersionUID = 2650513871021243241L;

    public IntegerTooBig(DecaParser recognizer, ParserRuleContext ctx) {
        super(recognizer, ctx);
    }

    @Override
    public String getMessage() {
        return "value cannot be stored as an integer value";
    }
}
