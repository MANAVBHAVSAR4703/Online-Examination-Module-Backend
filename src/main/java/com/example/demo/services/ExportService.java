package com.example.demo.services;

import com.example.demo.models.Option;
import com.example.demo.models.Question;
import com.example.demo.models.Student;
import com.example.demo.models.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportService {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ExportService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static boolean checkFileType(MultipartFile file){
        return TYPE.equals(file.getContentType());
    }

    public List<Question> excelToQuestionList(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            List<Question> queList = new ArrayList<>();
            int rowNumber = 0;

            for (Row currentRow : sheet) {
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Question question = new Question();
                List<Option> optionList = new ArrayList<>();

                for (int cellIdx = 0; cellIdx <= 7; cellIdx++) {
                    Cell currentCell = currentRow.getCell(cellIdx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                    try {
                        switch (cellIdx) {
                            case 0:
                                if (currentCell != null && currentCell.getCellType() == CellType.STRING) {
                                    question.setText(currentCell.getStringCellValue());
                                }
                                break;

                            case 1:
                                if (currentCell != null && currentCell.getCellType() == CellType.STRING) {
                                    question.setCategory(Question.Category.valueOf(currentCell.getStringCellValue()));
                                }
                                break;

                            case 2: case 3: case 4: case 5:
                                if (currentCell != null) {
                                    String cellValue = "";
                                    if (currentCell.getCellType() == CellType.STRING) {
                                        cellValue = currentCell.getStringCellValue().trim();
                                    } else if (currentCell.getCellType() == CellType.NUMERIC) {
                                        cellValue = String.valueOf((int) currentCell.getNumericCellValue());
                                    }

                                    if (!cellValue.isEmpty()) {
                                        Option op = new Option();
                                        op.setText(cellValue);
                                        op.setQuestion(question);
                                        optionList.add(op);
                                    }
                                }
                                break;

                            case 6:
                                if (currentCell != null) {
                                    if (currentCell.getCellType() == CellType.NUMERIC) {
                                        question.setCorrectOptionIndex((int) currentCell.getNumericCellValue());
                                    } else if (currentCell.getCellType() == CellType.STRING) {
                                        question.setCorrectOptionIndex(Integer.parseInt(currentCell.getStringCellValue()));
                                    }
                                }
                                break;

                            case 7:
                                if (currentCell != null && currentCell.getCellType() == CellType.STRING) {
                                    question.setDifficulty(Question.Difficulty.valueOf(currentCell.getStringCellValue()));
                                }
                                break;

                            default:
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing cell " + cellIdx + ": " + e.getMessage());
                    }
                }

                if (!optionList.isEmpty()) {
                    question.setOptions(optionList);
                }
                queList.add(question);
            }

            workbook.close();
            return queList;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    public List<Student> excelToStudentList(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            List<Student> studList = new ArrayList<>();
            int rowNumber = 0;

            for (Row currentRow : sheet) {
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Student student = new Student();
                User user=new User();
                for (int cellIdx = 0; cellIdx <= 4; cellIdx++) {
                    Cell currentCell = currentRow.getCell(cellIdx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    try {
                        switch (cellIdx) {
                            case 0:
                                if (currentCell != null && currentCell.getCellType() == CellType.STRING) {
                                    user.setFullName(currentCell.getStringCellValue());
                                }
                                break;

                            case 1:
                                if (currentCell != null && currentCell.getCellType() == CellType.STRING) {
                                    user.setEmail(currentCell.getStringCellValue());
                                }
                                break;

                            case 2:
                                if (currentCell != null && currentCell.getCellType() == CellType.STRING) {
                                    user.setPassword(passwordEncoder.encode(currentCell.getStringCellValue()));
                                }
                                break;
                            case 3:
                                if (currentCell != null && currentCell.getCellType() == CellType.STRING) {
                                    student.setEnrollNo(Long.parseLong(currentCell.getStringCellValue()));
                                }
                                if (currentCell != null && currentCell.getCellType() == CellType.NUMERIC) {
                                    student.setEnrollNo(Long.parseLong(String.valueOf(currentCell.getNumericCellValue())));
                                }
                                break;
                            case 4:
                                if (currentCell != null && currentCell.getCellType() == CellType.STRING) {
                                    student.setCollege(currentCell.getStringCellValue());
                                }
                                break;
                            default:
                                break;
                        }
                        user.setRole("STUDENT");
                        student.setUser(user);
                    } catch (Exception e) {
                        System.err.println("Error processing cell " + cellIdx + ": " + e.getMessage());
                    }
                }
                studList.add(student);
            }

            workbook.close();
            return studList;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }
}
