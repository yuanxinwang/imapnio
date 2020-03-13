package com.yahoo.imapnio.async.data;

import javax.annotation.Nonnull;

import com.sun.mail.imap.protocol.IMAPResponse;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException;

/**
 * This class represents a vanished response obtained from the input stream of an IMAP server based on
 * https://tools.ietf.org/html/rfc7162#section-3.2.10 . The VANISHED response has two forms. The first
 * form contains the EARLIER tag, which signifies that the response was caused by a UID FETCH (VANISHED)
 * or a SELECT/EXAMINE (QRESYNC) command.  The second form doesn't contain the EARLIER tag and is used
 * for announcing message removals within an already selected mailbox.
 *
 * <pre>
 * expunged-resp       =  "VANISHED" [SP "(EARLIER)"] SP known-uids
 *
 * known-uids          =  sequence-set
 *                     ;; Sequence of UIDs; "*" is not allowed.
 * </pre>
 */
public class VanishedResponse {

    /** The flag indicate whether the vanished response contains EARLIER tag. */
    private boolean earlier;

    /** The collection of UIDs as known-uids. */
    private MessageNumberSet[] knownUids;

    /**
     * Initializes a {@link VanishedResponse} object with the flag indicate whether the vanished response contains EARLIER tag
     * and the collection of UIDs as known-uids by parsing IMAP response.
     *
     * @param response the IMAP response
     * @throws ImapAsyncClientException if the IMAP response format cannot be parsed to vanished response
     */
    public VanishedResponse(@Nonnull final IMAPResponse response) throws ImapAsyncClientException {
        response.skipSpaces();
        String msg = response.readAtom();
        if (msg.isEmpty()) {
            if (response.readByte() == '(' && response.readAtom().equalsIgnoreCase("EARLIER")) {
                earlier = true;
                response.readByte(); // read ')'
                response.skipSpaces();
                msg = response.readAtom();
            } else {
                throw new ImapAsyncClientException(ImapAsyncClientException.FailureType.UNKNOWN_PARSE_RESULT_TYPE);
            }
        }
        knownUids = MessageNumberSet.buildMessageNumberSets(msg);
    }

    /**
     * Initializes a {@link VanishedResponse} object with the flag indicate whether the vanished response contains EARLIER tag
     * and the collection of UIDs as known-uids.
     *
     * @param earlier the flag indicate whether the vanished response contains EARLIER tag
     * @param knownUids the collection of UIDs as known-uids.
     */
    public VanishedResponse(final boolean earlier, @Nonnull final MessageNumberSet[] knownUids) {
        this.earlier = earlier;
        this.knownUids = knownUids;
    }

    /**
     * @return the flag indicate whether the vanished response contains EARLIER tag
     */
    public boolean isEarlier() {
        return earlier;
    }

    /**
     * @return the collection of UIDs as known-uids
     */
    @Nonnull
    public MessageNumberSet[] getKnownUids() {
        return knownUids;
    }
}

