package com.yahoo.imapnio.async.data;

import java.util.List;

import javax.annotation.Nullable;

/**
 * This class provides the list of IMAP responses and the highest modification sequence.
 */
public class ExpungeResult {

    /** The collection of IMAP responses. */
    private final VanishedResponse vanishedResponse;

    /** The highest modification sequence. */
    private final Long highestModSeq;

    /** The expunge message number list. */
    private final List<Integer> expungeMessageNumberList;

    /**
     * Initializes a {@link ExpungeResult} object with the vanished response and the highest modification sequence.
     *
     * @param vanishedResponse vanished response from Expunge command result
     * @param highestModSeq the highest modification from Expunge command result
     */
    public ExpungeResult(@Nullable final VanishedResponse vanishedResponse, @Nullable final Long highestModSeq) {
        this.vanishedResponse = vanishedResponse;
        this.highestModSeq = highestModSeq;
        this.expungeMessageNumberList = null;
    }

    /**
     * Initializes a {@link ExpungeResult} object with IMAP responses collection and the highest modification sequence.
     *
     * @param expungeMessageNumberList the expunge message number list from Expunge command result
     * @param highestModSeq the highest modification from Expunge command result
     */
    public ExpungeResult(@Nullable final List<Integer> expungeMessageNumberList, @Nullable final Long highestModSeq) {
        this.vanishedResponse = null;
        this.highestModSeq = highestModSeq;
        this.expungeMessageNumberList = expungeMessageNumberList;
    }

    /**
     * @return the highest modification sequence from Expunge or UID Expunge command result
     */
    @Nullable
    public Long getHighestModSeq() {
        return highestModSeq;
    }

    /**
     * @return IMAP responses collection from Expunge or UID Expunge command result
     */
    @Nullable
    public VanishedResponse getVanishedResponse() {
        return vanishedResponse;
    }

    /**
     * @return the expunge message number list
     */
    @Nullable
    public List<Integer> getExpungeMessageNumberList() {
        return expungeMessageNumberList;
    }
}
