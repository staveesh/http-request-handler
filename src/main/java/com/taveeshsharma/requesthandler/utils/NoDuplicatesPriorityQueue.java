package com.taveeshsharma.requesthandler.utils;

import java.util.Comparator;
import java.util.PriorityQueue;

public class NoDuplicatesPriorityQueue<E> extends PriorityQueue<E> {

    public NoDuplicatesPriorityQueue(Comparator<? super E> cmp) {
        super(cmp);
    }

    public boolean add(E e) {
        boolean isAdded = false;
        if (!super.contains(e)) {
            isAdded = super.add(e);
        }
        return isAdded;
    }
}
