package com.piotrwalczak;

import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ReactiveExecutionQueueImpl<K, V> implements ReactiveExecutionQueue<K, V> {

    private final Map<K, Queue<V>> queuePerKey = new ConcurrentHashMap<>();

    @Setter
    private Function<V, K> keyFunction;

    Sinks.Many<V> sink = Sinks.many().unicast().onBackpressureBuffer();

    /**
    put(v):
        mono = v -> Mono.just(v)
                .map(v -> function(v))
                .doFinally(flushForKey(k))
        if queuePerKey.isEmpty() then
            v -> sink(v)
        else
            queuePerKey.put(mono)

     flushForKey(k):
        queuePerKey(k).poll() -> sink()
     */

    public Flux<V> toFlux() {
        return this.sink.asFlux();
    }

    public void put(V value) {
        K key = keyFunction.apply(value);
        Queue<V> queue = this.queuePerKey.get(key);
        queue.add(value);
        if (queue.size() == 1) {
            processNextForKey(key);
        }
    }

    private Mono<V> process(V value) {
        K key = keyFunction.apply(value);
        return Mono.just(value)
                // here
                .doFinally(signalType -> {
                    removeProcessed(key, value);
                    processNextForKey(key);
                });
    }

    private void processNextForKey(K key) {
        V value = this.queuePerKey.get(key).peek();
        if (value != null) {
            Mono<V> mono = process(value);
            mono.subscribe(v -> this.sink.emitNext(v, (signalType, emitResult) -> true));
        }
    }

    private void removeProcessed(K key, V value) {
        var q = this.queuePerKey.get(key);
        V v = q.poll();
        if (v != value) {
            throw new RuntimeException("Shit went wrong big time.");
        }
    }


}
