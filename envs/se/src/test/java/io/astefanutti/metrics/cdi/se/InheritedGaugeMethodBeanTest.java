/**
 * Copyright © 2013 Antonin Stefanutti (antonin.stefanutti@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.astefanutti.metrics.cdi.se;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import io.astefanutti.metrics.cdi.MetricsExtension;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.Arrays;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class InheritedGaugeMethodBeanTest {

    private final static String PARENT_GAUGE_NAME = MetricRegistry.name(GaugeMethodBean.class, "gaugeMethod");
    private final static String CHILD_GAUGE_NAME = MetricRegistry.name(InheritedGaugeMethodBean.class, "childGaugeMethod");

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class)
            // Test beans
            .addClass(GaugeMethodBean.class)
            .addClass(InheritedGaugeMethodBean.class)
            // Metrics CDI extension
            .addPackage(MetricsExtension.class.getPackage())
            // Bean archive deployment descriptor
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private MetricRegistry registry;

    @Inject
    private InheritedGaugeMethodBean bean;

    @Before
    public void instantiateApplicationScopedBean() {
        // Let's trigger the instantiation of the application scoped bean explicitly
        // as only a proxy gets injected otherwise
        bean.getChildGauge();
    }

    @Test
    @InSequence(1)
    public void gaugesCalledWithDefaultValues() {
        assertThat("Gauges are not registered correctly", registry.getGauges(), allOf(hasKey(PARENT_GAUGE_NAME), hasKey(CHILD_GAUGE_NAME)));
        @SuppressWarnings("unchecked")
        Gauge<Long> parentGauge = registry.getGauges().get(PARENT_GAUGE_NAME);
        @SuppressWarnings("unchecked")
        Gauge<Long> childGauge = registry.getGauges().get(CHILD_GAUGE_NAME);

        // Make sure that the gauge has the expected value
        assertThat("Gauge values are incorrect", Arrays.asList(parentGauge.getValue(), childGauge.getValue()), contains(0L, 0L));
    }

    @Test
    @InSequence(2)
    public void callGaugesAfterSetterCalls() {
        assertThat("Gauges are not registered correctly", registry.getGauges(), allOf(hasKey(PARENT_GAUGE_NAME), hasKey(CHILD_GAUGE_NAME)));
        @SuppressWarnings("unchecked")
        Gauge<Long> parentGauge = registry.getGauges().get(PARENT_GAUGE_NAME);
        @SuppressWarnings("unchecked")
        Gauge<Long> childGauge = registry.getGauges().get(CHILD_GAUGE_NAME);

        // Call the setter methods and assert the gauges are up-to-date
        long parentValue = Math.round(Math.random() * Long.MAX_VALUE);
        bean.setGauge(parentValue);
        long childValue = Math.round(Math.random() * Long.MAX_VALUE);
        bean.setChildGauge(childValue);
        assertThat("Gauge values are incorrect", Arrays.asList(parentGauge.getValue(), childGauge.getValue()), contains(parentValue, childValue));
    }
}