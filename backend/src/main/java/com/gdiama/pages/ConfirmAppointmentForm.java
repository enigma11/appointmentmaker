package com.gdiama.pages;

import com.gdiama.Audit;
import com.gdiama.util.ElementFinder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.gdiama.util.WaitFor.waitForOptionToDisplayAndReturn;

public class ConfirmAppointmentForm {

    private Audit audit;
    private WebDriver driver;

    public ConfirmAppointmentForm(WebDriver driver, Audit audit) {
        this.audit = audit;
        this.driver = driver;
    }

    public void confirm() throws Exception {
        WebElement confirmAppointment = waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
            @Override
            public WebElement find() {
                return driver.findElement(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_step3NextTable']//a[@id='dnn_ctr549_ViewAppointmentWizard_step3NextCommandButton']"));
            }
        });

        audit.append("Confirming appointment");
        System.out.println("Confirming appointment");
        confirmAppointment.click();
    }
}
