package czz.study;

import czz.study.run.Runn;
import java.util.concurrent.*;

/**
 * Hello world!
 */
public class App {

    //指挥官的命令。设置为1，指挥官一下达命令。则cutDown,变为0，战士们运行任务
    public static final CountDownLatch cdOrder = new CountDownLatch(1);

    //由于有三个战士，所以初始值为3，每个战士运行任务完成则cutDown一次，当三个都运行完成，变为0。则指挥官停止等待。
    public static final CountDownLatch cdAnswer = new CountDownLatch(1000);

    public static void main(String[] args) {

        //创建一个线程池
        ExecutorService service = new ThreadPoolExecutor(1000,
                1000,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        for (int i = 0; i < 1000; i++) {

            Runn runn = new Runn();
            service.execute(runn);//为线程池加入任务
        }
        try {
            long start = System.currentTimeMillis();
            System.out.println("线程" + Thread.currentThread().getName() + "即将开始发红包");
            cdOrder.countDown(); //发送命令，cdOrder减1，处于等待的战士们停止等待转去运行任务。
            System.out.println("线程" + Thread.currentThread().getName() + "已发送命令，正在等待结果");
            cdAnswer.await(); //命令发送后指挥官处于等待状态。一旦cdAnswer为0时停止等待继续往下运行
            System.out.println("线程" + Thread.currentThread().getName() + "已发完所有红包");
            long end = System.currentTimeMillis();
            long time = end - start;
            System.out.println("总时间为" + time + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            service.shutdown(); //任务结束。停止线程池的全部线程

        }

    }
}
