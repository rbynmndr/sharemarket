package com.example.webcrawler.Controller;

import com.example.webcrawler.StockService.Stock;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Calendar;

@Controller
public class MainController {

    private Stock stock;

    public MainController(Stock stock){
        this.stock = stock;
    }

    @GetMapping(value = {"/","/live-nepse"})
    public String home(Model model){
        model.addAttribute("stocks", stock.getLiveStock());
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int day = LocalDate.now().getDayOfWeek().getValue();
        model.addAttribute("time", hour >= 11 && hour <= 15 && day != 6 && day != 5);
        return "Home";
    }

    @GetMapping("/live-nepse-cond")
    public String liveWithCondition(@RequestParam(value = "condition") String condition, @RequestParam(value = "ltpValue") @Nullable Double ltpValue, Model model){
        if(condition.isBlank()) return "redirect:/live-nepse";
        if(ltpValue == null) return "redirect:/live-nepse";
        model.addAttribute("stocks", stock.getLiveStockWithCondition(condition, ltpValue));
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int day = LocalDate.now().getDayOfWeek().getValue();
        model.addAttribute("time", hour >= 11 && hour <= 15 && day != 6 && day != 5);
        return "Home";
    }

    @GetMapping("/market-depth")
    public String marketDepth(Model model){
        model.addAttribute("stocks", stock.getAllSymbolNo());
        return "MarketDepth";
    }

    @GetMapping("/market-depth-no")
    public String getMarketDepth(@RequestParam(value = "symbol") String symbol, Model model, RedirectAttributes redirectAttributes){
        if(symbol.isEmpty() || symbol.isBlank()){
            redirectAttributes.addFlashAttribute("toast", true);
            return "redirect:/market-depth";
        }
        model.addAttribute("stocks", stock.getAllSymbolNo());
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int day = LocalDate.now().getDayOfWeek().getValue();
        redirectAttributes.addFlashAttribute("marketStatus", (hour >= 11 && hour <= 15 && day != 6 && day != 5) ? true : false);
        redirectAttributes.addFlashAttribute("depthIndex", stock.getMarketDepthBySymbol(symbol));
        redirectAttributes.addFlashAttribute("buyer", stock.getBuyerBySymbol(symbol));
        redirectAttributes.addFlashAttribute("seller", stock.getSellerBySymbol(symbol));
        redirectAttributes.addFlashAttribute("chart", stock.getChartBySymbol(symbol));
        return "redirect:/market-depth";
    }

    @GetMapping("/tms-test")
    public String tmsHome(){
        return "TMS";
    }

    @PostMapping("/tms-test")
    public String tmsPost(@RequestParam(value = "id") String id, @RequestParam(value = "pass") String pass, Model model){
        System.setProperty("webdriver.edge.driver","C:\\edgedriver_win64\\msedgedriver.exe");
        WebDriver driver = new EdgeDriver();
        driver.get("https://tms47.nepsetms.com.np/tms/client/dashboard");
        WebElement amt = driver.findElement(By.cssSelector("#main div div div:nth-child(1) div.col-lg-4 div:nth-child(2) div.card-body div.total-count div.h4"));
        //WebElement username = driver.findElement(By.cssSelector("[placeholder='Client Code/ User Name']"));
        //WebElement password = driver.findElement(By.id("password-field"));
        //WebElement login = driver.findElement(By.className("btn-primary"));
        //username.sendKeys(id);
        //password.sendKeys(pass);
        //login.click();
        String url = amt.getText();
        driver.quit();
        model.addAttribute("url", url);
        return "TMS";
    }
}
