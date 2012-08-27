import java.util.concurrent.TimeUnit;

public class AppointmentRunner {

    public static void main(String[] args) throws Exception {
        while (true) {
            new AppointmentMaker().run(args[0].toUpperCase());

            System.out.println("Sleeping...");
            TimeUnit.MINUTES.sleep(15);
        }
    }
}