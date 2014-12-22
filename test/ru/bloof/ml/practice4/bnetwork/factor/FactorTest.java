package ru.bloof.ml.practice4.bnetwork.factor;

import org.junit.Test;
import ru.bloof.ml.practice4.bnetwork.Event;
import ru.bloof.ml.practice4.bnetwork.Evidence;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static ru.bloof.ml.practice4.bnetwork.Helper.*;

public class FactorTest {
    @Test
    public void testMultiplyFactors() throws Exception {
        Set<Event> scope1 = createSet(Event.BusTourGroup, Event.Storm);
        Factor factor1 = new Factor(scope1);
        addEvidence(factor1, 0.5, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, true));
        addEvidence(factor1, 0.8, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, false));
        addEvidence(factor1, 0.1, new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, true));
        addEvidence(factor1, 0.0, new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, false));
        Set<Event> scope2 = createSet(Event.Storm, Event.CampFire);
        Factor factor2 = new Factor(scope2);
        addEvidence(factor2, 0.5, new Evidence(Event.Storm, true), new Evidence(Event.CampFire, true));
        addEvidence(factor2, 0.7, new Evidence(Event.Storm, true), new Evidence(Event.CampFire, false));
        addEvidence(factor2, 0.1, new Evidence(Event.Storm, false), new Evidence(Event.CampFire, true));
        addEvidence(factor2, 0.2, new Evidence(Event.Storm, false), new Evidence(Event.CampFire, false));

        Factor productFactor = factor1.multiply(factor2);

        assertEquals(0.25, productFactor.value(createSet(new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, true))), EPS);
        assertEquals(0.35, productFactor.value(createSet(new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, false))), EPS);
        assertEquals(0.08, productFactor.value(createSet(new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, true))), EPS);
        assertEquals(0.16, productFactor.value(createSet(new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, false))), EPS);
        assertEquals(0.05, productFactor.value(createSet(new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, true))), EPS);
        assertEquals(0.07, productFactor.value(createSet(new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, false))), EPS);
        assertEquals(0, productFactor.value(createSet(new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, true))), EPS);
        assertEquals(0, productFactor.value(createSet(new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, false))), EPS);
    }

    @Test
    public void testMarginalize() throws Exception {
        Set<Event> scope = createSet(Event.BusTourGroup, Event.Storm, Event.CampFire);
        Factor factor = new Factor(scope);
        addEvidence(factor, 0.25, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.35, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, false));
        addEvidence(factor, 0.08, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.16, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, false));
        addEvidence(factor, 0.05, new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.07, new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, false));
        addEvidence(factor, 0., new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0., new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, false));

        Factor marginalizedFactor = factor.marginalize(Event.Storm);

        assertEquals(0.33, marginalizedFactor.value(createSet(new Evidence(Event.BusTourGroup, true), new Evidence(Event.CampFire, true))), EPS);
        assertEquals(0.51, marginalizedFactor.value(createSet(new Evidence(Event.BusTourGroup, true), new Evidence(Event.CampFire, false))), EPS);
        assertEquals(0.05, marginalizedFactor.value(createSet(new Evidence(Event.BusTourGroup, false), new Evidence(Event.CampFire, true))), EPS);
        assertEquals(0.07, marginalizedFactor.value(createSet(new Evidence(Event.BusTourGroup, false), new Evidence(Event.CampFire, false))), EPS);
    }

    @Test
    public void testReduce() throws Exception {
        Set<Event> scope = createSet(Event.BusTourGroup, Event.Storm, Event.CampFire);
        Factor factor = new Factor(scope);
        addEvidence(factor, 0.25, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.35, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, false));
        addEvidence(factor, 0.08, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.16, new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, false));
        addEvidence(factor, 0.05, new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0.07, new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, true), new Evidence(Event.CampFire, false));
        addEvidence(factor, 0., new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, true));
        addEvidence(factor, 0., new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, false), new Evidence(Event.CampFire, false));

        Factor reducedFactor = factor.reduce(new Evidence(Event.CampFire, true));

        assertEquals(0.25, reducedFactor.value(createSet(new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, true))), EPS);
        assertEquals(0.08, reducedFactor.value(createSet(new Evidence(Event.BusTourGroup, true), new Evidence(Event.Storm, false))), EPS);
        assertEquals(0.05, reducedFactor.value(createSet(new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, true))), EPS);
        assertEquals(0., reducedFactor.value(createSet(new Evidence(Event.BusTourGroup, false), new Evidence(Event.Storm, false))), EPS);
    }
}