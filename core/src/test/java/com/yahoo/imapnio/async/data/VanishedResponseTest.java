package com.yahoo.imapnio.async.data;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.IMAPResponse;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException;

/**
 * Unit test for {@link VanishedResponse}.
 */
public class VanishedResponseTest {
    /**
     * Tests VanishedResponse constructor and getters.
     *
     * @throws IOException will not throw
     * @throws ProtocolException will not throw
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testVanishedResponseParsing() throws IOException, ProtocolException, ImapAsyncClientException {

        final IMAPResponse ir = new IMAPResponse("* VANISHED (EARLIER) 41,43:116,118,120:211,214:540");
        final VanishedResponse vr = new VanishedResponse(ir);
        Assert.assertTrue(vr.isEarlier(), "isEarlier() mismatched.");
        Assert.assertEquals(MessageNumberSet.buildString(vr.getKnownUids()), "41,43:116,118,120:211,214:540",
                "getKnownUids() mismatched.");
    }

    /**
     * Tests VanishedResponse constructor and getters.
     */
    @Test
    public void testVanishedResponse() {
        final MessageNumberSet[] messageNumberSets = new MessageNumberSet[] { new MessageNumberSet(1L, 1L) };
        final VanishedResponse vr = new VanishedResponse(true, messageNumberSets);
        Assert.assertTrue(vr.isEarlier(), "isEarlier() mismatched.");
        Assert.assertEquals(MessageNumberSet.buildString(vr.getKnownUids()), "1", "getKnownUids() mismatched.");
    }

    /**
     * Tests VanishedResponse with exception.
     *
     * @throws IOException will not throw
     * @throws ProtocolException will not throw
     */
    @Test
    public void testVanishedResponseException() throws IOException, ProtocolException {

        final IMAPResponse ir = new IMAPResponse("* VANISHED (EARLIE 41,43:116,118,120:211,214:540");
        ImapAsyncClientException exception = null;
        try {
            new VanishedResponse(ir);
        } catch (ImapAsyncClientException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getFaiureType(), ImapAsyncClientException.FailureType.UNKNOWN_PARSE_RESULT_TYPE, "Result mismatched.");
    }
}
