import java.util.concurrent.*;




//import static net.mindview.util.Print.*;

class Car {
    private boolean waxOn = false;
    private boolean waxing = false;
    public synchronized void waxed() {

        waxOn = true; // Ready to buff
        notifyAll();

    }

    public synchronized void buffed() {
        waxOn = false; // Ready for another coat of wax
        waxing = false;
        notifyAll();
    }

    public synchronized void waitForOtherWaxing()
            throws InterruptedException{
        while(waxing)
            wait();
        waxing = true;

    }
    public synchronized void waitForWaxing()
            throws InterruptedException {
        while (waxOn == false)
            wait();
    }

    public synchronized void waitForBuffing()
            throws InterruptedException {
        while (waxOn == true)
            wait();
    }
}
class WaxOn implements Runnable {
    private Car car;
    private int id;
    public WaxOn(Car c, int id_i) {
        car = c;
        id = id_i;
    }

    public  void run() {
        synchronized(car)
        {
            try {
                while (!Thread.interrupted()) {
                    car.waitForOtherWaxing();
                    System.out.println("WaxOn"+id+": Wax On! \n");
                    TimeUnit.MILLISECONDS.sleep(200);
                    car.waxed();
                    car.waitForBuffing();
                }
            } catch (InterruptedException e) {
                System.out.println("Exiting via interrupt");
            }
            System.out.println("Ending Wax"+id+" On task");
        }
    }
}

class WaxOff implements Runnable {
    private Car car;

    public WaxOff(Car c) {
        car = c;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                car.waitForWaxing();
                System.out.println("Wax Off! \n");
                TimeUnit.MILLISECONDS.sleep(200);
                car.buffed();
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting via interrupt");
        }
        System.out.println("Ending Wax Off task");
    }
}

public class WaxOMatic {
    public static void main(String[] args) throws Exception {
        Car car = new Car();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new WaxOff(car));
        exec.execute(new WaxOn(car,1));
        exec.execute(new WaxOn(car,2));
        TimeUnit.SECONDS.sleep(5); // Run for a while...
        exec.shutdownNow(); // Interrupt all tasks
    }
}
