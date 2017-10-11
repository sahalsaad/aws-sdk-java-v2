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

import software.amazon.awssdk.annotation.SdkPublicApi;
import software.amazon.awssdk.config.ClientOverrideConfiguration;
import software.amazon.awssdk.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.retry.backoff.EqualJitterBackoffStrategy;
import software.amazon.awssdk.retry.backoff.FixedDelayBackoffStrategy;
import software.amazon.awssdk.retry.backoff.FullJitterBackoffStrategy;
import software.amazon.awssdk.retry.conditions.AndRetryCondition;
import software.amazon.awssdk.retry.conditions.OrRetryCondition;
import software.amazon.awssdk.retry.conditions.RetryCondition;
import software.amazon.awssdk.retry.conditions.RetryOnErrorCodeCondition;
import software.amazon.awssdk.retry.conditions.RetryOnExceptionsCondition;
import software.amazon.awssdk.retry.conditions.RetryOnStatusCodeCondition;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

/**
 * Interface for specifying a retry policy to use when evaluating whether or not a request should be retried. An implementation
 * of this interface can be provided to {@link ClientOverrideConfiguration#retryPolicy} or the {@link #builder()}} can be used
 * to construct a retry policy from SDK provided policies or policies that directly implement {@link BackoffStrategy} and/or
 * {@link RetryCondition}.
 *
 * When using the {@link #builder()} the SDK will use default values for fields that are not provided. The default number of
 * retries that will be used is {@link SdkDefaultRetrySettings#DEFAULT_NUM_RETRIES}. The default retry condition is
 * {@link RetryCondition#DEFAULT} and the default backoff strategy is {@link BackoffStrategy#DEFAULT}.
 *
 * @see RetryCondition for a list of SDK provided retry condition strategies
 * @see BackoffStrategy for a list of SDK provided backoff strategies
 */
@SdkPublicApi
public interface RetryPolicy extends BackoffStrategy, RetryCondition, ToCopyableBuilder<RetryPolicy.Builder, RetryPolicy> {

    RetryPolicy DEFAULT = RetryPolicy.builder()
                                     .backoffStrategy(BackoffStrategy.DEFAULT)
                                     .numRetries(SdkDefaultRetrySettings.DEFAULT_NUM_RETRIES)
                                     .retryCondition(RetryCondition.DEFAULT)
                                     .build();

    RetryPolicy NONE = RetryPolicy.builder()
                                  .backoffStrategy(BackoffStrategy.NONE)
                                  .retryCondition(RetryCondition.NONE)
                                  .build();

    static Builder builder() {
        return new SimpleRetryPolicy.Builder();
    }

    /**
     * Builder interface for {@link RetryPolicy}
     */
    interface Builder extends CopyableBuilder<Builder, RetryPolicy> {

        /**
         * Specifies the maximum number of retries to be executed for a request.
         *
         * @param numRetries Number of retries
         * @return This builder for method chaining
         */
        Builder numRetries(Integer numRetries);

        /**
         * The number of retries configured with {@link #numRetries()}.
         */
        Integer numRetries();

        /**
         * Specifies the backoff strategy to use when retrying requests.
         *
         * @see BackoffStrategy
         * @see EqualJitterBackoffStrategy
         * @see FixedDelayBackoffStrategy
         * @see FullJitterBackoffStrategy
         *
         * @param backoffStrategy The backoff strategy
         * @return This builder for method chaining
         */
        Builder backoffStrategy(BackoffStrategy backoffStrategy);

        /**
         * The backoff strategy configured with {@link #backoffStrategy()}.
         */
        BackoffStrategy backoffStrategy();

        /**
         * Specifies the retry condition to use when retrying requests.
         *
         * @see RetryCondition
         * @see AndRetryCondition
         * @see OrRetryCondition
         * @see RetryOnErrorCodeCondition
         * @see RetryOnStatusCodeCondition
         * @see RetryOnExceptionsCondition
         *
         * @param retryCondition The retry condition
         * @return This builder for method chaining
         */
        Builder retryCondition(RetryCondition retryCondition);

        /**
         * The retry condition configured with {@link #retryCondition()}.
         */
        RetryCondition retryCondition();
    }
}
