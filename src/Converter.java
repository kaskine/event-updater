import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

class Converter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMM d, YYYY");
    private static final Console console = System.console();

    private static final String KEY_BEGIN = "<!-- Begin Events Body -->";
    private static final String KEY_END = "<!-- End Events Body -->";
    private static final String TEMP_FILE_PREFIX = "tempOutputFile";
    private static final String TEMP_FILE_SUFFIX = ".tmp";

    private Converter() {}

    /**
     * @param excelPath - A String representing the Path to the Excel file
     * @param htmlPath  - A String representing the Path to the HTML file
     */
    static void convert(String excelPath, String htmlPath) {
        convert(Paths.get(excelPath), Paths.get(htmlPath));
    }

    /**
     * Gathers the data in the Excel file and the HTML file and sends it to the output method
     *
     * @param excelPath - The path to the input Excel file
     * @param htmlPath  - The path to the output HTML file
     */
    static void convert(Path excelPath, Path htmlPath) {

        try (BufferedReader reader = Files.newBufferedReader(htmlPath)) {

            Path tempPath = Files.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            BufferedWriter writer = Files.newBufferedWriter(tempPath);

            final boolean[] shouldIgnoreLine = {false};
            reader.lines().forEach(line -> {
                try {

                    if (!shouldIgnoreLine[0]) {
                        writer.write(line);
                        writer.newLine();
                    }

                    if (line.contains(KEY_BEGIN)) {
                        shouldIgnoreLine[0] = true;
                        getExcelData(excelPath, writer);
                    }
                    else if (line.contains(KEY_END)) {
                        shouldIgnoreLine[0] = false;
                        writer.write(line);
                        writer.newLine();
                    }

                } catch (IOException e) {
                    console.writer().println("Error: Writer cannot write to file.");
                    e.printStackTrace();
                } catch (InvalidFormatException e) {
                    console.writer().println("Error: Incorrect excel row formatting.");
                    e.printStackTrace();
                }
            });

            writer.close();
            Files.move(tempPath, htmlPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (FileNotFoundException e) {
            console.writer().println("Error: File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            console.writer().println("Error: Writer cannot write to file.");
            e.printStackTrace();
        }
    }

    /**
     *
     * @param excelPath - The Path represent the file system location of the Excel file
     * @param writer - The BufferedWriter writing the output file
     * @throws IOException - If there is an error writing to file
     * @throws InvalidFormatException - If the Excel rows are in an invalid format
     */
    private static void getExcelData(Path excelPath, BufferedWriter writer) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(excelPath.toFile());
        for (Row row : workbook.getSheet("Sheet1")) {
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
            writer.write(new Event(date, label, location, address).toString());
            writer.newLine();
        }
    }
}