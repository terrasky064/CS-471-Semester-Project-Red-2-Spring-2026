package PRODUCER_CONSUMER;
public class SalesRecord {
    String date;
    int storeId;
    int month;
    int register;
    double amount;
    boolean isPoison = false;


    // This constructor initializes a SalesRecord with the provided date, store ID, month, register number, and sales amount. The date is formatted as "dd/MM/16" using the day and month values. The storeId represents the identifier for the store where the sale occurred, month indicates the month of the sale, register is the register number at which the sale was made, and amount is the total sales amount for that record.
    public SalesRecord(int dd, int mm, int store, int reg, double amt) {
        this.date = String.format("%02d/%02d/16", dd, mm);
        this.storeId = store;
        this.month = mm;
        this.register = reg;
        this.amount = amt;
    }
    // This constructor creates a SalesRecord that serves as a poison pill. The isPoison flag is set to true, which allows consumer threads to recognize this record as a signal to stop consuming further records from the buffer. This is a common technique in producer-consumer scenarios to gracefully shut down consumer threads after all producers have finished producing items.
    public SalesRecord() { this.isPoison = true; } // poison pill: This ensures that the consumer can detect when to stop consuming without needing a separate flag or condition.
}