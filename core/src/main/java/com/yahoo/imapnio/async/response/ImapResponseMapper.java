package com.yahoo.imapnio.async.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.Folder;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.AppendUID;
import com.sun.mail.imap.CopyUID;
import com.sun.mail.imap.protocol.FetchItem;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPResponse;
import com.sun.mail.imap.protocol.ListInfo;
import com.sun.mail.imap.protocol.MailboxInfo;
import com.sun.mail.imap.protocol.Status;
import com.sun.mail.imap.protocol.UIDSet;
import com.yahoo.imapnio.async.data.Capability;
import com.yahoo.imapnio.async.data.ExtensionMailboxInfo;
import com.yahoo.imapnio.async.data.FetchResult;
import com.yahoo.imapnio.async.data.IdResult;
import com.yahoo.imapnio.async.data.ListInfoList;
import com.yahoo.imapnio.async.data.MessageNumberSet;
import com.yahoo.imapnio.async.data.SearchResult;
import com.yahoo.imapnio.async.data.StoreResult;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException.FailureType;

/**
 * This class parses the IMAP response to the proper IMAP object that SUN supports.
 */
public class ImapResponseMapper {

    /** APPENDUID keyword. */
    private static final String APPENDUID = "APPENDUID";

    /** EQUAL sign. */
    private static final String EQUAL = "=";

    /** [ char. */
    private static final char L_BRACKET = '[';

    /** ] char. */
    private static final char R_BRACKET = ']';

    /** Inner class instance parser. */
    private ImapResponseParser parser;

    /** Empty fetch items array. */
    private static final FetchItem[] EMPTY_FETCH_ITEMS = new FetchItem[0];

    /**
     * Initializes a @{code ImapResponseMapper} object.
     */
    public ImapResponseMapper() {
        parser = new ImapResponseParser();
    }

    /**
     * Method to deserialize IMAPResponse content from given IMAPResponse content String.
     *
     * @param <T> the object to serialize to
     * @param content list of IMAPResponse obtained from server.
     * @param valueType class name that this api will convert to.
     * @return the serialized object
     * @throws ImapAsyncClientException when target class to covert to is not supported
     * @throws ProtocolException if underlying input contains invalid content of type for the returned type
     * @throws IOException when fetch response cannot be casted to the expected item
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public <T> T readValue(@Nonnull final IMAPResponse[] content, @Nonnull final Class<T> valueType)
            throws ImapAsyncClientException, ProtocolException, IOException {
        if (valueType == Capability.class) {
            return (T) parser.parseToCapabilities(content);
        }
        if (valueType == AppendUID.class) {
            return (T) parser.parseToAppendUid(content);
        }
        if (valueType == CopyUID.class) {
            return (T) parser.parseToCopyUid(content);
        }
        if (valueType == ExtensionMailboxInfo.class) {
            return (T) parser.parseToExtensionMailboxInfo(content);
        }
        if (valueType == MailboxInfo.class) {
            return (T) parser.parseToMailboxInfo(content);
        }
        if (valueType == ListInfoList.class) {
            return (T) parser.parseToListInfoList(content);
        }
        if (valueType == Status.class) {
            return (T) parser.parseToStatus(content);
        }
        if (valueType == IdResult.class) {
            return (T) parser.parseToIdResult(content);
        }
        if (valueType == SearchResult.class) {
            return (T) parser.parseToSearchResult(content);
        }

        return readValue(content, valueType, EMPTY_FETCH_ITEMS);
    }

    /**
     * Method to deserialize IMAPResponse content from given IMAPResponse content String.
     *
     * @param <T> the object to serialize to
     * @param content list of IMAPResponse obtained from server.
     * @param valueType class name that this api will convert to.
     * @param extensionItems the array of extension FetchItem
     * @return the serialized object
     * @throws ImapAsyncClientException when target class to covert to is not supported
     * @throws ProtocolException if underlying input contains invalid content of type for the returned type
     */
    @Nonnull
    public <T> T readValue(@Nonnull final IMAPResponse[] content, @Nonnull final Class<T> valueType, @Nonnull final FetchItem[] extensionItems)
            throws ImapAsyncClientException, ProtocolException {
        if (valueType == StoreResult.class) {
            return (T) parser.parseToStoreResult(content, extensionItems);
        }
        if (valueType == FetchResult.class) {
            return (T) parser.parseToFetchResult(content, extensionItems);
        }

        throw new ImapAsyncClientException(FailureType.UNKNOWN_PARSE_RESULT_TYPE);
    }

    /**
     * Inner class to perform the parsing of IMAPResponse to various objects.
     */
    private class ImapResponseParser {

        /** Capability string. */
        private static final String CAPABILITY = "CAPABILITY";

        /**
         * Parses the capabilities from a CAPABILITY response or from a CAPABILITY response code attached to (e.g.) an OK response.
         *
         * @param rs the CAPABILITY responses
         * @return a map that has the key (token) and all its values (after = sign), if there is no equal sign, value is same as key
         * @throws ImapAsyncClientException when input IMAPResponse array is not valid
         */
        @Nonnull
        private Capability parseToCapabilities(@Nonnull final IMAPResponse[] rs) throws ImapAsyncClientException {
            String s;
            final Map<String, List<String>> capas = new HashMap<String, List<String>>();
            if (rs.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }

            for (final IMAPResponse r : rs) {
                if (!hasCapability(r)) {
                    continue;
                }
                while ((s = r.readAtom()) != null) {
                    if (s.length() == 0) {
                        if (r.peekByte() == (byte) R_BRACKET) {
                            break;
                        }
                        // Probably found something here that's not an atom. Rather than loop forever or fail completely, we'll try to skip this bogus
                        // capability. This is known to happen with: Netscape Messaging Server 4.03 (built Apr 27 1999) that returns:
                        // * CAPABILITY * CAPABILITY IMAP4 IMAP4rev1 ...
                        // The "*" in the middle of the capability list causes us to loop forever here.
                        r.skipToken();
                    } else {
                        final String[] tokens = s.split(EQUAL);
                        final String key = tokens[0];
                        final String value = (tokens.length > 1) ? tokens[1] : null;
                        final String upperCase = key.toUpperCase(Locale.ENGLISH);
                        List<String> values = capas.get(upperCase);
                        if (values == null) {
                            values = new ArrayList<>();
                            capas.put(upperCase, values);
                        }
                        // AUTH key allows more than one pair(ex:AUTH=XOAUTH2 AUTH=PLAIN), parsing value out to List, otherwise add key to list
                        if (value != null) {
                            values.add(value);
                        }
                    }
                }
            }
            // making the value list immutable
            for (final Map.Entry<String, List<String>> entry : capas.entrySet()) {
                entry.setValue(Collections.unmodifiableList(entry.getValue()));
            }
            return new Capability(capas);
        }

        /**
         * Returns true if the response has capability keyword; false otherwise.
         *
         * @param r the response to check
         * @return true if the response has capability keyword; false otherwise
         */
        private boolean hasCapability(final IMAPResponse r) {
            // case 1, from capability or authenticate command. EX: * CAPABILITY IMAP4rev1 SASL-IR
            if (r.keyEquals(CAPABILITY)) {
                return true;
            }

            // case 2. from server greeting. EX: OK [CAPABILITY IMAP4rev1 SASL-IR AUTH=PLAIN] IMAP4rev1 Hello
            byte b;
            while ((b = r.readByte()) > 0 && b != (byte) '[') {
                // eat chars till [
            }
            if (b == 0) { // left bracket not found
                return false;
            }
            final String s = r.readAtom();
            return s.equalsIgnoreCase(CAPABILITY);
        }

        /**
         * Parses APPEND response to a AppendUID instance.
         *
         * @param rs the APPEND responses
         * @return AppendUID instance
         * @throws ImapAsyncClientException when input value is not valid
         */
        @Nullable
        private AppendUID parseToAppendUid(@Nonnull final IMAPResponse[] rs) throws ImapAsyncClientException {
            if (rs.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final IMAPResponse r = rs[rs.length - 1];
            if (!r.isOK()) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            byte b;
            while ((b = r.readByte()) > 0 && b != (byte) L_BRACKET) {
                // eat chars till [
            }

            if (b == 0) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final String s = r.readAtom();
            if (!s.equalsIgnoreCase(APPENDUID)) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final long uidvalidity = r.readLong();
            final long uid = r.readLong();
            return new AppendUID(uidvalidity, uid);
        }

        /**
         * Parses COPY or MOVE command responses to a CopyUID instance.
         *
         * @param rr the COPY responses
         * @return COPYUID instance built from copy command responses
         * @throws ImapAsyncClientException when input value is not valid
         */
        @Nonnull
        private CopyUID parseToCopyUid(@Nonnull final IMAPResponse[] rr) throws ImapAsyncClientException {
            // For copy response, it is at the last response, for move command response, it is the first response
            for (int i = rr.length - 1; i >= 0; i--) {
                final Response r = rr[i];
                if (r == null || !r.isOK()) {
                    continue;
                }
                byte b;
                while ((b = r.readByte()) > 0 && b != (byte) L_BRACKET) {
                    // eat chars till [
                }

                if (b == 0) {
                    continue;
                }
                final String s = r.readAtom();
                if (!s.equalsIgnoreCase("COPYUID")) { // expunge response from MOVE, for ex: 2 EXPUNGE
                    continue;
                }
                final long uidvalidity = r.readLong();
                final String src = r.readAtom();
                final String dst = r.readAtom();
                return new CopyUID(uidvalidity, UIDSet.parseUIDSets(src), UIDSet.parseUIDSets(dst));
            }
            throw new ImapAsyncClientException(FailureType.INVALID_INPUT); // when rr length is 0
        }

        /**
         * Parses the SELECT or EXAMINE responses to a MailboxInfo instance.
         *
         * @param rr the list of responses from SELECT or EXAMINE, this input r array should contain the tagged/final one
         * @return MailboxInfo instance
         * @throws ParsingException when encountering parsing exception
         * @throws ImapAsyncClientException when input is invalid
         */
        @Nonnull
        private MailboxInfo parseToMailboxInfo(@Nonnull final IMAPResponse[] rr) throws ParsingException, ImapAsyncClientException {
            if (rr.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final MailboxInfo minfo = new MailboxInfo(rr);
            setupMailboxInfoAccessMode(rr[rr.length - 1], minfo);

            return minfo;
        }

        /**
         * Sets up the mode for the @{code MailboxInfo}.
         *
         * @param lastResp the tagged response
         * @param minfo the @{code MailboxInfo} instance
         */
        private void setupMailboxInfoAccessMode(@Nonnull final Response lastResp, @Nonnull final MailboxInfo minfo) {
            if (lastResp.isTagged() && lastResp.isOK()) { // command successful
                if (lastResp.toString().indexOf("READ-ONLY") != -1) {
                    minfo.mode = Folder.READ_ONLY;
                } else {
                    minfo.mode = Folder.READ_WRITE;
                }
            }
        }

        /**
         * Parses the SELECT or EXAMINE responses to a @{code ExtensionMailboxInfo} instance.
         *
         * @param rr the list of responses from SELECT or EXAMINE, this input r array should contain the tagged/final one
         * @return MailboxInfo instance
         * @throws ParsingException when encountering parsing exception
         * @throws ImapAsyncClientException when input is invalid
         */
        @Nonnull
        private ExtensionMailboxInfo parseToExtensionMailboxInfo(@Nonnull final IMAPResponse[] rr) throws ParsingException, ImapAsyncClientException {
            if (rr.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final ExtensionMailboxInfo minfo = new ExtensionMailboxInfo(rr);
            setupMailboxInfoAccessMode(rr[rr.length - 1], minfo);
            return minfo;
        }

        /**
         * Parses the LIST or LSUB responses to a @{code ListInfo} list. List responses example:
         *
         * <pre>
         * * LIST () "/" INBOX
         * * LIST (\NoSelect) "/" "Public mailboxes"
         * </pre>
         *
         * @param r the list of responses from SELECT, the input responses array should contain the tagged/final one
         * @return list of ListInfo objects
         * @throws ParsingException when encountering parsing exception
         * @throws ImapAsyncClientException when input value is not valid
         */
        @Nonnull
        private ListInfoList parseToListInfoList(@Nonnull final IMAPResponse[] r) throws ParsingException, ImapAsyncClientException {
            if (r.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final Response response = r[r.length - 1];
            if (!response.isOK()) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }

            // command successful reaching here
            final List<ListInfo> v = new ArrayList<ListInfo>();
            for (int i = 0, len = r.length - 1; i < len; i++) {
                final IMAPResponse ir = r[i];
                if (ir.keyEquals("LIST") || ir.keyEquals("LSUB")) {
                    v.add(new ListInfo(ir));
                }
            }

            // could be an empty list if the search criteria ends up no result. Ex:
            // a002 LIST "" "*t3*"
            // a002 OK LIST completed
            return new ListInfoList(v);
        }

        /**
         * Parses the Status responses to a @{code Status}.
         *
         * @param r the list of responses from Status command, the input responses array should contain the tagged/final one
         * @return Status object constructed based on the r array
         * @throws ParsingException when encountering parsing exception
         * @throws ImapAsyncClientException when input value is not valid
         */
        @Nonnull
        private Status parseToStatus(@Nonnull final IMAPResponse[] r) throws ParsingException, ImapAsyncClientException {
            if (r.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final Response taggedResponse = r[r.length - 1];
            if (!taggedResponse.isOK()) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            Status status = null;
            for (int i = 0, len = r.length; i < len; i++) {
                final IMAPResponse ir = r[i];
                if (ir.keyEquals("STATUS")) {
                    if (status == null) {
                        status = new Status(ir);
                    } else { // collect them all if each attributes comes in its own line
                        Status.add(status, new Status(ir));
                    }
                }
            }
            if (status == null) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT); // when r length is 0 or no Status response
            }
            return status;
        }

        /**
         * Parses the ID responses to a @{code IdResult} object.
         *
         * @param r the list of responses from ID command, the input responses array should contain the tagged/final one
         * @return IdResult object constructed based on the given IMAPResponse array
         * @throws ParsingException when encountering parsing exception
         * @throws ImapAsyncClientException when input value is not valid
         */
        @Nonnull
        private IdResult parseToIdResult(@Nonnull final IMAPResponse[] ir) throws ParsingException, ImapAsyncClientException {
            if (ir.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final Response taggedResponse = ir[ir.length - 1];
            if (!taggedResponse.isOK()) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }

            final Map<String, String> serverParams = new HashMap<String, String>();
            for (int j = 0, len = ir.length; j < len; j++) {
                final IMAPResponse r = ir[j];
                if (r.keyEquals("ID")) {
                    r.skipSpaces();
                    int c = r.peekByte();
                    if (c == 'N' || c == 'n') { // assume NIL
                        return new IdResult(Collections.unmodifiableMap(Collections.EMPTY_MAP));
                    }

                    final String[] v = r.readStringList();
                    if (v == null) {
                        // this means it does not start with (, ID result is expected to have () enclosed
                        throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
                    }

                    for (int i = 0; i < v.length; i += 2) {
                        final String name = v[i];
                        if (name == null || (i + 1 >= v.length)) {
                            throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
                        }
                        final String value = v[i + 1];
                        serverParams.put(name, value);
                    }
                }
            }

            return new IdResult(Collections.unmodifiableMap(serverParams));
        }

        /**
         * Parses the responses from UID search command to a @{code SearchResult} object.
         *
         * @param ir the list of responses from UID search command, the input responses array should contain the tagged/final one
         * @return SearchResult object constructed based on the given IMAPResponse array
         * @throws ImapAsyncClientException when tagged response is not OK or given response length is 0
         */
        @Nonnull
        private SearchResult parseToSearchResult(@Nonnull final IMAPResponse[] ir) throws ImapAsyncClientException {
            if (ir.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final Response taggedResponse = ir[ir.length - 1];
            if (!taggedResponse.isOK()) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final List<Long> v = new ArrayList<Long>(); // will always return a non-null array

            Long modSeq = null;

            // Grab all SEARCH responses
            long num;
            for (final IMAPResponse sr : ir) {
                // There *will* be one SEARCH response.
                if (sr.keyEquals("SEARCH")) {
                    while ((num = sr.readLong()) != -1) {
                        v.add(num);
                    }
                    if (sr.readByte() == '(') {
                        final String s = sr.readAtom();
                        if (s.equalsIgnoreCase("MODSEQ")) {
                            modSeq = sr.readLong();
                        }
                    }
                }
            }

            return new SearchResult(v, modSeq);
        }

        /**
         * Parses the responses from Store command and UID Store command to a @{code StoreResult} object.
         *
         * @param ir the list of responses from Store or UID Store command, the input responses array should contain the tagged/final one
         * @param extensionItems the array of extension FetchItem
         * @return StoreResult object constructed based on the given IMAPResponse array,
         * @throws ImapAsyncClientException when tagged response is bad or given response length is 0
         * @throws ProtocolException when fetch response cannot be parsed
         */
        @Nonnull
        private StoreResult parseToStoreResult(@Nonnull final IMAPResponse[] ir, @Nonnull final FetchItem[] extensionItems)
                throws ImapAsyncClientException, ProtocolException {
            if (ir.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final Response taggedResponse = ir[ir.length - 1];
            if (taggedResponse.isBAD()) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final List<IMAPResponse> imapResponses = new ArrayList<>(ir.length); // will always return a non-null array

            MessageNumberSet[] modifiedMsgsets = null; // only one response will contain modified numbers
            Long highestModSeq = null;

            for (final IMAPResponse sr: ir) {
                imapResponses.add(parseToFetchResponse(sr, extensionItems));

                sr.skipSpaces();
                if (sr.readByte() == (byte) L_BRACKET) { // HIGHESTMODSEQ or MODIFIED
                    final String responseCode = sr.readAtom();
                    if (sr.isOK() && responseCode.equalsIgnoreCase("HIGHESTMODSEQ")) {
                        highestModSeq = sr.readLong();
                    } else if (responseCode.equalsIgnoreCase("MODIFIED")) {
                        modifiedMsgsets = MessageNumberSet.buildMessageNumberSets(sr.readAtom());
                    }
                }
            }

            return new StoreResult(highestModSeq, imapResponses, modifiedMsgsets);
        }

        /**
         * Parses the responses from Fetch command and UID Fetch command to a @{code FetchResult} object.
         *
         * @param ir the list of responses from Fetch or UID Fetch command, the input responses array should contain the tagged/final one
         * @param extensionItems the array of extension FetchItem
         * @return FetchResult object constructed based on the given IMAPResponse array,
         * @throws ImapAsyncClientException when tagged response is not OK or given response length is 0
         * @throws ProtocolException when fetch response cannot be parsed
         */
        @Nonnull
        private FetchResult parseToFetchResult(@Nonnull final IMAPResponse[] ir, @Nonnull final FetchItem[] extensionItems)
                throws ImapAsyncClientException, ProtocolException {
            if (ir.length < 1) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final Response taggedResponse = ir[ir.length - 1];
            if (taggedResponse.isBAD()) {
                throw new ImapAsyncClientException(FailureType.INVALID_INPUT);
            }
            final List<IMAPResponse> imapResponses = new ArrayList<>(ir.length); // will always return a non-null array

            for (final IMAPResponse sr: ir) {
                imapResponses.add(parseToFetchResponse(sr, extensionItems));
            }

            return new FetchResult(imapResponses);
        }

        /**
         * This methods converts a response to @{code FetchResponse} if it detects it is a fetch response format; otherwise it keeps as it is.
         *
         * @param response IMAPResponse to be converted to Fetch response
         * @param extensionItems the array of extension FetchItem
         * @return parsed response if IMAPResponse is a fetch response else return the passed value
         * @throws ProtocolException when encountering error
         */
        @Nonnull
        private IMAPResponse parseToFetchResponse(@Nonnull final IMAPResponse response, @Nonnull final FetchItem[] extensionItems)
                throws ProtocolException {
            if (response.keyEquals("FETCH")) {
                try {
                    final FetchResponse r = new FetchResponse(response, extensionItems);
                    return r;
                } catch (final IOException e) {
                    throw new ProtocolException("Encountering IOException when constructing FetchResponse", e);
                }
            }
            return response;
        }
    }
}
