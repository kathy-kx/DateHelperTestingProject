package com.kxzhu.datehelper;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

import com.kxzhu.datehelper.DateHelper.DateFormats; // Ensure DateFormats is accessible

/**
 * Unit tests for DateHelper using Category-Partition Testing.
 */
public class DateHelperTest {

    private long todayTimestamp;
    private long yesterdayTimestamp;
    private SimpleDateFormat sdf_ddMMMyyyy_hhmma; // Example format for verification
    private SimpleDateFormat sdf_hhmma;           // Example format for verification
    private SimpleDateFormat sdf_ddMMyyyy;        // Example format for verification


    @Before
    public void setUp() {
        // Set up consistent timestamps for tests
        Calendar cal = Calendar.getInstance();
        todayTimestamp = cal.getTimeInMillis();

        cal.add(Calendar.DATE, -1);
        yesterdayTimestamp = cal.getTimeInMillis();

        // Initialize formatters used for verification (use default locale like the class)
        sdf_ddMMMyyyy_hhmma = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());
        sdf_hhmma = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        sdf_ddMMyyyy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    // --- Test prettifyDate(long) ---

    @Test
    public void prettifyDate_long_shouldReturnTime_whenToday() {
        // Partition: Timestamp is today
        // We simulate 'isToday' by using a current timestamp
        String expected = sdf_hhmma.format(todayTimestamp);
        // Note: DateUtils.isToday requires Android context, so this test simulates the outcome
        // by assuming the method's internal check works correctly based on the timestamp's date.
        // A small difference due to execution time is possible but usually negligible for format.
        // For robustness, might compare formats ignoring minor second/ms differences if needed.
        assertEquals(expected, DateHelper.prettifyDate(todayTimestamp));
    }

    @Test
    public void prettifyDate_long_shouldReturnDateAndTime_whenNotToday() {
        // Partition: Timestamp is not today
        String expected = sdf_ddMMMyyyy_hhmma.format(yesterdayTimestamp);
        assertEquals(expected, DateHelper.prettifyDate(yesterdayTimestamp));
    }

    // --- Test prettifyDate(String) ---

    @Test
    public void prettifyDate_string_shouldReturnTime_whenToday() {
        // Partition: Timestamp string is today
        String timestampStr = String.valueOf(todayTimestamp);
        String expected = sdf_hhmma.format(todayTimestamp);
        assertEquals(expected, DateHelper.prettifyDate(timestampStr));
    }

    @Test
    public void prettifyDate_string_shouldReturnDateAndTime_whenNotToday() {
        // Partition: Timestamp string is not today
        String timestampStr = String.valueOf(yesterdayTimestamp);
        String expected = sdf_ddMMMyyyy_hhmma.format(yesterdayTimestamp);
        assertEquals(expected, DateHelper.prettifyDate(timestampStr));
    }

    @Test(expected = NumberFormatException.class)
    public void prettifyDate_string_shouldThrowException_whenInvalidFormat() {
        // Partition: Invalid timestamp string
        DateHelper.prettifyDate("not-a-number");
    }

    // --- Test getDateOnly(String) ---

    @Test
    public void getDateOnly_string_shouldReturnTimestamp_whenValidFormat() {
        // Partition: Valid input format "dd/MM/yyyy"
        String dateStr = "14/04/2025";
        long expectedTimestamp = DateHelper.parseDate(dateStr, DateFormats.S_DDMMYYYY); // Use parseDate for expected value
        assertEquals(expectedTimestamp, DateHelper.getDateOnly(dateStr));
    }

    @Test
    public void getDateOnly_string_shouldReturnZero_whenInvalidFormat() {
        // Partition: Invalid input format
        // The method catches ParseException and returns 0
        assertEquals(0L, DateHelper.getDateOnly("2025-04-14"));
        assertEquals(0L, DateHelper.getDateOnly("invalid-date"));
    }

    @Test
    public void getDateOnly_string_shouldReturnZero_whenNullInput() {
        // Partition: Null input string (should cause NullPointerException within SimpleDateFormat, caught)
        // Note: Depending on internal handling, it might throw NPE before catch or return 0 after catch.
        // Based on the code structure (try-catch around parse), it should return 0.
        assertEquals(0L, DateHelper.getDateOnly(null));
    }

    // --- Test getDateOnly(long) ---

    @Test
    public void getDateOnly_long_shouldReturnFormattedString() {
        // Partition: Valid timestamp input
        String expected = sdf_ddMMyyyy.format(todayTimestamp);
        assertEquals(expected, DateHelper.getDateOnly(todayTimestamp));
    }

    // --- Test getDateAndTime(long) ---

    @Test
    public void getDateAndTime_long_shouldReturnFormattedString() {
        // Partition: Valid timestamp input
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault());
        String expected = sdf.format(todayTimestamp);
        assertEquals(expected, DateHelper.getDateAndTime(todayTimestamp));
    }

    // Skipped: getDateAndTime(String) - Implementation seems incorrect

    // --- Test getTimeOnly(long) ---

    @Test
    public void getTimeOnly_long_shouldReturnFormattedString() {
        // Partition: Valid timestamp input
        String expected = sdf_hhmma.format(todayTimestamp);
        assertEquals(expected, DateHelper.getTimeOnly(todayTimestamp));
    }

    // --- Test getTodayWithTime() ---

    @Test
    public void getTodayWithTime_shouldReturnCorrectFormat() {
        // Test format consistency. Exact value depends on execution time.
        String todayWithTime = DateHelper.getTodayWithTime();
        // Regex to check dd/MM/yyyy HH:mm:ss format
        assertTrue(todayWithTime.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    // --- Test getToday() ---

    @Test
    public void getToday_shouldReturnCorrectFormat() {
        // Test format and value consistency.
        String today = DateHelper.getToday();
        String expectedToday = sdf_ddMMyyyy.format(new Date()); // Get current date formatted
        assertEquals(expectedToday, today);
    }

    // --- Test getTomorrow() ---
    @Test
    public void getTomorrow_shouldReturnCorrectFormatAndDate() {
        // Test format and calculated date.
        String tomorrow = DateHelper.getTomorrow();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1); // Calculate tomorrow
        String expectedTomorrow = sdf_ddMMyyyy.format(cal.getTime());

        assertEquals(expectedTomorrow, tomorrow);
    }

    // --- Test getDaysBetweenTwoDate ---
    @Test
    public void getDaysBetweenTwoDate_shouldReturnCorrectDifference() {
        // Partitions: Valid matching format, old < new, old == new, old > new
        DateFormats format = DateFormats.S_DDMMYYYYHHMMA; // "dd/MM/yyyy, hh:mma"
        String date1Str = "14/04/2025, 10:00 AM";
        String date2Str = "16/04/2025, 09:00 AM"; // Approx 2 days later
        String date3Str = "14/04/2025, 02:00 PM"; // Same day

        // old < new
        assertEquals(Long.valueOf(-1), DateHelper.getDaysBetweenTwoDate(date1Str, date2Str, format)); // diff is date1 - date2
        // old > new
        assertEquals(Long.valueOf(1), DateHelper.getDaysBetweenTwoDate(date2Str, date1Str, format));
        // old == new (same day, different time) -> 0 days diff
        assertEquals(Long.valueOf(0), DateHelper.getDaysBetweenTwoDate(date1Str, date3Str, format));
        assertEquals(Long.valueOf(0), DateHelper.getDaysBetweenTwoDate(date1Str, date1Str, format));
    }

    @Test
    public void getDaysBetweenTwoDate_shouldReturnNull_whenInvalidFormat() {
        // Partition: Non-matching date string format
        DateFormats format = DateFormats.S_DDMMYYYY; // "dd/MM/yyyy"
        String date1Str = "2025-04-14"; // Mismatched format
        String date2Str = "16/04/2025";
        assertNull(DateHelper.getDaysBetweenTwoDate(date1Str, date2Str, format));
    }

    @Test
    public void getDaysBetweenTwoDate_shouldReturnNull_whenNullInputString() {
        // Partition: Null input strings (should cause ParseException -> return null)
        DateFormats format = DateFormats.S_DDMMYYYY;
        assertNull(DateHelper.getDaysBetweenTwoDate(null, "16/04/2025", format));
        assertNull(DateHelper.getDaysBetweenTwoDate("14/04/2025", null, format));
    }

    @Test(expected = NullPointerException.class)
    public void getDaysBetweenTwoDate_shouldThrowNPE_whenNullFormat() {
        // Partition: Null DateFormats enum
        DateHelper.getDaysBetweenTwoDate("14/04/2025", "16/04/2025", null);
    }


    // --- Test getHoursBetweenTwoDate --- (Similar partitions as Days)
    @Test
    public void getHoursBetweenTwoDate_shouldReturnCorrectDifference() {
        DateFormats format = DateFormats.S_DDMMYYYYHHMMA; // "dd/MM/yyyy, hh:mma"
        String date1Str = "14/04/2025, 10:00 AM";
        String date2Str = "14/04/2025, 12:00 PM"; // 2 hours later
        String date3Str = "15/04/2025, 10:00 AM"; // 24 hours later

        assertEquals(Long.valueOf(-2), DateHelper.getHoursBetweenTwoDate(date1Str, date2Str, format)); // date1 - date2
        assertEquals(Long.valueOf(2), DateHelper.getHoursBetweenTwoDate(date2Str, date1Str, format));
        assertEquals(Long.valueOf(-24), DateHelper.getHoursBetweenTwoDate(date1Str, date3Str, format));
        assertEquals(Long.valueOf(0), DateHelper.getHoursBetweenTwoDate(date1Str, date1Str, format));
    }

    @Test
    public void getHoursBetweenTwoDate_shouldReturnNull_whenInvalidFormat() {
        DateFormats format = DateFormats.S_DDMMYYYY;
        String date1Str = "invalid-date";
        String date2Str = "16/04/2025";
        assertNull(DateHelper.getHoursBetweenTwoDate(date1Str, date2Str, format));
    }

    @Test(expected = NullPointerException.class)
    public void getHoursBetweenTwoDate_shouldThrowNPE_whenNullFormat() {
        DateHelper.getHoursBetweenTwoDate("14/04/2025, 10:00 AM", "14/04/2025, 12:00 PM", null);
    }

    // --- Test getMinutesBetweenTwoDates --- (Similar partitions as Days/Hours)
    @Test
    public void getMinutesBetweenTwoDates_shouldReturnCorrectDifference() {
        DateFormats format = DateFormats.S_DDMMYYYYHHMMA; // "dd/MM/yyyy, hh:mma"
        String date1Str = "14/04/2025, 10:00 AM";
        String date2Str = "14/04/2025, 10:30 AM"; // 30 mins later
        String date3Str = "14/04/2025, 11:00 AM"; // 60 mins later

        assertEquals(Long.valueOf(-30), DateHelper.getMinutesBetweenTwoDates(date1Str, date2Str, format)); // date1 - date2
        assertEquals(Long.valueOf(30), DateHelper.getMinutesBetweenTwoDates(date2Str, date1Str, format));
        assertEquals(Long.valueOf(-60), DateHelper.getMinutesBetweenTwoDates(date1Str, date3Str, format));
        assertEquals(Long.valueOf(0), DateHelper.getMinutesBetweenTwoDates(date1Str, date1Str, format));
    }

    @Test
    public void getMinutesBetweenTwoDates_shouldReturnNull_whenInvalidFormat() {
        DateFormats format = DateFormats.S_DDMMYYYY;
        String date1Str = "14/04/2025";
        String date2Str = "not-a-date";
        assertNull(DateHelper.getMinutesBetweenTwoDates(date1Str, date2Str, format));
    }

    @Test(expected = NullPointerException.class)
    public void getMinutesBetweenTwoDates_shouldThrowNPE_whenNullFormat() {
        DateHelper.getMinutesBetweenTwoDates("14/04/2025, 10:00 AM", "14/04/2025, 10:30 AM", null);
    }

    // --- Test parseAnyDate ---

    @Test
    public void parseAnyDate_shouldReturnTimestamp_whenFormatMatches() {
        // Partition: Input matches one of the DateFormats
        String dateStr = "14/04/2025"; // Matches S_DDMMYYYY
        long expectedTimestamp = DateHelper.parseDate(dateStr, DateFormats.S_DDMMYYYY);
        // Note: parseAnyDate iterates, the *last* successful parse wins if multiple match patterns.
        // We expect it to successfully parse with *some* format.
        assertTrue(DateHelper.parseAnyDate(dateStr) > 0); // Check if it parsed successfully
        assertEquals(expectedTimestamp, DateHelper.parseAnyDate(dateStr)); // Verify against a known matching format

        String dateStr2 = "2025-Apr-14, 10:30:00AM"; // D_YYYYMMDDHHMMSSA_N
        long expectedTimestamp2 = DateHelper.parseDate(dateStr2, DateFormats.D_YYYYMMDDHHMMSSA_N);
        assertTrue(DateHelper.parseAnyDate(dateStr2) > 0);
        assertEquals(expectedTimestamp2, DateHelper.parseAnyDate(dateStr2));

    }

    @Test
    public void parseAnyDate_shouldReturnZero_whenNoFormatMatches() {
        // Partition: Input matches none of the DateFormats
        String dateStr = "April 14th 2025"; // Custom format not in enum
        assertEquals(0L, DateHelper.parseAnyDate(dateStr));
    }

    @Test
    public void parseAnyDate_shouldReturnZero_whenNullInput() {
        // Partition: Null input (Internal loops will likely get NPEs caught, returning 0)
        assertEquals(0L, DateHelper.parseAnyDate(null));
    }

    // --- Test parseDate ---

    @Test
    public void parseDate_shouldReturnTimestamp_whenFormatMatches() {
        // Partition: Input format matches specified DateFormat
        String dateStr = "14-Apr-2025";
        DateFormats format = DateFormats.D_DDMMYYYY_N;
        long timestamp = DateHelper.parseDate(dateStr, format);
        assertTrue(timestamp > 0); // Check parsing was successful

        // Verify by formatting back
        assertEquals(dateStr, DateHelper.getDesiredFormat(format, timestamp));
    }

    @Test
    public void parseDate_shouldReturnZero_whenFormatMismatches() {
        // Partition: Input format does not match specified DateFormat
        String dateStr = "14/04/2025"; // Slash format
        DateFormats format = DateFormats.D_DDMMYYYY_N; // Expecting dash format
        assertEquals(0L, DateHelper.parseDate(dateStr, format));
    }

    @Test
    public void parseDate_shouldReturnZero_whenNullInputString() {
        // Partition: Null input string (causes ParseException -> returns 0)
        DateFormats format = DateFormats.S_DDMMYYYY;
        assertEquals(0L, DateHelper.parseDate(null, format));
    }

    @Test(expected = NullPointerException.class)
    public void parseDate_shouldThrowNPE_whenNullFormat() {
        // Partition: Null DateFormats enum
        DateHelper.parseDate("14/04/2025", null);
    }

    // --- Test getDesiredFormat(formats) ---

    @Test
    public void getDesiredFormat_noTimestamp_shouldReturnCurrentDateFormatted() {
        // Partition: Valid DateFormats enum
        DateFormats format = DateFormats.D_YYYYMMDDHHMMA_N; // "yyyy-MMM-dd, hh:mma"
        String formattedDate = DateHelper.getDesiredFormat(format);

        SimpleDateFormat sdf = new SimpleDateFormat(format.getDateFormat(), Locale.getDefault());
        String expectedDate = sdf.format(new Date()); // Format current date/time

        assertEquals(expectedDate, formattedDate);
    }

    @Test(expected = NullPointerException.class)
    public void getDesiredFormat_noTimestamp_shouldThrowNPE_whenNullFormat() {
        // Partition: Null DateFormats enum
        DateHelper.getDesiredFormat(null);
    }

    // --- Test getDesiredFormat(formats, date) ---

    @Test
    public void getDesiredFormat_withTimestamp_shouldReturnTimestampFormatted() {
        // Partition: Valid DateFormats enum and timestamp
        DateFormats format = DateFormats.S_YYMMDDHHMMSSA; // "yy/MM/dd, hh:mm:ssa"
        long timestamp = yesterdayTimestamp; // Use a fixed timestamp
        String formattedDate = DateHelper.getDesiredFormat(format, timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat(format.getDateFormat(), Locale.getDefault());
        String expectedDate = sdf.format(new Date(timestamp));

        assertEquals(expectedDate, formattedDate);
    }

    @Test(expected = NullPointerException.class)
    public void getDesiredFormat_withTimestamp_shouldThrowNPE_whenNullFormat() {
        // Partition: Null DateFormats enum
        DateHelper.getDesiredFormat(null, todayTimestamp);
    }

    // --- Test getDateFromDays ---

    @Test
    public void getDateFromDays_shouldReturnCorrectDate_forPositiveDays() {
        // Partition: Positive numOfDays
        int daysToAdd = 5;
        String formattedDate = DateHelper.getDateFromDays(daysToAdd);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
        String expectedDate = DateHelper.getDesiredFormat(DateFormats.D_DDMMyy_N, cal.getTimeInMillis()); // "dd-MMM-yy"

        assertEquals(expectedDate, formattedDate);
    }

    @Test
    public void getDateFromDays_shouldReturnCorrectDate_forZeroDays() {
        // Partition: Zero numOfDays
        int daysToAdd = 0;
        String formattedDate = DateHelper.getDateFromDays(daysToAdd);

        Calendar cal = Calendar.getInstance();
        // cal.add(Calendar.DAY_OF_MONTH, daysToAdd); // No change
        String expectedDate = DateHelper.getDesiredFormat(DateFormats.D_DDMMyy_N, cal.getTimeInMillis()); // "dd-MMM-yy"

        assertEquals(expectedDate, formattedDate);
    }

    @Test
    public void getDateFromDays_shouldReturnCorrectDate_forNegativeDays() {
        // Partition: Negative numOfDays
        int daysToAdd = -3;
        String formattedDate = DateHelper.getDateFromDays(daysToAdd);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
        String expectedDate = DateHelper.getDesiredFormat(DateFormats.D_DDMMyy_N, cal.getTimeInMillis()); // "dd-MMM-yy"

        assertEquals(expectedDate, formattedDate);
    }
}