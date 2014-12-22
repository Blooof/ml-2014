package ru.bloof.ml.practice4.bnetwork;

import org.junit.Test;
import ru.bloof.ml.practice4.bnetwork.factor.Factor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.bloof.ml.practice4.bnetwork.Helper.*;

public class BayesNetworkTest {
    @Test
    public void testEliminateSimple() throws Exception {
        BayesNetwork network = createTestNetwork();

        Factor result = network.eliminate(createSet(Event.Storm), null);

        assertEquals(createSet(Event.Storm), result.getScope());
        assertEquals(0.2, result.value(createSet(new Evidence(Event.Storm, true))), EPS);
        assertEquals(0.8, result.value(createSet(new Evidence(Event.Storm, false))), EPS);
    }

    @Test
    public void testEliminateSimple2() throws Exception {
        BayesNetwork network = createTestNetwork();

        Factor result = network.eliminate(createSet(Event.BusTourGroup), null);

        assertEquals(createSet(Event.BusTourGroup), result.getScope());
        assertEquals(0.5, result.value(createSet(new Evidence(Event.BusTourGroup, true))), EPS);
        assertEquals(0.5, result.value(createSet(new Evidence(Event.BusTourGroup, false))), EPS);
    }

    @Test
    public void testEliminateWithoutEvidence() throws Exception {
        BayesNetwork network = createTestNetwork();

        Factor result = network.eliminate(createSet(Event.CampFire), null);

        assertEquals(createSet(Event.CampFire), result.getScope());
        assertEquals(0.45, result.value(createSet(new Evidence(Event.CampFire, true))), EPS);
        assertEquals(0.55, result.value(createSet(new Evidence(Event.CampFire, false))), EPS);
    }

    @Test
    public void testEliminateWithEvidence() throws Exception {
        BayesNetwork network = createTestNetwork();

        Factor result = network.eliminate(createSet(Event.CampFire), createSet(new Evidence(Event.Storm, true)));

        assertEquals(createSet(Event.CampFire), result.getScope());
        assertEquals(0.25, result.value(createSet(new Evidence(Event.CampFire, true))), EPS);
        assertEquals(0.75, result.value(createSet(new Evidence(Event.CampFire, false))), EPS);
    }

    @Test
    public void testEliminateWithTwoEvidences() throws Exception {
        BayesNetwork network = createTestNetwork();

        Factor result = network.eliminate(createSet(Event.CampFire), createSet(new Evidence(Event.Storm, false), new Evidence(Event.BusTourGroup, true)));

        assertEquals(createSet(Event.CampFire), result.getScope());
        assertEquals(0.8, result.value(createSet(new Evidence(Event.CampFire, true))), EPS);
        assertEquals(0.2, result.value(createSet(new Evidence(Event.CampFire, false))), EPS);
    }

    @Test
    public void testEliminateUpWithEvidence() throws Exception {
        BayesNetwork network = createTestNetwork();

        Factor result = network.eliminate(createSet(Event.BusTourGroup), createSet(new Evidence(Event.Storm, false), new Evidence(Event.CampFire, true)));

        assertEquals(createSet(Event.BusTourGroup), result.getScope());
        assertEquals(0.8, result.value(createSet(new Evidence(Event.BusTourGroup, true))), EPS);
        assertEquals(0.2, result.value(createSet(new Evidence(Event.BusTourGroup, false))), EPS);
    }

    private BayesNetwork createTestNetwork() {
        List<Factor> factors = new ArrayList<>();
        Factor factor = new Factor(createSet(Event.Storm));
        addEvidence(factor, 0.2, new Evidence(Event.Storm, true));
        addEvidence(factor, 0.8, new Evidence(Event.Storm, false));
        factors.add(factor);
        factor = new Factor(createSet(Event.BusTourGroup));
        addEvidence(factor, 0.5, new Evidence(Event.BusTourGroup, true));
        addEvidence(factor, 0.5, new Evidence(Event.BusTourGroup, false));
        factors.add(factor);
        factor = new Factor(createSet(Event.Storm, Event.BusTourGroup, Event.CampFire));
        addEvidence(factor, 0.4, new Evidence(Event.Storm, true), new Evidence(Event.BusTourGroup, true), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.6, new Evidence(Event.Storm, true), new Evidence(Event.BusTourGroup, true), new Evidence(Event.CampFire, false));
        addEvidence(factor, 0.1, new Evidence(Event.Storm, true), new Evidence(Event.BusTourGroup, false), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.9, new Evidence(Event.Storm, true), new Evidence(Event.BusTourGroup, false), new Evidence(Event.CampFire, false));
        addEvidence(factor, 0.8, new Evidence(Event.Storm, false), new Evidence(Event.BusTourGroup, true), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.2, new Evidence(Event.Storm, false), new Evidence(Event.BusTourGroup, true), new Evidence(Event.CampFire, false));
        addEvidence(factor, 0.2, new Evidence(Event.Storm, false), new Evidence(Event.BusTourGroup, false), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.8, new Evidence(Event.Storm, false), new Evidence(Event.BusTourGroup, false), new Evidence(Event.CampFire, false));
        factors.add(factor);
        return new BayesNetwork(factors);
    }
}