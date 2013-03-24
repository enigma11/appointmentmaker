package com.gdiama.pages;

import com.gdiama.Audit;
import com.gdiama.util.ElementFinder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.gdiama.util.WaitFor.waitForOptionToDisplayAndReturn;

public class DoneForm {

    private WebDriver driver;
    private Audit audit;

    public DoneForm(WebDriver driver, Audit audit) {
        this.driver = driver;
        this.audit = audit;
    }

    public void done() throws Exception {
        WebElement doneButton = waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
            @Override
            public WebElement find() {
                return driver.findElement(By.xpath("//a[@id='dnn_ctr549_ViewAppointmentWizard_doneCommandButton']"));
            }
        });

        String bookedAppointment = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_appointmentDateTimeLabel")).getText();
        audit.append("Booked appointment on " + bookedAppointment);
        doneButton.click();
    }
}
