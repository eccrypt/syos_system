package com.syos.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import com.syos.model.StockBatch;

public class StockBatchIterator implements Iterator<StockBatch> {
	private final PriorityQueue<StockBatch> queue;
	public StockBatchIterator(List<StockBatch> batches, Comparator<StockBatch> comparator) {
		this.queue = new PriorityQueue<>(comparator);
		this.queue.addAll(batches);
	}

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public StockBatch next() {
		return queue.poll();
	}
}