package dev.epicpuppy;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AlgorithmBenchmark {
    static Logger logger = Logger.getLogger("log");

    @State(Scope.Thread)
    public static class ThreadState {
        PlayerDamage player;
        long t;

        @Setup(Level.Iteration)
        public void setUp() {
            player = new PlayerDamage();
            t = 0;
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testDamageAlgorithm(ThreadState state) {
        state.player.tick(state.t++);
        state.player.dealDamage(Math.random() * 50_000 + 50_000, state.t);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AlgorithmBenchmark.class.getSimpleName())
                .forks(1)
                .addProfiler(AsyncProfiler.class, "lock=1ms simple=true output=flamegraph")
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(5))
                .threads(4)
                .build();

        new Runner(opt).run();
    }

}
