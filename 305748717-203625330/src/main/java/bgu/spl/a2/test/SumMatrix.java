package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

import java.util.ArrayList;
import java.util.List;

public class SumMatrix extends Task<int[]> {
    public class SumRow extends Task<Integer> {
        private int[][] array;
        private int r;

        public SumRow(int[][] array, int r) {
            this.array = array;
            this.r = r;
        }

        protected void start() {
            int sum = 0;
            for (int j = 0; j < array[0].length; j++)
                sum += array[r][j];
            complete(sum);
        }
    }

    private int[][] array;

    public SumMatrix(int[][] array) {
        this.array = array;
    }

    protected void start() {
        int sum = 0;
        List<Task<Integer>> tasks = new ArrayList<>();
        int rows = array.length;
        for (int i = 0; i < rows; i++) {
            SumRow newTask = new SumRow(array, i);
            spawn(newTask);
            tasks.add(newTask);
        }
        whenResolved(tasks, () -> {
                    int[] res = new int[rows];
                    for (int j = 0; j < rows; j++) {
                        res[j] = tasks.get(j).getResult().get();
                    }
                    complete(res);
                }
        );
    }

    public static void main(String[] args) throws Exception{
        WorkStealingThreadPool pool = new WorkStealingThreadPool(5);
        int[][] array = {{1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5}};

        SumMatrix myTask = new SumMatrix(array);
        pool.start();
        pool.submit(myTask);
        myTask.getResult().whenResolved(
                () -> {
                    for (int x : myTask.getResult().get()) {
                        System.out.println(x);
                    }
                }
        );
    }
}