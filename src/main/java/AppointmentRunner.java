import java.util.concurrent.TimeUnit;

public class AppointmentRunner {

    public static void main(String[] args) throws Exception {
        while (true) {
            try {
                System.out.println("Running...");
                new AppointmentMaker().run(args[0].toUpperCase());
                System.out.println("Sleeping...");
            } catch (Exception e) {
                e.printStackTrace();
            }
            TimeUnit.MINUTES.sleep(15);
        }
    }
}