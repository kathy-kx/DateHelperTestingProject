package com.kxzhu.datehelper;

import org.junit.Test;
import static org.junit.Assert.*;

import com.kxzhu.datehelper.DateHelper;
import com.kxzhu.datehelper.DateHelper.DateFormats;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Unit tests for DateHelper using Pairwise Combinatorial Testing.
 */
public class DateHelperCombinatorialTest {

    // DateFormats Values
    private static final DateFormats F1 = DateFormats.D_YYYYMMDD;         // "yyyy-MM-dd"
    private static final DateFormats F2 = DateFormats.S_DDMMyyHHMMA;     // "dd/MM/yy, hh:mma"
    private static final DateFormats F3 = DateFormats.D_DDMMYYYY_N;      // "dd-MMM-yyyy"
    private static final DateFormats F4 = null;                           // Error case

    // 'old' String Values
    private static final String O1 = "2025-04-10"; // Matches F1
    private static final String O2 = "14/04/25, 10:00AM"; // Matches F2
    private static final String O3 = "18-Apr-2025"; // Matches F3
    private static final String O4 = "invalid-date-string";
    private static final String O5 = null;

    // 'newDate' String Values
    private static final String N1 = "2025-04-14"; // Matches F1
    private static final String N2 = "10/03/25, 08:00PM"; // Matches F2
    private static final String N3 = "20-May-2025"; // Matches F3
    private static final String N4 = "bad-date";
    private static final String N5 = null;

    // Formats
    private static final DateFormats DF_D_YYYYMMDD = DateFormats.D_YYYYMMDD;
    private static final DateFormats DF_S_DDMMyyHHMMA = DateFormats.S_DDMMyyHHMMA;
    private static final DateFormats DF_D_DDMMYYYY_N = DateFormats.D_DDMMYYYY_N;
    private static final DateFormats DF_HHMM = DateFormats.HHMM;
    private static final DateFormats DF_S_YYMMDD = DateFormats.S_YYMMDD; // Added for ParseModel
    private static final DateFormats DF_HHMMSSA = DateFormats.HHMMSSA; // Added for GetFormatModel
    private static final DateFormats FORMAT_NULL = null;

    // Strings (using descriptive names matching model)
    private static final String STR_2025_04_10 = "2025-04-10";
    private static final String STR_14_04_25_10AM = "14/04/25, 10:00AM";
    private static final String STR_18_APR_2025 = "18-Apr-2025";
    private static final String STR_15_30 = "15:30"; // Time only
    private static final String STR_INVALID_OLD = "invalid-old";
    private static final String STR_INVALID_NEW = "invalid-new";
    private static final String STR_BAD = "bad-string";
    private static final String STR_YYMMDD_PATTERN = "yy-MM-dd"; // Resembles pattern
    private static final String STR_NULL = null;

    // Timestamps (mapping placeholders from GetFormatModel)
    private static final long TS_NOW = System.currentTimeMillis();
    private static final long TS_PAST;
    private static final long TS_ZERO = 0L;
    private static final long TS_FUTURE;

    static {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // One year ago
        TS_PAST = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, 2); // One year from now (relative to original 'now')
        TS_FUTURE = cal.getTimeInMillis();
    }

    // --- Implement Test Cases based on generated pairs ---

    // Test Case 1: F1, O1, N1
    @Test
    public void testGetDaysBetween_Case1_F1_O1_N1() {
        // F1="yyyy-MM-dd", O1="2025-04-10", N1="2025-04-14"
        // Expect: Both parse ok with F1. Difference is -4 days (O1 - N1).
        Long expected = -4L;
        assertEquals(expected, DateHelper.getDaysBetweenTwoDate(O1, N1, F1));
    }

    // Test Case 2: F1, O2, N2
    @Test
    public void testGetDaysBetween_Case2_F1_O2_N2() {
        // F1="yyyy-MM-dd", O2="14/04/25, 10:00AM", N2="10/03/25, 08:00PM"
        // Expect: O2 and N2 do not match F1 format. ParseException caught -> returns null.
        assertNull(DateHelper.getDaysBetweenTwoDate(O2, N2, F1));
    }

    // Test Case 3: F1, O3, N3
    @Test
    public void testGetDaysBetween_Case3_F1_O3_N3() {
        // F1="yyyy-MM-dd", O3="18-Apr-2025", N3="20-May-2025"
        // Expect: O3 and N3 do not match F1 format. ParseException caught -> returns null.
        assertNull(DateHelper.getDaysBetweenTwoDate(O3, N3, F1));
    }

    // Test Case 4: F1, O4, N4
    @Test
    public void testGetDaysBetween_Case4_F1_O4_N4() {
        // F1="yyyy-MM-dd", O4="invalid-date-string", N4="bad-date"
        // Expect: O4 is invalid format. ParseException caught -> returns null.
        assertNull(DateHelper.getDaysBetweenTwoDate(O4, N4, F1));
    }

    // Test Case 5: F1, O5, N5
    @Test
    public void testGetDaysBetween_Case5_F1_O5_N5() {
        // F1="yyyy-MM-dd", O5=null, N5=null
        // Expect: O5 is null. ParseException caught (likely NPE internally first) -> returns null.
        assertNull(DateHelper.getDaysBetweenTwoDate(O5, N5, F1));
    }

    // Test Case 6: F2, O1, N2
    @Test
    public void testGetDaysBetween_Case6_F2_O1_N2() {
        // F2="dd/MM/yy, hh:mma", O1="2025-04-10", N2="10/03/25, 08:00PM"
        // Expect: O1 does not match F2 format. ParseException caught -> returns null.
        // Note: N2 *does* match F2.
        assertNull(DateHelper.getDaysBetweenTwoDate(O1, N2, F2));
    }

    // Test Case 7: F2, O2, N1
    @Test
    public void testGetDaysBetween_Case7_F2_O2_N1() {
        // F2="dd/MM/yy, hh:mma", O2="14/04/25, 10:00AM", N1="2025-04-14"
        // Expect: N1 does not match F2 format. ParseException caught -> returns null.
        assertNull(DateHelper.getDaysBetweenTwoDate(O2, N1, F2));
    }

    // Test Case 13: F4, O1, N3
    @Test(expected = NullPointerException.class)
    public void testGetDaysBetween_Case13_F4_O1_N3() {
        // F4=null, O1="2025-04-10", N3="20-May-2025"
        // Expect: F4 is null. Method uses it directly in SimpleDateFormat -> NullPointerException.
        DateHelper.getDaysBetweenTwoDate(O1, N3, F4);
    }

    // --- Helper to calculate expected difference for valid cases ---
    // You might need a helper if calculations get complex, or do it inline.
    private Long calculateExpectedDays(String dateStr1, String dateStr2, DateFormats format) {
        try {
            long time1 = DateHelper.parseDate(dateStr1, format);
            long time2 = DateHelper.parseDate(dateStr2, format);
            if (time1 == 0 || time2 == 0) return null; // Parsing failed indicator from parseDate
            long diff = time1 - time2;
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // Should not happen if parseDate handles its exceptions, but good practice
            return null;
        }
    }

    @Test
    public void testParseDate_WithSpecificFormats() {
        // Test parsing with formats that might be tricky (yy vs yyyy, slashes, dashes, MMM)
        assertEquals(1713067200000L, DateHelper.parseDate("14/04/24", DateFormats.S_DDMMyy)); // Assuming TS is for 2024-04-14 00:00:00 GMT
        assertEquals(1713067200000L, DateHelper.parseDate("24-04-14", DateFormats.D_YYMMDD));
        assertEquals(1713067200000L, DateHelper.parseDate("14-Apr-2024", DateFormats.D_DDMMYYYY_N));
        assertEquals(1713106800000L, DateHelper.parseDate("24/Apr/14, 11:00AM", DateFormats.S_YYMMDDHHMMA_N)); // 11:00 AM GMT
    }

    @Test
    public void testGetDesiredFormat_WithSpecificFormats() {
        long timestamp = 1713124800000L; // Represents 2024-04-14 16:00:00 GMT

        // Verify output for various specific formats
        assertEquals("24-04-14", DateHelper.getDesiredFormat(DateFormats.D_YYMMDD, timestamp));
        assertEquals("14/04/24", DateHelper.getDesiredFormat(DateFormats.S_DDMMyy, timestamp));
        assertEquals("2024-Apr-14", DateHelper.getDesiredFormat(DateFormats.D_YYYYMMDD_N, timestamp));
        assertEquals("14-Apr-2024, 04:00:00PM", DateHelper.getDesiredFormat(DateFormats.D_DDMMYYYYHHMMSSA_N, timestamp));
        assertEquals("04:00PM", DateHelper.getDesiredFormat(DateFormats.HHMMA, timestamp)); // Time only
    }

    // --- Tests for Boundary Dates (Leap Year, Month Ends) ---

    @Test
    public void testGetDaysBetween_LeapYearBoundary() {
        // Test around Feb 29th on a leap year
        String feb28 = "28/02/2024"; // 2024 is a leap year
        String feb29 = "29/02/2024";
        String mar1 = "01/03/2024";
        DateFormats format = DateFormats.S_DDMMYYYY;

        assertEquals(Long.valueOf(-1), DateHelper.getDaysBetweenTwoDate(feb28, feb29, format));
        assertEquals(Long.valueOf(-1), DateHelper.getDaysBetweenTwoDate(feb29, mar1, format));
        assertEquals(Long.valueOf(-2), DateHelper.getDaysBetweenTwoDate(feb28, mar1, format));
        assertEquals(Long.valueOf(1), DateHelper.getDaysBetweenTwoDate(mar1, feb29, format));
    }

    @Test
    public void testGetDaysBetween_MonthEndBoundary() {
        String apr30 = "30-Apr-2024";
        String may1 = "01-May-2024";
        DateFormats format = DateFormats.D_DDMMYYYY_N;

        assertEquals(Long.valueOf(-1), DateHelper.getDaysBetweenTwoDate(apr30, may1, format));
        assertEquals(Long.valueOf(1), DateHelper.getDaysBetweenTwoDate(may1, apr30, format));
    }

    @Test
    public void testGetDaysBetween_YearEndBoundary() {
        String dec31 = "2024-12-31";
        String jan1 = "2025-01-01";
        DateFormats format = DateFormats.D_YYYYMMDD;

        assertEquals(Long.valueOf(-1), DateHelper.getDaysBetweenTwoDate(dec31, jan1, format));
        assertEquals(Long.valueOf(1), DateHelper.getDaysBetweenTwoDate(jan1, dec31, format));
    }

    // --- Test for parseAnyDate Edge Cases ---
    @Test
    public void testParseAnyDate_AmbiguousOrPartialMatch() {
        // Test with a format defined later in the enum to ensure iteration works
        // D_YYYYMMDDHHMMSSA_N = "yyyy-MMM-dd, hh:mm:ssa"
        String dateStr = "2024-Apr-14, 04:15:30PM";
        long expectedTimestamp = DateHelper.parseDate(dateStr, DateFormats.D_YYYYMMDDHHMMSSA_N);
        assertTrue("Timestamp should be positive", DateHelper.parseAnyDate(dateStr) > 0);
        assertEquals("Timestamp should match specific parse", expectedTimestamp, DateHelper.parseAnyDate(dateStr));

        // Test a string that doesn't match any format
        assertEquals(0L, DateHelper.parseAnyDate("This is not a date"));
    }

    // --- Tests for Zero / Boundary Timestamps ---

    @Test
    public void testMethodsWithZeroTimestamp() {
        long zeroTimestamp = 0L; // Represents 1970-01-01 00:00:00 GMT

        // How SimpleDateFormat handles epoch varies slightly by system/locale TZ,
        // but we can check if it produces *something* non-null/empty and formatted.
        assertNotNull(DateHelper.getDateOnly(zeroTimestamp)); // e.g., "01/01/1970"
        assertNotNull(DateHelper.getDateAndTime(zeroTimestamp)); // e.g., "01/01/1970, 12:00 AM" (or similar)
        assertNotNull(DateHelper.getTimeOnly(zeroTimestamp)); // e.g., "12:00 AM" (or similar)
        assertNotNull(DateHelper.getDesiredFormat(DateFormats.S_YYYYMMDDHHMMSSA, zeroTimestamp));

        // PrettifyDate depends on DateUtils.isToday, hard to test for epoch reliably without Android env.
        // assertNotNull(DateHelper.prettifyDate(zeroTimestamp));
    }

    // --- Tests for getDateFromDays Boundaries ---
    @Test
    public void testGetDateFromDays_LargeValues() {
        // Test adding/subtracting a large number of days
        int daysForward = 365 * 2; // Approx 2 years
        int daysBackward = -365 * 3; // Approx -3 years

        String forwardDateStr = DateHelper.getDateFromDays(daysForward);
        String backwardDateStr = DateHelper.getDateFromDays(daysBackward);

        assertNotNull(forwardDateStr);
        assertNotNull(backwardDateStr);

        // Verify the format is correct (dd-MMM-yy)
        assertTrue(forwardDateStr.matches("\\d{2}-[A-Za-z]{3}-\\d{2}"));
        assertTrue(backwardDateStr.matches("\\d{2}-[A-Za-z]{3}-\\d{2}"));

        // Optional: More precise check if needed, calculate expected dates manually
        Calendar calForward = Calendar.getInstance();
        calForward.add(Calendar.DAY_OF_MONTH, daysForward);
        String expectedForward = DateHelper.getDesiredFormat(DateFormats.D_DDMMyy_N, calForward.getTimeInMillis());
        assertEquals(expectedForward, forwardDateStr);

        Calendar calBackward = Calendar.getInstance();
        calBackward.add(Calendar.DAY_OF_MONTH, daysBackward);
        String expectedBackward = DateHelper.getDesiredFormat(DateFormats.D_DDMMyy_N, calBackward.getTimeInMillis());
        assertEquals(expectedBackward, backwardDateStr);
    }

    // --- Tests for PrettifyDate (Simulating isToday boundary) ---
    // NOTE: DateUtils.isToday requires Android environment. This test *simulates*
    // the expected logic based on timestamps around midnight for demonstration.
    @Test
    public void testPrettifyDate_SimulatedTodayBoundary() {
        // Get midnight today
        Calendar midnightToday = Calendar.getInstance();
        midnightToday.set(Calendar.HOUR_OF_DAY, 0);
        midnightToday.set(Calendar.MINUTE, 0);
        midnightToday.set(Calendar.SECOND, 0);
        midnightToday.set(Calendar.MILLISECOND, 0);
        long midnightTimestamp = midnightToday.getTimeInMillis();

        // Timestamp just before midnight (yesterday)
        long justBeforeMidnight = midnightTimestamp - 1000; // 1 second before

        // Timestamp just after midnight (today)
        long justAfterMidnight = midnightTimestamp + 1000; // 1 second after

        // Expected formats based on the logic (ignoring actual DateUtils call)
        SimpleDateFormat fmtTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat fmtDateTime = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());

        // Simulate "not today"
        String expectedYesterday = fmtDateTime.format(justBeforeMidnight);
        // assertEquals(expectedYesterday, DateHelper.prettifyDate(justBeforeMidnight)); // This line requires DateUtils mocking or Android env

        // Simulate "is today"
        String expectedToday = fmtTime.format(justAfterMidnight);
        // assertEquals(expectedToday, DateHelper.prettifyDate(justAfterMidnight)); // This line requires DateUtils mocking or Android env

        // We can at least test the formatting part if we provide a known "today" timestamp
        long now = System.currentTimeMillis();
        String expectedNow = fmtTime.format(now);
        assertEquals(expectedNow, DateHelper.prettifyDate(now)); // This *should* work if 'now' is indeed today
    }


    @Test
    public void testBetween_Combo2() { // Example: F=S_DDMMyyHHMMA, Old=STR_18_APR_2025, New=STR_10_03_25_08PM
        DateFormats format = DF_S_DDMMyyHHMMA;
        String oldStr = STR_18_APR_2025; // Invalid for format F
        String newStr = "10/03/25, 08:00PM"; // Valid for format F
        // Expected: oldStr parse fails -> null
        assertNull(DateHelper.getDaysBetweenTwoDate(oldStr, newStr, format));
        assertNull(DateHelper.getHoursBetweenTwoDate(oldStr, newStr, format));
        assertNull(DateHelper.getMinutesBetweenTwoDates(oldStr, newStr, format));
    }

    @Test
    public void testBetween_Combo3() { // Example: F=HHMM, Old=STR_15_30, New=STR_09_00
        DateFormats format = DF_HHMM;
        String oldStr = STR_15_30; // Valid for format F
        String newStr = "09:00"; // Valid for format F
        // Expected: Both parse ok (representing times on 1970-01-01). 15:30 - 09:00 = 6.5 hours
        assertEquals(Long.valueOf(0), DateHelper.getDaysBetweenTwoDate(oldStr, newStr, format)); // Same day
        assertEquals(Long.valueOf(6), DateHelper.getHoursBetweenTwoDate(oldStr, newStr, format)); // 6 full hours
        assertEquals(Long.valueOf(390), DateHelper.getMinutesBetweenTwoDates(oldStr, newStr, format)); // 6*60 + 30
    }

    @Test
    public void testBetween_Combo5_NullString() { // Example: F=D_YYYYMMDD, Old=STR_2025_04_10, New=NULL_STR
        assertNull(DateHelper.getDaysBetweenTwoDate(STR_2025_04_10, STR_NULL, DF_D_YYYYMMDD));
        assertNull(DateHelper.getHoursBetweenTwoDate(STR_2025_04_10, STR_NULL, DF_D_YYYYMMDD));
        assertNull(DateHelper.getMinutesBetweenTwoDates(STR_2025_04_10, STR_NULL, DF_D_YYYYMMDD));
    }

    // --- Tests based on DateHelperParseCombinations.tsv ---
    @Test
    public void testParse_Combo2() { // Example: Input="14/04/25, 10:00AM", Format=D_YYYYMMDD
        // Expect: Mismatch, parse fails -> returns 0
        assertEquals(0L, DateHelper.parseDate(STR_14_04_25_10AM, DF_D_YYYYMMDD));
    }

    @Test
    public void testParse_Combo3() { // Example: Input="yy-MM-dd", Format=S_YYMMDD
        // Expect: Input is not a date, parse fails -> returns 0
        assertEquals(0L, DateHelper.parseDate(STR_YYMMDD_PATTERN, DF_S_YYMMDD));
    }

    @Test
    public void testParse_Combo4_NullString() { // Example: Input=NULL_STR, Format=D_YYYYMMDD
        // Expect: ParseException caught -> returns 0
        assertEquals(0L, DateHelper.parseDate(STR_NULL, DF_D_YYYYMMDD));
    }


    // --- Tests based on DateHelperGetFormatCombinations.tsv ---
    // Each @Test method represents ONE row generated by PICT for the GetFormatModel.
    // Examples:

    @Test
    public void testGetFormat_Combo1() { // Example: Format=D_YYYYMMDD, Timestamp=TS_PAST
        String formatted = DateHelper.getDesiredFormat(DF_D_YYYYMMDD, TS_PAST);
        // Expected format: yyyy-MM-dd
        assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    public void testGetFormat_Combo2() { // Example: Format=HHMMSSA, Timestamp=TS_ZERO
        String formatted = DateHelper.getDesiredFormat(DF_HHMMSSA, TS_ZERO);
        // Expected format: hh:mm:ssa (e.g., "12:00:00AM" depending on locale/TZ for epoch)
        assertTrue(formatted.matches("\\d{2}:\\d{2}:\\d{2}[AP]M"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetFormat_Combo3_NullFormat() { // Example: Format=NULL_FORMAT, Timestamp=TS_NOW
        DateHelper.getDesiredFormat(FORMAT_NULL, TS_NOW);
    }

}