package com.gdiama.pages;

import com.gdiama.domain.AppointmentCategory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class AvailabilityReport {

    private final WebDriver driver;
    private final Map<AppointmentCategory, Integer> availabilityPerCategory = new HashMap<AppointmentCategory, Integer>();

    public AvailabilityReport(WebDriver webDriver) {
        this.driver = webDriver;
    }

    public AvailabilityReport fetch() {
        driver.get("http://www.greekembassy.org.uk/ScheduleanAppointment/ctl/ViewAvailability/mid/549.aspx");

        int availabilityDataGridNoOfRows = driver.findElements(By.xpath("//table[@id='dnn_ctr549_ViewAvailability_dataGrid']//tr[starts-with(@class, 'Normal')]")).size();

        AppointmentCategory[] categories = AppointmentCategory.values();
        for (AppointmentCategory category : categories) {
            for (int index = 1; index < availabilityDataGridNoOfRows + 1; index++) {
                if (driver.findElements(By.xpath("//table[@id='dnn_ctr549_ViewAvailability_dataGrid']//tr[starts-with(@class, 'Normal')][" + index + "]/td")).get(1).getText().contains(category.description())) {
                    String noOfAppointmentsForCategoryText = driver.findElements(By.xpath("//table[@id='dnn_ctr549_ViewAvailability_dataGrid']//tr[starts-with(@class, 'Normal')][" + index + "]/td")).get(2).getText();
                    Integer noOfAppointmentsForCategory = Integer.valueOf(noOfAppointmentsForCategoryText);
                    availabilityPerCategory.put(category, noOfAppointmentsForCategory);
                }
            }
        }

        return this;
    }

    public boolean hasAvailableSlotsFor(AppointmentCategory category) {
        return availabilityPerCategory.get(category) > 0;
    }
}
