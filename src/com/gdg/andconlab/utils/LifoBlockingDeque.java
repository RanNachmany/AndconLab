package com.gdg.andconlab.utils;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class LifoBlockingDeque <E> extends LinkedBlockingDeque<E> {

    private static final long serialVersionUID = -4854985351588039351L;

    public LifoBlockingDeque() {
    }

    public LifoBlockingDeque(int capacity) {
        super(capacity);
    }

    public LifoBlockingDeque(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public boolean offer(E e) {
        return super.offerFirst(e);
    }

    @Override
    public boolean offer(E e,long timeout, TimeUnit unit) throws InterruptedException {
        return super.offerFirst(e,timeout, unit);
    }


    @Override
    public boolean add(E e) {
        return super.offerFirst(e);
    }

    @Override
    public void put(E e) throws InterruptedException {
        super.putFirst(e);
    }
}