package com.gdiama.pages;

import com.gdiama.domain.ContactDetails;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ContactForm {

    private WebDriver driver;

    public ContactForm(WebDriver driver) {
        this.driver = driver;
    }

    public void enterContactDetails(ContactDetails contactDetails) {
        WebElement fn = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_firstNameTextBox"));
        fn.sendKeys(contactDetails.getFirstName());

        WebElement ln = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_lastNameTextBox"));
        ln.sendKeys(contactDetails.getLastName());

        WebElement email = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_emailTextBox"));
        email.sendKeys(contactDetails.getEmail());

        WebElement tel = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_phoneTextBox"));
        tel.sendKeys(contactDetails.getTelephone());
    }

    public void proceedToCategorySelection() {
        WebElement nextStep = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_step2NextCommandButton"));
        nextStep.click();
    }
}
