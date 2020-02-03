package com.yahoo.imapnio.async.request;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import com.yahoo.imapnio.async.data.MessageNumberSet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * This class defines IMAP fetch command request from client. ABNF in RFC3501 is described as following:
 *
 * <pre>
 * {@code
 * fetch           = "FETCH" SP sequence-set SP ("ALL" / "FULL" / "FAST" /
 *                   fetch-att / "(" fetch-att *(SP fetch-att) ")")
 *
 * fetch-att       = "ENVELOPE" / "FLAGS" / "INTERNALDATE" /
 *                   "RFC822" [".HEADER" / ".SIZE" / ".TEXT"] /
 *                   "BODY" ["STRUCTURE"] / "UID" /
 *                   "BODY" section ["<" number "." nz-number ">"] /
 *                   "BODY.PEEK" section ["<" number "." nz-number ">"]
 * }
 * </pre>
 */
public abstract class AbstractFetchCommand extends ImapRequestAdapter {

    /** FETCH and space. */
    private static final String FETCH_SP = "FETCH ";

    /** Byte array for FETCH. */
    private static final byte[] FETCH_SP_B = FETCH_SP.getBytes(StandardCharsets.US_ASCII);

    /** UID FETCH and space. */
    private static final String UID_FETCH_SP = "UID FETCH ";

    /** Byte array for UID FETCH. */
    private static final byte[] UID_FETCH_SP_B = UID_FETCH_SP.getBytes(StandardCharsets.US_ASCII);

    /** Changed Since and space. */
    private static final String CHANGEDSINCE_SP = "CHANGEDSINCE ";

    /** Byte array for Changed Since. */
    private static final byte[] CHANGEDSINCE_SP_B = CHANGEDSINCE_SP.getBytes(StandardCharsets.US_ASCII);

    /** Byte array for CR and LF, keeping the array local so it cannot be modified by others. */
    private static final byte[] CRLF_B = { '\r', '\n' };

    /** Message numbers, either message sequence or UID. */
    private String msgNumbers;

    /** Fetch items. */
    private String dataItems;

    /** Changed since the given modification sequence. */
    private Long changedSince;

    /** Fetch macro. */
    private FetchMacro macro;

    /** True if prepending UID; false otherwise. */
    private boolean isUid;

    /**
     * Initializes a @{code FetchCommand} with the @{code MessageNumberSet} array.
     *
     * @param isUid whether prepending UID
     * @param msgsets the set of message set
     * @param items the data items
     */
    public AbstractFetchCommand(final boolean isUid, @Nonnull final MessageNumberSet[] msgsets, @Nonnull final String items) {
        this.isUid = isUid;
        this.msgNumbers = MessageNumberSet.buildString(msgsets);
        this.macro = null;
        this.dataItems = items;
        this.changedSince = null;
    }

    /**
     * Initializes a @{code FetchCommand} with the @{code MessageNumberSet} array.
     *
     * @param isUid whether prepending UID
     * @param msgsets the set of message set
     * @param macro the macro
     */
    public AbstractFetchCommand(final boolean isUid, @Nonnull final MessageNumberSet[] msgsets, @Nonnull final FetchMacro macro) {
        this(isUid, MessageNumberSet.buildString(msgsets), macro);
    }

    /**
     * Initializes a @{code FetchCommand} with the @{code MessageNumberSet} array.
     *
     * @param isUid whether prepending UID
     * @param msgNumbers the set of message set
     * @param macro the macro
     */
    public AbstractFetchCommand(final boolean isUid, @Nonnull final String msgNumbers, @Nonnull final FetchMacro macro) {
        this.isUid = isUid;
        this.msgNumbers = msgNumbers;
        this.macro = macro;
        this.dataItems = null;
        this.changedSince = null;
    }

    /**
     * Initializes a @{code FetchCommand} with the @{code MessageNumberSet} array.
     *
     * @param isUid whether prepending UID
     * @param msgNumbers the message numbers string
     * @param items the data items
     */
    public AbstractFetchCommand(final boolean isUid, @Nonnull final String msgNumbers, @Nonnull final String items) {
        this.isUid = isUid;
        this.msgNumbers = msgNumbers;
        this.dataItems = items;
        this.macro = null;
        this.changedSince = null;
    }

    /**
     * Initializes a @{code FetchCommand} with the @{code MessageNumberSet} array and changed since modification sequence.
     *
     * @param isUid whether prepending UID
     * @param msgsets the set of message set
     * @param items the data items
     * @param changedSince changed since the given modification sequence
     */
    public AbstractFetchCommand(final boolean isUid, @Nonnull final MessageNumberSet[] msgsets, @Nonnull final String items,
                                @Nonnull final Long changedSince) {
        this(isUid, MessageNumberSet.buildString(msgsets), items, changedSince);
    }

    /**
     * Initializes a @{code FetchCommand} with the @{code MessageNumberSet} array and changed since modification sequence.
     *
     * @param isUid whether prepending UID
     * @param msgsets the set of message set
     * @param macro the macro
     * @param changedSince changed since the given modification sequence
     */
    public AbstractFetchCommand(final boolean isUid, @Nonnull final MessageNumberSet[] msgsets, @Nonnull final FetchMacro macro,
                                @Nonnull final Long changedSince) {
        this(isUid, MessageNumberSet.buildString(msgsets), macro, changedSince);
    }

    /**
     * Initializes a @{code FetchCommand} with the @{code MessageNumberSet} array.
     *
     * @param isUid whether prepending UID
     * @param msgNumbers the message numbers string
     * @param items the data items
     * @param changedSince changed since the given modification sequence
     */
    protected AbstractFetchCommand(final boolean isUid, @Nonnull final String msgNumbers, @Nonnull final String items,
                                   @Nonnull final Long changedSince) {
        this.isUid = isUid;
        this.msgNumbers = msgNumbers;
        this.dataItems = items;
        this.macro = null;
        this.changedSince = changedSince;
    }

    /**
     * Initializes a @{code FetchCommand} with the @{code MessageNumberSet} array.
     *
     * @param isUid whether prepending UID
     * @param msgNumbers the message numbers string
     * @param macro the macro
     * @param changedSince changed since the given modification sequence
     */
    protected AbstractFetchCommand(final boolean isUid, @Nonnull final String msgNumbers, @Nonnull final FetchMacro macro,
                                   @Nonnull final Long changedSince) {
        this.isUid = isUid;
        this.msgNumbers = msgNumbers;
        this.macro = macro;
        this.dataItems = null;
        this.changedSince = changedSince;
    }

    @Override
    public void cleanup() {
        this.msgNumbers = null;
        this.dataItems = null;
        this.macro = null;
        this.changedSince = null;
    }

    @Override
    public ByteBuf getCommandLineBytes() {
        final ByteBuf sb = Unpooled.buffer();
        sb.writeBytes(isUid ? UID_FETCH_SP_B : FETCH_SP_B);
        sb.writeBytes(msgNumbers.getBytes(StandardCharsets.US_ASCII));
        sb.writeByte(ImapClientConstants.SPACE);

        if (dataItems != null) {
            sb.writeByte(ImapClientConstants.L_PAREN);
            sb.writeBytes(dataItems.getBytes(StandardCharsets.US_ASCII));
            sb.writeByte(ImapClientConstants.R_PAREN);
        } else {
            sb.writeBytes(macro.name().getBytes(StandardCharsets.US_ASCII));
        }

        if (changedSince != null) {
            sb.writeByte(ImapClientConstants.SPACE);
            sb.writeByte(ImapClientConstants.L_PAREN);
            sb.writeBytes(CHANGEDSINCE_SP_B);
            sb.writeBytes(String.valueOf(changedSince).getBytes(StandardCharsets.US_ASCII));
            sb.writeByte(ImapClientConstants.R_PAREN);
        }

        sb.writeBytes(CRLF_B);

        return sb;
    }
}
