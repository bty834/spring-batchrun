package site.btyhub.batchrun.core;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author: baotingyu
 * @date: 2023/7/20
 **/
public class PartitionArray implements Iterable<Object[]> {

    private final Object[] original;
    private final int batchSize;

    public PartitionArray(Object[] original, int batchSize) {
        this.original = original;
        this.batchSize = batchSize;
    }

    @Override
    public Iterator<Object[]> iterator() {
        return new PartitionArrayIterator();
    }

    private class PartitionArrayIterator implements Iterator<Object[]> {

        private int cursor;

        private final int iterNum;

        public PartitionArrayIterator() {
            iterNum = getIterNum();
        }

        @Override
        public boolean hasNext() {
            return cursor != iterNum;
        }

        @Override
        public Object[] next() {
            final int tmpIterNum = iterNum;
            int i = cursor;

            if (i >= tmpIterNum) {
                throw new NoSuchElementException();
            }
            if (i >= getIterNum()) {
                throw new ConcurrentModificationException();
            }
            cursor = i + 1;

            return Arrays.copyOfRange(original, batchSize * (cursor-1),
                    Math.min(batchSize * cursor , original.length));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        final int getIterNum() {
            return original.length % batchSize == 0 ? original.length / batchSize : original.length / batchSize + 1;
        }
    }
}
