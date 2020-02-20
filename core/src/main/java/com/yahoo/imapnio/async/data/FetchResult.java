package com.yahoo.imapnio.async.data;

import java.util.List;

import javax.annotation.Nonnull;

import com.sun.mail.imap.protocol.IMAPResponse;

/**
 * This class provides the list of IMAP responses from fetch command response with parsing from ImapResponseMapper. It will convert the response to
 * {@code FetchResponse} when the response contains key "FETCH". ex: * 1 FETCH (UID 4 MODSEQ (65402) FLAGS (\Seen)). If the response doesn't contain
 * key "FETCH", it won't convert. ex: * VANISHED (EARLIER) 41,43:116,118,120:211,214:540
 */
public class FetchResult {

    /** The collection of IMAP responses. */
    private final List<IMAPResponse> imapResponses;

    /**
     * Initializes a {@code FetchResult} object with IMAP responses collection.
     *
     * @param imapResponses collection of IMAP responses from fetch command result
     */
    public FetchResult(@Nonnull final List<IMAPResponse> imapResponses) {
        this.imapResponses = imapResponses;
    }

    /**
     * @return IMAP responses collection from fetch or UID fetch command result
     */
    @Nonnull
    public List<IMAPResponse> getIMAPResponses() {
        return imapResponses;
    }
}
