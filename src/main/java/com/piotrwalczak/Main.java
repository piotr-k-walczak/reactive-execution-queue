package com.piotrwalczak;

public class Main {
    public static void main(String[] args) {

        /*
        Flux<ABC> settlementFlux;
        OrderedExecution.partitionBy(Settlement::getId())
            .processFlux(settlementFlux)
            .flatMap(v -> WebClient.get()
                    .body(v.getId())
                    .retrieve()
                    .bodyToMono()
            )
            .toFlux()
         */


        System.out.println("Hello world!");
    }
}