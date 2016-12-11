package bgu.spl.a2.test;

/**
 * Created by Lior Greenspoon on 11/12/2016.
 */

import bgu.spl.a2.VersionMonitor;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class VersionMonitorTest {

    private VersionMonitor versionMonitor;


    @Before
    public void setUp() throws Exception{
        this.versionMonitor =new VersionMonitor();
    }

    @Test
    public void initialVersionIsZero(){
    assertEquals(versionMonitor.getVersion(),0);
    }

    @Test
    public void isIncreaseBiggerByOne(){
        int initialVersion=versionMonitor.getVersion();
        versionMonitor.inc();
        assertEquals(initialVersion+1,versionMonitor.getVersion());

    }

    @Test
    public void awaitUntilSpecificVersion() throws Exception{
        int initialVersion=versionMonitor.getVersion();
        int waitingVersions = 4;
        Boolean [] isCalled = {false};
        Thread waitingThread = new Thread(()->{
            try {
                versionMonitor.await(initialVersion+waitingVersions);
                isCalled[0] =true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        waitingThread.start();
        for(int i=0;i<waitingVersions; i++){
            versionMonitor.inc();
        }
        waitingThread.join(5000);
        assertEquals(isCalled[0],true);
    }

    @Test
    public void awaitUntilSpecificVersionAndFail() throws Exception{
        int initialVersion=versionMonitor.getVersion();
        int waitingVersions = 4;
        Boolean [] isCalled = {false};
        Thread waitingThread = new Thread(()->{
            try {
                versionMonitor.await(initialVersion+waitingVersions);
                isCalled[0] =true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        waitingThread.start();
        for(int i=0;i<waitingVersions-1; i++){
            versionMonitor.inc();
        }
        waitingThread.join(5000);
        assertEquals(isCalled[0],false);
    }
}
