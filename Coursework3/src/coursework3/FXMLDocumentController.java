/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package coursework3;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author boonsim
 */
public class FXMLDocumentController implements Initializable {
    private static String markup;
    private static List<Sales> sales;
    private static List<String> region;
    private static List<String> vehicle;
    private static List<Integer> quarter;
    private static List<Integer> quantity;
    private static List<Integer> year;
    private DashService service;
    
    private CheckBox[] yr;
    private CheckBox[] qtr;
    private CheckBox[] rgn;
    
    @FXML
    private StackPane stackPane;
    
    @FXML
    private ProgressIndicator loadingProgress;
    
    //barChart
    @FXML
    private BarChart <?,?> barChart1;
    @FXML
    private BarChart <?,?> barChart2;
    @FXML
    private BarChart <?,?> barChart3;
    
    //buttons
    @FXML
    private Button go;
    @FXML
    private Button docx;
    @FXML
    private Button close;
    
    @FXML
    private HBox hboxYear;
    @FXML
    private HBox hboxQuarter;
    @FXML
    private HBox hboxRegion;
    
    @FXML
    private TableView table1;
    
    
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        
        constructCheckBoxes();
        constructTableColumn();
    }
    
    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }
    
    //To establish connection with JSON server, converting data into values
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        service = new DashService();
        service.setAddress("http://glynserver.cms.livjm.ac.uk/DashService/SalesGetSales");
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent e) {
                markup = e.getSource().getValue().toString();
                
                sales = (new Gson()).fromJson(markup, new TypeToken<LinkedList<Sales>>() {}.getType());
                
                quarter= sales.stream().map(o -> o.getQuarter()).distinct().collect(Collectors.toList());
                quantity= sales.stream().map(o -> o.getQuantity()).distinct().collect(Collectors.toList());
                region = sales.stream().map(o -> o.getRegion()).distinct().collect(Collectors.toList());
                vehicle= sales.stream().map(o -> o.getVehicle()).distinct().collect(Collectors.toList());
                year= sales.stream().map(o -> o.getYear()).distinct().collect(Collectors.toList());
                
                loadingProgress.visibleProperty().bind(service.runningProperty());
                
                for(Sales sales:sales){
                    System.out.println(sales);
                }
            }
            
        });
        
        service.start();
    }
    
    //creating Table to display data
    private void constructTableColumn(){
        table1.getColumns().clear();
        
        ObservableList<Sales>storingList = FXCollections.observableArrayList();
        
        for(Sales sales:sales){
            storingList.add(new Sales(sales.getYear(),sales.getQuarter(),sales.getRegion(),sales.getVehicle(),sales.getQuantity()));
        }
        
        TableColumn<Sales,String> dtYear = new TableColumn<>("Year");
        dtYear.setCellValueFactory(new PropertyValueFactory<>("Year"));
        
        TableColumn<Sales,String> dtQuarter = new TableColumn<>("Quarter");
        dtQuarter.setCellValueFactory(new PropertyValueFactory<>("quarter"));
        
        TableColumn<Sales,String> dtRegion = new TableColumn<>("Region");
        dtRegion.setCellValueFactory(new PropertyValueFactory<>("Region"));
        
        TableColumn<Sales,String> dtVehicle = new TableColumn<>("Vehicle");
        dtVehicle.setCellValueFactory(new PropertyValueFactory<>("Vehicle"));
        
        TableColumn<Sales,String> dtQuantity = new TableColumn<>("Quantity");
        dtQuantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        
        table1.setItems(storingList);
        table1.getColumns().addAll(dtYear,dtQuarter,dtRegion,dtVehicle,dtQuantity);
        table1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    //Display checkboxes
    private void constructCheckBoxes() {
        hboxYear.getChildren().clear();
        hboxRegion.getChildren().clear();
        hboxQuarter.getChildren().clear();
        
        rgn = new CheckBox[region.size()];
        qtr = new CheckBox[quarter.size()];
        yr = new CheckBox[year.size()];
        
        //Event filters, handlers, and actions
        for (byte index = 0; index < region.size(); index++) {
            rgn[index] = new CheckBox(region.get(index));
            rgn[index].setSelected(true);
            rgn[index].addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("Firstly, Event Filters !");
                }
            });
            rgn[index].addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("Secondly, Event Handlers !");
                }
            });
            rgn[index].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("Thirdly, Convenience Methods !");
                    constructSeries(rgn[0].getText());
                }
            });
            
            hboxRegion.getChildren().add(rgn[index]);
        }
        
        for (byte index = 0; index < quarter.size(); index++) {
            qtr[index] = new CheckBox(quarter.get(index).toString());
            qtr[index].setSelected(true);
            qtr[index].addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("Firstly, Event Filters !");
                }
            });
            qtr[index].addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("Secondly, Event Handlers !");
                }
            });
            qtr[index].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("Thirdly, Convenience Methods !");
                    constructSeries(qtr[0].getText());
                }
            });
            
            hboxQuarter.getChildren().add(qtr[index]);
        }
        
        for (byte index = 0; index < year.size(); index++) {
            yr[index] = new CheckBox(year.get(index).toString());
            yr[index].setSelected(true);
            yr[index].addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("Firstly, Event Filters !");
                }
            });
            yr[index].addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("Secondly, Event Handlers !");
                }
            });
            yr[index].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.out.println("Thirdly, Convenience Methods !");
                    constructSeries(yr[0].getText());
                }
            });
            
            hboxYear.getChildren().add(yr[index]);
        }
        
        // NOTE : More Controls Visible !
        stackPane.getScene().getWindow().sizeToScene();
        
        constructSeries("");
    }
    
    private void constructSeries(String checkBoxName) {
        barChart1.getData().clear();
        barChart2.getData().clear();
        barChart3.getData().clear();
        
        //To untick other checkboxes when one category of checkbox is ticked
        for(int i=0;i<rgn.length;i++){
            if(checkBoxName.equals(rgn[i].getText())){
                for(int j=0;j<qtr.length;j++){qtr[j].selectedProperty().set(false);}
                for(int j=0;j<yr.length;j++){yr[j].selectedProperty().set(false);}
            }
        }
        
        for(int i=0;i<qtr.length;i++){
            if(checkBoxName.equals(qtr[i].getText())){
                for(int j=0;j<rgn.length;j++){rgn[j].selectedProperty().set(false);}
                for(int j=0;j<yr.length;j++){yr[j].selectedProperty().set(false);}
            }
        }
        
        for(int i=0;i<yr.length;i++){
            if(checkBoxName.equals(yr[i].getText())){
                for(int j=0;j<rgn.length;j++){rgn[j].selectedProperty().set(false);}
                for(int j=0;j<qtr.length;j++){qtr[j].selectedProperty().set(false);}
            }
        }
        
        for (CheckBox checkBox : rgn) {
            Map<String,Integer> salesRegion1 = new HashMap<>();
            Map<String,Integer> salesRegion2 = new HashMap<>();
            Map<String,Integer> salesRegion3 = new HashMap<>();
            
            for(Sales sale:sales){
                if(sale.getVehicle().equals("Elise")){
                    salesRegion1.merge(sale.getRegion(),sale.getQuantity(),Integer::sum);
                }
                else if(sale.getVehicle().equals("Evora")){
                    salesRegion2.merge(sale.getRegion(),sale.getQuantity(),Integer::sum);
                }
                else if(sale.getVehicle().equals("Exige")){
                    salesRegion3.merge(sale.getRegion(),sale.getQuantity(),Integer::sum);
                }
            }
            //to filter selected checkbox, match with JSON values
            if (checkBox.isSelected()) {
                XYChart.Series series = new XYChart.Series();
                series.setName(checkBox.getText());
                for(Map.Entry<String,Integer>region1:salesRegion1.entrySet()){
                    if(region1.getKey().equals(checkBox.getText()))
                        series.getData().add(new XYChart.Data<>("Elise",region1.getValue()));
                }
                for(Map.Entry<String,Integer>region2:salesRegion2.entrySet()){
                    if(region2.getKey().equals(checkBox.getText()))
                        series.getData().add(new XYChart.Data<>("Evora",region2.getValue()));
                }
                for(Map.Entry<String,Integer>region3:salesRegion3.entrySet()){
                    if(region3.getKey().equals(checkBox.getText()))
                        series.getData().add(new XYChart.Data<>("Exige",region3.getValue()));
                }
                barChart1.getData().add(series);
                barChart1.setVisible(true);
                barChart2.setVisible(false);
                barChart3.setVisible(false);
            }/*else{
                barChart1.setVisible(true);
            }*/
            }
        
        for (CheckBox checkBox: qtr) {
            Map<String,Integer> salesQuarter1 = new HashMap<>();
            Map<String,Integer> salesQuarter2 = new HashMap<>();
            Map<String,Integer> salesQuarter3 = new HashMap<>();
            
            for(Sales sale:sales){
                if(sale.getVehicle().equals("Elise")){
                    salesQuarter1.merge(sale.getQuarter().toString(),sale.getQuantity(),Integer::sum);
                }
                else if(sale.getVehicle().equals("Evora")){
                    salesQuarter2.merge(sale.getQuarter().toString(),sale.getQuantity(),Integer::sum);
                }
                else if(sale.getVehicle().equals("Exige")){
                    salesQuarter3.merge(sale.getQuarter().toString(),sale.getQuantity(),Integer::sum);
                }
            }
            if (checkBox.isSelected()) {
                XYChart.Series series = new XYChart.Series();
                series.setName(checkBox.getText());
                for(Map.Entry<String,Integer>quarter1:salesQuarter1.entrySet()){
                    if(quarter1.getKey().equals(checkBox.getText()))
                        series.getData().add(new XYChart.Data<>("Elise",quarter1.getValue()));
                }
                for(Map.Entry<String,Integer>quarter2:salesQuarter2.entrySet()){
                    if(quarter2.getKey().equals(checkBox.getText()))
                        series.getData().add(new XYChart.Data<>("Evora",quarter2.getValue()));
                }
                for(Map.Entry<String,Integer>quarter3:salesQuarter3.entrySet()){
                    if(quarter3.getKey().equals(checkBox.getText()))
                        series.getData().add(new XYChart.Data<>("Exige",quarter3.getValue()));
                }
                barChart2.getData().add(series);
                barChart2.setVisible(true);
                barChart1.setVisible(false);
                barChart3.setVisible(false);
                
                
            }/*else{
                barChart2.setVisible(true);
            }*/
        }
        
        for (CheckBox checkBox : yr) {
            Map<String,Integer> salesYear1 = new HashMap<>();
            Map<String,Integer> salesYear2 = new HashMap<>();
            Map<String,Integer> salesYear3 = new HashMap<>();
            
            for(Sales sale:sales){
                if(sale.getVehicle().equals("Elise")){
                    salesYear1.merge(sale.getYear().toString(),sale.getQuantity(),Integer::sum);
                }
                else if(sale.getVehicle().equals("Evora")){
                    salesYear2.merge(sale.getYear().toString(),sale.getQuantity(),Integer::sum);
                }
                else if(sale.getVehicle().equals("Exige")){
                    salesYear3.merge(sale.getYear().toString(),sale.getQuantity(),Integer::sum);
                }
            }
            if (checkBox.isSelected()) {
                XYChart.Series series = new XYChart.Series();
                series.setName(checkBox.getText());
                for(Map.Entry<String,Integer>year1:salesYear1.entrySet()){
                    if(year1.getKey().equals(checkBox.getText()))
                        series.getData().add(new XYChart.Data<>("Elise",year1.getValue()));
                }
                for(Map.Entry<String,Integer>year2:salesYear2.entrySet()){
                    if(year2.getKey().equals(checkBox.getText()))
                        series.getData().add(new XYChart.Data<>("Evora",year2.getValue()));
                }
                for(Map.Entry<String,Integer>year3:salesYear3.entrySet()){
                    if(year3.getKey().equals(checkBox.getText()))
                        series.getData().add(new XYChart.Data<>("Exige",year3.getValue()));
                }
                barChart3.getData().add(series);
                barChart3.setVisible(true);
                barChart2.setVisible(false);
                barChart1.setVisible(false);
            }/*else{
                barChart3.setVisible(true);
            }*/
        }
    }
    
    @FXML //To export all data as Excel document
    private void exportButtonAction() throws IOException{
            Writer writer = null;
            try {
                File file = new File("Sales.csv");
                writer = new BufferedWriter(new FileWriter(file));
                writer.write("Year,Quarter,Region,Vehicle,Sales\n");
                for (Sales sales : sales) {
                  String text = sales.getYear()+","+sales.getQuarter()+","+sales.getRegion()+","+sales.getVehicle()+","+sales.getQuantity()+"\n";
                  writer.write(text);
                  System.out.println("Print Excel");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                writer.flush();
                writer.close();
            }
    }
    
    //TO establish connection with web service
    private static class DashService extends Service<String> {
        private StringProperty address = new SimpleStringProperty();
        
        public final void setAddress(String address) {
            this.address.set(address);
        }
        
        public final String getAddress() {
            return address.get();
        }
        
        public final StringProperty addressProperty() {
            return address;
        }
        
        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                private URL url;
                private HttpURLConnection connect;
                private String markup = "";
                
                @Override
                protected String call() {
                    try {
                        url = new URL(getAddress());
                        connect = (HttpURLConnection)url.openConnection();
                        connect.setRequestMethod("GET");
                        connect.setRequestProperty("Accept", "application/json");
                        connect.setRequestProperty("Content-Type", "application/json");
                        
                        markup = (new BufferedReader(new InputStreamReader(connect.getInputStream()))).readLine();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        if (connect != null) {
                            connect.disconnect();
                        }
                    }
                    
                    return markup;
                }
            };
        }
    }
}

