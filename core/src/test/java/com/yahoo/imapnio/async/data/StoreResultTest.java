package com.yahoo.imapnio.async.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPResponse;
import com.sun.mail.imap.protocol.MODSEQ;

/**
 * Unit test for {@code StoreResult}.
 */
public class StoreResultTest {
    /**
     * Tests StoreResult constructor and getters.
     */
    @Test
    public void testStorehResult() throws IOException, ProtocolException {
        final IMAPResponse imapResponse = new IMAPResponse("* 1 FETCH (UID 4 MODSEQ (12121231000))");
        final List<IMAPResponse> imapResponses = new ArrayList<>();
        imapResponses.add(imapResponse);
        final List<Long> modifiedMsgNums = new ArrayList<>();
        modifiedMsgNums.add(1L);
        final StoreResult infos = new StoreResult(1L, imapResponses, modifiedMsgNums);
        final List<Long> modifiedMsgsets = infos.getModifiedMsgsets();
        final List<IMAPResponse>fetchResponsesResult = infos.getIMAPResponses();
        final long highestModSeq = infos.getHighestModSeq();
        final long fetchedModSeq = new FetchResponse(fetchResponsesResult.get(0)).getItem(MODSEQ.class).modseq;
        Assert.assertEquals(modifiedMsgsets.size(), 1, "Result mismatched.");
        Assert.assertEquals(fetchResponsesResult.size(), 1, "Result mismatched.");
        Assert.assertEquals(fetchedModSeq, 12121231000L, "Result mismatched.");
        Assert.assertEquals(highestModSeq, 1L, "Result mismatched.");
    }

    /**
     * Tests StoreResult constructor and getters when passing null highest mod sequence, empty fetch responses collection,
     * and empty modified message number collection.
     */
    @Test
    public void testStorehResultNullHighestModSeq() {
        final StoreResult infos = new StoreResult(new ArrayList<>(), new ArrayList<>());
        final List<Long> modifiedMsgsets = infos.getModifiedMsgsets();
        final List<IMAPResponse>fetchResponsesResult = infos.getIMAPResponses();
        final Long highestModSeq = infos.getHighestModSeq();
        Assert.assertEquals(modifiedMsgsets.size(), 0, "Result mismatched.");
        Assert.assertEquals(fetchResponsesResult.size(), 0, "Result mismatched.");
        Assert.assertNull(highestModSeq, "Result mismatched.");
    }
}
