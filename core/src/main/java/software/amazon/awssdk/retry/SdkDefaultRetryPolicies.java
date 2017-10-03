/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.retry;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.RetryableException;
import software.amazon.awssdk.http.HttpStatusCodes;

public class SdkDefaultRetryPolicies {

    /**
     * When throttled retries are enabled, each retry attempt will consume this much capacity.
     * Successful retry attempts will release this capacity back to the pool while failed retries
     * will not.  Successful initial (non-retry) requests will always release 1 capacity unit to the
     * pool.
     */
    public static final int THROTTLED_RETRY_COST = 5;

    /**
     * When throttled retries are enabled, this is the total number of subsequent failed retries
     * that may be attempted before retry capacity is fully drained.
     */
    public static final int THROTTLED_RETRIES = 100;

    public static final Integer BASE_DELAY = 100;

    public static final Integer MAX_BACKOFF_IN_MILLIS = 20_000;

    public static final Integer DEFAULT_NUM_RETRIES = 3;

    public static final Set<String> THROTTLING_ERROR_CODES;
    public static final Set<String> CLOCK_SKEW_ERROR_CODES;
    public static final Set<String> RETRYABLE_ERROR_CODES;

    public static final Set<Integer> RETRYABLE_STATUS_CODES;
    public static final Set<Class> RETRYABLE_EXCEPTIONS;

    static {
        THROTTLING_ERROR_CODES = new HashSet<>();
        THROTTLING_ERROR_CODES.add("Throttling");
        THROTTLING_ERROR_CODES.add("ThrottlingException");
        THROTTLING_ERROR_CODES.add("ProvisionedThroughputExceededException");
        THROTTLING_ERROR_CODES.add("SlowDown");
        THROTTLING_ERROR_CODES.add("TooManyRequestsException");
        THROTTLING_ERROR_CODES.add("RequestLimitExceeded");
        THROTTLING_ERROR_CODES.add("BandwidthLimitExceeded");
        THROTTLING_ERROR_CODES.add("RequestThrottled");

        CLOCK_SKEW_ERROR_CODES = new HashSet<>();
        CLOCK_SKEW_ERROR_CODES.add("RequestTimeTooSkewed");
        CLOCK_SKEW_ERROR_CODES.add("RequestExpired");
        CLOCK_SKEW_ERROR_CODES.add("InvalidSignatureException");
        CLOCK_SKEW_ERROR_CODES.add("SignatureDoesNotMatch");
        CLOCK_SKEW_ERROR_CODES.add("AuthFailure");
        CLOCK_SKEW_ERROR_CODES.add("RequestInTheFuture");

        RETRYABLE_ERROR_CODES = new HashSet<>();
        RETRYABLE_ERROR_CODES.addAll(THROTTLING_ERROR_CODES);
        RETRYABLE_ERROR_CODES.addAll(CLOCK_SKEW_ERROR_CODES);

        RETRYABLE_STATUS_CODES = new HashSet<>();
        RETRYABLE_STATUS_CODES.add(HttpStatusCodes.INTERNAL_SERVER_ERROR);
        RETRYABLE_STATUS_CODES.add(HttpStatusCodes.BAD_GATEWAY);
        RETRYABLE_STATUS_CODES.add(HttpStatusCodes.SERVICE_UNAVAILABLE);
        RETRYABLE_STATUS_CODES.add(HttpStatusCodes.GATEWAY_TIMEOUT);

        RETRYABLE_EXCEPTIONS = new HashSet<>();
        RETRYABLE_EXCEPTIONS.add(RetryableException.class);
        RETRYABLE_EXCEPTIONS.add(IOException.class);
    }
}
