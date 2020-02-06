package com.yahoo.imapnio.async.data;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sun.mail.imap.protocol.IMAPResponse;

/**
 * This class provides the highest modification sequence, the list of IMAP responses, and the list of modified messages
 * number from store command response.
 */
public class StoreResult extends FetchResult {

    /** The highest modification sequence, null when Fetch command doesn't contain CHANGEDSINCE or Mailbox doesn't support MODSEQ. **/
    private Long highestModSeq;

    /** The collection of message number, only shown when CondStore is enabled. */
    private final List<Long> modifiedMsgsets;

    /**
     * Initializes a {@code StoreResult} object with IMAP responses collection.
     *
     * @param imapResponses collection of IMAP responses from store command result
     */
    public StoreResult(@Nonnull final List<IMAPResponse> imapResponses) {
        super(imapResponses);
        this.highestModSeq = null;
        this.modifiedMsgsets = null;
    }

    /**
     * Initializes a {@code StoreResult} object with IMAP responses collection,
     * and modified message number collection.
     *
     * @param imapResponses collection of IMAP responses from store command result
     * @param modifiedMsgsets collection of modified message number from store command result
     */
    public StoreResult(@Nonnull final List<IMAPResponse> imapResponses, @Nonnull final List<Long> modifiedMsgsets) {
        super(imapResponses);
        this.highestModSeq = null;
        this.modifiedMsgsets = modifiedMsgsets;
    }

    /**
     * Initializes a {@code StoreResult} object with the highest modification sequence, IMAP responses collection,
     * and modified message number collection.
     *
     * @param highestModSeq the highest modification from store command result
     * @param imapResponses collection of IMAP responses from store command result
     * @param modifiedMsgsets collection of modified message number from store command result
     */
    public StoreResult(@Nonnull final Long highestModSeq, @Nonnull final List<IMAPResponse> imapResponses,
                       @Nonnull final List<Long> modifiedMsgsets) {
        super(imapResponses);
        this.highestModSeq = highestModSeq;
        this.modifiedMsgsets = modifiedMsgsets;
    }

    /**
     * @return the highest modification sequence from store or UID store command result
     */
    @Nullable
    public Long getHighestModSeq() {
        return highestModSeq;
    }

    /**
     * @return modified message number collection from store or UID store command result
     */
    @Nullable
    public List<Long> getModifiedMsgsets() {
        return modifiedMsgsets;
    }
}
