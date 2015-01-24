package com.acme;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.springframework.xd.dirt.test.process.SingleNodeProcessingChainSupport.*;

import org.springframework.xd.dirt.server.SingleNodeApplication;
import org.springframework.xd.dirt.test.SingleNodeIntegrationTestSupport;
import org.springframework.xd.dirt.test.SingletonModuleRegistry;
import org.springframework.xd.dirt.test.process.SingleNodeProcessingChain;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.test.RandomConfigurationSupport;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpollack on 12/19/14.
 */
public class MovingAverageIntegrationTests {

    private static SingleNodeApplication application;

    private static int RECEIVE_TIMEOUT = 5000;

    private static String moduleName = "rxjava-moving-average";

    private SingleNodeProcessingChain chain;

    /**
     * Start the single node container, binding random unused ports, etc. to not conflict with any other instances
     * running on this host. Configure the ModuleRegistry to include the project module.
     */
    @BeforeClass
    public static void setUp() {

        RandomConfigurationSupport randomConfigSupport = new RandomConfigurationSupport();
        application = new SingleNodeApplication().run();
        SingleNodeIntegrationTestSupport singleNodeIntegrationTestSupport = new SingleNodeIntegrationTestSupport
                (application);
        singleNodeIntegrationTestSupport.addModuleRegistry(new SingletonModuleRegistry(ModuleType.processor,
                moduleName));

    }

    @After
    public void tearDown() {
        chain.destroy();
    }

    @AfterClass
    public static void after() {
        application.close();
    }

    @Test
    public void testTupleType() {

        String streamName = "testMovingAverage";

        chain = chain(application, streamName, "rxjava-moving-average");

        List<Tuple> inputData = new ArrayList<Tuple>();
        for (int i = 0; i < 10; i++) {
            inputData.add(TupleBuilder.tuple().of("id", i, "measurement", new Double(i+10)));
        }

        for (Tuple tuple: inputData) {
            chain.sendPayload(tuple);
        }

        assertResults(chain);
    }

    @Test
    public void testJson() throws IOException {

        String streamName = "testMovingAverageJson";

        chain = chain(application, streamName, "rxjava-moving-average --inputType=application/x-xd-tuple");
        List<String> jsonData = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            String measurement = Integer.toString(i+10);
            jsonData.add("{\"id\":\"" + i + "\" , \"measurement\" : \"" + measurement + "\"}");
        }
        for (String json : jsonData) {
            chain.sendPayload(json);
        }
        assertResults(chain);
    }

    private void assertResults(SingleNodeProcessingChain chain) {
        List<Tuple> outputData = new ArrayList<Tuple>();
        for (int i = 0; i < 3; i++) {
            Tuple tuple = (Tuple)chain.receivePayload(RECEIVE_TIMEOUT);
            outputData.add(tuple);
            System.out.println("output Tuple = [ + " + tuple + "]");
        }
        assertEquals(12D, outputData.get(0).getDouble("average"), 0.01);
        assertEquals(17D, outputData.get(1).getDouble("average"), 0.01);
        assertNull(outputData.get(2));
    }
}
