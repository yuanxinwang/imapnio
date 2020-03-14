package com.yahoo.imapnio.async.request;

/**
 * Search return option for Search Extension from https://tools.ietf.org/html/rfc4731.
 *
 * <pre>
 * search-return-data = "MIN" SP nz-number /
 *                      "MAX" SP nz-number /
 *                      "ALL" SP sequence-set /
 *                      "COUNT" SP number
 *                      ;; conforms to the generic
 *                      ;; search-return-data syntax defined
 *                      ;; in [IMAPABNF]
 *
 * search-return-opt  = "MIN" / "MAX" / "ALL" / "COUNT"
 *                      ;; conforms to generic search-return-opt
 *                      ;; syntax defined in [IMAPABNF]
 * </pre>
 */
public enum SearchReturnOption {
    /** Return the lowest message number/UID that satisfies the SEARCH criteria. */
    MIN,
    /** Return the highest message number/UID that satisfies the SEARCH criteria. */
    MAX,
    /** Return all message numbers/UIDs that satisfy the SEARCH criteria. */
    ALL,
    /** Return number of the messages that satisfy the SEARCH criteria. */
    COUNT
}
