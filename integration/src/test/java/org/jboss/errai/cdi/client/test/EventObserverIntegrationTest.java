package org.jboss.errai.cdi.client.test;

import java.util.List;
import java.util.Map;

import org.jboss.errai.cdi.client.CDITestObserverModule;

import com.google.gwt.user.client.Timer;

/**
 * Tests CDI event observers.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class EventObserverIntegrationTest extends AbstractEventIntegrationTest {

    @Override
    public String getModuleName() {
        return "org.jboss.errai.cdi.CDITestObserverModule";
    }

    public void testBusReadyEventObserver() {
        Timer timer = new Timer() {
            public void run() {
                assertEquals("Wrong number of BusReadyEvents received:", 1, 
                        CDITestObserverModule.getInstance().getBusReadyEventsReceived());
                finishTest();
            }
        };
        timer.schedule(10000);
        delayTestFinish(15000);
    }
   
    public void testEventObservers() {
        assertNotNull(CDITestObserverModule.getInstance().getStartEvent());
        CDITestObserverModule.getInstance().start();

        Timer timer = new Timer() {
            public void run() {
                Map<String, List<String>> actualEvents = CDITestObserverModule.getInstance().getReceivedEvents();
                
                // assert that the client received all events
                EventObserverIntegrationTest.this.verifyEvents(actualEvents);
                finishTest();
            }
        };
        timer.schedule(20000);
        delayTestFinish(25000);
    }
}
