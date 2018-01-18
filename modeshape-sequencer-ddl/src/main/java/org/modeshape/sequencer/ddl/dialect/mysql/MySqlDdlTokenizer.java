package org.modeshape.sequencer.ddl.dialect.mysql;

import org.modeshape.common.CommonI18n;
import org.modeshape.common.text.ParsingException;
import org.modeshape.common.text.Position;
import org.modeshape.common.text.TokenStream.CharacterStream;
import org.modeshape.common.text.TokenStream.Tokens;
import org.modeshape.sequencer.ddl.DdlTokenStream;

public class MySqlDdlTokenizer extends DdlTokenStream.DdlTokenizer {
    public MySqlDdlTokenizer(boolean useComments) {
        super(useComments);
    }

    @Override
    protected void handleInputCharacter(char c, Tokens tokens, CharacterStream input) {
        int startIndex;
        int endIndex;
        
        
        switch (c) {
        case '#':
            startIndex = input.index();
            Position startPosition = input.position(startIndex);
            // # END OF LINE comment ...
            boolean foundLineTerminator = false;
            while (input.hasNext()) {
                c = input.next();
                if (c == '\n' || c == '\r') {
                    foundLineTerminator = true;
                    break;
                }
            }
            endIndex = input.index(); // the token won't include the '\n' or '\r' character(s)
            if (!foundLineTerminator) ++endIndex; // must point beyond last char
            if (c == '\r' && input.isNext('\n')) input.next();

            // Check for PARSER_ID
            if (includeComments()) {
                tokens.addToken(startPosition, startIndex, endIndex, COMMENT);
            }
            
            break;
        case '`':
            { char quoteChar = c;
            startIndex = input.index();
            Position startingPosition = input.position(startIndex);
            boolean foundClosingQuote = false;
            while (input.hasNext()) {
                c = input.next();
                if (c == '\\' && input.isNext(quoteChar)) {
                    c = input.next(); // consume the ' character since it is escaped
                } else if (c == quoteChar) {
                    foundClosingQuote = true;
                    break;
                }
            }
            if (!foundClosingQuote) {
                String msg = CommonI18n.noMatchingSingleQuoteFound.text(startingPosition.getLine(),
                                                                        startingPosition.getColumn());
                throw new ParsingException(startingPosition, msg);
            }
            endIndex = input.index() + 1; // beyond last character read
            tokens.addToken(startingPosition, startIndex, endIndex, SINGLE_QUOTED_STRING); }
            break;
        case '\'':
            {
                char quoteChar = c;
                startIndex = input.index();
                Position startingPosition = input.position(startIndex);
                boolean foundClosingQuote = false;
                while (input.hasNext()) {
                    c = input.next();
                    if (c == '\\' && input.isNext(quoteChar)) {
                        c = input.next(); // consume the ' character since it is escaped
                    } else if (c == quoteChar) {
                        if (input.isNext(quoteChar)) {
                            c = input.next(); // consume the ' character since it is escaped
                        } else {
                            foundClosingQuote = true;
                            break;
                        }
                    }
                }
                if (!foundClosingQuote) {
                    String msg = CommonI18n.noMatchingSingleQuoteFound.text(startingPosition.getLine(),
                            startingPosition.getColumn());
                    throw new ParsingException(startingPosition, msg);
                }
                endIndex = input.index() + 1; // beyond last character read
                tokens.addToken(startingPosition, startIndex, endIndex, SINGLE_QUOTED_STRING);
            }
            break;
        default:   
            super.handleInputCharacter(c, tokens, input);
            break;
        }
    }
}
