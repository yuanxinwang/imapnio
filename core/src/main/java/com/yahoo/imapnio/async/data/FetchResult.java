package com.yahoo.imapnio.async.data;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sun.mail.imap.protocol.IMAPResponse;

/**
 * This class provides the highest modification sequence and the list of IMAP response from fetch command response.
 */
public class FetchResult {

    /** The highest modification sequence, null when Fetch command doesn't contain CHANGEDSINCE or Mailbox doesn't support MODSEQ. **/
    private Long highestModSeq;

    /** The collection of IMAP responses. */
    private final List<IMAPResponse> imapResponseses;

    /**
     * Initializes a {@code FetchResult} object with IMAP responses collection.
     *
     * @param imapResponseses collection of IMAP responses from fetch command result
     */
    public FetchResult(@Nonnull final List<IMAPResponse> imapResponseses) {
        this.highestModSeq = null;
        this.imapResponseses = imapResponseses;
    }

    /**
     * Initializes a {@code FetchResult} object with the highest modification sequence and IMAP responses collection.
     *
     * @param highestModSeq the highest modification from fetch command result
     * @param imapResponseses collection of IMAP responses from fetch command result
     */
    public FetchResult(@Nonnull final Long highestModSeq, @Nonnull final List<IMAPResponse> imapResponseses) {
        this.highestModSeq = highestModSeq;
        this.imapResponseses = imapResponseses;
    }

    /**
     * @return the highest modification sequence from fetch or UID IMAP command result
     */
    @Nullable
    public Long getHighestModSeq() {
        return highestModSeq;
    }

    /**
     * @return IMAP responses collection from fetch or UID fetch command result
     */
    @Nonnull
    public List<IMAPResponse> getIMAPResponses() {
        return imapResponseses;
    }
}
