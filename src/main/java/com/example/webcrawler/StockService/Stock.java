package com.example.webcrawler.StockService;

import com.example.webcrawler.Repository.NepseModelRepository;
import com.example.webcrawler.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class Stock implements IStock {

    @Autowired
    NepseModelRepository nepseModelRepository;

    private final String NEPSEURL = "http://www.nepalstock.com/stocklive";
    private final String MDURL = "http://nepalstock.com/marketdepth";
    private String MDBYIDURL = "http://nepalstock.com/marketdepthofcompany/";

    public List<StockModel> getLiveStock(){
        List<StockModel> listStock = new ArrayList<>();
        try{
            Document document = Jsoup.connect(NEPSEURL).get();
            Element table = document.select("table.table.table-condensed").get(0);
            Elements tableRows = table.select("table.table-condensed tbody tr");
            for(Element row : tableRows){
                String porl = "";
                if(Double.parseDouble(row.select("td:nth-of-type(5)").text().replaceAll(",","")) > 0) porl = "p";
                else if(Double.parseDouble(row.select("td:nth-of-type(5)").text().replaceAll(",","")) < 0) porl = "l";
                else porl = "n";
                StockModel stock = new StockModel(row.select("td:nth-of-type(2)").text(),row.select("td:nth-of-type(3)").text(),row.select("td:nth-of-type(5)").text(),porl);
                listStock.add(stock);
            }
        }
        catch (Exception e){
            listStock = null;
        }
        return listStock;
    }

    public List<StockModel> getLiveStockWithCondition(String condition, Double ltpValue){
        List<StockModel> listStock = new ArrayList<>();
        try{
            Document document = Jsoup.connect(NEPSEURL).get();
            Element table = document.select("table.table.table-condensed").get(0);
            Elements tableRows = table.select("table.table-condensed tbody tr");
            if(condition.equals("gt")){
                for(Element row : tableRows){
                    String porl = "";
                    if(Double.parseDouble(row.select("td:nth-of-type(3)").text().replaceAll(",","")) > ltpValue){
                        if(Double.parseDouble(row.select("td:nth-of-type(5)").text().replaceAll(",","")) > 0) porl = "p";
                        else if(Double.parseDouble(row.select("td:nth-of-type(5)").text().replaceAll(",","")) < 0) porl = "l";
                        else porl = "n";
                        StockModel stock = new StockModel(row.select("td:nth-of-type(2)").text(),row.select("td:nth-of-type(3)").text(),row.select("td:nth-of-type(5)").text(),porl);
                        listStock.add(stock);
                    }
                }
            }
            else if(condition.equals("lt")){
                for(Element row : tableRows){
                    String porl = "";
                    if(Double.parseDouble(row.select("td:nth-of-type(3)").text().replaceAll(",","")) < ltpValue){
                        if(Double.parseDouble(row.select("td:nth-of-type(5)").text().replaceAll(",","")) > 0) porl = "p";
                        else if(Double.parseDouble(row.select("td:nth-of-type(5)").text().replaceAll(",","")) < 0) porl = "l";
                        else porl = "n";
                        StockModel stock = new StockModel(row.select("td:nth-of-type(2)").text(),row.select("td:nth-of-type(3)").text(),row.select("td:nth-of-type(5)").text(),porl);
                        listStock.add(stock);
                    }
                }
            }
            else{
                for(Element row : tableRows){
                    String porl = "";
                    if(Double.parseDouble(row.select("td:nth-of-type(3)").text().replaceAll(",","")) == ltpValue){
                        if(Double.parseDouble(row.select("td:nth-of-type(5)").text().replaceAll(",","")) > 0) porl = "p";
                        else if(Double.parseDouble(row.select("td:nth-of-type(5)").text().replaceAll(",","")) < 0) porl = "l";
                        else porl = "n";
                        StockModel stock = new StockModel(row.select("td:nth-of-type(2)").text(),row.select("td:nth-of-type(3)").text(),row.select("td:nth-of-type(5)").text(),porl);
                        listStock.add(stock);
                    }
                }
            }
        }
        catch (Exception e){
            listStock = null;
        }
        return listStock;
    }

    public String getStockNoBySymbol(String symbol){
        String stockNo = "";
        try{
            Document document = Jsoup.connect(MDURL).get();
            Elements options = document.select("select#StockSymbol_Select1 option");
            for(Element option : options){
                if(option.text() == symbol) stockNo = option.attr("value");
            }
        }
        catch(Exception e){
            return stockNo;
        }
        return stockNo;
    }

    public List<StockModel> getAllSymbolNo(){
        List<StockModel> stocks = new ArrayList<>();
        try{
            Document document = Jsoup.connect(MDURL).get();
            Elements options = document.select("select#StockSymbol_Select1 option");
            for(Element option : options){
                StockModel stock = new StockModel(option.text(),option.attr("value"));
                stocks.add(stock);
            }
            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 15 && findByDate(LocalDate.now()).isEmpty() && LocalDate.now().getDayOfWeek().getValue() != 6 && LocalDate.now().getDayOfWeek().getValue() != 5){
                for(StockModel stockModel : stocks){
                    if(!stockModel.getSymbol().equals("Choose Symbol")) {
                        NepseModel model = new NepseModel();
                        model.setDate(LocalDate.now());
                        model.setSymbol(stockModel.getSymbol());
                        model.setLtp(getMarketDepthBySymbol(stockModel.getStockNo()).getLivePrice());
                        model.setSymbolNo(stockModel.getStockNo());
                        saveNepse(model);
                    }
                }
            }
        }
        catch (Exception e){
            stocks = null;
        }
        return stocks;
    }

    public DepthModel getMarketDepthBySymbol(String symbol){
        DepthModel depthModel = new DepthModel();
        try{
            MDBYIDURL = MDBYIDURL + symbol;
            Document document = Jsoup.connect(MDBYIDURL).get();
            MDBYIDURL = "http://nepalstock.com/marketdepthofcompany/";
            depthModel.setName(document.select("table tbody tr td div").text());
            Element depthIndexTable = document.select("table.depthIndex tbody tr").get(0);
            depthModel.setLivePrice(depthIndexTable.select("td label.livePrice").text());
            String[] indicator = depthIndexTable.select("td span").get(1).text().split(" ");
            depthModel.setIndicator(indicator[0]+" "+indicator[1]);
            depthModel.setIndicatorColor(depthIndexTable.select("td span").get(1).attr("class"));
            depthModel.setPrClose(depthIndexTable.select("td").get(1).text().split(" ")[2]);
            depthModel.setOpen(depthIndexTable.select("td").get(2).text().split(" ")[1]);
            depthModel.setHigh(depthIndexTable.select("td").get(3).text().split(" ")[1]);
            depthModel.setLow(depthIndexTable.select("td").get(4).text().split(" ")[1]);
            depthModel.setClose(depthIndexTable.select("td").get(5).text().split(" ")[1]);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return depthModel;
    }
    public String getChartBySymbol(String symbol){
        String chart = "";
        List<NepseModel> listModel = findBySymbolNo(symbol);
        String dates = "", data = "";
        for(NepseModel model : listModel){
            dates += "'"+ model.getDate().toString() +"',";
            data += Double.parseDouble(model.getLtp().replaceAll(",","")) +",";
        }
        dates = dates.substring(0, dates.length()-1);
        data = data.substring(0, data.length()-1);
        chart = "<canvas id=\"myChart\" width=\"500\" height=\"300\"></canvas>\n" +
                "<script>\n" +
                "var ctx = document.getElementById('myChart').getContext('2d');\n" +
                "var myChart = new Chart(ctx, {\n" +
                "    type: 'line',\n" +
                "    data: {\n" +
                "        labels: ["+ dates +"],\n" +
                "        datasets: [{\n" +
                "            label: 'Closing Price',\n" +
                "            data: ["+ data +"],\n" +
                "            backgroundColor: [\n" +
                "                'rgba(255, 99, 132, 0.2)'\n" +
                "            ],\n" +
                "            borderColor: [\n" +
                "                'rgba(255, 99, 132, 1)'\n" +
                "            ],\n" +
                "            fill: 'start',\n" +
                "            cubicInterpolationMode: 'monotone',\n" +
                "            tension: 0.4\n" +
                "        }]\n" +
                "    },\n" +
                "    options: {\n" +
                "       plugins: {\n" +
                "           filler: {\n" +
                "               propagate: false,\n" +
                "           }\n" +
                "       },\n" +
                "       interaction: {\n" +
                "           intersect: false,\n" +
                "       },\n" +
                "        scales: {\n" +
                "            y: {\n" +
                "                beginAtZero: true\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "});\n" +
                "</script>";
        return chart;
    }

    @Override
    public NepseModel saveNepse(NepseModel nepseModel){
        return nepseModelRepository.save(nepseModel);
    }
    @Override
    public List<NepseModel> findByDate(LocalDate date){
        return nepseModelRepository.findByDate(date);
    }

    @Override
    public List<NepseModel> findBySymbolNo(String symbol) {
        return nepseModelRepository.findBySymbolNo(symbol);
    }

    @Override
    public int updateNepseModelSetSymbolNo(String SymbolNo, String symbol) {
        return nepseModelRepository.updateNepseModelSetSymbolNo(SymbolNo, symbol);
    }

    public List<BuyModel> getBuyerBySymbol(String symbol){
        List<BuyModel> buyModels = new ArrayList<>();
        try{
            MDBYIDURL = MDBYIDURL + symbol;
            Document document = Jsoup.connect(MDBYIDURL).get();
            MDBYIDURL = "http://nepalstock.com/marketdepthofcompany/";
            Element buyTable = document.select("table.table.table-striped.table-bordered.orderTable").get(0);
            Elements buyRow = buyTable.select("tbody tr");
            for(Element row : buyRow){
                BuyModel buyModel = new BuyModel(row.select("td").get(0).text(), row.select("td").get(1).text(), row.select("td").get(2).text());
                buyModels.add(buyModel);
            }
        }
        catch (Exception e){
            buyModels = null;
        }
        return buyModels;
    }
    public List<SellModel> getSellerBySymbol(String symbol){
        List<SellModel> sellModels = new ArrayList<>();
        try{
            MDBYIDURL = MDBYIDURL + symbol;
            Document document = Jsoup.connect(MDBYIDURL).get();
            MDBYIDURL = "http://nepalstock.com/marketdepthofcompany/";
            Element sellTable = document.select("table.table.table-striped.table-bordered.orderTable").get(1);
            Elements sellRow = sellTable.select("tbody tr");
            for(Element row : sellRow){
                SellModel sellModel = new SellModel(row.select("td").get(2).text(), row.select("td").get(1).text(), row.select("td").get(0).text());
                sellModels.add(sellModel);
            }
        }
        catch (Exception e){
            sellModels = null;
        }
        return sellModels;
    }
}
