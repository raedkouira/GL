package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Syntax error for a float that is too big
 *
 * @author gl28
 * @date 18/01/2020
 */
public class FloatTooBig extends DecaRecognitionException {

    private static final long serialVersionUID = 1470123176101273384L;

    public FloatTooBig(DecaParser recognizer, ParserRuleContext ctx) {
        super(recognizer, ctx);
    }

    @Override
    public String getMessage() {
        return "value cannot be stored as a float value";
    }
}
