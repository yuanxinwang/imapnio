package com.yahoo.imapnio.async.data;

import com.sun.mail.iap.Argument;
import com.sun.mail.imap.protocol.SearchSequence;

import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;

import java.io.IOException;

/**
 * This class extends the search sequence for modification sequence with the optional fields entry
 * name and type defined in https://tools.ietf.org/html/rfc7162#section-3.1.5.
 */
public class ExtendedSearchSequence extends SearchSequence {
    @Override
    /**
     * Generate the IMAP search sequence for the given search expression.
     *
     * @param term the search term
     * @param charset the character set
     * @return the IMAP search sequence argument
     * @throws SearchException will not throw
     * @throws IOException will not throw
     */
    public Argument generateSequence(final SearchTerm term, final String charset)
            throws SearchException, IOException {
        if (term instanceof ExtendedModifiedSinceTerm) {
            return modifiedSince((ExtendedModifiedSinceTerm) term);
        } else if (term instanceof AndTerm) {
            return and((AndTerm) term, charset);
        } else if (term instanceof OrTerm) {
            return or((OrTerm) term, charset);
        } else if (term instanceof NotTerm) {
            return not((NotTerm) term, charset);
        } else {
            return super.generateSequence(term, charset);
        }
    }

    /**
     * Copy of SearchSequence and method to support recursively searching with modification sequence.
     *
     * @param term the and search term
     * @param charset the character set
     * @return the IMAP search sequence argument
     * @throws SearchException will not throw
     * @throws IOException will not throw
     */
    protected Argument and(final AndTerm term, final String charset)
            throws SearchException, IOException {
        // Combine the sequences for both terms
        SearchTerm[] terms = term.getTerms();
        // Generate the search sequence for the first term
        Argument result = generateSequence(terms[0], charset);
        // Append other terms
        for (int i = 1; i < terms.length; i++) {
            result.append(generateSequence(terms[i], charset));
        }
        return result;
    }

    /**
     * Copy of SearchSequence or method to support recursively searching with modification sequence.
     *
     * @param term the or search term
     * @param charset the character set
     * @return the IMAP search sequence argument
     * @throws SearchException will not throw
     * @throws IOException will not throw
     */
    protected Argument or(final OrTerm term, final String charset)
            throws SearchException, IOException {
        SearchTerm[] terms = term.getTerms();
        OrTerm temp = term;
        /* The IMAP OR operator takes only two operands. So if
         * we have more than 2 operands, group them into 2-operand
         * OR Terms.
         */
        if (terms.length > 2) {
            SearchTerm t = terms[0];

            // Include rest of the terms
            for (int i = 1; i < terms.length; i++) {
                t = new OrTerm(t, terms[i]);
            }

            temp = (OrTerm) t; 	// set 'term' to the new jumbo OrTerm we
            // just created
            terms = temp.getTerms();
        }

        // 'term' now has only two operands
        Argument result = new Argument();

        // Add the OR search-key, if more than one term
        if (terms.length > 1) {
            result.writeAtom("OR");
        }

        /* If this term is an AND expression, we need to enclose it
         * within paranthesis.
         *
         * AND expressions are either AndTerms or FlagTerms
         */
        if (terms[0] instanceof AndTerm || terms[0] instanceof FlagTerm) {
            result.writeArgument(generateSequence(terms[0], charset));
        } else {
            result.append(generateSequence(terms[0], charset));
        }

        // Repeat the above for the second term, if there is one
        if (terms.length > 1) {
            if (terms[1] instanceof AndTerm || terms[1] instanceof FlagTerm) {
                result.writeArgument(generateSequence(terms[1], charset));
            } else {
                result.append(generateSequence(terms[1], charset));
            }
        }

        return result;
    }

    /**
     * Copy of SearchSequence not method to support recursively searching with modification sequence.
     *
     * @param term the not search term
     * @param charset the character set
     * @return the IMAP search sequence argument
     * @throws SearchException will not throw
     * @throws IOException will not throw
     */
    protected Argument not(final NotTerm term, final String charset)
            throws SearchException, IOException {
        Argument result = new Argument();

        // Add the NOT search-key
        result.writeAtom("NOT");

        /* If this term is an AND expression, we need to enclose it
         * within paranthesis.
         *
         * AND expressions are either AndTerms or FlagTerms
         */
        SearchTerm nterm = term.getTerm();
        if (nterm instanceof AndTerm || nterm instanceof FlagTerm) {
            result.writeArgument(generateSequence(nterm, charset));
        } else {
            result.append(generateSequence(nterm, charset));
        }

        return result;
    }

    /**
     * Modification of SearchSequence modifiedSince method to support optional entry name and entry type.
     *
     * @param term the extended modified since search term
     * @return the IMAP search sequence argument
     */
    protected Argument modifiedSince(final ExtendedModifiedSinceTerm term) {
        Argument result = new Argument();
        result.writeAtom("MODSEQ");

        if (term.getEntryName() != null && term.getEntryType() != null) {
            result.writeAtom("\"" + term.getEntryName() + "\"");
            result.writeAtom(term.getEntryType());
        }

        result.writeNumber(term.getModSeq());
        return result;
    }
}
