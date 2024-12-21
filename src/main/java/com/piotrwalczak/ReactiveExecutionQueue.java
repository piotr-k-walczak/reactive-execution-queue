package com.piotrwalczak;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ReactiveExecutionQueue<K, V> {

    static <K, V> ReactiveExecutionQueue<K, V> withKeyFunction(Function<V, K> keyFunction) {
        var x = new ReactiveExecutionQueueImpl<K, V>();
        x.setKeyFunction(keyFunction);
        return x;
    }
}
