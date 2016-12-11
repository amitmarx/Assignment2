package bgu.spl.a2.test;

import bgu.spl.a2.Deferred;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Lior Greenspoon on 11/12/2016.
 */
public class DeferredTest<T> {

    private Deferred<Integer> deferredObj;
    private int randomNum;

    @Before
    public void setUp() throws Exception{
        this.deferredObj =new Deferred<Integer>();
        randomNum = new Random().nextInt();
    }

    @Test
    public void getValueIsNullOnInit(){
        assertNull(deferredObj.get());
    }

    @Test
    public void isResolvedIsFalseOnInit(){
        assertFalse(deferredObj.isResolved());
    }

    @Test
    public void isResolvedIsTrueOnResolve(){
        deferredObj.resolve(randomNum);
        assertTrue(deferredObj.isResolved());
    }

    @Test
    public void resolveAndGetValue(){
        deferredObj.resolve(randomNum);
        assertEquals(new Long(deferredObj.get()),new Long(randomNum));
    }

    @Test
    public void callbackIsCalledOnResolve() {
        Boolean [] isCalled = {false};
        deferredObj.whenResolved(()-> isCalled[0]=true);
        deferredObj.resolve(randomNum);
        assertTrue(isCalled[0]);
    }
}
