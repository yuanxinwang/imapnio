package com.yahoo.imapnio.async.data;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.IMAPResponse;

/**
 * Unit test for {@code FetchResult}.
 */
public class FetchResultTest {
    /**
     * Tests FetchResult constructor and getters.
     */
    @Test
    public void testFetchResult() throws IOException, ProtocolException {
        final IMAPResponse imapResponse = new IMAPResponse("* 1 FETCH (UID 4 MODSEQ (12121231000))");
        final List<IMAPResponse> expectedFetchResponses = Collections.singletonList(imapResponse);
        final FetchResult infos = new FetchResult(expectedFetchResponses);
        final List<IMAPResponse>fetchResponsesResult = infos.getIMAPResponses();
        Assert.assertEquals(fetchResponsesResult.size(), 1, "getIMAPResponses() mismatched.");
        Assert.assertTrue(fetchResponsesResult.get(0).keyEquals("FETCH"), "getIMAPResponses() mismatched.");
    }
}
