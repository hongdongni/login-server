/*******************************************************************************
 *     Cloud Foundry 
 *     Copyright (c) [2009-2014] Pivotal Software, Inc. All Rights Reserved.
 *
 *     This product is licensed to you under the Apache License, Version 2.0 (the "License").
 *     You may not use this product except in compliance with the License.
 *
 *     This product includes a number of subcomponents with
 *     separate copyright notices and license terms. Your use of these
 *     subcomponents is subject to the terms and conditions of the
 *     subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/
package org.cloudfoundry.identity.uaa.login.feature;

import static org.junit.Assert.assertEquals;

import org.cloudfoundry.identity.uaa.login.test.DefaultIntegrationTestConfig;
import org.cloudfoundry.identity.uaa.login.test.IntegrationTestRule;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.test.TestAccounts;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DefaultIntegrationTestConfig.class)
public class HomeIT {
    @Autowired
    TestAccounts testAccounts;
    
    @Autowired @Rule
    public IntegrationTestRule integrationTestRule;

    @Autowired
    WebDriver webDriver;

    @Value("${integration.test.base_url}")
    String baseUrl;

    private HomePagePerspective asOnHomePage;

    @Before
    public void setUp() {
        webDriver.get(baseUrl + "/logout.do");

        webDriver.get(baseUrl + "/login");
        webDriver.findElement(By.name("username")).sendKeys(testAccounts.getUserName());
        webDriver.findElement(By.name("password")).sendKeys(testAccounts.getPassword());
        webDriver.findElement(By.xpath("//input[@value='Sign in']")).click();

        asOnHomePage = new HomePagePerspective(webDriver, testAccounts.getUserName());
    }

    @Test
    public void theHeaderDropdown() throws Exception {
        Assert.assertNotNull(asOnHomePage.getUsernameElement());
        Assert.assertFalse(asOnHomePage.getAccountSettingsElement().isDisplayed());
        Assert.assertFalse(asOnHomePage.getSignOutElement().isDisplayed());

        asOnHomePage.getUsernameElement().click();

        Assert.assertTrue(asOnHomePage.getAccountSettingsElement().isDisplayed());
        Assert.assertTrue(asOnHomePage.getSignOutElement().isDisplayed());

        asOnHomePage.getAccountSettingsElement().click();

        Assert.assertThat(webDriver.findElement(By.cssSelector("h1")).getText(), Matchers.containsString("Account Settings"));
    }

    @Test
    public void tiles() throws Exception {
        List<WebElement> tiles = asOnHomePage.getTiles();
        assertEquals(3, tiles.size());

        assertEquals("Pivotal Network", tiles.get(0).getText());
        assertEquals("https://network.gopivotal.com/login", tiles.get(0).getAttribute("href"));
        assertEquals("url(http://localhost:8080/login/resources/pivotal/images/network-logo-gray.png)", tiles.get(0).getCssValue("background-image"));

        assertEquals("Pivotal Web Services", tiles.get(1).getText());

        assertEquals("Pivotal Partners", tiles.get(2).getText());
    }

    static class HomePagePerspective {
        private final WebDriver webDriver;
        private final String username;

        public HomePagePerspective(WebDriver webDriver, String username) {
            this.webDriver = webDriver;
            this.username = username;
        }

        public List<WebElement> getTiles() {
            return webDriver.findElements(By.cssSelector(".tiles li a"));
        }

        public WebElement getUsernameElement() {
            return getWebElementWithText(username);
        }

        public WebElement getAccountSettingsElement() {
            return getWebElementWithText("Account Settings");
        }

        public WebElement getSignOutElement() {
            return getWebElementWithText("Sign Out");
        }

        private WebElement getWebElementWithText(String text) {
            return webDriver.findElement(By.xpath("//*[text()='" + text + "']"));
        }
    }
}
