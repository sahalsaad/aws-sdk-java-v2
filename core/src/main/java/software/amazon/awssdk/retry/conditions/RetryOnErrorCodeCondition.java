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

package software.amazon.awssdk.retry.conditions;

import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.AmazonServiceException;
import software.amazon.awssdk.retry.RetryPolicyContext;

public class RetryOnErrorCodeCondition implements RetryCondition {

    private final Set<String> retryableErrorCodes;

    public RetryOnErrorCodeCondition(Set<String> retryableErrorCodes) {
        this.retryableErrorCodes = new HashSet<>(retryableErrorCodes);
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {

        Exception ex = context.exception();
        if (ex != null && ex instanceof AmazonServiceException) {
            AmazonServiceException ase = (AmazonServiceException) ex;

            if (retryableErrorCodes.contains(ase.getErrorCode())) {
                return true;
            }
        }
        return false;
    }
}
