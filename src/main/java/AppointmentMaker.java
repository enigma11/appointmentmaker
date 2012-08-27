import com.google.code.tempusfugit.temporal.Condition;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;

public class AppointmentMaker {

    private static final SimpleDateFormat df = new SimpleDateFormat("dd MMMMMMMMM yyyy");
    private static final String GREEK_EMBASSY_SCHEDULE_APPOINTMENT_PAGE = "http://www.greekembassy.org.uk/ScheduleanAppointment.aspx";
    private static HtmlUnitDriver driver;
    private static Audit audit;
    private final MongoTemplate mongoTemplate;


    public AppointmentMaker() throws Exception {
        turnOffHtmlUnitLoggerOff();
        String mongolab_uri = System.getenv("MONGOLAB_URI");
        System.out.println("mongolab_uri = " + mongolab_uri);
        mongoTemplate = new MongoTemplate(new Mongo(new MongoURI(mongolab_uri)), "heroku_app7072950");
    }

    public void run(String categoryAsString) throws Exception {
        try {
            audit = new Audit(mongoTemplate);
            driver = new HtmlUnitDriver(true);

            AppointmentCategory category = AppointmentCategory.valueOf(categoryAsString);

            if (!appointmentSlotAvailableFor(category)) {
                String message = "No available slot found for " + category.name();
                System.out.println("message = " + message);
                audit.append(message);
            } else {
                ContactDetails contactDetails = loadContactDetails();

                goTo(GREEK_EMBASSY_SCHEDULE_APPOINTMENT_PAGE);

                audit.append("Looking appointment for " + category.name());
                fillOutContactDetailsAndProceedToNextPage(contactDetails);
                selectAppointmentCategoryIfSlotAvailable(category);

                List<WebElement> availableDays = availableDays(category);
                WebElement earliestAvailableDay = availableDays.get(0);

                String date = earliestAvailableDate(earliestAvailableDay);
                if (isAlreadyExistingAppointmentEarlierThanNewlyFoundSlot(category, date, mongoTemplate)) {
                    return;
                }

                audit.append("Finding available appointments for " + date);
                earliestAvailableDay.click();

                List<WebElement> availableTimes = waitForOptionToDisplayAndReturn(new ElementFinder<List<WebElement>>() {
                    @Override
                    public List<WebElement> find() {
                        return driver.findElements(By.xpath("//a[starts-with(@id, 'dnn_ctr549_ViewAppointmentWizard_availableTimesRepeater_ctl')]"));
                    }
                });


                WebElement earliestTime = availableTimes.get(0);
                earliestTime.click();

                String appointment = category + " appointment on " + date + earliestTime.getText();
                audit.append("Found " + appointment);

                WebElement confirmAppointment = waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
                    @Override
                    public WebElement find() {
                        return driver.findElement(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_step3NextTable']//a[@id='dnn_ctr549_ViewAppointmentWizard_step3NextCommandButton']"));
                    }
                });

                audit.append("Confirming appointment");
                confirmAppointment.click();

                WebElement doneButton = waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
                    @Override
                    public WebElement find() {
                        return driver.findElement(By.xpath("//a[@id='dnn_ctr549_ViewAppointmentWizard_doneCommandButton']"));
                    }
                });

                String bookedAppointment = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_appointmentDateTimeLabel")).getText();
                doneButton.click();

                mongoTemplate.save(new Appointment(category, df.parse(date)));
                audit.append("Booked appointment on " + bookedAppointment + " for " + category);
            }
        } finally {
            String auditMessage = audit.getAuditMessage();
            System.out.println("auditMessage = " + auditMessage);
            audit.save();
            driver.close();
        }
    }

    private static String earliestAvailableDate(WebElement earliestAvailableDay) {
        String monthYear = driver.findElement(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_calendar']//table[@class='LL_Modules_AppointmentWizard_TitleStyle SubHead']//td[2]")).getText();
        return earliestAvailableDay.getText() + " " + monthYear;
    }

    private static List<WebElement> availableDays(AppointmentCategory category) throws Exception {
        List<WebElement> availableDays = waitForOptionToDisplayAndReturn(new ElementFinder<List<WebElement>>() {
            @Override
            public List<WebElement> find() {
                return driver.findElements(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_calendar']/tbody/tr/td[@class='LL_Modules_AppointmentWizard_DayStyle Normal']/a"));
            }
        });

        if (availableDays.isEmpty()) {
            noAppointmentAvailable(category);
        }

        return availableDays;
    }

    private static void selectAppointmentCategoryIfSlotAvailable(AppointmentCategory category) throws Exception {
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

        audit.append("Found appointments for " + category);
        option.click();
    }

    private static void fillOutContactDetailsAndProceedToNextPage(ContactDetails contactDetails) {
        fillOutContactDetails(contactDetails);
        WebElement nextStep = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_step2NextCommandButton"));
        nextStep.click();
    }

    private static void goTo(String greekEmbassyAppointmentMakerApp) {
        driver.get(greekEmbassyAppointmentMakerApp);
    }

    private static boolean appointmentSlotAvailableFor(AppointmentCategory category) {
        driver.get("http://www.greekembassy.org.uk/ScheduleanAppointment/ctl/ViewAvailability/mid/549.aspx");
        int availabilityDataGridNoOfRows = driver.findElements(By.xpath("//table[@id='dnn_ctr549_ViewAvailability_dataGrid']//tr[starts-with(@class, 'Normal')]")).size();

        for (int index = 1; index < availabilityDataGridNoOfRows + 1; index++) {
            if (driver.findElements(By.xpath("//table[@id='dnn_ctr549_ViewAvailability_dataGrid']//tr[starts-with(@class, 'Normal')][" + index + "]/td")).get(1).getText().contains(category.description())) {
                String noOfAppointmentsForCategoryText = driver.findElements(By.xpath("//table[@id='dnn_ctr549_ViewAvailability_dataGrid']//tr[starts-with(@class, 'Normal')][" + index + "]/td")).get(2).getText();
                Integer noOfAppointmentsForCategory = Integer.valueOf(noOfAppointmentsForCategoryText);
                return noOfAppointmentsForCategory > 0;
            }
        }

        return false;
    }

    private static void fillOutContactDetails(ContactDetails contactDetails) {
        audit.append("Filling form");
        WebElement fn = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_firstNameTextBox"));
        fn.sendKeys(contactDetails.getFirstName());

        WebElement ln = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_lastNameTextBox"));
        ln.sendKeys(contactDetails.getLastName());

        WebElement email = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_emailTextBox"));
        email.sendKeys(contactDetails.getEmail());

        WebElement tel = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_phoneTextBox"));
        tel.sendKeys(contactDetails.getTelephone());
    }

    private ContactDetails loadContactDetails() throws IOException {
        return mongoTemplate.findAll(ContactDetails.class).get(0);
//
//        InputStream contactResource = Thread.currentThread().getContextClassLoader().getResourceAsStream("contact.properties");
//
//        Properties contact = new Properties();
//        contact.load(contactResource);
//        String firstName = contact.getProperty("name.first");
//        String lastName = contact.getProperty("name.last");
//        String email = contact.getProperty("email");
//        String telephone = contact.getProperty("telephone");
//
//        return new ContactDetails(firstName, lastName, email, telephone);
    }

    private static boolean isAlreadyExistingAppointmentEarlierThanNewlyFoundSlot(AppointmentCategory category, String newDateAsString, MongoTemplate mongoTemplate) throws Exception {
        Query query = Query.query(Criteria.where("appointmentCategory").is(category.name()));
        List<Appointment> appointments = mongoTemplate.find(query, Appointment.class);

        if (!appointments.isEmpty()) {
            Date newAppointmentAsDate = df.parse(newDateAsString);
            for (Appointment appointment : appointments) {
                if (appointment.isBeforeOrOn(newAppointmentAsDate)) {
                    audit.append("Found earlier existing appointment. Not booked new appointment");
                    return true;
                }
            }
        }
        return false;
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
            audit.append("No appointment found for " + category);
            System.out.println("audit.getAuditMessage() = " + audit.getAuditMessage());
            System.exit(-1);
        } finally {
            driver.close();
        }
    }

    public static abstract class ElementFinder<T> {
        public abstract T find();
    }

    public enum AppointmentCategory {
        PASSPORT(4, "Greek Passports"),
        MILITARY(6, "Military Affairs / Permanent Residence Certificates"),
        CERTIFICATES(5, "Certificates (Births - Deaths Marriages)"),
        PERMANENT_RESIDENCE(2, "Permanent Residence Certificates (Not Military)");

        private final int optionValue;
        private final String description;

        AppointmentCategory(int optionValue, String description) {
            this.optionValue = optionValue;
            this.description = description;
        }

        public int optionValue() {
            return optionValue;
        }

        public String description() {
            return description;
        }
    }

    private void turnOffHtmlUnitLoggerOff() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }
}
