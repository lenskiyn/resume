package com.sbrf.telegrambot.util;

import com.sbrf.telegrambot.config.Config;
import com.sbrf.telegrambot.model.User;
import com.sbrf.telegrambot.model.UserService;
import com.sbrf.telegrambot.service.ServiceDateSupplier;
import com.sbrf.telegrambot.util.UtilGetScheduleFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Component
public class
DS_ScheduleParser {
    @Autowired
    UserService userService;
    @Autowired
    UtilGetScheduleFile getScheduleFile;

    private final static DataFormatter formatter = new DataFormatter();
    private static LinkedList<LinkedList<String>> parsedXlsx = new LinkedList<>();
    private static HashMap<Long, LinkedHashMap<Integer, String>> individualSchedules = new HashMap<>();

    public void parse() throws IOException {
        parseXlsxToList();
        parseListToMap();
    }

    private void parseXlsxToList() throws IOException {
        parsedXlsx = new LinkedList<>();

        if (!Files.exists(Paths.get(Config.SCHEDULE_NAME))) {
            getScheduleFile.downloadFromCloud(Config.SCHEDULE_NAME);
        }

        FileInputStream fileInputStream = new FileInputStream(Config.SCHEDULE_NAME);

        Workbook wb = new XSSFWorkbook(fileInputStream);
        Sheet sheet = wb.getSheet(Config.MONTH_NAMES[ServiceDateSupplier.getCurrentMonth()]);

        for (Row row : sheet) {
            LinkedList<String> cells = new LinkedList<>();
            for (Cell cell : row) {
                if (cell.getColumnIndex() <= ServiceDateSupplier.getLastDayOfMonth()) {
                    if (cell.getCellStyle() != null)
                        cells.add(formatter.formatCellValue(cell).trim() + checkShiftType(cell));
                }
            }
            parsedXlsx.add(cells);
        }
        fileInputStream.close();
        wb.close();
    }

    private void parseListToMap() {
        individualSchedules = new HashMap<>();
        List<User> users = userService.getAll();
        for (User u : users) {
            individualSchedules.put(u.getId(), new LinkedHashMap<>());
            for (LinkedList<String> list : parsedXlsx) {
                if (list.contains(u.getName())) {
//                    System.out.println("\n" + u.getName() + " ");
                    for (int i = 1; i <= ServiceDateSupplier.getLastDayOfMonth(); i++) {
                        individualSchedules.get(u.getId()).put(i, list.get(i));
//                        System.out.println(u.getId() + " " + i + " " + list.get(i));
                    }
                }
            }
        }
    }

    private String checkShiftType(Cell cell) {
        if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor() != null) {
            if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2).contains(Config.GREEN_CELLCOLOR)) {
                return " смена в день";
            } else if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2).contains(Config.WAY4_11_CELLCOLOR)) {
                return " смена в день с 11 на way4";
            } else if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2).contains(Config.WAY4_8_CELLCOLOR)) {
                return " смена в день с 8 на way4";
            } else if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2).contains(Config.BLUE_CELLCOLOR_LIBREOFFICE) ||
                    ((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2).contains(Config.BLUE_CELLCOLOR_MCOFFICE)) {
                return " смена в ночь";
            } else if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2).contains(Config.WAY4_HBMON_CELLCOLOR)) {
                return " смена на мониторинге'";
            } else if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2).contains(Config.X_CELLCOLOR)) {
                return " смена на way4'";
            } else if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2).contains(Config.ILL_CELLCOLOR)) {
                return " болн";
            } else if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2).contains(Config.VACATION_CELLCOLOR)) {
                return " отпуск";
            } else if (formatter.formatCellValue(cell).equals("12")) return " смена в день";
            else if (formatter.formatCellValue(cell).equals("")) return " вых";
        } else if (formatter.formatCellValue(cell).equals("")) return " вых";
        return "";
    }

    public String getNextShift(String telegramId) {
        try {
            Long id = Long.parseLong(telegramId);
            if (individualSchedules.isEmpty()) parse();
            for (int i = ServiceDateSupplier.getTomorrowDay(); i < ServiceDateSupplier.getLastDayOfMonth(); i++) {
                String day = individualSchedules.get(id).get(i);
                if (day.contains("в ")) {
                    //System.out.println(day + " " + i);
                    return day.contains("в день") ?
                            String.format("Следующая смена %d-го в день%n%s%n%n%s", i, "Также в этот день работают:", getShiftsForSpecificDay(i)) :
                            String.format("Следующая смена %d-го в ночь%n%s%n%n%s", i, "Также в этот день работают:", getShiftsForSpecificDay(i));
                }
            }
            return "До конца месяца смен не будет";
        } catch (Exception e) {
            return "До конца месяца смен не будет";
        }
    }

    public String getAllShiftsForUser(String userName) {
        try {
            Long telegramId = Long.parseLong(userService.getUserIdByName(userName));
            if (individualSchedules.isEmpty()) parse();
            StringBuilder allShifts = new StringBuilder();
            allShifts.append(Config.MONTH_NAMES[ServiceDateSupplier.getCurrentMonth()]).append("\n\n");

            for (Map.Entry<Integer, String> e : individualSchedules.get(telegramId).entrySet()) {
                if (ServiceDateSupplier.getDayOfWeekByDate(e.getKey()).equals("пн")) {
                    allShifts.append("———————————").append("\n");
                }
                allShifts.append(ServiceDateSupplier.getDayOfWeekByDate(e.getKey()))
                        .append(" | ")
                        .append(e.getKey())
                        .append("   --  ")
                        .append(e.getValue().replaceFirst("\\d\\d", ""))
                        .append("\n");
            }
            return allShifts.toString();
        } catch (Exception e) {
            return "Не получилось составить индивидуальный график";
        }
    }

    public List<String> getShiftsForSpecificDayWithType(int day, String type) {
        List<String> shifts = new ArrayList<>();
        try {
            if (individualSchedules.isEmpty()) parse();
            for (User u : userService.getAll()) {
                if (!individualSchedules.get(u.getId()).isEmpty()
                        && individualSchedules.get(u.getId()).get(day).endsWith(type)) {
                    shifts.add(u.getName() + ":" + type);
                }
            }
        } catch (NullPointerException | NumberFormatException | IOException e) {
            shifts.add("Ошибка");
        }
        return shifts;
    }

    public String getShiftsForSpecificDay(int day) {
        StringBuilder resultString = new StringBuilder();
        resultString.append("В ДЕНЬ").append("\n").append("————————").append("\n");
        for (String s : getShiftsForSpecificDayWithType(day, "в день")) {
            resultString.append(s.split(":")[0]).append("\n");
        }
        resultString.append("\n").append("WAY4").append("\n").append("————————").append("\n");
        for (String s : getShiftsForSpecificDayWithType(day, "way4")) {
            resultString.append(s.split(":")[0]).append("\n");
        }
        resultString.append("\n").append("В НОЧЬ").append("\n").append("————————").append("\n");
        for (String s : getShiftsForSpecificDayWithType(day, "в ночь")) {
            resultString.append(s.split(":")[0]).append("\n");
        }
        resultString.append("\n").append("ХАБАРОВСК").append("\n").append("————————").append("\n");
        for (String s : getShiftsForSpecificDayWithType(day, "'")) {
            resultString.append(s.split(":")[0]).append("\n");
        }
        return resultString.toString();
    }

    /////////////
    // SERVICE //
    /////////////
    public String getCellsColorNameFor(String userName) {
        try {
            if (individualSchedules.isEmpty()) parse();
            Workbook wb = new XSSFWorkbook(Config.SCHEDULE_NAME);
            Sheet sheet = wb.getSheet(Config.MONTH_NAMES[ServiceDateSupplier.getCurrentMonth()]);
            StringBuilder allShifts = new StringBuilder();

            for (Row row : sheet) {
                if (formatter.formatCellValue(row.getCell(row.getFirstCellNum())).trim().equals(userName)) {
                    for (Cell cell : row) {
                        if (cell.getColumnIndex() <= ServiceDateSupplier.getLastDayOfMonth()) {
                            if (((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor() != null)
                                allShifts.append(formatter.formatCellValue(cell)).append(" - ")
                                        .append(((XSSFCellStyle) cell.getCellStyle()).getFillForegroundXSSFColor().getARGBHex().substring(2))
                                        .append("\n");
                        }
                    }
                }
            }
            wb.close();
            return allShifts.toString().isEmpty() ? "список пуст" : allShifts.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception in <getCellsColorNameFor>: " + e;
        }
    }
}