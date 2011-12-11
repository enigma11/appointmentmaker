import com.google.code.tempusfugit.temporal.Condition;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;

public class AppointmentMaker {

    private static final SimpleDateFormat df = new SimpleDateFormat("dd MMMMMMMMM yyyy");
    private static final String GREEK_EMBASSY_APPOINTMENT_MAKER_APP = "http://www.greekembassy.org.uk/ScheduleanAppointment.aspx";
    private static final WebDriver driver = new FirefoxDriver();

    public static void main(String[] args) throws Exception {
        try {
            AppointmentCategory category = AppointmentCategory.valueOf(args[0].toUpperCase());
            ContactDetails contactDetails = loadContactDetails();

            System.out.println("Looking appointment for " + category.name());
            fillOutContactDetails(contactDetails);

            WebElement nextStep = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_step2NextCommandButton"));
            nextStep.click();


            final int optionValue = category.optionValue();
            WebElement option = waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
                @Override
                public WebElement find() {
                    return driver.findElement(By.xpath("//select/option[@value=" + optionValue + "]"));
                }
            });

            if (option == null) {
                noAppointmentAvailable(category);
            }

            String optionClass = driver.findElement(By.xpath("//select/option[@value=" + optionValue + "]")).getAttribute("class");
            if (!optionClass.isEmpty()) {
                noAppointmentAvailable(category);
            }

            System.out.println("Found appointments for " + category);
            option.click();

            List<WebElement> availableDays = waitForOptionToDisplayAndReturn(new ElementFinder<List<WebElement>>() {
                @Override
                public List<WebElement> find() {
                    return driver.findElements(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_calendar']/tbody/tr/td[@class='LL_Modules_AppointmentWizard_DayStyle Normal']/a"));
                }
            });

            if (availableDays.isEmpty()) {
                noAppointmentAvailable(category);
            }


            String monthYear = driver.findElement(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_calendar']//table[@class='LL_Modules_AppointmentWizard_TitleStyle SubHead']//td[2]")).getText();

            StringBuilder appointment = new StringBuilder();
            WebElement earliestAvailableDay = availableDays.get(0);
            String date = earliestAvailableDay.getText() + " " + monthYear;

            Set<String> existingAppointments = findExistingAppointmentsFor(category);

            if (isAvailableAppointmentAfter(existingAppointments, date, category)) {
                try {
                    System.out.println("Found earlier existing appointment. Not booked new appointment");
                    System.exit(0);
                } finally {
                    driver.close();
                }
            }

            appointment.append(category).append(" appointment on ").append(date);

            System.out.println("Finding available appointments for " + date);
            earliestAvailableDay.click();

            List<WebElement> availableTimes = waitForOptionToDisplayAndReturn(new ElementFinder<List<WebElement>>() {
                @Override
                public List<WebElement> find() {
                    return driver.findElements(By.xpath("//a[starts-with(@id, 'dnn_ctr549_ViewAppointmentWizard_availableTimesRepeater_ctl')]"));
                }
            });


            WebElement earliestTime = availableTimes.get(0);
            earliestTime.click();

            appointment.append(" ").append(earliestTime.getText());
            System.out.println("Found " + appointment.toString());

            WebElement confirmAppointment = waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
                @Override
                public WebElement find() {
                    return driver.findElement(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_step3NextTable']//a[@id='dnn_ctr549_ViewAppointmentWizard_step3NextCommandButton']"));
                }
            });

            System.out.println("Confirming appointment");
            confirmAppointment.click();

            WebElement doneButton = waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
                @Override
                public WebElement find() {
                    return driver.findElement(By.xpath("//a[@id='dnn_ctr549_ViewAppointmentWizard_doneCommandButton']"));
                }
            });

            String bookedAppointment = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_appointmentDateTimeLabel")).getText();
            doneButton.click();
            System.out.println("Booked appointment on " + bookedAppointment + " for " + category);

            File appointmentMarker = new File(fileFormat(category, date));
            appointmentMarker.createNewFile();

            System.exit(0);
        } finally {
            driver.close();
        }
    }

    private static void fillOutContactDetails(ContactDetails contactDetails) {
        System.out.println("Filling form");
        driver.get(GREEK_EMBASSY_APPOINTMENT_MAKER_APP);
        WebElement fn = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_firstNameTextBox"));
        fn.sendKeys(contactDetails.getFirstName());

        WebElement ln = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_lastNameTextBox"));
        ln.sendKeys(contactDetails.getLastName());

        WebElement email = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_emailTextBox"));
        email.sendKeys(contactDetails.getEmail());

        WebElement tel = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_phoneTextBox"));
        tel.sendKeys(contactDetails.getTelephone());
    }

    private static ContactDetails loadContactDetails() throws IOException {
        InputStream contactResource = Thread.currentThread().getContextClassLoader().getResourceAsStream("contact.properties");

        Properties contact = new Properties();
        contact.load(contactResource);
        String firstName = contact.getProperty("name.first");
        String lastName = contact.getProperty("name.last");
        String email = contact.getProperty("email");
        String telephone = contact.getProperty("telephone");

        return new ContactDetails(firstName, lastName, email, telephone);
    }

    private static boolean isAvailableAppointmentAfter(Set<String> existingAppointments, String dateAsString, AppointmentCategory category) throws Exception {
        for (String existingAppointment : existingAppointments) {
            String existingAppointmentDateAsString = existingAppointment.replace(category.name(), "").replace("_", " ");
            Date existingAppointmentAsDate = df.parse(existingAppointmentDateAsString);
            Date newAppointmentAsDate = df.parse(dateAsString);
            if (newAppointmentAsDate.after(existingAppointmentAsDate)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> findExistingAppointmentsFor(AppointmentCategory category) {
        HashSet<String> existingAppointments = new HashSet<String>();
        File dir = new File(".");
        if (!dir.isDirectory()) {
            throw new IllegalStateException("wtf mate?");
        }
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith(category.name())) {
                existingAppointments.add(file.getName());
            }
        }
        return existingAppointments;
    }

    private static String fileFormat(AppointmentCategory category, String date) {
        return category + "_" + date.replaceAll(" ", "_");
    }

    private static <T> T waitForOptionToDisplayAndReturn(final ElementFinder<?> elementFinder) throws Exception {
        final Object[] category = new Object[1];
        waitOrTimeout(new Condition() {
            public boolean isSatisfied() {
                try {
                    category[0] = elementFinder.find();
                    if (category[0] instanceof List) {
                        return ((List) category[0]).size() > 0;
                    }
                    if (category[0] != null) {
                        return true;
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }, timeout(seconds(120)));
        return (T) category[0];
    }

    private static void noAppointmentAvailable(AppointmentCategory category) {
        try {
            System.out.println("No appointment found for " + category);
            System.exit(-1);
        } finally {
            driver.close();
        }
    }

    public static abstract class ElementFinder<T> {
        public abstract T find();
    }

    enum AppointmentCategory {
        PASSPORT(4),
        MILITARY(6),
        CERTIFICATES(5);

        private final int optionValue;

        AppointmentCategory(int optionValue) {
            this.optionValue = optionValue;
        }

        public int optionValue() {
            return optionValue;
        }
    }

    private static class ContactDetails {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String telephone;

        public ContactDetails(String firstName, String lastName, String email, String telephone) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.telephone = telephone;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getTelephone() {
            return telephone;
        }
    }
}
