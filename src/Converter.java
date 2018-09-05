import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.text.SimpleDateFormat;

class Converter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, YYYY");
    private static final Console console = System.console();

    private static final String KEY_BEGIN = "<!-- Begin Events Body -->";
    private static final String KEY_END = "<!-- End Events Body -->";
    private static final String TEMP_FILE_PREFIX = "tempOutputFile";
    private static final String TEMP_FILE_SUFFIX = ".tmp";

    private Converter() {}

    static boolean convert(String excelPath, String htmlPath) {
        return convert(new File(excelPath), new File(htmlPath));
    }

    /**
     * Gathers the data in the Excel file and the HTML file and sends it to the output method
     *
     * @param excelFile - The File Object representing the input Excel file
     * @param htmlFile  - The File Object representing the output HTML file
     * @return Returns true if file update is successful.
     */
    static boolean convert(File excelFile, File htmlFile) {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(htmlFile))) {

            File tempFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(KEY_BEGIN)) {
                    break;
                }
                bufferedWriter.write(line);
                bufferedWriter.newLine();

            }

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(KEY_END)) {
                    break;
                }
            }

            bufferedWriter.write(KEY_BEGIN);
            bufferedWriter.newLine();
            Workbook workbook = WorkbookFactory.create(excelFile);
            Sheet sheet = workbook.getSheet("Sheet1");
            for (Row row : sheet) {
                try {
                    CellType dateCellType = row.getCell(0).getCellTypeEnum();
                    String date = "";
                    if (dateCellType == CellType.STRING) {
                        date = row.getCell(0).getStringCellValue();
                    }
                    else if (dateCellType == CellType.NUMERIC) {
                        date = DATE_FORMAT.format(row.getCell(0).getDateCellValue());
                    }

                    String label = row.getCell(1).getStringCellValue();
                    String location = row.getCell(2).getStringCellValue();
                    String address = "";
                    Hyperlink hyperlink = row.getCell(1).getHyperlink();
                    if (hyperlink != null) {
                        address = hyperlink.getAddress();
                    }
                    bufferedWriter.write(new Event(date, label, location, address).toString());
                    bufferedWriter.newLine();
                } catch (Exception e) {
                    console.writer().println("Error: Incorrect excel row formatting.");
                    e.printStackTrace();
                }
            }
            bufferedWriter.write(KEY_END);
            bufferedWriter.newLine();

            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedReader.close();
            bufferedWriter.close();

            return htmlFile.delete() && tempFile.renameTo(htmlFile);
        } catch (FileNotFoundException e) {
            console.writer().println("Error: File not found.");
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            console.writer().println("Error: Excel file is improperly formatted.");
            e.printStackTrace();
        } catch (IOException e) {
            console.writer().println("Error: Writer cannot write to file.");
            e.printStackTrace();
        }
        return false;
    }
}