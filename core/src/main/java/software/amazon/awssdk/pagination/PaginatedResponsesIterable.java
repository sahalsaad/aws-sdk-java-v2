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

package software.amazon.awssdk.pagination;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public final class PaginatedResponsesIterable<ResponseT> implements SdkIterable<ResponseT> {

    private final ResponseT firstResponse;
    private final Function<ResponseT, ResponseT> getNextResponse;

    public PaginatedResponsesIterable(ResponseT firstResponse,
                                      Function<ResponseT, ResponseT> getNextResponse) {

        this.firstResponse = firstResponse;
        this.getNextResponse = getNextResponse;
    }

    @Override
    public Iterator<ResponseT> iterator() {
        return new PaginatedIterator(firstResponse);
    }


    private class PaginatedIterator implements Iterator<ResponseT> {

        private ResponseT currentResponse;

        PaginatedIterator(ResponseT currentResponse) {
            this.currentResponse = currentResponse;
        }

        @Override
        public boolean hasNext() {
            return currentResponse != null;
        }

        @Override
        public ResponseT next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            ResponseT oldValue = currentResponse;

            currentResponse = getNextResponse.apply(currentResponse);

            return oldValue;
        }
    }

}
