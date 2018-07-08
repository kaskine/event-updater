import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ExcelReadThread implements Runnable {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, YYYY");

    private CountDownLatch countDownLatch;
    private File excelFile;
    private List<Event> events;

    /**
     * Creates a new ExcelReadThread Object
     * @param excelFile - The Excel file to read
     * @param countDownLatch - The CountDownLatch used to indicate that reading the Excel file is complete
     */
    ExcelReadThread(File excelFile, CountDownLatch countDownLatch) {
        this.excelFile = excelFile;
        this.countDownLatch = countDownLatch;

        events = new LinkedList<>();
    }

    /**
     * Getter method for the List of Events
     * @return Returns a List of all Events generated from the Excel file
     */
    List<Event> getEvents() {
        return events;
    }

    /**
     * Reads the Excel file to gather event data
     * @throws IOException - If there is an error reading the Workbook file
     * @throws InvalidFormatException - If the Workbook file is not a valid format
     */
    private void readExcel() throws IOException, InvalidFormatException {

        Workbook workbook = WorkbookFactory.create(excelFile);
        Sheet sheet = workbook.getSheet("Sheet1");

        for (Row row : sheet) {

            CellType dateCellType = row.getCell(0).getCellTypeEnum();
            String date = "";

            if (dateCellType == CellType.STRING) {
                date = row.getCell(0).getStringCellValue();
                if (date.equalsIgnoreCase("date")) {
                    continue;
                }
            }
            else if (dateCellType == CellType.NUMERIC) {
                date = DATE_FORMAT.format(row.getCell(0).getDateCellValue());
            }

            String label = row.getCell(1).getStringCellValue();
            String address = row.getCell(1).getHyperlink().getAddress();
            String location = row.getCell(2).getStringCellValue();

            events.add(new Event(date, label, location, address));
        }
    }

    /**
     * Executes the Runnable in order to read the Excel file and indicates to the count down latch when complete
     */
    @Override
    public void run() {
        try {
            readExcel();
        } catch (InvalidFormatException | IOException e) {
            System.out.println("Error reading Excel file.");
            e.printStackTrace();
        }

        countDownLatch.countDown();
    }
}
