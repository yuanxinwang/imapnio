package com.yahoo.imapnio.async.data;

import com.sun.mail.imap.protocol.FetchResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class provides the highest modification sequence, the list of fetch response, and the list of modified message
 * number from store command response.
 */
public class StoreResult {

    /** The highest modification sequence, only shown when CondStore is enabled. */
    private Long highestModSeq;

    /** The collection of fetch response. */
    private final List<FetchResponse> fetchResponses;

    /** The collection of message number, only shown when CondStore is enabled. */
    private final List<Long> modifiedMsgsets;

    /**
     * Initializes a {@code StoreResult} object with fetch responses collection,
     * and modified message number collection.
     *
     * @param fetchResponses collection of fetch responses from store command result
     * @param modifiedMsgsets collection of modified message number from store command result
     */
    public StoreResult(@Nonnull final List<FetchResponse> fetchResponses, @Nonnull final List<Long> modifiedMsgsets) {
        this.highestModSeq = null;
        this.fetchResponses = fetchResponses;
        this.modifiedMsgsets = modifiedMsgsets;
    }

    /**
     * Initializes a {@code StoreResult} object with the highest modification sequence, fetch responses collection,
     * and modified message number collection.
     *
     * @param highestModSeq the highest modification from store command result
     * @param fetchResponses collection of fetch responses from store command result
     * @param modifiedMsgsets collection of modified message number from store command result
     */
    public StoreResult(@Nullable final Long highestModSeq, @Nonnull final List<FetchResponse> fetchResponses,
                       @Nonnull final List<Long> modifiedMsgsets) {
        this.highestModSeq = highestModSeq;
        this.fetchResponses = fetchResponses;
        this.modifiedMsgsets = modifiedMsgsets;
    }

    /**
     * @return the highest modification sequence from store or UID store command result
     */
    public Long getHighestModSeq() {
        return highestModSeq;
    }

    /**
     * @return fetch responses collection from store or UID store command result
     */
    public List<FetchResponse> getFetchResponses() {
        return fetchResponses;
    }

    /**
     * @return modified message number collection from store or UID store command result
     */
    public List<Long> getModifiedMsgsets() {
        return modifiedMsgsets;
    }
}
