package coursework3;

public class Sales {
    final private String Region;
    final private String Vehicle;
    final private Integer QTR;
    final private Integer Quantity;
    final private Integer Year;

    // NOTE : Constructors Compulsory !
    public Sales(Integer year, Integer quarter, String region, String vehicle, Integer quantity) {
        this.Region = region;
        this.Vehicle = vehicle;
        this.QTR = quarter;
        this.Quantity = quantity;
        this.Year = year;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s%s%s", 
        ("Year:" + Year + " "),("Quarter:" + QTR + " "),("Region:" + Region + " "), ("Vehicle:" + Vehicle + " "),("Quantity:" + Quantity + " "));
    }

    // ToDo : setMethods ?
    public Integer getQuarter() {
        return QTR;
    }
    public Integer getQuantity() {
        return Quantity;
    }
    public String getRegion() {
        return Region;
    }
    public String getVehicle() {
        return Vehicle;
    }
    public Integer getYear() {
        return Year;
    }
}
