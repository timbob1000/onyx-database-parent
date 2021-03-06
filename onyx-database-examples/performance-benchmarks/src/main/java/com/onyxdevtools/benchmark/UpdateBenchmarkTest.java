package com.onyxdevtools.benchmark;

import com.onyxdevtools.benchmark.base.BenchmarkTest;
import com.onyxdevtools.entities.Player;
import com.onyxdevtools.entities.Stats;
import com.onyxdevtools.provider.manager.ProviderPersistenceManager;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tosborn1 on 8/26/16.
 *
 * This test indicates how quickly records can be updated
 */
@SuppressWarnings("unused")
public class UpdateBenchmarkTest extends BenchmarkTest {

    private static AtomicInteger playerIdCounter = new AtomicInteger(0);
    private static AtomicInteger statIdCounter = new AtomicInteger(0);

    @SuppressWarnings("FieldCanBeLocal")
    private int NUMBER_OF_UPDATES = 20000;
    @SuppressWarnings("FieldCanBeLocal")
    private int NUMBER_OF_WARM_UP_INSERTIONS = 20000;

    /**
     * Default Constructor
     *
     * @param providerPersistenceManager The underlying persistence manager
     */
    @SuppressWarnings("unused")
    public UpdateBenchmarkTest(ProviderPersistenceManager providerPersistenceManager) {
        super(providerPersistenceManager);
    }

    /**
     * Do a little warm up.  In this case, we insert a bunch of test data that we are then going to use to update.
     */
    @Override
    public void before() {
        // Execute a bunch of insertions
        // Note, the Warm up insertions are the same as number of updates
        super.before();
        // Reset the IDs so that we can reuse the existing records
        playerIdCounter.set(0);
        statIdCounter.set(0);
    }

    /**
     * Number of executions that count towards the test
     * @return Number of iterations we run through the database
     */
    @Override
    public int getNumberOfExecutions() {
        return NUMBER_OF_UPDATES;
    }

    /**
     * Number of iterations we do as a warm up.  It is an invalid assumption to think a database will start with 0 records.
     * Therefore, we let it cycle through a warm up and seed the database and get down to its post cache or initial
     * index construction period.
     *
     * @return The number of iterations to warm up on.
     */
    @Override
    public int getNumberOfWarmupExecutions() {
        return NUMBER_OF_WARM_UP_INSERTIONS;
    }

    /**
     * Get the unit of work to run during the test.  In this case, we are inserting 3 entities.  One of which is a related entity.
     *
     * @return A runnable thread
     */
    public Runnable getTestingUnitRunnable() {
        return () -> {

            // I had to generate the id myself for JPA databases.  Apparently they don't like if you try to build an entire
            // graph and save it.  That is a limitation that Onyx does not need.
            Player player = new Player(playerIdCounter.addAndGet(1));
            player.setFirstName(generateRandomString());
            player.setLastName(generateRandomString());
            player.setActive(true);
            player.setPosition(generateRandomString());

            Stats stats = new Stats(statIdCounter.addAndGet(1));
            stats.setFantasyPoints(generateRandomInt());
            stats.setPassAttempts(generateRandomInt());
            stats.setPassingYards(generateRandomInt());
            stats.setReceptions(generateRandomInt());
            stats.setRushingTouchdowns(generateRandomInt());
            stats.setRushingAttempts(generateRandomInt());

            player.setStats(Collections.singletonList(stats));

            providerPersistenceManager.update(player);

            completionLatch.countDown();
        };
    }
}
