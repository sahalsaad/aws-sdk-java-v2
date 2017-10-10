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

public class PaginatedItemsIterable<ResponseT, ItemT> implements SdkIterable<ItemT> {

    private final Iterator<ResponseT> responseIterator;
    private final Function<ResponseT, Iterator<ItemT>> getPaginatedItemIterator;

    public PaginatedItemsIterable(Iterator<ResponseT> responseIterator,
                                  Function<ResponseT, Iterator<ItemT>> getPaginatedItemIterator) {
        this.responseIterator = responseIterator;
        this.getPaginatedItemIterator = getPaginatedItemIterator;
    }


    @Override
    public Iterator<ItemT> iterator() {
        return new ItemIterator(responseIterator);
    }

    private class ItemIterator implements Iterator<ItemT> {

        private Iterator<ItemT> itemsIterator;

        ItemIterator(Iterator<ResponseT> responseIterator) {
            this.itemsIterator = getPaginatedItemIterator.apply(responseIterator.next());
        }

        @Override
        public boolean hasNext() {
            return (itemsIterator != null && itemsIterator.hasNext()) ||
                    responseIterator.hasNext();
        }

        @Override
        public ItemT next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            if (itemsIterator == null || !itemsIterator.hasNext()) {
                itemsIterator = getPaginatedItemIterator.apply(responseIterator.next());
            }

            return itemsIterator.next();
        }
    }

}
