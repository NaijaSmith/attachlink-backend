package com.attachlink.service;

import com.attachlink.dto.StudentReportResponse;
import com.attachlink.entity.Evaluation;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import com.attachlink.repository.EmployerFeedbackRepository;
import com.attachlink.repository.EvaluationRepository;
import com.attachlink.repository.LogEntryRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service responsible for aggregating data from LogEntries, EmployerFeedback, 
 * and Supervisor Evaluations to generate a comprehensive performance report.
 */
@Service
public class ReportService {

    private final LogEntryRepository logEntryRepository;
    private final EmployerFeedbackRepository feedbackRepository;
    private final EvaluationRepository evaluationRepository;

    public ReportService(
            LogEntryRepository logEntryRepository,
            EmployerFeedbackRepository feedbackRepository,
            EvaluationRepository evaluationRepository) {
        this.logEntryRepository = logEntryRepository;
        this.feedbackRepository = feedbackRepository;
        this.evaluationRepository = evaluationRepository;
    }

    /**
     * Aggregates student attachment data into a Report DTO.
     */
    @Transactional(readOnly = true)
    public StudentReportResponse generateStudentReport(User student) {
        
        // 1. Fetch Basic Log Statistics
        long totalLogs = logEntryRepository.countByStudent(student);
        long approvedLogs = logEntryRepository.countByStudentAndStatus(student, LogStatus.APPROVED);
        long rejectedLogs = logEntryRepository.countByStudentAndStatus(student, LogStatus.REJECTED);

        // 2. Calculate Approval Rate
        double rawApprovalRate = totalLogs == 0
                ? 0.0
                : (double) approvedLogs / totalLogs * 100;
        double approvalRate = Math.round(rawApprovalRate * 100.0) / 100.0;

        // 3. Fetch Average Employer Rating
        Double avgRating = feedbackRepository.getAverageRatingForStudent(student);

        // 4. Collect Supervisor Remarks from the Evaluation Entity
        // We fetch evaluations linked to logs belonging to this student
        List<String> remarks = evaluationRepository.findAllByStudent(student)
                .stream()
                .map(Evaluation::getRemarks)
                .filter(Objects::nonNull)
                .filter(remark -> !remark.trim().isEmpty())
                .collect(Collectors.toList());

        // 5. Construct Response
        StudentReportResponse report = new StudentReportResponse();
        report.setStudentId(student.getId());
        report.setStudentName(student.getFullName());
        report.setRegistrationNumber(student.getRegistrationNumber());
        
        report.setTotalLogs(totalLogs);
        report.setApprovedLogs(approvedLogs);
        report.setRejectedLogs(rejectedLogs);
        report.setApprovalRate(approvalRate);
        
        report.setAverageEmployerRating(avgRating != null ? avgRating : 0.0);
        
        // Map the remarks to the DTO field (still named supervisorComments for frontend compatibility)
        report.setSupervisorComments(remarks);

        return report;
    }

    /**
     * Exports the generated report as a PDF byte array.
     */
    public byte[] exportReportAsPdf(StudentReportResponse report) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Font Styles
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);

            // Title
            Paragraph title = new Paragraph("AttachLink Attachment Progress Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Student Info
            document.add(new Paragraph("Student Name: " + report.getStudentName(), subTitleFont));
            document.add(new Paragraph("Registration Number: " + report.getRegistrationNumber(), subTitleFont));
            document.add(Chunk.NEWLINE);

            // Statistics Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            
            addTableCell(table, "Total Log Entries", normalFont);
            addTableCell(table, String.valueOf(report.getTotalLogs()), normalFont);
            
            addTableCell(table, "Approved Logs", normalFont);
            addTableCell(table, String.valueOf(report.getApprovedLogs()), normalFont);
            
            addTableCell(table, "Approval Rate", normalFont);
            addTableCell(table, report.getApprovalRate() + "%", normalFont);
            
            addTableCell(table, "Avg Employer Rating", normalFont);
            addTableCell(table, String.valueOf(report.getAverageEmployerRating()), normalFont);

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Supervisor Remarks Section
            document.add(new Paragraph("Supervisor Remarks Summary:", subTitleFont));
            if (report.getSupervisorComments() == null || report.getSupervisorComments().isEmpty()) {
                document.add(new Paragraph("No supervisor remarks available yet.", normalFont));
            } else {
                com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
                for (String remark : report.getSupervisorComments()) {
                    list.add(new ListItem(remark, normalFont));
                }
                document.add(list);
            }

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}