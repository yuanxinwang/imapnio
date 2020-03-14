package com.yahoo.imapnio.async.request;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.internet.MimeUtility;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;

import com.sun.mail.iap.Argument;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.SearchSequence;
import com.yahoo.imapnio.async.data.Capability;
import com.yahoo.imapnio.async.data.MessageNumberSet;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException.FailureType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * This class defines IMAP search command request from client.
 *
 * <pre>
 * search              = "SEARCH" [SP "CHARSET" SP astring] 1*(SP search-key)
 *                       ; CHARSET argument to MUST be registered with IANA
 *
 * search-key          = "ALL" / "ANSWERED" / "BCC" SP astring /
 *                       "BEFORE" SP date / "BODY" SP astring /
 *                       "CC" SP astring / "DELETED" / "FLAGGED" /
 *                       "FROM" SP astring / "KEYWORD" SP flag-keyword /
 *                       "NEW" / "OLD" / "ON" SP date / "RECENT" / "SEEN" /
 *                       "SINCE" SP date / "SUBJECT" SP astring /
 *                       "TEXT" SP astring / "TO" SP astring /
 *                       "UNANSWERED" / "UNDELETED" / "UNFLAGGED" /
 *                       "UNKEYWORD" SP flag-keyword / "UNSEEN" /
 *                         ; Above this line were in [IMAP2]
 *                       "DRAFT" / "HEADER" SP header-fld-name SP astring /
 *                       "LARGER" SP number / "NOT" SP search-key /
 *                       "OR" SP search-key SP search-key /
 *                       "SENTBEFORE" SP date / "SENTON" SP date /
 *                       "SENTSINCE" SP date / "SMALLER" SP number /
 *                       "UID" SP sequence-set / "UNDRAFT" / sequence-set /
 *                       "(" search-key *(SP search-key) ")"
 *
 * search-key          =/ search-modsequence
 *                       ;; Modifies original IMAP4 search-key.
 *                       ;;
 *                       ;; This change applies to all commands
 *                       ;; referencing this non-terminal -- in
 *                       ;; particular, SEARCH, SORT, and THREAD.
 *
 * search-modsequence  = "MODSEQ" [search-modseq-ext] SP
 *                          mod-sequence-valzer
 *
 * search-modseq-ext   = SP entry-name SP entry-type-req
 * </pre>
 */
public abstract class AbstractSearchCommand extends ImapRequestAdapter {

    /** Byte array for CR and LF, keeping the array local so it cannot be modified by others. */
    private static final byte[] CRLF_B = { '\r', '\n' };

    /** Character set literal. */
    private static final String CHARSET = "CHARSET";

    /** Charset literal following by a space. */
    private static final byte[] CHARSET_B = CHARSET.getBytes(StandardCharsets.US_ASCII);

    /** RETURN. */
    private static final String RETURN = "RETURN";

    /** SEARCH . */
    private static final String SEARCH = "SEARCH";

    /** SEARCH in byte array. */
    private static final byte[] SEARCH_B = SEARCH.getBytes(StandardCharsets.US_ASCII);

    /** UID SEARCH. */
    private static final String UID_SEARCH = "UID SEARCH";

    /** UID SEARCH in byte array. */
    private static final byte[] UID_SEARCH_B = UID_SEARCH.getBytes(StandardCharsets.US_ASCII);

    /** Flag whether adding UID before search. */
    private boolean isUid;

    /** Options for search return. */
    private SearchReturnOption[] options;

    /** Message numbers in string type, specified based on RFC3501 sequence-set syntax. */
    private String msgNumbers;

    /** The search expression. */
    private Argument searchExpr;

    /** Character set. */
    private String charset;

    /** flag whether server allows LITERAL+. */
    private boolean isLiteralPlusEnabled;

    /**
     * Initializes the object with the MessageNumberSet array, search string and character set name.
     *
     * @param isUid whether it is UID Search command
     * @param msgsets the set of MessageNumberSet
     * @param term the search string
     * @param capa the capability instance to find if it has literal
     * @throws ImapAsyncClientException when both msgsets and searchString are null
     * @throws IOException when parsing error for generate sequence
     * @throws SearchException when search term cannot be found
     */
    protected AbstractSearchCommand(final boolean isUid, @Nullable final MessageNumberSet[] msgsets, @Nullable final SearchTerm term,
            @Nullable final Capability capa) throws ImapAsyncClientException, SearchException, IOException {
        this(isUid, MessageNumberSet.buildString(msgsets), term, capa);
    }

    /**
     * Initializes the object with the MessageNumberSet array, search string and character set name.
     *
     * @param isUid whether it is UID Search command
     * @param options options for search return
     * @param msgsets the set of MessageNumberSet
     * @param term the search string
     * @param capa the capability instance to find if it has literal
     * @throws ImapAsyncClientException when both msgsets and searchString are null
     * @throws IOException when parsing error for generate sequence
     * @throws SearchException when search term cannot be found
     */
    protected AbstractSearchCommand(final boolean isUid, @Nullable final SearchReturnOption[] options, @Nullable final MessageNumberSet[]
            msgsets, @Nullable final SearchTerm term, @Nullable final Capability capa) throws ImapAsyncClientException, SearchException, IOException {
        this(isUid, options, MessageNumberSet.buildString(msgsets), term, capa);
    }

    /**
     * Initializes the object with the string form of message sequence, search string and character set name.
     *
     * @param isUid whether it is UID Search command
     * @param msgNumbers the set of MessageNumberSet
     * @param term the search term
     * @param capa the capability instance to find if it has literal
     * @throws ImapAsyncClientException when both msgsets and searchString are null
     * @throws IOException when parsing error for generate sequence
     * @throws SearchException when search term cannot be found
     */
    protected AbstractSearchCommand(final boolean isUid, @Nullable final String msgNumbers, @Nullable final SearchTerm term,
            @Nullable final Capability capa) throws ImapAsyncClientException, SearchException, IOException {
        this(isUid, null, msgNumbers, term, capa);
    }

    /**
     * Initializes the object with the string form of message sequence, search string and character set name.
     *
     * @param isUid whether it is UID Search command
     * @param msgNumbers the set of MessageNumberSet
     * @param charset the character set
     * @param args the argument containing the search term
     * @param capa the capability instance to find if it has literal
     * @throws ImapAsyncClientException when both msgsets and searchString are null
     */
    protected AbstractSearchCommand(final boolean isUid, @Nullable final String msgNumbers, @Nullable final String charset,
            @Nonnull final Argument args, @Nullable final Capability capa) throws ImapAsyncClientException {
        this(isUid, null, msgNumbers, charset, args, capa);
    }

    /**
     * Initializes the object with the string form of message sequence, search string and character set name.
     *
     * @param isUid whether it is UID Search command
     * @param options options for search return
     * @param msgNumbers the set of MessageNumberSet
     * @param term the search term
     * @param capa the capability instance to find if it has literal
     * @throws ImapAsyncClientException when both msgsets and searchString are null
     * @throws IOException when parsing error for generate sequence
     * @throws SearchException when search term cannot be found
     */
    protected AbstractSearchCommand(final boolean isUid, @Nullable final SearchReturnOption[] options, @Nullable final String msgNumbers,
            @Nullable final SearchTerm term, @Nullable final Capability capa) throws ImapAsyncClientException, SearchException, IOException {
        // based on [ABNF] above, 1*(SP search-key), cannot have both null
        if (msgNumbers == null && term == null) {
            throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
        }
        this.isUid = isUid;
        this.options = options;
        this.msgNumbers = msgNumbers;

        // maintain same semantic with IMAPProtocol, but we do not retry when fail in various of charsets. we only use UTF_8
        this.charset = SearchSequence.isAscii(term) ? null : StandardCharsets.UTF_8.name();

        if (term != null) {
            final ExtendedSearchSequence searchSeq = new ExtendedSearchSequence();
            this.searchExpr = searchSeq.generateSequence(term, charset == null ? null : MimeUtility.javaCharset(charset));
        }
        this.isLiteralPlusEnabled = (capa != null) ? capa.hasCapability(ImapClientConstants.LITERAL_PLUS) : false;
    }

    /**
     * Initializes the object with the string form of message sequence, search string and character set name.
     *
     * @param isUid whether it is UID Search command
     * @param options options for search return
     * @param msgNumbers the set of MessageNumberSet
     * @param charset the character set
     * @param args the argument containing the search term
     * @param capa the capability instance to find if it has literal
     * @throws ImapAsyncClientException when both msgsets and searchString are null
     */
    protected AbstractSearchCommand(final boolean isUid, @Nullable final SearchReturnOption[] options, @Nullable final String msgNumbers,
            @Nullable final String charset, @Nonnull final Argument args, @Nullable final Capability capa) throws ImapAsyncClientException {
        // based on [ABNF] above, 1*(SP search-key), cannot have both null
        if (msgNumbers == null && args == null) {
            throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
        }

        this.isUid = isUid;
        this.options = options;
        this.msgNumbers = msgNumbers;
        this.charset = charset;
        this.searchExpr = args;
        this.isLiteralPlusEnabled = (capa != null) ? capa.hasCapability(ImapClientConstants.LITERAL_PLUS) : false;
    }

    @Override
    public void cleanup() {
        this.options = null;
        this.msgNumbers = null;
        this.searchExpr = null;
        this.charset = null;
    }

    @Override
    public ByteBuf getCommandLineBytes() throws ImapAsyncClientException {
        final StringBuilder sb = new StringBuilder();
        int optionsSize = 0;
        if (options != null) {
            sb.append(ImapClientConstants.SPACE);
            sb.append(RETURN);
            sb.append(ImapClientConstants.SPACE);
            sb.append(ImapClientConstants.L_PAREN);
            sb.append(options[0].name());
            for (int i = 1; i < options.length; i++) {
                sb.append(ImapClientConstants.SPACE);
                sb.append(options[i].name());
            }

            sb.append(ImapClientConstants.R_PAREN);
            optionsSize = sb.length();
        }

        final int charsetSize = charset != null ? charset.length() : 0;
        final int msgNumbersSize = msgNumbers != null ? msgNumbers.length() : 0;
        final int searchExprSize = searchExpr != null ? searchExpr.toString().length() : 0;
        final int len = ImapClientConstants.PAD_LEN  + optionsSize + charsetSize + msgNumbersSize + searchExprSize;
        final ByteBuf byteBuf = Unpooled.buffer(len);
        byteBuf.writeBytes(isUid ? UID_SEARCH_B : SEARCH_B);

        if (optionsSize > 0) {
            byteBuf.writeBytes(sb.toString().getBytes(StandardCharsets.US_ASCII));
        }

        if (charset != null) {
            byteBuf.writeByte(ImapClientConstants.SPACE);
            byteBuf.writeBytes(CHARSET_B);
            byteBuf.writeByte(ImapClientConstants.SPACE);
            byteBuf.writeBytes(charset.getBytes(StandardCharsets.US_ASCII));
        }

        if (msgNumbers != null) {
            byteBuf.writeByte(ImapClientConstants.SPACE);
            byteBuf.writeBytes(msgNumbers.getBytes(StandardCharsets.US_ASCII));
        }

        if (searchExpr != null) {
            byteBuf.writeByte(ImapClientConstants.SPACE);
            try {
                searchExpr.write(new ByteBufWriter(byteBuf, isLiteralPlusEnabled));
            } catch (final IOException | ProtocolException e) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT, e);
            }
        }
        byteBuf.writeBytes(CRLF_B);
        return byteBuf;
    }
}
