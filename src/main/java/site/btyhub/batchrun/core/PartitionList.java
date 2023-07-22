package site.btyhub.batchrun.core;

import java.util.AbstractList;
import java.util.List;

/**
 *
 * @author: baotingyu
 * @date: 2023/7/20
 **/
public class PartitionList extends AbstractList<List> {

    final List list;
    final int batchSize;

    public PartitionList(List list, int batchSize) {
        this.list = list;
        this.batchSize = batchSize;
    }

    @Override
    public List get(int index) {
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException();
        }
        int start = index * batchSize;
        int end = Math.min(start + batchSize, list.size());
        return list.subList(start, end);
    }

    @Override
    public int size() {
        return list.size() % batchSize == 0 ? list.size() / batchSize : list.size() / batchSize + 1;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
