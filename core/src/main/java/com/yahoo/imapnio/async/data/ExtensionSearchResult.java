package com.yahoo.imapnio.async.data;

import javax.annotation.Nullable;

/**
 * This class provides the modification sequence, tag and the list of message sequence numbers from search command response.
 */
public class ExtensionSearchResult {
    /** The tag before search command. */
    private String tag;

    /** The lowest message number/UID that satisfies the SEARCH criteria. */
    private Long min;

    /** The highest message number/UID that satisfies the SEARCH criteria. */
    private Long max;

    /** All message numbers/UIDs that satisfy the SEARCH criteria. */
    private MessageNumberSet[] all;

    /** Number of the messages that satisfy the SEARCH criteria. */
    private Long count;

    /** The modification sequence satisfy the SEARCH criteria. */
    private Long modSeq;

    /**
     * Initializes a {@link ExtensionSearchResult} object with the lowest message number/UID, the highest message number/UID, the lowest message
     * number, number of the messages, and the modification sequence that satisfy the SEARCH criteria.
     *
     * @param tag the tag from search command
     * @param min the lowest message number/UID that satisfies the SEARCH criteria
     * @param max the highest message number/UID that satisfies the SEARCH criteria
     * @param all all message numbers/UIDs that satisfy the SEARCH criteria
     * @param count number of the messages that satisfy the SEARCH criteria
     * @param modSeq the modification sequence that satisfy the SEARCH criteria
     */
    public ExtensionSearchResult(@Nullable final String tag, @Nullable final Long min, @Nullable final  Long max,
                                 @Nullable final  MessageNumberSet[] all, @Nullable final Long count, @Nullable final Long modSeq) {
        this.tag = tag;
        this.min = min;
        this.max = max;
        this.all = all;
        this.count = count;
        this.modSeq = modSeq;
    }

    /**
     * @return the tag before search command
     */
    @Nullable
    public String getTag() {
        return tag;
    }

    /**
     * @return the lowest message number/UID that satisfies the SEARCH criteria
     */
    @Nullable
    public Long getMin() {
        return min;
    }

    /**
     * @return the highest message number/UID that satisfies the SEARCH criteria
     */
    @Nullable
    public Long getMax() {
        return max;
    }

    /**
     * @return all message numbers/UIDs that satisfy the SEARCH criteria
     */
    @Nullable
    public MessageNumberSet[] getAll() {
        return all;
    }

    /**
     * @return number of the messages that satisfy the SEARCH criteria
     */
    @Nullable
    public Long getCount() {
        return count;
    }

    /**
     * @return the modification sequence that satisfy the SEARCH criteria
     */
    @Nullable
    public Long getModSeq() {
        return modSeq;
    }
}
