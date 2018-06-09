package xin.yansh.course.searchengine.spider.scheduler.component;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import xin.yansh.course.searchengine.spider.Task;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

public class BloomFilterDuplicateRemover implements DuplicateRemover {

    private int expectedInsertions;

    private double fpp;

    private AtomicInteger counter;

    public BloomFilterDuplicateRemover(int expectedInsertions) {
        this(expectedInsertions, 0.01);
    }

    public BloomFilterDuplicateRemover(int expectedInsertions, double fpp) {
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.bloomFilter = rebuildBloomFilter();
    }

    protected BloomFilter<CharSequence> rebuildBloomFilter() {
        counter = new AtomicInteger(0);
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
    }

    private final BloomFilter<CharSequence> bloomFilter;

    @Override
    public boolean isDuplicate(String url, Task task) {
        boolean isDuplicate = bloomFilter.mightContain(url);
        if (!isDuplicate) {
            bloomFilter.put(url);
            counter.incrementAndGet();
        }
        return isDuplicate;
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        rebuildBloomFilter();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return counter.get();
    }
}