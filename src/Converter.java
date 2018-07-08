import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

class Converter {

    private static final String KEY_BEGIN = "<!-- Begin Events Body -->";
    private static final String KEY_END = "<!-- End Events Body -->";
    private static final String CHARSET_FORMAT = "UTF-8";

    /**
     * Constructs a new Converter Object
     * @param excelPath - The path of the input Excel file
     * @param htmlPath - The path of the output HTML file
     */
    Converter(String excelPath, String htmlPath) {

        try {
            convert(new File(excelPath), new File(htmlPath));
        } catch (IOException e) {
            System.out.println("Error in file conversion. Check input.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Error reading Excel file. Check input.");
            e.printStackTrace();
        }
    }

    /**
     * Constructs a new Converter Object
     * @param excelPath - The path of the input Excel file
     * @param htmlPath - The path of the output HTML file
     */
    Converter(File excelPath, File htmlPath) {
        try {
            convert(excelPath, htmlPath);
        } catch (IOException e) {
            System.out.println("Error writing file. Check input.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts the data in the Excel file to HTML
     * @param excelPath - The path of the Excel file
     * @param htmlPath - The path of the HTML file
     * @throws InterruptedException - If there is an error reading the Excel file
     * @throws IOException - If there is an error writing the output file
     */
    private void convert(File excelPath, File htmlPath) throws InterruptedException, IOException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        ExcelReadThread excelReadThread = new ExcelReadThread(excelPath, countDownLatch);
        new Thread(excelReadThread).start();

        Scanner scanner = new Scanner(htmlPath, CHARSET_FORMAT);
        List<String> header = getHeader(scanner);
        List<String> footer = getFooter(scanner);
        scanner.close();

        countDownLatch.await();
        write(htmlPath, header, excelReadThread.getEvents(), footer);
    }

    /**
     * Gets the page text above the event information section
     * @param scanner - The Scanner reading the file
     * @return Returns the page text above the event information section
     */
    private List<String> getHeader(Scanner scanner) {

        List<String> header = new LinkedList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains(KEY_BEGIN)) {
                break;
            }
            header.add(line);
        }

        return header;
    }

    /**
     * Gets the page text below the event information section
     * @param scanner - The Scanner reading the file
     * @return Returns the page text below the event information section
     */
    private List<String> getFooter(Scanner scanner) {

        List<String> footer = new LinkedList<>();

        while (scanner.hasNextLine()) {
            if (scanner.nextLine().contains(KEY_END)) {
                break;
            }
        }
        while (scanner.hasNextLine()) {
            footer.add(scanner.nextLine());
        }
        return footer;
    }

    /**
     * Overwrites the output file location with the new page
     * @param outputPath - The path of the output file
     * @param header - The page section above the event info section
     * @param events - The event info to write to the page
     * @param footer - The page section below the event info section
     * @throws IOException - If the program cannot write the output file
     */
    private void write(File outputPath, List<String> header, List<Event> events, List<String> footer) throws IOException {
        Writer writer = new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8);
        for (String line : header) {
            writer.write(line + "\n");
        }
        writer.write(KEY_BEGIN + "\n");
        for (Event event : events) {
            writer.write(event.toString());
        }
        writer.write("\n" + KEY_END + "\n");
        for (String line : footer) {
            writer.write(line + "\n");
        }
        writer.close();
    }
}