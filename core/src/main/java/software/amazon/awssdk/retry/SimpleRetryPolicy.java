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

import software.amazon.awssdk.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.retry.conditions.AndRetryCondition;
import software.amazon.awssdk.retry.conditions.MaxNumberOfRetriesCondition;
import software.amazon.awssdk.retry.conditions.RetryCondition;

public class SimpleRetryPolicy implements RetryPolicy {

    private final BackoffStrategy backoffStrategy;
    private final RetryCondition retryCondition;
    private final Integer numRetries;

    SimpleRetryPolicy(Builder builder) {
        this.backoffStrategy = builder.backoffStrategy != null ? builder.backoffStrategy : BackoffStrategy.DEFAULT;

        int numRetries = builder.numRetries != null ? builder.numRetries : SdkDefaultRetryPolicies.DEFAULT_NUM_RETRIES;
        this.numRetries = numRetries;

        RetryCondition condition = builder.retryCondition != null ? builder.retryCondition : RetryCondition.DEFAULT;
        this.retryCondition = new AndRetryCondition(new MaxNumberOfRetriesCondition(numRetries),
                                                    condition);
    }

    @Override
    public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
        return backoffStrategy.computeDelayBeforeNextRetry(context);
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return retryCondition.shouldRetry(context);
    }

    @Override
    public RetryPolicy.Builder toBuilder() {
        return new Builder()
            .retryCondition(retryCondition)
            .numRetries(numRetries)
            .backoffStrategy(backoffStrategy);
    }

    /**
     * Builder for a {@link SimpleRetryPolicy}.
     */
    static final class Builder implements RetryPolicy.Builder {

        private Integer numRetries;
        private BackoffStrategy backoffStrategy;
        private RetryCondition retryCondition;

        @Override
        public RetryPolicy.Builder numRetries(Integer numRetries) {
            this.numRetries = numRetries;
            return this;
        }

        @Override
        public Integer numRetries() {
            return numRetries;
        }

        @Override
        public RetryPolicy.Builder backoffStrategy(BackoffStrategy backoffStrategy) {
            this.backoffStrategy = backoffStrategy;
            return this;
        }

        @Override
        public BackoffStrategy backoffStrategy() {
            return backoffStrategy;
        }

        @Override
        public RetryPolicy.Builder retryCondition(RetryCondition retryCondition) {
            this.retryCondition = retryCondition;
            return this;
        }

        @Override
        public RetryCondition retryCondition() {
            return retryCondition;
        }

        @Override
        public RetryPolicy build() {
            return new SimpleRetryPolicy(this);
        }
    }
}
