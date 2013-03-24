package com.gdiama.pages;

import com.gdiama.Audit;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.util.ElementFinder;
import com.gdiama.util.WaitFor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ChooseAppointmentCategoryForm {

    private Audit audit;
    private WebDriver driver;

    public ChooseAppointmentCategoryForm(WebDriver driver, Audit audit) {
        this.audit = audit;
        this.driver = driver;
    }

    public void chooseCategory(AppointmentCategory category) throws Exception {
        final int optionValue = category.optionValue();
        WebElement option = WaitFor.waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
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
        System.out.println("Found appointments for " + category);
        option.click();
    }

    private void noAppointmentAvailable(AppointmentCategory category) {
        try {
            audit.append("No appointment found for " + category);
            System.out.println("No appointment found for " + category);
            System.exit(-1);
        } finally {
            driver.close();
        }
    }
}
