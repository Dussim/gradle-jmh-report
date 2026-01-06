package io.morethan.kotlinbenchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@Fork(value = 1)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class QuickBenchmark {
    @Benchmark
    fun sleep50Milliseconds() {
        Thread.sleep(50)
    }

    @Benchmark
    fun sleep100Milliseconds() {
        Thread.sleep(100)
    }
}
