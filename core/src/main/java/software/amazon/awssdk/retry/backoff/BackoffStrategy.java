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

package software.amazon.awssdk.retry.backoff;

import software.amazon.awssdk.retry.RetryPolicyContext;
import software.amazon.awssdk.retry.SdkDefaultRetryPolicies;

public interface BackoffStrategy {

    BackoffStrategy DEFAULT = new FullJitterBackoffStrategy(SdkDefaultRetryPolicies.BASE_DELAY,
                                                            SdkDefaultRetryPolicies.MAX_BACKOFF_IN_MILLIS,
                                                            SdkDefaultRetryPolicies.DEFAULT_NUM_RETRIES);

    BackoffStrategy NONE = new FixedDelayBackoffStrategy(1);

    /**
     * Compute the delay before the next retry request. This strategy is only consulted when there will be a next retry.
     *
     * @param context Context about the state of the last request and information about the number of requests made.
     * @return Amount of time in milliseconds to wait before the next attempt. Must be non-negative (can be zero).
     */
    long computeDelayBeforeNextRetry(RetryPolicyContext context);

    default int calculateExponentialDelay(int retriesAttempted, int baseDelay, int maxBackoffTime, int maxRetries) {
        int retries = Math.min(retriesAttempted, maxRetries);
        return (int) Math.min((1L << retries) * baseDelay, maxBackoffTime);
    }
}
