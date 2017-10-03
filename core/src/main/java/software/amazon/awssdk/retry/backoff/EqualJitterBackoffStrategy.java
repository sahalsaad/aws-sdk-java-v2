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

import java.util.Random;
import software.amazon.awssdk.retry.RetryPolicyContext;

public class EqualJitterBackoffStrategy implements BackoffStrategy {
    private final int baseDelay;
    private final int maxBackoffTime;
    private final int numRetries;
    private final Random random = new Random();

    public EqualJitterBackoffStrategy(final int baseDelay,
                                      final int maxBackoffTime,
                                      final int numRetries) {
        this.baseDelay = baseDelay;
        this.maxBackoffTime = maxBackoffTime;
        this.numRetries = numRetries;
    }

    @Override
    public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
        int ceil = calculateExponentialDelay(context.retriesAttempted(), baseDelay, maxBackoffTime, numRetries);
        return (ceil / 2) + random.nextInt((ceil / 2) + 1);
    }
}
