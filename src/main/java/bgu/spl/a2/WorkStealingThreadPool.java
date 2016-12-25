package bgu.spl.a2;

import java.util.*;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ThreadFactory;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {

    private ArrayList<ConcurrentLinkedDeque<Task<?>>> processorsQueues;
    private ArrayList<Processor> processors;
    private ArrayList<VersionMonitor> queuesVersions;
    private ArrayList<Thread> workingThreads;

    private Random randomGenerator = new Random();
    /**
     * creates a {@link WorkStealingThreadPool} which has nthreads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     *
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     * thread pool
     */
    public WorkStealingThreadPool(int nthreads) {
        processorsQueues = new ArrayList<>();
        processors = new ArrayList<>();
        queuesVersions = new ArrayList<>();
        workingThreads = new ArrayList<>();

        for(int i=0; i<nthreads; i++){
            processorsQueues.add(new ConcurrentLinkedDeque<>());
            queuesVersions.add(new VersionMonitor());
            processors.add(new Processor(i,this));
            workingThreads.add(new Thread(processors.get(i)));
        }
    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     *
     * @param task the task to execute
     */
    public void submit(Task<?> task) {

        int queue =processors.size()==1 ? 0 : randomGenerator.nextInt(processors.size()-1);
        addTaskToSpecificQueue(task,queue);
    }

    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     *
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is
     * interrupted
     * @throws UnsupportedOperationException if the thread that attempts to
     * shutdown the queue is itself a processor of this queue
     */
    public void shutdown() throws InterruptedException {
        for(Thread t: workingThreads){
            if(Thread.currentThread()==t){
                throw new UnsupportedOperationException("A processor cannot shutdown the WorkingStealingThreadPool");
            }
        }

        for(Thread t: workingThreads){
            t.interrupt();
        }

        for (int i = 0; i < workingThreads.size() && !Thread.currentThread().isInterrupted(); i++) {
            while (workingThreads.get(i).isAlive() && !Thread.currentThread().isInterrupted()){
                Thread.sleep(10);
            }
        }
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        for(Thread t: workingThreads){
            t.start();
        }
    }

    public Task<?> getTask(int processorId) throws InterruptedException {
        ConcurrentLinkedDeque<Task<?>> queue = processorsQueues.get(processorId);
        Task<?> task = queue.pollFirst();
        while (task ==null && !Thread.currentThread().isInterrupted()){
            Logger.Log(processorId +" trying to steal");
            tryStealingTasks(processorId);
            task = queue.pollFirst();
        }
        throwInterruptedIfNeeded();
        return task;
    }


    private void tryStealingTasks(int processorId) throws InterruptedException {
        ConcurrentLinkedDeque<Task<?>> destProcessor = processorsQueues.get(processorId);
        Boolean didSteal = false;
        int i=processorId+1;
        while (!didSteal && destProcessor.size()==0 && !Thread.currentThread().isInterrupted()){
            i = i % processors.size();
            if(i!=processorId) {
                ConcurrentLinkedDeque<Task<?>> sourceProcessor = processorsQueues.get(i);
                VersionMonitor queueMonitor = queuesVersions.get(i);

                lockObject(queueMonitor);

                Logger.Log(processorId+ " STARTED TO Steal from "+i );
                int tasks = sourceProcessor.size();
                if (tasks > 1) {
                    Logger.Log(processorId+ " is Stealing from "+i );
                    moveKTasks(sourceProcessor, destProcessor, tasks / 2);
                    didSteal = true;
                }
                queueMonitor.inc();
                Logger.Log(processorId+ " FINISHED TO Steal from "+i );
            }
            i++;
        }
        throwInterruptedIfNeeded();
    }

    private void throwInterruptedIfNeeded() throws InterruptedException {
        if(Thread.currentThread().isInterrupted()){
            throw new InterruptedException();
        }
    }

    private void lockObject(VersionMonitor queueMonitor) throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            queueMonitor.awaitIfOddVersion();
            int version = queueMonitor.getVersion();
            if (version % 2 == 0) {
               Boolean wasIncreased = queueMonitor.incIfEquals(version);
                if (wasIncreased) {
                    break;
                }
            }
        }
        throwInterruptedIfNeeded();
    }

    private void moveKTasks(Deque<Task<?>> source, Deque<Task<?>> dest, int numOfTasks) {
        for(int x=0; x<numOfTasks && !source.isEmpty();x++){
            Task<?> task = source.pollLast();
            if(task != null){
                dest.add(task);
            }
        }
    }

    public void addTaskToSpecificQueue(Task<?> task, int id) {
        processorsQueues.get(id).addLast(task);
    }
}
