package com.yahoo.imapnio.async.data;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPResponse;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException;

/**
 * Unit test for {@code FetchResult}.
 */
public class FetchResultTest {
    /**
     * Tests FetchResult constructor and getters.
     *
     * @throws IOException will not throw
     * @throws ProtocolException will not throw
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testFetchResult() throws IOException, ProtocolException, ImapAsyncClientException {
        final FetchResponse fetchResponse = new FetchResponse(new IMAPResponse("* 1 FETCH (UID 4 MODSEQ (12121231000))"));
        final VanishedResponse vanishedResponse = new VanishedResponse((new IMAPResponse("* VANISHED (EARLIER) 300:310,405,411")));
        final List<FetchResponse> expectedFetchResponses = Collections.singletonList(fetchResponse);
        final FetchResult fr = new FetchResult(expectedFetchResponses, vanishedResponse);
        final List<FetchResponse>fetchResponsesResult = fr.getFetchResponses();
        Assert.assertEquals(fetchResponsesResult.size(), 1, "getFetchResponses() mismatched.");
        Assert.assertTrue(fetchResponsesResult.get(0).keyEquals("FETCH"), "getFetchResponses() mismatched.");
        Assert.assertTrue(fr.getVanishedResponse().isEarlier(), "getVanishedResponse() mismatched.");
    }
}
