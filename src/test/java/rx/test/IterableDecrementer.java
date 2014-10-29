/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.test;

import java.util.Iterator;

/**
 * Iterates from some arbitrary starting value down to 0, or forever if the starting value is
 * Long.MAX_VALUE.
 */
public class IterableDecrementer implements Iterable<Integer> {

    private final long from;

    /**
     * Creates new {@link IterableDecrementer}.
     *
     * @param from the value to start from. If equal to Long.MAX_VALUE, iteration never stops.
     */
    public IterableDecrementer(final long from) {
        if (from < 0) {
            throw new IllegalArgumentException("from < 0");
        }
        this.from = from;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Repeater((int) from);
    }

    class Repeater implements Iterator<Integer> {

        private int i;

        public Repeater(final int repeats) {
            i = repeats;
        }

        @Override
        public boolean hasNext() {
            return i > 0 || from == Long.MAX_VALUE;
        }

        @Override
        public Integer next() {
            return --i;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}
