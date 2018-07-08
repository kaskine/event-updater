import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Event {

    private static final String linefeed = "\n";
    private static final String tab = "\t";

    private static final String header = "<div>";
    private static final String footer = "</div>";

    private static final String nameTag = "<h3 class=\"sl1-text mt-25 font-poppins\" style=\"font-size:22px\">";
    private static final String nameTagEnd = "</h3>";

    private static final String dateTag = "<h3 class=\"sl1-text mt-5 font-poppins\" style=\"font-size:14px; line-height:25px\">";
    private static final String dateTagEnd = "</h3>";

    private static final String locationTagLine = "<br> Location: ";

    private static final String urlTagEnd = "</a>";

    private String name, date, location, urlTag;

    /**
     * Constructs a new Event Object
     * @param date - The date of the Event
     * @param name - The name of the Event
     * @param location - The location of the Event
     * @param url - The URL of the Event website
     */
    Event(String date, String name, String location, String url) {
        this.date = removeQuotations(date);
        this.name = removeQuotations(name);
        this.location = removeQuotations(location);
        this.urlTag = "<a href=\"" + url + "\">";
    }

    /**
     * Removes quotations in a String
     * @param str - The String containing the quotations to be removed
     * @return Returns a String with all quotation marks removed
     */
    @NotNull
    @Contract(pure = true)
    private String removeQuotations(String str) {
        return str.replace("\"", "");
    }

    /**
     * Generates a block of HTML from the Event information
     * @return Returns a String containing HTML which will display the Event information
     */
    @Override
    public String toString() {

        return header + linefeed +
               tab + nameTag + linefeed +
               tab + tab + urlTag + name + urlTagEnd + linefeed +
               tab + nameTagEnd + linefeed +

               linefeed +

               tab + dateTag + linefeed +
               tab + tab + date + linefeed +
               tab + tab + locationTagLine + location + linefeed +
               tab + dateTagEnd + linefeed +
               footer + linefeed + linefeed;
    }
}
